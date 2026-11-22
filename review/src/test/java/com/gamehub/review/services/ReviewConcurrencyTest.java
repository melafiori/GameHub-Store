package com.gamehub.review.services;

import com.gamehub.review.models.Review;
import com.gamehub.review.repositories.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:reviewconcurrent;DB_CLOSE_DELAY=-1", "spring.jpa.hibernate.ddl-auto=create-drop", "eureka.client.enabled=false", "JWT_SECRET=0123456789abcdef0123456789abcdef"})
class ReviewConcurrencyTest {
    @Autowired ReviewRepository repository;
    @Test void sameOrderDifferentProductsCanOnlyBeSavedOnce() throws Exception {
        var pool = Executors.newFixedThreadPool(2);
        var gate = new CountDownLatch(1);
        try {
            Callable<Boolean> first = () -> save(gate, 1L);
            Callable<Boolean> second = () -> save(gate, 2L);
            var a = pool.submit(first); var b = pool.submit(second); gate.countDown();
            assertThat((a.get() ? 1 : 0) + (b.get() ? 1 : 0)).isEqualTo(1);
            assertThat(repository.findAll().stream().filter(r -> Long.valueOf(987654L).equals(r.getOrderId())).count()).isEqualTo(1);
        } finally { pool.shutdownNow(); }
    }
    private boolean save(CountDownLatch gate, Long product) throws InterruptedException {
        Review r = new Review(); r.setOrderId(987654L); r.setProductId(product); r.setUserId(1L);
        r.setEstado("ACTIVA"); r.setFecha(LocalDateTime.now()); r.setPuntuacion(5); r.setComentario("Prueba");
        gate.await();
        try { repository.saveAndFlush(r); return true; }
        catch (org.springframework.dao.DataIntegrityViolationException e) { return false; }
    }
}
