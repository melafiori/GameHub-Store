package com.gamehub.order.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
@FeignClient(name="payment")
public interface PaymentClient {
    record Request(BigDecimal monto) {}
    record Response(Long id, Long orderId, BigDecimal monto, String estado) {}
    @PutMapping("/internal/payments/{orderId}/cancel")
    Response cancel(@PathVariable("orderId") Long orderId, @RequestHeader("X-Service-Secret") String secret);
    @PutMapping("/internal/payments/{orderId}")
    Response pay(@PathVariable("orderId") Long orderId, @RequestHeader("X-Service-Secret") String secret, @RequestBody Request request);
}
