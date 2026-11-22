package com.gamehub.order.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "msvc-inventory")
public interface InventoryClient {
    @PutMapping("/internal/stock-reservations/{orderKey}")
    Map<String, String> reserve(@PathVariable("orderKey") String orderKey,
                                @RequestHeader("X-Service-Secret") String secret,
                                @RequestBody Map<Long, Integer> items);
    @PutMapping("/internal/stock-reservations/{orderKey}/return")
    Map<String, String> returnStock(@PathVariable("orderKey") String orderKey, @RequestHeader("X-Service-Secret") String secret);
    // Comprobamos si hay stock suficiente de un juego
    @GetMapping("/api/v1/inventory/check")
    boolean checkStock(@RequestParam("productId") Long productId, @RequestParam("cantidad") Integer cantidad);

    // Descontamos del inventario tras confirmar la compra
    @PutMapping("/api/v1/inventory/reduce")
    void reduceStock(@RequestParam("productId") Long productId, @RequestParam("cantidad") Integer cantidad);
}
