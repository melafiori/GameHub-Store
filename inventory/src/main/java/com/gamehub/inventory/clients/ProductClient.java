package com.gamehub.inventory.clients;

import com.gamehub.inventory.models.dtos.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-product")
public interface ProductClient {

    @GetMapping("/api/v1/products/{id}")
    ProductDto getProductById(@PathVariable("id")  Long id);
}
