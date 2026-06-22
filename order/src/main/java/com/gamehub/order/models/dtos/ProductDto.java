package com.gamehub.order.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long productId;
    private String nombre;
    private String marca;
    private String modelo;
    private Double precio;
    private String estado;
}