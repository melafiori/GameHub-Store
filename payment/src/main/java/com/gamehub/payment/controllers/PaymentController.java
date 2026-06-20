package com.gamehub.payment.controllers;

import com.gamehub.payment.models.Payment;
import com.gamehub.payment.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Validated
@Tag(name = "Payments V1", description = "Se encarga de procesar pagos.")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Procesar un pago", description = "Procesa un pago y devuelve el resultado.")
    @ApiResponse(responseCode = "200", description = "Pago procesado correctamente.")
    private ResponseEntity<?> procesarPago(@RequestBody Payment request) {
        return ResponseEntity.ok(paymentService.procesarPago(request));
    }
}
