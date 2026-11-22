package com.gamehub.promotion.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;
@FeignClient(name="msvc-product")
public interface ProductClient {
    @GetMapping("/api/v1/products/{id}") Map<String,Object> find(@PathVariable("id") Long id);
}