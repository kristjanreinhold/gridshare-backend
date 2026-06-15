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
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/** Dispute — spec §5. */
@Entity
@Table(name = "dispute")
@Getter
@Setter
public class Dispute {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    private DisputeStatus status;

    @Column(columnDefinition = "text")
    private String resolution;

    private Instant createdAt = Instant.now();
}
