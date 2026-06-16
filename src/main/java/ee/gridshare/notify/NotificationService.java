package ee.gridshare.notify;

import ee.gridshare.domain.AppUser;
import ee.gridshare.domain.Booking;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Composes the booking-handshake notifications (spec §2 steps 9, 11, 12) and
 * dispatches them via SMS / email. Called from BookingService at each transition.
 */
@Service
public class NotificationService {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd.MM HH:mm", Locale.forLanguageTag("et")).withZone(ZoneId.of("Europe/Tallinn"));

    private final SmsService sms;
    private final EmailService email;
    private final String frontendUrl;

    public NotificationService(
            SmsService sms, EmailService email, @Value("${app.frontend-url:http://localhost:3000}") String frontendUrl) {
        this.sms = sms;
        this.email = email;
        this.frontendUrl = frontendUrl;
    }

    /** Step 9: new request → notify the host (SMS + email). */
    public void hostNewBooking(Booking b) {
        AppUser host = b.getListing().getHost();
        String when = FMT.format(b.getStartTime());
        String money = money(b);
        sms.send(
                host.getPhone(),
                "GridShare: uus laadimispäring %s, %s. Halda: %s/app/bookings".formatted(when, money, frontendUrl));
        email.send(
                host.getEmail(),
                "Uus laadimispäring — GridShare",
                "Sul on uus broneeringupäring %s (%s).\nHalda: %s/app/bookings".formatted(when, money, frontendUrl));
    }

    /** Step 11: host accepted → send the driver the payment link. */
    public void driverAccepted(Booking b) {
        sms.send(
                b.getDriverPhone(),
                "GridShare: omanik kinnitas! Maksa ja kinnita: %s/broneering/%s?token=%s"
                        .formatted(frontendUrl, b.getId(), b.getId()));
    }

    /** Step 12: payment confirmed → driver gets access instructions, host gets a heads-up. */
    public void confirmed(Booking b) {
        sms.send(
                b.getDriverPhone(),
                "GridShare: broneering kinnitatud! Juurdepääs: %s"
                        .formatted(b.getAccessInstructions() == null ? "—" : b.getAccessInstructions()));
        AppUser host = b.getListing().getHost();
        sms.send(host.getPhone(), "GridShare: broneering kinnitatud (%s).".formatted(FMT.format(b.getStartTime())));
    }

    /** Host declined → tell the driver. */
    public void declined(Booking b) {
        sms.send(b.getDriverPhone(), "GridShare: kahjuks ei õnnestunud — omanik ei saanud laadijat jagada.");
    }

    /** Auto-expired (host never responded in time) → tell the driver. */
    public void expired(Booking b) {
        sms.send(
                b.getDriverPhone(),
                "GridShare: broneering aegus — omanik ei jõudnud vastata. Proovi mõnda teist laadijat.");
    }

    private static String money(Booking b) {
        return b.getPrice() + " €";
    }
}
