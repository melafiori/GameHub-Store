package com.gamehub.order.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
@FeignClient(name="promotion") public interface PromotionClient {
    record Request(String codigo, BigDecimal subtotal) {}
    record Quote(String codigo, BigDecimal subtotal, BigDecimal descuento, BigDecimal total) {}
    @PutMapping("/internal/coupon-uses/{orderKey}") Quote redeem(@PathVariable("orderKey") String orderKey,@RequestHeader("X-Service-Secret") String secret,@RequestBody Request request);
}
