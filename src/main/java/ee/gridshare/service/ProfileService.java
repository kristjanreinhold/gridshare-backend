package ee.gridshare.service;

import ee.gridshare.domain.AppUser;
import ee.gridshare.web.dto.Dtos.UpdateProfileRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final CurrentHost currentHost;

    public ProfileService(CurrentHost currentHost) {
        this.currentHost = currentHost;
    }

    @Transactional(readOnly = true)
    public AppUser get() {
        return currentHost.get();
    }

    @Transactional
    public AppUser update(UpdateProfileRequest req) {
        AppUser u = currentHost.get();
        if (req.name() != null) u.setName(req.name());
        if (req.email() != null) u.setEmail(req.email());
        if (req.phone() != null) u.setPhone(req.phone());
        if (req.iban() != null) u.setIban(req.iban());
        return u;
    }
}
