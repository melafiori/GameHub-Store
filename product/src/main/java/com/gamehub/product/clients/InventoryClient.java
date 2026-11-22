package com.gamehub.product.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-inventory")
public interface InventoryClient {
    @org.springframework.web.bind.annotation.PutMapping("/internal/inventory-initialization/{productId}")
    java.util.Map<String,Object> initialize(@PathVariable("productId") Long productId, @org.springframework.web.bind.annotation.RequestHeader("X-Service-Secret") String secret, @org.springframework.web.bind.annotation.RequestBody java.util.Map<String,Integer> stock);
    @GetMapping("/api/v1/inventory/product/{productId}/stock")
    Integer getStock(@PathVariable("productId") Long productId);
}
