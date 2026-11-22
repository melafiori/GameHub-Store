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
    private String sku;
    private String nombre;
    private String marca;
    private String modelo;
    private Double precio;
    private Double precioVigente;
    private String descripcion;
    private String imagen;
    private String especificaciones;
    private Integer stock;
    private String estado;
    private CategoryDto categoria; // El objeto completo traído por Feign
    // Getters y Setters...
}
