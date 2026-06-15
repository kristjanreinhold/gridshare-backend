package ee.gridshare.service;

import ee.gridshare.domain.Listing;
import ee.gridshare.repo.ListingRepository;
import ee.gridshare.web.ApiException;
import ee.gridshare.web.dto.Dtos.CreateListingRequest;
import ee.gridshare.web.dto.Dtos.UpdateListingRequest;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListingService {

    private final ListingRepository listings;
    private final CurrentHost currentHost;

    public ListingService(ListingRepository listings, CurrentHost currentHost) {
        this.listings = listings;
        this.currentHost = currentHost;
    }

    /** Public discovery — available + active, nearest first when coords given. */
    @Transactional(readOnly = true)
    public List<Listing> listAvailable(Double lat, Double lng) {
        List<Listing> result = listings.findByActiveTrueAndAvailableTrue();
        if (lat != null && lng != null) {
            result = result.stream()
                    .sorted(Comparator.comparingDouble(l -> dist(l, lat, lng)))
                    .toList();
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Listing get(UUID id) {
        return listings.findByIdAndActiveTrue(id).orElseThrow(() -> ApiException.notFound("Laadijat ei leitud"));
    }

    @Transactional(readOnly = true)
    public List<Listing> myListings() {
        return listings.findByHostOrderByCreatedAtDesc(currentHost.get());
    }

    @Transactional
    public Listing create(CreateListingRequest req) {
        if (req.pricePerHour() == null || req.pricePerHour().signum() <= 0) {
            throw ApiException.badRequest("Tunnihind peab olema positiivne");
        }
        Listing l = new Listing();
        l.setHost(currentHost.get());
        l.setLat(req.lat());
        l.setLng(req.lng());
        l.setChargerType(req.chargerType());
        l.setPowerKw(req.powerKw());
        l.setPricePerHour(req.pricePerHour());
        l.setInstructions(req.instructions());
        l.setAvailable(req.available() == null || req.available());
        l.setActive(true);
        return listings.save(l);
    }

    @Transactional
    public Listing update(UUID id, UpdateListingRequest patch) {
        Listing l = ownedListing(id);
        if (patch.lat() != null) l.setLat(patch.lat());
        if (patch.lng() != null) l.setLng(patch.lng());
        if (patch.chargerType() != null) l.setChargerType(patch.chargerType());
        if (patch.powerKw() != null) l.setPowerKw(patch.powerKw());
        if (patch.pricePerHour() != null) l.setPricePerHour(patch.pricePerHour());
        if (patch.instructions() != null) l.setInstructions(patch.instructions());
        if (patch.available() != null) l.setAvailable(patch.available());
        return l;
    }

    @Transactional
    public Listing setAvailability(UUID id, boolean available) {
        Listing l = ownedListing(id);
        l.setAvailable(available);
        return l;
    }

    private Listing ownedListing(UUID id) {
        Listing l = listings.findById(id).orElseThrow(() -> ApiException.notFound("Laadijat ei leitud"));
        if (!l.getHost().getId().equals(currentHost.get().getId())) {
            throw ApiException.notFound("Laadijat ei leitud");
        }
        return l;
    }

    private static double dist(Listing l, double lat, double lng) {
        double dLat = l.getLat() - lat;
        double dLng = l.getLng() - lng;
        return dLat * dLat + dLng * dLng;
    }
}
