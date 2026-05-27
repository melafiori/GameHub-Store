package com.gamehub.order.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-product", url ="localhost:8003/api/v1/products")
public interface ProductClient {

    @GetMapping("/{id}")
    ResponseEntity<Object> getProductById(@PathVariable("id") Long id);
}
