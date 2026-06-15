package ee.gridshare.web;

import ee.gridshare.service.BookingService;
import ee.gridshare.service.DtoMapper;
import ee.gridshare.web.dto.Dtos.BookingDto;
import ee.gridshare.web.dto.Dtos.CreateBookingRequest;
import ee.gridshare.web.dto.Dtos.PaymentLinkResponse;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookings;

    public BookingController(BookingService bookings) {
        this.bookings = bookings;
    }

    // ── Driver (token-gated once security lands) ─────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestBody CreateBookingRequest req) {
        return DtoMapper.booking(bookings.create(req));
    }

    @GetMapping("/{id}")
    public BookingDto get(@PathVariable UUID id, @RequestParam(required = false) String token) {
        return DtoMapper.booking(bookings.get(id, token));
    }

    @PostMapping("/{id}/pay")
    public PaymentLinkResponse pay(@PathVariable UUID id, @RequestParam(required = false) String token) {
        String base = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        return bookings.pay(id, token, base);
    }

    // ── Host ─────────────────────────────────────────────────

    @PostMapping("/{id}/accept")
    public BookingDto accept(@PathVariable UUID id, @RequestParam(required = false) String token) {
        return DtoMapper.booking(bookings.accept(id, token));
    }

    @PostMapping("/{id}/decline")
    public BookingDto decline(@PathVariable UUID id, @RequestParam(required = false) String token) {
        return DtoMapper.booking(bookings.decline(id, token));
    }
}
