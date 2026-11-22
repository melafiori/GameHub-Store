package com.gamehub.review.services;

import com.gamehub.review.clients.OrderClient;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewPreparationServiceTest {
    private final OrderClient orders = mock(OrderClient.class);
    private final ReviewPreparationService service = new ReviewPreparationService(orders);
    private final Jwt jwt = Jwt.withTokenValue("signed-token").header("alg", "HS256").subject("42").claim("role", "CLIENTE").build();

    @Test void preparesOnlyForPaidOwnedOrderContainingProduct() {
        when(orders.myOrders("Bearer signed-token")).thenReturn(List.of(new OrderClient.Order(9L,"PAGADA",List.of(new OrderClient.Item(7L)))));
        var review = service.prepare(jwt, 9L, 7L, 5, " excelente ");
        assertThat(review.getUserId()).isEqualTo(42L);
        assertThat(review.getOrderId()).isEqualTo(9L);
        assertThat(review.getComentario()).isEqualTo("excelente");
        assertThatThrownBy(() -> service.prepare(jwt, 10L, 7L, 5, "bien")).isInstanceOf(ResponseStatusException.class).hasMessageContaining("404");
        assertThatThrownBy(() -> service.prepare(jwt, 9L, 8L, 5, "bien")).isInstanceOf(ResponseStatusException.class).hasMessageContaining("403");
    }
    @Test void rejectsUnpaidAndInvalidInput() {
        when(orders.myOrders("Bearer signed-token")).thenReturn(List.of(new OrderClient.Order(9L,"PENDIENTE_DE_PAGO",List.of(new OrderClient.Item(7L)))));
        assertThatThrownBy(() -> service.prepare(jwt, 9L, 7L, 5, "bien")).isInstanceOf(ResponseStatusException.class).hasMessageContaining("403");
        assertThatThrownBy(() -> service.prepare(jwt, 9L, 7L, 0, "bien")).isInstanceOf(ResponseStatusException.class).hasMessageContaining("400");
        assertThatThrownBy(() -> service.prepare(jwt, 9L, 7L, 5, "   ")).isInstanceOf(ResponseStatusException.class).hasMessageContaining("400");
    }
}
