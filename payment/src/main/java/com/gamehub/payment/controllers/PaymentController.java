package com.gamehub.payment.controllers;

import com.gamehub.payment.models.Payment;
import com.gamehub.payment.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    private ResponseEntity<?> procesarPago(@RequestBody Payment request) {
        return ResponseEntity.ok(paymentService.procesarPago(request));
    }
}
