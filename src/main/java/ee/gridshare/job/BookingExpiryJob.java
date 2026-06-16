package ee.gridshare.job;

import ee.gridshare.domain.Booking;
import ee.gridshare.domain.BookingStatus;
import ee.gridshare.notify.NotificationService;
import ee.gridshare.repo.BookingRepository;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Auto-expires bookings still awaiting host acceptance once their start time has
 * passed (spec §2 timeout logic). Runs on a cron schedule; notifies the driver.
 */
@Component
public class BookingExpiryJob {

    private static final Logger log = LoggerFactory.getLogger(BookingExpiryJob.class);

    private final BookingRepository bookings;
    private final NotificationService notifications;

    public BookingExpiryJob(BookingRepository bookings, NotificationService notifications) {
        this.bookings = bookings;
        this.notifications = notifications;
    }

    /** Default: every 5 minutes (sec min hour day month weekday). Overridable via property. */
    @Scheduled(cron = "${app.jobs.booking-expiry-cron:0 */5 * * * *}")
    @Transactional
    public void expirePastUnacceptedBookings() {
        Instant now = Instant.now();
        List<Booking> stale = bookings.findByStatusAndStartTimeBefore(BookingStatus.PENDING_HOST, now);
        if (stale.isEmpty()) return;

        for (Booking b : stale) {
            b.setStatus(BookingStatus.EXPIRED); // dirty-checked → flushed on commit
            notifications.expired(b);
        }
        log.info("Booking expiry: expired {} past unaccepted booking(s)", stale.size());
    }
}
