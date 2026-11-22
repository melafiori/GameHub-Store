package com.gamehub.auth.services;

import com.gamehub.auth.clients.UserClient;
import com.gamehub.auth.models.Auth;
import com.gamehub.auth.models.dtos.AuthDTO;
import com.gamehub.auth.repositories.AuthRepository;
import com.gamehub.auth.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import com.gamehub.auth.models.RegistrationAttempt;
import com.gamehub.auth.models.dtos.RegistrationRequest;
import com.gamehub.auth.repositories.RegistrationAttemptRepository;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AuthServiceImpl implements AuthService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private AuthRepository authUserRepository;

    @Autowired
    private UserClient userFeignClient;

    @Autowired
    private JwtIssuer jwtIssuer;

    @Autowired
    private RegistrationAttemptRepository attempts;

    @Autowired
    private RegistrationFinalizer finalizer;

    @Value("${REGISTRATION_SERVICE_SECRET:}")
    private String serviceSecret;

    public void registrarNuevo(RegistrationRequest request) {
        String email = request.email().trim().toLowerCase(java.util.Locale.ROOT);
        String fingerprint = fingerprint(request, email);
        RegistrationAttempt attempt = attempts.findByEmail(email).orElse(null);
        if (attempt == null) {
            if (authUserRepository.findByEmail(email).isPresent() || attempts.findByRut(request.rut()).isPresent())
                throw new IllegalStateException("Correo o RUT ya registrado");
            attempt = new RegistrationAttempt();
            attempt.setRegistrationId(UUID.randomUUID().toString());
            attempt.setEmail(email); attempt.setRut(request.rut());
            attempt.setPasswordHash(passwordEncoder.encode(request.password()));
            attempt.setRequestFingerprint(fingerprint); attempt.setEstado("PENDIENTE");
            try {
                attempt = attempts.saveAndFlush(attempt);
            } catch (org.springframework.dao.DataIntegrityViolationException conflict) {
                throw new IllegalStateException("Correo o RUT ya registrado", conflict);
            }
        } else if (!"PENDIENTE".equals(attempt.getEstado()) ||
                !attempt.getRequestFingerprint().equals(fingerprint) ||
                !passwordEncoder.matches(request.password(), attempt.getPasswordHash())) {
            throw new IllegalStateException("Correo o datos de registro no disponibles");
        }
        if (serviceSecret == null || serviceSecret.getBytes(StandardCharsets.UTF_8).length < 32)
            throw new IllegalStateException("REGISTRATION_SERVICE_SECRET no configurado");
        var address = request.direccion();
        Map<String, Object> profile = Map.of("nombre", request.nombre(), "rut", request.rut(), "email", email,
                "telefono", request.telefono(), "rol", "CLIENTE", "estado", "ACTIVO", "direcciones",
                List.of(Map.of("calle", address.calle(), "numero", address.numero(), "comuna", address.comuna(), "ciudad", address.ciudad())));
        Map<String, Object> result = userFeignClient.register(attempt.getRegistrationId(), serviceSecret, profile);
        if (!(result.get("userId") instanceof Number id)) throw new IllegalStateException("Respuesta de User inválida");
        finalizer.activate(attempt, id.longValue());
    }

    private String fingerprint(RegistrationRequest request, String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (String value : List.of(request.nombre(), request.rut(), email, request.telefono(), request.direccion().calle(),
                    request.direccion().numero().toString(), request.direccion().comuna(), request.direccion().ciudad())) {
                byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
                digest.update(java.nio.ByteBuffer.allocate(4).putInt(bytes.length).array()); digest.update(bytes);
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (java.security.NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }

    @Override
    public String registrar(AuthDTO authDto) {
        try {
            userFeignClient.getByEmail(authDto.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("No se puede crear la credencial: El usuario no está registrado en el sistema de perfiles.");
        }

        if (authUserRepository.findByEmail(authDto.getEmail()).isPresent()) {
            throw new RuntimeException("Las credenciales para este correo ya existen.");
        }

        Auth authUser = new Auth();
        authUser.setEmail(authDto.getEmail());
        authUser.setPasswordHash(passwordEncoder.encode(authDto.getPassword()));
        authUser.setRol("CLIENTE");
        authUser.setEstado("ACTIVO");

        authUserRepository.save(authUser);

        return "Credenciales registradas exitosamente de forma lógica.";
    }

    @Override
    public String login(AuthDTO authDto) {
        Auth authUser = authUserRepository.findByEmail(authDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o credenciales incorrectas."));

        String hash = authUser.getPasswordHash();
        if (hash == null || !hash.matches("^\\$2[aby]\\$.*") || !passwordEncoder.matches(authDto.getPassword(), hash)
                || !"ACTIVO".equalsIgnoreCase(authUser.getEstado())) {
            throw new RuntimeException("Usuario no encontrado o credenciales incorrectas.");
        }

        Object body = userFeignClient.getByEmail(authUser.getEmail()).getBody();
        if (!(body instanceof Map<?, ?> profile) || !(profile.get("userId") instanceof Number id)
                || !"ACTIVO".equalsIgnoreCase(String.valueOf(profile.get("estado")))
                || (authUser.getProfileId() != null && authUser.getProfileId().longValue() != id.longValue())) {
            throw new RuntimeException("Usuario no encontrado o credenciales incorrectas.");
        }
        return jwtIssuer.issue(id.longValue(), authUser.getRol());
    }
}
