package com.gamehub.order.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
@Configuration
public class SecurityConfig {
    @Bean JwtDecoder jwtDecoder(@Value("${JWT_SECRET}") String secret) {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        if (key.length < 32) throw new IllegalArgumentException("JWT_SECRET debe tener al menos 32 bytes");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(new SecretKeySpec(key, "HmacSHA256")).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator(Duration.ZERO),
            jwt -> jwt.getExpiresAt() != null ? OAuth2TokenValidatorResult.success() : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Falta expiracion", null))));
        return decoder;
    }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var roles = new JwtGrantedAuthoritiesConverter(); roles.setAuthoritiesClaimName("role"); roles.setAuthorityPrefix("ROLE_");
        var converter = new JwtAuthenticationConverter(); converter.setJwtGrantedAuthoritiesConverter(roles);
        return http.csrf(csrf -> csrf.disable()).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.requestMatchers("/doc/swagger-ui.html", "/doc/swagger-ui/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll().dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR).permitAll().requestMatchers(HttpMethod.GET, "/internal/shipping-orders/*").permitAll().requestMatchers(HttpMethod.PUT, "/internal/shipping-orders/*/start").permitAll().requestMatchers(HttpMethod.POST, "/api/v1/orders/*/payment/cancel").hasAnyRole("OPERADOR", "ADMINISTRADOR").requestMatchers(HttpMethod.POST, "/api/v1/orders/*/payment/confirm").hasAnyRole("OPERADOR", "ADMINISTRADOR").requestMatchers(HttpMethod.POST, "/api/v1/orders", "/api/v1/orders/*/payment").hasRole("CLIENTE").requestMatchers(HttpMethod.GET, "/api/v1/orders/paid", "/api/v1/orders/operator").hasAnyRole("OPERADOR", "ADMINISTRADOR").requestMatchers(HttpMethod.GET, "/api/v1/orders/me").hasRole("CLIENTE").anyRequest().denyAll())
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter))).build();
    }
}
