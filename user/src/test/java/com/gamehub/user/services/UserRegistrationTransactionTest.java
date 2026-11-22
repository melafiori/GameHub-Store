package com.gamehub.user.services;

import com.gamehub.user.models.Direccion;
import com.gamehub.user.models.User;
import com.gamehub.user.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:registrationtest", "eureka.client.enabled=false"})
class UserRegistrationTransactionTest {
    @Autowired UserService service;
    @Autowired UserServiceImpl userService;
    @Autowired UserRepository users;

    @Test
    void repeatedRegistrationIdReturnsSameProfile() {
        User first = new User();
        first.setNombre("Cliente"); first.setRut("98765433-0"); first.setEmail("idempotente@example.test");
        first.setTelefono("912345678");
        Direccion address = new Direccion();
        address.setCalle("Calle"); address.setNumero(1); address.setComuna("Santiago"); address.setCiudad("Santiago");
        first.setDirecciones(List.of(address));
        String registrationId = "15ff53e4-1bd7-4ef9-9b26-2e39081b3b1a";
        User created = userService.registerOnce(registrationId, first);
        User repeated = userService.registerOnce(registrationId, new User());
        assertThat(repeated.getUserId()).isEqualTo(created.getUserId());
        assertThat(service.findById(created.getUserId()).getDirecciones()).hasSize(1);
    }

    @Test
    void invalidAddressRollsBackProfileAndAllowsRetry() {
        User user = new User();
        user.setNombre("Prueba"); user.setRut("98765432-1"); user.setEmail("rollback@example.test");
        user.setTelefono("912345678"); user.setRol("CLIENTE"); user.setEstado("ACTIVO");
        Direccion address = new Direccion();
        address.setCalle("Calle"); address.setComuna("Santiago"); address.setCiudad("Santiago");
        user.setDirecciones(List.of(address));

        assertThatThrownBy(() -> service.save(user)).isInstanceOf(RuntimeException.class);
        assertThat(users.findByEmail(user.getEmail())).isEmpty();

        user.setUserId(null);
        address.setNumero(123);
        User saved = service.save(user);
        assertThat(service.findById(saved.getUserId()).getDirecciones()).hasSize(1);
    }
}
