package ee.gridshare.web;

import ee.gridshare.service.DisputeService;
import ee.gridshare.service.DtoMapper;
import ee.gridshare.web.dto.Dtos.DisputeDto;
import ee.gridshare.web.dto.Dtos.ResolveDisputeRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Admin (founder) — dispute handling. Spec §7. */
@RestController
@RequestMapping("/disputes")
public class DisputeController {

    private final DisputeService disputes;

    public DisputeController(DisputeService disputes) {
        this.disputes = disputes;
    }

    @GetMapping
    public List<DisputeDto> list() {
        return disputes.all().stream().map(DtoMapper::dispute).toList();
    }

    @PatchMapping("/{id}")
    public DisputeDto resolve(@PathVariable UUID id, @RequestBody ResolveDisputeRequest req) {
        return DtoMapper.dispute(disputes.resolve(id, req));
    }
}
