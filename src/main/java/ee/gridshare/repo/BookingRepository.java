package ee.gridshare.repo;

import ee.gridshare.domain.AppUser;
import ee.gridshare.domain.Booking;
import ee.gridshare.domain.BookingStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByListingHostOrderByCreatedAtDesc(AppUser host);

    /** Stale bookings still in {@code status} whose start time is already past. */
    List<Booking> findByStatusAndStartTimeBefore(BookingStatus status, Instant time);

    /** Active bookings for a listing — used to reject overlapping time windows. */
    List<Booking> findByListingIdAndStatusIn(UUID listingId, Collection<BookingStatus> statuses);
}
