package com.gamehub.auth.services;

import com.gamehub.auth.models.Auth;
import com.gamehub.auth.models.RegistrationAttempt;
import com.gamehub.auth.repositories.AuthRepository;
import com.gamehub.auth.repositories.RegistrationAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationFinalizer {
    private final RegistrationAttemptRepository attempts;
    private final AuthRepository credentials;
    public RegistrationFinalizer(RegistrationAttemptRepository attempts, AuthRepository credentials) {
        this.attempts = attempts; this.credentials = credentials;
    }
    @Transactional
    public void activate(RegistrationAttempt attempt, long profileId) {
        attempt = attempts.findForUpdate(attempt.getRegistrationId()).orElseThrow();
        if ("ACTIVO".equals(attempt.getEstado()) && Long.valueOf(profileId).equals(attempt.getProfileId())) return;
        if (credentials.findByEmail(attempt.getEmail()).isPresent()) throw new IllegalStateException("Correo ya registrado");
        Auth auth = new Auth();
        auth.setEmail(attempt.getEmail()); auth.setPasswordHash(attempt.getPasswordHash());
        auth.setRol("CLIENTE"); auth.setEstado("ACTIVO"); auth.setProfileId(profileId);
        credentials.save(auth);
        attempt.setProfileId(profileId); attempt.setEstado("ACTIVO"); attempts.save(attempt);
    }
}
