package ee.gridshare.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/** Booking — spec §5 + §6. */
@Entity
@Table(name = "booking")
@Getter
@Setter
public class Booking {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id")
    private Listing listing;

    private String driverPhone;
    private Instant startTime;
    private int durationMin;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private Instant createdAt = Instant.now();
    private Instant hostRespondedAt;
    private Instant paidAt;

    @Column(columnDefinition = "text")
    private String accessInstructions;
}
