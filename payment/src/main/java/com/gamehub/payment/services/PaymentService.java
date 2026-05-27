package com.gamehub.payment.services;

import com.gamehub.payment.models.Payment;

public interface PaymentService {
    Payment procesarPago(Payment request);
}
