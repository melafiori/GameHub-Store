package com.gamehub.product.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@ToString
public class ProductDetalleDto {
    private Long id;
    private String nombre;
    private String marca;
    private String modelo;
    private Double precio;
    private String descripcion;
    private String estado;
    private CategoryDto categoria; // El objeto completo traído por Feign
    // Getters y Setters...
}