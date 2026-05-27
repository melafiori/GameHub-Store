package com.gamehub.inventory.clients;

import com.gamehub.inventory.models.dtos.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-product", url ="localhost:8002/api/v1/products")
public interface ProductClient {

    @GetMapping("/{id}")
    ProductDto getProductById(@PathVariable Long id);
}
