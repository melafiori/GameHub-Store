package com.gamehub.payment.controllers;

import com.gamehub.payment.models.Pago;
import com.gamehub.payment.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping
    public List<Pago> findAll() {
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    public Pago findById(@PathVariable Long id) {
        return paymentService.findById(id);
    }

    @PostMapping
    public Pago save(@RequestBody Pago pago) {
        return paymentService.save(pago);
    }

    @PutMapping("/{id}")
    public Pago updateById(@PathVariable Long id, @RequestBody Pago pago) {
        return paymentService.updateById(id, pago);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        paymentService.deleteById(id);
    }

    @GetMapping("/order/{orderId}")
    public List<Pago> findByOrder(@PathVariable Long orderId) {
        return paymentService.findByOrderId(orderId);
    }

    @GetMapping("/user/{userId}")
    public List<Pago> findByUser(@PathVariable Long userId) {
        return paymentService.findByUserId(userId);
    }

    @GetMapping("/estado/{estado}")
    public List<Pago> findByEstado(@PathVariable String estado) {
        return paymentService.findByEstado(estado);
    }
}
