package ee.gridshare.web;

import ee.gridshare.service.BookingService;
import ee.gridshare.service.DtoMapper;
import ee.gridshare.web.dto.Dtos.BookingDto;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private final BookingService bookings;

    public WebhookController(BookingService bookings) {
        this.bookings = bookings;
    }

    /**
     * POST /webhooks/montonio — payment confirmation. Accepts the ref via ?ref=
     * (dev pay stub) or a JSON body {"montonioRef": "..."}.
     */
    @PostMapping("/montonio")
    public BookingDto montonio(
            @RequestParam(required = false) String ref, @RequestBody(required = false) Map<String, String> body) {
        String montonioRef = ref != null ? ref : (body != null ? body.get("montonioRef") : null);
        if (montonioRef == null || montonioRef.isBlank()) {
            throw ApiException.badRequest("montonioRef puudub");
        }
        return DtoMapper.booking(bookings.confirmPayment(montonioRef));
    }
}
