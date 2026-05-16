package com.gamehub.shipping.services;

import com.gamehub.shipping.models.Shipping;

import java.util.List;

public interface ShippingService {
    List<Shipping> findAll();
    Shipping findById(Long id);
    Shipping save(Shipping shipping);
    Shipping updateById(Long id, Shipping shipping);
    void deleteById(Long id);
    List<Shipping> findByOrderId(Long orderId);
    List<Shipping> findByEstado(String estado);
}
