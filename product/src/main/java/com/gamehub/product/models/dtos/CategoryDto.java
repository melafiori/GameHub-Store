package com.gamehub.product.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CategoryDto {
    private Long categoryId;
    private String nombre;
    private String descripcion;
    private String estado;
}
