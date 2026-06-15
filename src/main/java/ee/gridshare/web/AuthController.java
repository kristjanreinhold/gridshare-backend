package ee.gridshare.web;

import ee.gridshare.service.DtoMapper;
import ee.gridshare.service.ProfileService;
import ee.gridshare.web.dto.Dtos.AuthResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ProfileService profile;

    public AuthController(ProfileService profile) {
        this.profile = profile;
    }

    /** POST /auth/google — mock login (real OAuth + JWT comes with security). */
    @PostMapping("/google")
    public AuthResponse google() {
        return new AuthResponse("mock-jwt-token", DtoMapper.host(profile.get()));
    }
}
