package com.gamehub.user.services;

import com.gamehub.user.exceptions.UserException;
import com.gamehub.user.models.User;
import com.gamehub.user.repositories.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User userPrueba;
    private List<User> userList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        this.userPrueba = new User();
        this.userPrueba.setUserId(1L);
        this.userPrueba.setNombre("Usuario prueba");
        this.userPrueba.setEstado("ACTIVO");
        this.userPrueba.setEmail("email@email.com");
        this.userPrueba.setRol("ROLE_USER");
        this.userPrueba.setTelefono("123456789");
        this.userList.add(userPrueba);

        Faker faker = new Faker(Locale.of("es", "CL"));
        for (int i = 0; i < 50; i++) {
            User user = new User();
            user.setUserId((long) (i + 2));
            user.setNombre(faker.name().fullName());
            user.setEstado("ACTIVO");
            user.setEmail(faker.internet().emailAddress());
            user.setRol("ROLE_USER");
            user.setTelefono(faker.phoneNumber().cellPhone());
            userList.add(user);
        }
    }

    /** Verifica la búsqueda exitsoad e un usuario por id. */
    @Test
    @DisplayName("Debe buscar un usuario por id")
    public void shouldFindUserById() {
        Long id = 1L;
        when(this.userRepository.findById(id)).thenReturn(Optional.of(this.userPrueba));

        User result = this.userService.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo("ACTIVO");
        verify(userRepository, times(1)).findById(id);
    }

    /** Verifica que se lanza excepción al buscar un id inexistente. */
    @Test
    @DisplayName("Debe lanzar excepción al buscar un usuario con id inexistente.")
    public void shouldNotFindUserById() {
        Long id = 9999L;
        when(this.userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.userService.findById(id))
                .isInstanceOf(UserException.class)
                .hasMessage("Usuario con id: " + id + " no encontrado.");
        verify(userRepository, times(1)).findById(id);
    }

    /** Verifica la actualización de un usuario existente. */
    @Test
    @DisplayName("Debe actualizar un usuario existente")
    public void shouldUpdateUsuario() {
        Long id = 1L;
        User cambios = new User();
        cambios.setEstado("ACTIVO");
        cambios.setNombre("Ejemplo Actualizado");
        cambios.setTelefono("123456789 actualizado");

        when(this.userRepository.findById(id)).thenReturn(Optional.of(this.userPrueba));
        when(this.userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = this.userService.updateById(id, cambios);

        assertThat(result.getNombre()).isEqualTo("Ejemplo Actualizado");
        assertThat(result.getEstado()).isEqualTo("ACTIVO");
        assertThat(result.getTelefono()).isEqualTo("123456789 actualizado");
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(any(User.class));
    }

    /** Verifica que se lanza excepción al actualizar un usuario inexistente. */
    @Test
    @DisplayName("Debe lanzar excepción al actualizar un usuario inexistente.")
    public void shouldNotUpdateUsuarioWhenNotExists() {
        Long id = 9999L;
        when(this.userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.userService.updateById(id, this.userPrueba))
                .isInstanceOf(UserException.class)
                .hasMessage("Usuario con id: " + id + " no encontrado.");
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any(User.class));
    }

    /** Verifica la eliminación de un usuario por id. */
    @Test
    @DisplayName("Debe eliminar un usuario por su id")
    public void shouldDeleteUsuarioById() {
        Long id = 1L;
        this.userPrueba.setEstado("ACTIVO");

        when(this.userRepository.findById(id)).thenReturn(Optional.of(this.userPrueba));
        this.userService.deleteById(id);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        assertEquals("INACTIVO", userCaptor.getValue().getEstado());

}

}
