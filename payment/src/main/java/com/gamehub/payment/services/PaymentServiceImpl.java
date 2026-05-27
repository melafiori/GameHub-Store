package com.gamehub.payment.services;


import com.gamehub.payment.models.Payment;
import com.gamehub.payment.repositories.PaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment procesarPago(Payment request) {
        // simular pago
        if (request.getMonto() == null || request.getMonto() <= 0) {
            request.setEstado("RECHAZADO");
            request.setTransactionId("TX-FAILED-" + "randomtoken");
        } else {
            request.setEstado("APROBADO");
            request.setTransactionId("TX-OK-" + "randomtoken");
        }
        return paymentRepository.save(request);
    }

}
