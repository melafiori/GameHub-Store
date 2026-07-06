package com.gamehub.order.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-product")
public interface ProductClient {

    @GetMapping("/api/v1/products/{id}")
    ResponseEntity<Object> getProductById(@PathVariable("id") Long id);
}
