package com.gamehub.auth.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "registration_attempts")
@Getter @Setter @NoArgsConstructor
public class RegistrationAttempt {
    @Id
    @Column(name = "registration_id", length = 36, nullable = false, updatable = false)
    private String registrationId;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(nullable = false, unique = true, updatable = false)
    private String rut;

    @Column(name = "password_hash", nullable = false, updatable = false)
    private String passwordHash;

    @Column(name = "request_fingerprint", nullable = false, updatable = false, length = 64)
    private String requestFingerprint;

    @Column(nullable = false)
    private String estado;

    private Long profileId;
}
