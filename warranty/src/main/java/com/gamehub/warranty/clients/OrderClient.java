package com.gamehub.warranty.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@FeignClient(name="order")
public interface OrderClient {
 record Item(Long productId) {}
 record Order(Long id,String estado,List<Item> items) {}
 @GetMapping("/api/v1/orders/me") List<Order> myOrders(@RequestHeader("Authorization") String authorization);
}
