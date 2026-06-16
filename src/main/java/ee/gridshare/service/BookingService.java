package ee.gridshare.service;

import ee.gridshare.domain.Booking;
import ee.gridshare.domain.BookingStatus;
import ee.gridshare.domain.Listing;
import ee.gridshare.domain.Payment;
import ee.gridshare.domain.PaymentStatus;
import ee.gridshare.domain.Payout;
import ee.gridshare.domain.PayoutStatus;
import ee.gridshare.notify.NotificationService;
import ee.gridshare.repo.BookingRepository;
import ee.gridshare.repo.ListingRepository;
import ee.gridshare.repo.PaymentRepository;
import ee.gridshare.repo.PayoutRepository;
import ee.gridshare.web.ApiException;
import ee.gridshare.web.dto.Dtos.CreateBookingRequest;
import ee.gridshare.web.dto.Dtos.PaymentLinkResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    /** Illustrative platform fee withheld from the host payout. */
    private static final BigDecimal PLATFORM_FEE = new BigDecimal("0.10");

    private final BookingRepository bookings;
    private final ListingRepository listings;
    private final PaymentRepository payments;
    private final PayoutRepository payouts;
    private final CurrentHost currentHost;
    private final NotificationService notifications;

    public BookingService(
            BookingRepository bookings,
            ListingRepository listings,
            PaymentRepository payments,
            PayoutRepository payouts,
            CurrentHost currentHost,
            NotificationService notifications) {
        this.bookings = bookings;
        this.listings = listings;
        this.payments = payments;
        this.payouts = payouts;
        this.currentHost = currentHost;
        this.notifications = notifications;
    }

    // ── Driver ───────────────────────────────────────────────

    @Transactional
    public Booking create(CreateBookingRequest req) {
        Listing listing =
                listings.findByIdAndActiveTrue(req.listingId()).orElseThrow(() -> ApiException.notFound("Laadijat ei leitud"));
        if (!listing.isAvailable()) {
            throw ApiException.conflict("Laadija pole hetkel saadaval");
        }
        if (req.phone() == null || req.phone().isBlank()) {
            throw ApiException.badRequest("Telefoninumber on kohustuslik");
        }

        // Reject overlapping time windows on the same listing (no double-booking).
        Instant reqStart = req.start();
        Instant reqEnd = reqStart.plus(Duration.ofMinutes(req.durationMin()));
        boolean overlaps = bookings
                .findByListingIdAndStatusIn(
                        listing.getId(),
                        List.of(BookingStatus.PENDING_HOST, BookingStatus.PENDING_PAYMENT, BookingStatus.CONFIRMED))
                .stream()
                .anyMatch(existing -> {
                    Instant s = existing.getStartTime();
                    Instant e = s.plus(Duration.ofMinutes(existing.getDurationMin()));
                    return reqStart.isBefore(e) && s.isBefore(reqEnd); // half-open interval overlap
                });
        if (overlaps) {
            throw ApiException.conflict("See ajavahemik on juba broneeritud");
        }

        BigDecimal price = listing.getPricePerHour()
                .multiply(BigDecimal.valueOf(req.durationMin()))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        Booking b = new Booking();
        b.setListing(listing);
        b.setDriverPhone(req.phone());
        b.setStartTime(req.start());
        b.setDurationMin(req.durationMin());
        b.setPrice(price);
        b.setStatus(BookingStatus.PENDING_HOST);
        Booking saved = bookings.save(b);
        // Step 9: SMS + email the host about the new request.
        notifications.hostNewBooking(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public Booking get(UUID id, String token) {
        // token validation comes with auth/security later
        return bookings.findById(id).orElseThrow(() -> ApiException.notFound("Broneeringut ei leitud"));
    }

    @Transactional
    public PaymentLinkResponse pay(UUID id, String token, String baseUrl) {
        Booking b = bookings.findById(id).orElseThrow(() -> ApiException.notFound("Broneeringut ei leitud"));
        if (b.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw ApiException.conflict("Broneering pole makse ootel (staatus: " + b.getStatus() + ")");
        }
        String ref = "mnt-" + UUID.randomUUID();
        Payment p = new Payment();
        p.setBooking(b);
        p.setMontonioRef(ref);
        p.setAmount(b.getPrice());
        p.setStatus(PaymentStatus.INITIATED);
        p.setPayoutStatus(PayoutStatus.PENDING);
        payments.save(p);
        // Dev stub URL pointing back at our own webhook simulator.
        return new PaymentLinkResponse(b.getId(), baseUrl + "/webhooks/montonio?ref=" + ref, ref);
    }

    // ── Host ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Booking> myBookings() {
        return bookings.findByListingHostOrderByCreatedAtDesc(currentHost.get());
    }

    @Transactional
    public Booking accept(UUID id, String token) {
        Booking b = bookings.findById(id).orElseThrow(() -> ApiException.notFound("Broneeringut ei leitud"));
        if (b.getStatus() != BookingStatus.PENDING_HOST) {
            throw ApiException.conflict("Ei saa aktsepteerida (staatus: " + b.getStatus() + ")");
        }
        b.setStatus(BookingStatus.PENDING_PAYMENT);
        b.setHostRespondedAt(Instant.now());
        // Step 11: SMS the driver the payment link.
        notifications.driverAccepted(b);
        return b;
    }

    @Transactional
    public Booking decline(UUID id, String token) {
        Booking b = bookings.findById(id).orElseThrow(() -> ApiException.notFound("Broneeringut ei leitud"));
        if (b.getStatus() != BookingStatus.PENDING_HOST) {
            throw ApiException.conflict("Ei saa keelduda (staatus: " + b.getStatus() + ")");
        }
        b.setStatus(BookingStatus.DECLINED);
        b.setHostRespondedAt(Instant.now());
        notifications.declined(b);
        return b;
    }

    // ── Montonio webhook ─────────────────────────────────────

    @Transactional
    public Booking confirmPayment(String montonioRef) {
        Payment payment =
                payments.findByMontonioRef(montonioRef).orElseThrow(() -> ApiException.notFound("Makset ei leitud"));
        Booking b = payment.getBooking();

        payment.setStatus(PaymentStatus.PAID);
        b.setStatus(BookingStatus.CONFIRMED);
        b.setPaidAt(Instant.now());
        // Reveal private access instructions now that payment cleared (§8).
        b.setAccessInstructions(b.getListing().getInstructions());

        BigDecimal payoutAmount = b.getPrice()
                .multiply(BigDecimal.ONE.subtract(PLATFORM_FEE))
                .setScale(2, RoundingMode.HALF_UP);
        Payout payout = new Payout();
        payout.setBooking(b);
        payout.setAmount(payoutAmount);
        payout.setStatus(PayoutStatus.PENDING);
        payouts.save(payout);
        payment.setPayoutStatus(PayoutStatus.PENDING);

        // Step 12: confirm to driver (with access instructions) + host.
        notifications.confirmed(b);
        return b;
    }
}
