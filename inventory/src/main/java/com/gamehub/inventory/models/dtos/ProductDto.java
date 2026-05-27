package com.gamehub.inventory.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@ToString
public class ProductDto {
    private Long productId;
    private String nombre;
    private String marca;
    private String modelo;
    private Double precio;
    private String estado;
}
