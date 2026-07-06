package com.gamehub.product.clients;


import com.gamehub.product.models.dtos.CategoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-category")
public interface CategoryClient {

    @GetMapping("/api/v1/categories/{id}")
    CategoryDto getCategoryById(@PathVariable Long id);
}
