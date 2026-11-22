package com.gamehub.user.controllers;

import com.gamehub.user.models.User;
import com.gamehub.user.services.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@RestController
@RequestMapping("/internal/registrations")
public class InternalRegistrationController {
    private final UserServiceImpl users;
    private final String secret;
    public InternalRegistrationController(UserServiceImpl users, @Value("${REGISTRATION_SERVICE_SECRET:}") String secret) {
        this.users = users;
        this.secret = secret;
    }

    @PostMapping("/{id}")
    public User register(@PathVariable UUID id, @RequestHeader(value = "X-Service-Secret", required = false) String supplied,
                         @Valid @RequestBody User user) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32 || supplied == null ||
                !MessageDigest.isEqual(secret.getBytes(StandardCharsets.UTF_8), supplied.getBytes(StandardCharsets.UTF_8)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        if (user.getDirecciones() == null || user.getDirecciones().size() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere una dirección");
        return users.registerOnce(id.toString(), user);
    }
}
