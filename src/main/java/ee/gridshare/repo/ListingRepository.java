package ee.gridshare.repo;

import ee.gridshare.domain.AppUser;
import ee.gridshare.domain.Listing;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, UUID> {

    List<Listing> findByActiveTrueAndAvailableTrue();

    Optional<Listing> findByIdAndActiveTrue(UUID id);

    List<Listing> findByHostOrderByCreatedAtDesc(AppUser host);
}
