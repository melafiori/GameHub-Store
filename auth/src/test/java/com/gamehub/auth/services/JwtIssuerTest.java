package com.gamehub.auth.services;

import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtIssuerTest {
    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    @Test
    void signsClaimsWithExpiry() throws Exception {
        SignedJWT jwt = SignedJWT.parse(new JwtIssuer(SECRET).issue(42, "CLIENTE"));
        assertThat(jwt.verify(new MACVerifier(SECRET.getBytes(StandardCharsets.UTF_8)))).isTrue();
        assertThat(jwt.getJWTClaimsSet().getSubject()).isEqualTo("42");
        assertThat(jwt.getJWTClaimsSet().getLongClaim("profileId")).isEqualTo(42L);
        assertThat(jwt.getJWTClaimsSet().getStringClaim("role")).isEqualTo("CLIENTE");
        assertThat(jwt.getJWTClaimsSet().getExpirationTime().toInstant()).isAfter(Instant.now());
        assertThat(jwt.verify(new MACVerifier("abcdef0123456789abcdef0123456789".getBytes(StandardCharsets.UTF_8)))).isFalse();
    }

    @Test
    void rejectsShortSecret() {
        assertThatThrownBy(() -> new JwtIssuer("corto")).isInstanceOf(IllegalArgumentException.class);
    }
}
