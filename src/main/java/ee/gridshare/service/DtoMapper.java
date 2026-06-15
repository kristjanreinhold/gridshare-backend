package ee.gridshare.service;

import ee.gridshare.domain.AppUser;
import ee.gridshare.domain.Booking;
import ee.gridshare.domain.Dispute;
import ee.gridshare.domain.Listing;
import ee.gridshare.domain.Payout;
import ee.gridshare.web.dto.Dtos.BookingDto;
import ee.gridshare.web.dto.Dtos.DisputeDto;
import ee.gridshare.web.dto.Dtos.HostDto;
import ee.gridshare.web.dto.Dtos.ListingDto;
import ee.gridshare.web.dto.Dtos.PayoutDto;

/** Entity → DTO mapping. */
public final class DtoMapper {

    private DtoMapper() {}

    public static HostDto host(AppUser u) {
        return new HostDto(u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.getIban(), u.getRating(), u.getCreatedAt());
    }

    public static ListingDto listing(Listing l) {
        return new ListingDto(
                l.getId(),
                l.getHost().getId(),
                l.getHost().getName(),
                l.getLat(),
                l.getLng(),
                l.getChargerType(),
                l.getPowerKw(),
                l.getPricePerHour(),
                l.getInstructions(),
                l.isAvailable(),
                l.isAutoAccept(),
                l.isActive(),
                l.getRating());
    }

    public static BookingDto booking(Booking b) {
        return new BookingDto(
                b.getId(),
                b.getListing().getId(),
                b.getDriverPhone(),
                b.getStartTime(),
                b.getDurationMin(),
                b.getPrice(),
                b.getStatus(),
                b.getCreatedAt(),
                b.getHostRespondedAt(),
                b.getPaidAt(),
                b.getAccessInstructions());
    }

    public static PayoutDto payout(Payout p) {
        return new PayoutDto(p.getId(), p.getBooking().getId(), p.getAmount(), p.getStatus(), p.getCreatedAt());
    }

    public static DisputeDto dispute(Dispute d) {
        return new DisputeDto(
                d.getId(),
                d.getBooking().getId(),
                d.getDescription(),
                d.getStatus(),
                d.getResolution(),
                d.getCreatedAt());
    }
}
