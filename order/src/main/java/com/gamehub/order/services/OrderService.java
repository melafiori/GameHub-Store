package com.gamehub.order.services;

import com.gamehub.order.models.Order;
import com.gamehub.order.models.OrderDetalle;
import com.gamehub.order.models.dtos.OrderRequestDTO;

import java.util.List;

public interface OrderService {
    OrderDetalle crearOrden(OrderRequestDTO orderRequestDto);
    List<OrderDetalle> obtenerHistorialPorEmail(String email);
}
