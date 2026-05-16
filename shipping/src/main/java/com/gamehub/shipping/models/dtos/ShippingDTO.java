package com.gamehub.shipping.models.dtos;

import lombok.Data;

@Data
public class ShippingDTO {
    private Long orderId;
    private Long userId;
    private String direccion;
    private String transportista;
}
