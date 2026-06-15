package ee.gridshare.security;

import ee.gridshare.domain.AppUser;
import ee.gridshare.repo.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Creates or updates a host from Google profile data on each login. */
@Service
public class HostProvisioningService {

    private final AppUserRepository users;

    public HostProvisioningService(AppUserRepository users) {
        this.users = users;
    }

    @Transactional
    public AppUser upsertFromGoogle(String googleSub, String email, String name) {
        AppUser host = users.findByGoogleSub(googleSub)
                .or(() -> users.findByEmail(email))
                .orElseGet(AppUser::new);

        host.setGoogleSub(googleSub);
        if (email != null) host.setEmail(email);
        if (name != null) host.setName(name);
        // phone / iban are filled later by the host on their profile page
        return users.save(host);
    }
}
