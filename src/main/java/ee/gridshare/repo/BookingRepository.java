package ee.gridshare.repo;

import ee.gridshare.domain.AppUser;
import ee.gridshare.domain.Booking;
import ee.gridshare.domain.BookingStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByListingHostOrderByCreatedAtDesc(AppUser host);

    /** Stale bookings still in {@code status} whose start time is already past. */
    List<Booking> findByStatusAndStartTimeBefore(BookingStatus status, Instant time);

    /**
     * Bookings still in {@code status} that should expire: either created before
     * {@code createdCutoff} (host-response window elapsed) OR whose start time is
     * already past — whichever comes first.
     */
    @Query("""
            select b from Booking b
            where b.status = :status
              and (b.createdAt < :createdCutoff or b.startTime < :now)
            """)
    List<Booking> findExpirable(
            @Param("status") BookingStatus status,
            @Param("createdCutoff") Instant createdCutoff,
            @Param("now") Instant now);

    /** Active bookings for a listing — used to reject overlapping time windows. */
    List<Booking> findByListingIdAndStatusIn(UUID listingId, Collection<BookingStatus> statuses);
}
