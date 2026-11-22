package com.gamehub.auth.services;

import com.gamehub.auth.clients.UserClient;
import com.gamehub.auth.models.Auth;
import com.gamehub.auth.models.dtos.AuthDTO;
import com.gamehub.auth.repositories.AuthRepository;
import com.gamehub.auth.repositories.RegistrationAttemptRepository;
import com.gamehub.auth.models.RegistrationAttempt;
import com.gamehub.auth.models.dtos.RegistrationRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private UserClient userFeignClient;

    @Mock
    private JwtIssuer jwtIssuer;

    @Mock private RegistrationAttemptRepository attempts;
    @Mock private RegistrationFinalizer finalizer;

    @InjectMocks
    private AuthServiceImpl authService;

    private Auth authPrueba;
    private AuthDTO authDTO;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(authService, "serviceSecret", "0123456789abcdef0123456789abcdef");

        authPrueba = new Auth();
        authPrueba.setAuthId(1L);
        authPrueba.setEmail("email@email.com");
        authPrueba.setPasswordHash(new BCryptPasswordEncoder().encode("123456"));
        authPrueba.setRol("CLIENTE");
        authPrueba.setEstado("ACTIVO");

        authDTO = new AuthDTO();
        authDTO.setEmail("email@email.com");
        authDTO.setPassword("123456");
    }

    @Test
    @DisplayName("Debe registrar credenciales correctamente")
    public void shouldRegisterAuthSuccessfully() {

        when(userFeignClient.getByEmail(authDTO.getEmail()))
                .thenReturn(ResponseEntity.ok().build());

        when(authRepository.findByEmail(authDTO.getEmail()))
                .thenReturn(Optional.empty());

        String result = authService.registrar(authDTO);

        assertThat(result)
                .isEqualTo("Credenciales registradas exitosamente de forma lógica.");

        verify(userFeignClient, times(1))
                .getByEmail(authDTO.getEmail());

        verify(authRepository, times(1))
                .findByEmail(authDTO.getEmail());

        ArgumentCaptor<Auth> saved = ArgumentCaptor.forClass(Auth.class);
        verify(authRepository).save(saved.capture());
        assertThat(new BCryptPasswordEncoder().matches("123456", saved.getValue().getPasswordHash())).isTrue();
        assertThat(saved.getValue().getPasswordHash()).isNotEqualTo("123456");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    public void shouldNotRegisterWhenUserNotExists() {

        when(userFeignClient.getByEmail(authDTO.getEmail()))
                .thenThrow(new RuntimeException());

        assertThatThrownBy(() -> authService.registrar(authDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No se puede crear la credencial: El usuario no está registrado en el sistema de perfiles.");

        verify(userFeignClient, times(1))
                .getByEmail(authDTO.getEmail());

        verify(authRepository, never())
                .save(any(Auth.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando las credenciales ya existen")
    public void shouldNotRegisterWhenCredentialsAlreadyExists() {

        when(userFeignClient.getByEmail(authDTO.getEmail()))
                .thenReturn(ResponseEntity.ok().build());

        when(authRepository.findByEmail(authDTO.getEmail()))
                .thenReturn(Optional.of(authPrueba));

        assertThatThrownBy(() -> authService.registrar(authDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Las credenciales para este correo ya existen.");

        verify(authRepository, never())
                .save(any(Auth.class));
    }

    @Test
    @DisplayName("Debe iniciar sesión correctamente")
    public void shouldLoginSuccessfully() {

        when(authRepository.findByEmail(authDTO.getEmail()))
                .thenReturn(Optional.of(authPrueba));

        when(userFeignClient.getByEmail(authDTO.getEmail())).thenReturn(ResponseEntity.ok(Map.of("userId", 42, "estado", "ACTIVO")));
        when(jwtIssuer.issue(42L, "CLIENTE")).thenReturn("signed-token");

        String result = authService.login(authDTO);

        assertThat(result)
                .isEqualTo("signed-token");

        verify(authRepository, times(1))
                .findByEmail(authDTO.getEmail());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    public void shouldNotLoginWhenUserNotExists() {

        when(authRepository.findByEmail(authDTO.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(authDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado o credenciales incorrectas.");

        verify(authRepository, times(1))
                .findByEmail(authDTO.getEmail());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la contraseña es incorrecta")
    public void shouldNotLoginWhenPasswordIsIncorrect() {

        Auth authIncorrecto = new Auth();
        authIncorrecto.setEmail("email@email.com");
        authIncorrecto.setPasswordHash("password_real");

        authDTO.setPassword("password_incorrecta");

        when(authRepository.findByEmail(authDTO.getEmail()))
                .thenReturn(Optional.of(authIncorrecto));

        assertThatThrownBy(() -> authService.login(authDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado o credenciales incorrectas.");

        verify(authRepository, times(1))
                .findByEmail(authDTO.getEmail());
    }

    @Test
    void rejectsLegacyPlaintextPassword() {
        authPrueba.setPasswordHash("123456");
        when(authRepository.findByEmail(authDTO.getEmail())).thenReturn(Optional.of(authPrueba));
        assertThatThrownBy(() -> authService.login(authDTO)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void rejectsInactiveAccount() {
        authPrueba.setEstado("INACTIVO");
        when(authRepository.findByEmail(authDTO.getEmail())).thenReturn(Optional.of(authPrueba));
        assertThatThrownBy(() -> authService.login(authDTO)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void retryAfterLostUserResponseKeepsIdAndRejectsWrongPassword() {
        var request = new RegistrationRequest("Cliente", "12345678-9", "nuevo@example.test", "912345678", "clave123",
                new RegistrationRequest.Address("Calle", 123, "Santiago", "Santiago"));
        when(attempts.findByEmail(request.email())).thenReturn(Optional.empty());
        when(attempts.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));
        when(userFeignClient.register(anyString(), anyString(), anyMap()))
                .thenThrow(new RuntimeException("respuesta perdida")).thenReturn(Map.of("userId", 42));
        assertThatThrownBy(() -> authService.registrarNuevo(request)).hasMessage("respuesta perdida");
        ArgumentCaptor<RegistrationAttempt> captured = ArgumentCaptor.forClass(RegistrationAttempt.class);
        verify(attempts).saveAndFlush(captured.capture());
        RegistrationAttempt pending = captured.getValue();
        when(attempts.findByEmail(request.email())).thenReturn(Optional.of(pending));
        var wrong = new RegistrationRequest(request.nombre(), request.rut(), request.email(), request.telefono(), "otra",
                request.direccion());
        assertThatThrownBy(() -> authService.registrarNuevo(wrong)).isInstanceOf(IllegalStateException.class);
        authService.registrarNuevo(request);
        verify(userFeignClient, times(2)).register(eq(pending.getRegistrationId()), anyString(), anyMap());
        verify(finalizer).activate(pending, 42L);
    }
}
