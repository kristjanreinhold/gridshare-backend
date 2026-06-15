package ee.gridshare.web.dto;

import ee.gridshare.domain.BookingStatus;
import ee.gridshare.domain.ChargerType;
import ee.gridshare.domain.DisputeStatus;
import ee.gridshare.domain.PayoutStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Response + request payloads. Field names map 1:1 to the frontend types. */
public final class Dtos {

    private Dtos() {}

    // ── Responses ────────────────────────────────────────────
    public record HostDto(
            UUID id, String name, String email, String phone, String iban, BigDecimal rating, Instant createdAt) {}

    public record ListingDto(
            UUID id,
            UUID hostId,
            String hostName,
            double lat,
            double lng,
            ChargerType chargerType,
            BigDecimal powerKw,
            BigDecimal pricePerHour,
            String instructions,
            boolean available,
            boolean autoAccept,
            boolean active,
            BigDecimal rating) {}

    public record BookingDto(
            UUID id,
            UUID listingId,
            String driverPhone,
            Instant start,
            int durationMin,
            BigDecimal price,
            BookingStatus status,
            Instant createdAt,
            Instant hostRespondedAt,
            Instant paidAt,
            String accessInstructions) {}

    public record PayoutDto(UUID id, UUID bookingId, BigDecimal amount, PayoutStatus status, Instant createdAt) {}

    public record DisputeDto(
            UUID id, UUID bookingId, String description, DisputeStatus status, String resolution, Instant createdAt) {}

    public record PaymentLinkResponse(UUID bookingId, String paymentUrl, String montonioRef) {}

    public record AuthResponse(String token, HostDto host) {}

    // ── Requests ─────────────────────────────────────────────
    public record CreateListingRequest(
            double lat,
            double lng,
            ChargerType chargerType,
            BigDecimal powerKw,
            BigDecimal pricePerHour,
            String instructions,
            Boolean available) {}

    public record UpdateListingRequest(
            Double lat,
            Double lng,
            ChargerType chargerType,
            BigDecimal powerKw,
            BigDecimal pricePerHour,
            String instructions,
            Boolean available) {}

    public record AvailabilityRequest(boolean available) {}

    public record UpdateProfileRequest(String name, String email, String phone, String iban) {}

    public record CreateBookingRequest(UUID listingId, String phone, Instant start, int durationMin) {}

    public record ResolveDisputeRequest(DisputeStatus status, String resolution) {}
}
