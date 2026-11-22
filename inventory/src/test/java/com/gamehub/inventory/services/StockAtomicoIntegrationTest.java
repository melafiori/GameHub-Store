package com.gamehub.inventory.services;

import com.gamehub.inventory.models.Inventario;
import com.gamehub.inventory.repositories.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:stockatomico;DB_CLOSE_DELAY=-1", "eureka.client.enabled=false"})
class StockAtomicoIntegrationTest {
    @Autowired StockAtomicoService stock;
    @Autowired InventarioRepository repository;

    @Test void concurrentPurchasesDoNotOversell() throws Exception {
        Inventario item = new Inventario();
        item.setProductId(987654L); item.setStockDisponible(1); item.setStockMinimo(0); item.setUbicacion("prueba");
        repository.saveAndFlush(item);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        Callable<Boolean> task = () -> { start.await(); return stock.descontar(987654L, 1); };
        try {
            Future<Boolean> first = executor.submit(task); Future<Boolean> second = executor.submit(task);
            start.countDown();
            assertThat(List.of(first.get(10, TimeUnit.SECONDS), second.get(10, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(true, false);
            assertThat(repository.findByProductId(987654L).orElseThrow().getStockDisponible()).isZero();
            assertThatThrownBy(() -> stock.descontar(987654L, -1)).isInstanceOf(IllegalArgumentException.class);
        } finally { executor.shutdownNow(); }
    }
}
