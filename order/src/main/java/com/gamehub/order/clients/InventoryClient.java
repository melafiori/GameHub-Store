package com.gamehub.order.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "msvc-inventory", url ="localhost:8003/api/v1/inventory")
public interface InventoryClient {
    // Comprobamos si hay stock suficiente de un juego
    @GetMapping("/check")
    boolean checkStock(@RequestParam("productId") Long productId, @RequestParam("cantidad") Integer cantidad);

    // Descontamos del inventario tras confirmar la compra
    @PutMapping("/reduce")
    void reduceStock(@RequestParam("productId") Long productId, @RequestParam("cantidad") Integer cantidad);
}
