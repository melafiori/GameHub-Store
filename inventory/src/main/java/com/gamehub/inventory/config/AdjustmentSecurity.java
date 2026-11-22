package com.gamehub.inventory.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
@Configuration public class AdjustmentSecurity {
 @Bean @Order(1) SecurityFilterChain adjustments(HttpSecurity http,@Value("${JWT_SECRET}") String secret) throws Exception {
  byte[] key=secret.getBytes(StandardCharsets.UTF_8);if(key.length<32)throw new IllegalArgumentException("JWT_SECRET debe tener al menos 32 bytes");
  var decoder=NimbusJwtDecoder.withSecretKey(new SecretKeySpec(key,"HmacSHA256")).macAlgorithm(MacAlgorithm.HS256).build();decoder.setJwtValidator(new org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator(java.time.Duration.ZERO), jwt -> jwt.getExpiresAt()!=null?org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success():org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(new org.springframework.security.oauth2.core.OAuth2Error("invalid_token"))));
  var roles=new JwtGrantedAuthoritiesConverter();roles.setAuthoritiesClaimName("role");roles.setAuthorityPrefix("ROLE_");var converter=new JwtAuthenticationConverter();converter.setJwtGrantedAuthoritiesConverter(roles);
  return http.securityMatcher("/api/v1/inventory/adjustments/**").csrf(c->c.disable()).authorizeHttpRequests(a->a.anyRequest().hasAnyRole("OPERADOR","ADMINISTRADOR")).oauth2ResourceServer(o->o.jwt(j->j.decoder(decoder).jwtAuthenticationConverter(converter))).build();
 }
 @Bean @Order(2) SecurityFilterChain existing(HttpSecurity http) throws Exception {return http.csrf(c->c.disable()).authorizeHttpRequests(a->a.anyRequest().permitAll()).build();}
}
