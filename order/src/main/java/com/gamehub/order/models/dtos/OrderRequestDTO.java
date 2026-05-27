package com.gamehub.order.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter @Setter
@ToString
public class OrderRequestDTO {
    @NotBlank(message = "El email del comprador es obligatorio.")
    @Email(message = "Formato de email inválido.")
    private String userEmail;

    @NotEmpty(message = "La orden debe contener al menos un producto.")
    private List<OrderDTO> items;
}
