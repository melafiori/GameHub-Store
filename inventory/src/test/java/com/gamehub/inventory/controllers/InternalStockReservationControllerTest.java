package com.gamehub.inventory.controllers;

import com.gamehub.inventory.services.StockReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class InternalStockReservationControllerTest {
    @Test void requiresSecretBeforeReserving() {
        StockReservationService service = mock(StockReservationService.class);
        var controller = new InternalStockReservationController(service, "0123456789abcdef0123456789abcdef");
        assertThatThrownBy(() -> controller.reserve("order-1", "incorrecto", Map.of(1L, 1)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));
        verifyNoInteractions(service);
        when(service.reservar("order-1", Map.of(1L, 1))).thenReturn("RESERVADO");
        assertThat(controller.reserve("order-1", "0123456789abcdef0123456789abcdef", Map.of(1L, 1)))
                .containsEntry("estado", "RESERVADO");
    }
}
