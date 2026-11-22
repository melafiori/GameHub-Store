package com.gamehub.shipping.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="order")
public interface OrderClient {
    record Address(String region,String comuna,String direccion) {}
    record Snapshot(Long id,Long customerId,String estado,Address direccionDespacho) {}
    @PutMapping("/internal/shipping-orders/{id}/start") Snapshot start(@PathVariable("id") Long id,@RequestHeader("X-Service-Secret") String secret);
    @GetMapping("/internal/shipping-orders/{id}") Snapshot get(@PathVariable("id") Long id,@RequestHeader("X-Service-Secret") String secret);
}
