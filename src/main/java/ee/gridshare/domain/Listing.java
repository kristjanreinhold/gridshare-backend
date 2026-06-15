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

/** Listing — spec §5. */
@Entity
@Table(name = "listing")
@Getter
@Setter
public class Listing {

    @Id
    private UUID id = UUID.randomUUID();

    // EAGER: hostName is always part of the ListingDto, so fetch it up front.
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "host_id")
    private AppUser host;

    private double lat;
    private double lng;

    @Enumerated(EnumType.STRING)
    private ChargerType chargerType;

    private BigDecimal powerKw;
    private BigDecimal pricePerHour;

    @Column(columnDefinition = "text")
    private String instructions;

    private boolean available = true;
    private boolean autoAccept = false;
    private boolean active = true;
    private BigDecimal rating;
    private Instant createdAt = Instant.now();
}
