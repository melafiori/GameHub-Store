package com.gamehub.auth.services;

import com.gamehub.auth.clients.UserClient;
import com.gamehub.auth.models.dtos.RegistrationRequest;
import com.gamehub.auth.repositories.AuthRepository;
import com.gamehub.auth.repositories.RegistrationAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:regauth;DB_CLOSE_DELAY=-1", "eureka.client.enabled=false",
        "JWT_SECRET=0123456789abcdef0123456789abcdef", "REGISTRATION_SERVICE_SECRET=0123456789abcdef0123456789abcdef"})
class RegistrationIntegrationTest {
    @Autowired AuthServiceImpl service;
    @Autowired AuthRepository credentials;
    @Autowired RegistrationAttemptRepository attempts;
    @MockitoBean UserClient user;

    @BeforeEach void clear() { credentials.deleteAll(); attempts.deleteAll(); }
    private RegistrationRequest request(String password) {
        return new RegistrationRequest("Cliente", "12345678-9", "nuevo@example.test", "912345678", password,
                new RegistrationRequest.Address("Calle", 123, "Santiago", "Santiago"));
    }

    @Test void lostResponseThenRetryPersistsOneCredential() {
        AtomicReference<String> id = new AtomicReference<>();
        when(user.register(anyString(), anyString(), anyMap())).thenAnswer(call -> {
            String current = call.getArgument(0);
            if (id.compareAndSet(null, current)) throw new RuntimeException("respuesta perdida");
            assertThat(current).isEqualTo(id.get());
            return Map.of("userId", 42);
        });
        assertThatThrownBy(() -> service.registrarNuevo(request("clave123"))).hasMessage("respuesta perdida");
        assertThat(attempts.findByEmail("nuevo@example.test").orElseThrow().getEstado()).isEqualTo("PENDIENTE");
        assertThatThrownBy(() -> service.registrarNuevo(request("otra"))).isInstanceOf(IllegalStateException.class);
        assertThat(attempts.findByEmail("nuevo@example.test").orElseThrow().getEstado()).isEqualTo("PENDIENTE");
        service.registrarNuevo(request("clave123"));
        assertThat(attempts.count()).isEqualTo(1);
        assertThat(credentials.count()).isEqualTo(1);
        var credential = credentials.findByEmail("nuevo@example.test").orElseThrow();
        assertThat(credential.getEstado()).isEqualTo("ACTIVO");
        assertThat(credential.getProfileId()).isEqualTo(42L);
        assertThat(attempts.findByEmail("nuevo@example.test").orElseThrow().getRegistrationId()).isEqualTo(id.get());
    }

    @Test void simultaneousRequestsHaveControlledResult() throws Exception {
        when(user.register(anyString(), anyString(), anyMap())).thenReturn(Map.of("userId", 42));
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        Callable<String> task = () -> { start.await(); try { service.registrarNuevo(request("clave123")); return "ok"; }
            catch (IllegalStateException expected) { return "conflicto"; } };
        try {
            Future<String> a = pool.submit(task); Future<String> b = pool.submit(task); start.countDown();
            assertThat(java.util.List.of(a.get(20, TimeUnit.SECONDS), b.get(20, TimeUnit.SECONDS))).contains("ok");
            assertThat(attempts.count()).isEqualTo(1);
            assertThat(credentials.count()).isEqualTo(1);
            assertThat(credentials.findByEmail("nuevo@example.test").orElseThrow().getEstado()).isEqualTo("ACTIVO");
        } finally { pool.shutdownNow(); }
    }
}
