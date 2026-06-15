package ee.gridshare.repo;

import ee.gridshare.domain.AppUser;
import ee.gridshare.domain.Booking;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByListingHostOrderByCreatedAtDesc(AppUser host);
}
