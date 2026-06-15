package ee.gridshare.repo;

import ee.gridshare.domain.Dispute;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisputeRepository extends JpaRepository<Dispute, UUID> {

    List<Dispute> findAllByOrderByCreatedAtDesc();
}
