package ee.gridshare.service;

import ee.gridshare.domain.Dispute;
import ee.gridshare.repo.DisputeRepository;
import ee.gridshare.web.ApiException;
import ee.gridshare.web.dto.Dtos.ResolveDisputeRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisputeService {

    private final DisputeRepository disputes;

    public DisputeService(DisputeRepository disputes) {
        this.disputes = disputes;
    }

    @Transactional(readOnly = true)
    public List<Dispute> all() {
        return disputes.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Dispute resolve(UUID id, ResolveDisputeRequest req) {
        Dispute d = disputes.findById(id).orElseThrow(() -> ApiException.notFound("Dispuuti ei leitud"));
        if (req.status() != null) d.setStatus(req.status());
        if (req.resolution() != null) d.setResolution(req.resolution());
        return d;
    }
}
