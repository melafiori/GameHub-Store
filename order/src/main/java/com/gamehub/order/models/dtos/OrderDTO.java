package com.gamehub.order.models.dtos;

import lombok.Data;

@Data
public class OrderDTO {
    private Long userId;

    private Double subtotal;

    private Double descuento;

    private Double total;
}
