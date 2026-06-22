package com.gamehub.auth.services;

import com.gamehub.auth.clients.UserClient;
import com.gamehub.auth.models.Auth;
import com.gamehub.auth.models.dtos.AuthDTO;
import com.gamehub.auth.repositories.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private UserClient userFeignClient;

    @InjectMocks
    private AuthServiceImpl authService;

    private Auth authPrueba;
    private AuthDTO authDTO;

    @BeforeEach
    public void setUp() {

        authPrueba = new Auth();
        authPrueba.setAuthId(1L);
        authPrueba.setEmail("email@email.com");
        authPrueba.setPasswordHash("123456");
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

        verify(authRepository, times(1))
                .save(any(Auth.class));
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

        String result = authService.login(authDTO);

        assertThat(result)
                .isEqualTo("Token simulado para prueba: EMAIL@EMAIL.COM");

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
}
