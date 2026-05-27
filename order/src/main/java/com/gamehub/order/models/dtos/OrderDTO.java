package com.gamehub.order.models.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@Getter @Setter
@ToString
public class OrderDTO {
    @NotNull(message = "El id del producto es obligatorio.")
    private Long productId;

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad mínima de compra es 1.")
    private Integer cantidad;
}