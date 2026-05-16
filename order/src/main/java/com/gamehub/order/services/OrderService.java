package com.gamehub.order.services;

import com.gamehub.order.models.Order;

import java.util.List;

public interface OrderService {
    List<Order> findAll();
    Order findById(Long id);
    Order save(Order order);
    Order updateById(Long id, Order order);
    void deleteById(Long id);
    List<Order> findByUserId(Long userId);
    List<Order> findByEstado(String estado);
}
