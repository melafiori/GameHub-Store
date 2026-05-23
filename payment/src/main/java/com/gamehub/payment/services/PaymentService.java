package com.gamehub.payment.services;

import com.gamehub.payment.models.Pago;

import java.util.List;

public interface PaymentService {
    List<Pago> findAll();
    Pago findById(Long id);
    Pago save(Pago pago);
    Pago updateById(Long id, Pago pago);
    void deleteById(Long id);
    List<Pago> findByOrdenId(Long ordenId);
    List<Pago> findByUserId(Long userId);
    List<Pago> findByEstado(String estado);
}
