package com.gamehub.order.controllers;

import com.gamehub.order.models.Order;
import com.gamehub.order.repositories.OrderRepository;
import com.gamehub.order.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PostMapping
    public Order save(@Valid @RequestBody Order order) {
        return orderService.save(order);
    }

    @PutMapping("/{id}")
    public Order updateById(
            @PathVariable Long id,
            @RequestBody Order order) {
        return orderService.updateById(id, order);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        orderService.deleteById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Order> findByUserId(
            @PathVariable Long userId) {

        return orderService.findByUserId(userId);
    }

    @GetMapping("/estado/{estado}")
    public List<Order> findByEstado(
            @PathVariable String estado) {
        return orderService.findByEstado(estado);
    }
}
