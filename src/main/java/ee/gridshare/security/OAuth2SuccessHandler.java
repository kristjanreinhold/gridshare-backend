package ee.gridshare.security;

import ee.gridshare.domain.AppUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * After Google sign-in: provision the host, mint our JWT, and bounce the browser
 * back to the SPA's /oauth/callback with the token.
 */
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final HostProvisioningService provisioning;
    private final JwtService jwtService;
    private final String frontendUrl;

    public OAuth2SuccessHandler(
            HostProvisioningService provisioning,
            JwtService jwtService,
            @Value("${app.frontend-url:http://localhost:3000}") String frontendUrl) {
        this.provisioning = provisioning;
        this.jwtService = jwtService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
            throws IOException {
        OAuth2User user = (OAuth2User) auth.getPrincipal();
        String sub = user.getAttribute("sub");
        String email = user.getAttribute("email");
        String name = user.getAttribute("name");

        AppUser host = provisioning.upsertFromGoogle(sub, email, name);
        String token = jwtService.issue(host);

        String target = UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/oauth/callback")
                .queryParam("token", token)
                .build()
                .toUriString();
        getRedirectStrategy().sendRedirect(request, response, target);
    }
}
