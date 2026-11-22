package com.gamehub.inventory.services;

import com.gamehub.inventory.models.Inventario;
import com.gamehub.inventory.repositories.InventarioRepository;
import com.gamehub.inventory.repositories.StockReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:reservationtest;DB_CLOSE_DELAY=-1", "eureka.client.enabled=false"})
class StockReservationIntegrationTest {
    @Autowired StockReservationService service;
    @Autowired InventarioRepository inventory;
    @Autowired StockReservationRepository reservations;
    @BeforeEach void setup() {
        reservations.deleteAll(); inventory.deleteAll();
        create(1L, 4); create(2L, 1);
    }
    private void create(Long id, int units) {
        Inventario item = new Inventario(); item.setProductId(id); item.setStockDisponible(units);
        item.setStockMinimo(0); item.setUbicacion("prueba"); inventory.saveAndFlush(item);
    }
    private int stock(long id) { return inventory.findByProductId(id).orElseThrow().getStockDisponible(); }

    @Test void retryAndRepeatedReturnDoNotChangeStockTwice() {
        assertThat(service.reservar("order-1", Map.of(1L, 2, 2L, 1))).isEqualTo("RESERVADO");
        assertThat(service.reservar("order-1", Map.of(1L, 2, 2L, 1))).isEqualTo("RESERVADO");
        assertThat(stock(1)).isEqualTo(2); assertThat(stock(2)).isZero();
        assertThat(reservations.count()).isEqualTo(1);
        assertThatThrownBy(() -> service.reservar("order-1", Map.of(1L, 1))).isInstanceOf(IllegalStateException.class);
        service.devolver("order-1"); service.devolver("order-1");
        assertThat(stock(1)).isEqualTo(4); assertThat(stock(2)).isEqualTo(1);
    }

    @Test void insufficientStockRollsBackEntireCart() {
        assertThatThrownBy(() -> service.reservar("order-2", Map.of(1L, 2, 2L, 2))).isInstanceOf(IllegalStateException.class);
        assertThat(stock(1)).isEqualTo(4); assertThat(stock(2)).isEqualTo(1);
        assertThat(reservations.count()).isZero();
    }
}
