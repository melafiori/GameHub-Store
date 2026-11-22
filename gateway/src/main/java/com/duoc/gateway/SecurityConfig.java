package com.duoc.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {
    @Bean
    JwtDecoder jwtDecoder(@Value("${JWT_SECRET}") String secret) {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        if (key.length < 32) throw new IllegalArgumentException("JWT_SECRET debe tener al menos 32 bytes");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(new SecretKeySpec(key, "HmacSHA256"))
                .macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator(Duration.ZERO),
                jwt -> jwt.getExpiresAt() != null ? OAuth2TokenValidatorResult.success() :
                        OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Falta expiración", null))));
        return decoder;
    }

    @Bean
    org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter jwtAuthenticationConverter() {
        var roles = new org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("role"); roles.setAuthorityPrefix("ROLE_");
        var converter = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles); return converter;
    }
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/promotions/preview").permitAll()
                        .requestMatchers("/api/v1/inventory/adjustments/**").hasAnyRole("OPERADOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/shippings/orders/*").hasAnyRole("OPERADOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings").hasAnyRole("OPERADOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/shippings/*/status").hasAnyRole("OPERADOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings/order/*").hasAnyRole("CLIENTE", "OPERADOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders/*/payment/cancel").hasAnyRole("OPERADOR", "ADMINISTRADOR").requestMatchers(HttpMethod.POST, "/api/v1/orders/*/payment/confirm").hasAnyRole("OPERADOR", "ADMINISTRADOR").requestMatchers(HttpMethod.POST, "/api/v1/orders", "/api/v1/orders/*/payment").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/paid", "/api/v1/orders/operator").hasAnyRole("OPERADOR", "ADMINISTRADOR").requestMatchers(HttpMethod.GET, "/api/v1/orders/me").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/warranties").hasAnyRole("OPERADOR", "ADMINISTRADOR").requestMatchers(HttpMethod.PATCH, "/api/v1/warranties/*/resolution").hasAnyRole("OPERADOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/warranties").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/warranties/me", "/api/v1/warranties/orders/*/eligibility").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/promotions", "/api/v1/promotions/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/promotions").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/promotions/*").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/promotions/*").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/product/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/reviews").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/reviews/*/visibility").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/products", "/api/v1/products/*/inventory", "/api/v1/categories").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/*", "/api/v1/categories/*").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/*", "/api/v1/categories/*").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/products", "/api/v1/products/**",
                                "/api/v1/categories", "/api/v1/categories/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                        .anyRequest().denyAll())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }
}
