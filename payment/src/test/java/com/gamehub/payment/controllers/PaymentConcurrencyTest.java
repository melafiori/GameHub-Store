package com.gamehub.payment.controllers;

import com.gamehub.payment.repositories.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:payment_concurrency;DB_CLOSE_DELAY=-1",
    "eureka.client.enabled=false",
    "PAYMENT_SERVICE_SECRET=0123456789abcdef0123456789abcdef"
})
class PaymentConcurrencyTest {
    @Autowired PaymentController controller;
    @Autowired PaymentRepository repository;
    @Test void acceptsZeroTotal() {
        var result=controller.pay(889L,"0123456789abcdef0123456789abcdef",new PaymentController.Request(BigDecimal.ZERO));
        assertEquals("APROBADO",result.estado());
        assertEquals(0,result.monto().compareTo(BigDecimal.ZERO));
    }
    @Test void concurrentRetriesCreateOneApprovedPayment() throws Exception {
        String secret = "0123456789abcdef0123456789abcdef";
        var start = new CountDownLatch(1);
        var pool = Executors.newFixedThreadPool(2);
        try {
            Callable<PaymentController.Response> pay = () -> { start.await(); return controller.pay(888L, secret, new PaymentController.Request(new BigDecimal("1990"))); };
            var first = pool.submit(pay);
            var second = pool.submit(pay);
            start.countDown();
            assertEquals(first.get().id(), second.get().id());
            assertEquals("APROBADO", first.get().estado());
            assertEquals(1, repository.findAll().stream().filter(p -> p.getOrderId().equals(888L)).count());
        } finally { pool.shutdownNow(); }
    }
}
