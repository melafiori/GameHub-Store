package com.gamehub.warranty.models.dtos;

import lombok.Data;

@Data
public class WarrantyDTO {
    private Long userId;
    private Long orderId;
    private Long productId;
    private String motivo;
    private String estado;
    private String resolution;
}
