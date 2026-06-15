package ee.gridshare.web;

import ee.gridshare.service.BookingService;
import ee.gridshare.service.DtoMapper;
import ee.gridshare.service.ListingService;
import ee.gridshare.service.PayoutService;
import ee.gridshare.service.ProfileService;
import ee.gridshare.web.dto.Dtos.BookingDto;
import ee.gridshare.web.dto.Dtos.HostDto;
import ee.gridshare.web.dto.Dtos.ListingDto;
import ee.gridshare.web.dto.Dtos.PayoutDto;
import ee.gridshare.web.dto.Dtos.UpdateProfileRequest;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Authenticated host's own data (spec §7). */
@RestController
@RequestMapping("/me")
public class MeController {

    private final ProfileService profile;
    private final ListingService listings;
    private final BookingService bookings;
    private final PayoutService payouts;

    public MeController(ProfileService profile, ListingService listings, BookingService bookings, PayoutService payouts) {
        this.profile = profile;
        this.listings = listings;
        this.bookings = bookings;
        this.payouts = payouts;
    }

    @GetMapping("/profile")
    public HostDto profile() {
        return DtoMapper.host(profile.get());
    }

    @PatchMapping("/profile")
    public HostDto updateProfile(@RequestBody UpdateProfileRequest req) {
        return DtoMapper.host(profile.update(req));
    }

    @GetMapping("/listings")
    public List<ListingDto> myListings() {
        return listings.myListings().stream().map(DtoMapper::listing).toList();
    }

    @GetMapping("/bookings")
    public List<BookingDto> myBookings() {
        return bookings.myBookings().stream().map(DtoMapper::booking).toList();
    }

    @GetMapping("/payouts")
    public List<PayoutDto> myPayouts() {
        return payouts.myPayouts().stream().map(DtoMapper::payout).toList();
    }
}
