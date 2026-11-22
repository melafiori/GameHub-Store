package com.duoc.gateway;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
public class ProfileController {
    private final ProfileClient profileClient;
    public ProfileController(ProfileClient profileClient) { this.profileClient = profileClient; }

    @GetMapping("/api/v1/users/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        try {
            long id = Long.parseLong(jwt.getSubject());
            if (id <= 0) throw new NumberFormatException();
            return profileClient.getProfile(id);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identidad inválida");
        }
    }
}
