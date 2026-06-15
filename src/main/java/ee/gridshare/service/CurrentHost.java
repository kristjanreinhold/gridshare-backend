package ee.gridshare.service;

import ee.gridshare.domain.AppUser;
import ee.gridshare.repo.AppUserRepository;
import ee.gridshare.web.ApiException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Resolves the authenticated host from the JWT principal (set by JwtAuthFilter). */
@Component
public class CurrentHost {

    private final AppUserRepository users;

    public CurrentHost(AppUserRepository users) {
        this.users = users;
    }

    public AppUser get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UUID hostId) {
            return users.findById(hostId)
                    .orElseThrow(() -> unauthorized("Hosti ei leitud"));
        }
        throw unauthorized("Hosti sessioon puudub");
    }

    private static ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message);
    }
}
