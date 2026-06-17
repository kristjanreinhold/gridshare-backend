package ee.gridshare.job;

import ee.gridshare.domain.Booking;
import ee.gridshare.domain.BookingStatus;
import ee.gridshare.notify.NotificationService;
import ee.gridshare.repo.BookingRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Auto-expires bookings still awaiting host acceptance once the host-response
 * window (default 30 min from creation) has elapsed OR the booking's start time
 * has passed — whichever comes first (spec §2 timeout logic). Runs on a cron
 * schedule; notifies the driver.
 */
@Component
public class BookingExpiryJob {

    private static final Logger log = LoggerFactory.getLogger(BookingExpiryJob.class);

    private final BookingRepository bookings;
    private final NotificationService notifications;

    /** Minutes a host has to respond before a pending booking auto-expires. */
    @Value("${app.bookings.host-response-minutes:30}")
    private long hostResponseMinutes;

    public BookingExpiryJob(BookingRepository bookings, NotificationService notifications) {
        this.bookings = bookings;
        this.notifications = notifications;
    }

    /** Default: every minute (sec min hour day month weekday). Overridable via property. */
    @Scheduled(cron = "${app.jobs.booking-expiry-cron:0 * * * * *}")
    @Transactional
    public void expireUnacceptedBookings() {
        Instant now = Instant.now();
        Instant createdCutoff = now.minus(Duration.ofMinutes(hostResponseMinutes));
        List<Booking> stale = bookings.findExpirable(BookingStatus.PENDING_HOST, createdCutoff, now);
        if (stale.isEmpty()) return;

        for (Booking b : stale) {
            b.setStatus(BookingStatus.EXPIRED); // dirty-checked → flushed on commit
            notifications.expired(b);
        }
        log.info("Booking expiry: expired {} unaccepted booking(s)", stale.size());
    }
}
