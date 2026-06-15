package ee.gridshare.service;

import ee.gridshare.domain.AppUser;
import ee.gridshare.repo.AppUserRepository;
import ee.gridshare.web.ApiException;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Resolves the "logged-in" host. Security comes later — for now this returns a
 * fixed seeded host (Mari Tamm). Swap this for the JWT subject once auth lands.
 */
@Component
public class CurrentHost {

    /** Seeded Mari Tamm (see V2__seed.sql). */
    private static final UUID MOCK_HOST_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final AppUserRepository users;

    public CurrentHost(AppUserRepository users) {
        this.users = users;
    }

    public AppUser get() {
        return users.findById(MOCK_HOST_ID)
                .orElseThrow(() -> new ApiException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Hosti sessioon puudub"));
    }
}
