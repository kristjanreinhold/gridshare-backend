package ee.gridshare.web;

import ee.gridshare.service.DtoMapper;
import ee.gridshare.service.ListingService;
import ee.gridshare.web.dto.Dtos.AvailabilityRequest;
import ee.gridshare.web.dto.Dtos.CreateListingRequest;
import ee.gridshare.web.dto.Dtos.ListingDto;
import ee.gridshare.web.dto.Dtos.UpdateListingRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/listings")
public class ListingController {

    private final ListingService listings;

    public ListingController(ListingService listings) {
        this.listings = listings;
    }

    /** GET /listings?near=lat,lng — public discovery. */
    @GetMapping
    public List<ListingDto> list(@RequestParam(required = false) String near) {
        Double lat = null;
        Double lng = null;
        if (near != null && near.contains(",")) {
            String[] parts = near.split(",");
            try {
                lat = Double.parseDouble(parts[0].trim());
                lng = Double.parseDouble(parts[1].trim());
            } catch (NumberFormatException ignored) {
                // ignore malformed near param
            }
        }
        return listings.listAvailable(lat, lng).stream().map(DtoMapper::listing).toList();
    }

    @GetMapping("/{id}")
    public ListingDto get(@PathVariable UUID id) {
        return DtoMapper.listing(listings.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingDto create(@RequestBody CreateListingRequest req) {
        return DtoMapper.listing(listings.create(req));
    }

    @PatchMapping("/{id}")
    public ListingDto update(@PathVariable UUID id, @RequestBody UpdateListingRequest req) {
        return DtoMapper.listing(listings.update(id, req));
    }

    @PatchMapping("/{id}/availability")
    public ListingDto setAvailability(@PathVariable UUID id, @RequestBody AvailabilityRequest req) {
        return DtoMapper.listing(listings.setAvailability(id, req.available()));
    }
}
