package ee.gridshare.repo;

import ee.gridshare.domain.AppUser;
import ee.gridshare.domain.Payout;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutRepository extends JpaRepository<Payout, UUID> {

    List<Payout> findByBookingListingHostOrderByCreatedAtDesc(AppUser host);
}
