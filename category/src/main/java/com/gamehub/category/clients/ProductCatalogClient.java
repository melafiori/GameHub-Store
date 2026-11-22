package com.gamehub.category.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
@FeignClient(name="msvc-product")
public interface ProductCatalogClient {
 record CategoryRef(Long categoryId) {}
 record Item(CategoryRef categoria) {}
 @GetMapping("/api/v1/products") List<Item> products();
}
