package com.gamehub.order.controllers;

import com.gamehub.order.models.OrderDetalle;
import com.gamehub.order.models.dtos.OrderRequestDTO;
import com.gamehub.order.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Validated
@Tag(name = "Order V1", description = "Metodos CRUD para gestión de pedidos.")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "Registrar pedido", description = "Crea un nuevo pedido y procesa la compra de los productos seleccionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido registrado correctamente."),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud o falta de stock.")
    })
    public ResponseEntity<?> checkout(
            @Parameter(description = "Datos necesarios para generar el pedido.")
            @Valid @RequestBody OrderRequestDTO orderRequestDto) {
        try {
            OrderDetalle nuevaOrden = orderService.crearOrden(orderRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaOrden);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/history")
    @Operation(summary = "Obtener historial de pedidos", description = "Obtiene el historial de pedidos asociados a un correo electrónico.")
    @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente.")
    public ResponseEntity<List<OrderDetalle>> getHistory(
            @Parameter(description = "Correo eletrónico del usuario.")
            @RequestParam String email) {

        List<OrderDetalle> historial = orderService.obtenerHistorialPorEmail(email);
        return ResponseEntity.ok(historial);
    }
}
