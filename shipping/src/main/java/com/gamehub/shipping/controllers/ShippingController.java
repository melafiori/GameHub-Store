package com.gamehub.shipping.controllers;

import com.gamehub.shipping.models.Shipping;
import com.gamehub.shipping.services.ShippingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shippings")
@RequiredArgsConstructor
public class ShippingController {
    private final ShippingService shippingService;

    @GetMapping
    public List<Shipping> findAll() {
        return shippingService.findAll();
    }

    @GetMapping("/{id}")
    public Shipping findById(@PathVariable Long id) {
        return shippingService.findById(id);
    }

    @PostMapping
    public Shipping save(@Valid @RequestBody Shipping shipping) {
        return shippingService.save(shipping);
    }

    @PutMapping("/{id}")
    public Shipping updateById(
            @PathVariable Long id,
            @RequestBody Shipping shipping) {
        return shippingService.updateById(id, shipping);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        shippingService.deleteById(id);
    }

    @GetMapping("/order/{orderId}")
    public List<Shipping> findByOrderId(@PathVariable Long orderId) {
        return shippingService.findByOrderId(orderId);
    }

    @GetMapping("/estado/{estado}")
    public List<Shipping> findByEstado(@PathVariable String estado) {
        return shippingService.findByEstado(estado);
    }
}
