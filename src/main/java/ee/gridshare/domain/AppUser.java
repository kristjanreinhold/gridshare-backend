package ee.gridshare.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/** Host account — spec §5 (User/Host). */
@Entity
@Table(name = "app_user")
@Getter
@Setter
public class AppUser {

    @Id
    private UUID id = UUID.randomUUID();

    private String googleSub;
    private String name;
    private String email;
    private String phone;
    private String iban;
    private BigDecimal rating;
    private Instant createdAt = Instant.now();
}
