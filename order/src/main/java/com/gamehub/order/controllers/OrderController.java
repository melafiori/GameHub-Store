package com.gamehub.order.controllers;

import com.gamehub.order.models.Order;
import com.gamehub.order.models.OrderDetalle;
import com.gamehub.order.models.dtos.OrderRequestDTO;
import com.gamehub.order.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> checkout(@Valid @RequestBody OrderRequestDTO orderRequestDto) {
        try {
            OrderDetalle nuevaOrden = orderService.crearOrden(orderRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaOrden);
        } catch (RuntimeException e) {
            // Si salta falta de stock o error de conexión, devolvemos un 400 Bad Request con la razón
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderDetalle>> getHistory(@RequestParam String email) {
        List<OrderDetalle> historial = orderService.obtenerHistorialPorEmail(email);
        return ResponseEntity.ok(historial);
    }
}
