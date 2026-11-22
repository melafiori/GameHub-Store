package com.gamehub.warranty.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
@FeignClient(name="shipping", configuration=ShippingDecoderConfig.class)
public interface ShippingClient {
 record Shipment(Long orderId,String estado,LocalDateTime fechaEntrega) {}
 @GetMapping("/api/v1/shippings/order/{id}") Shipment byOrder(@PathVariable("id") Long id, @RequestHeader("Authorization") String authorization);
}
