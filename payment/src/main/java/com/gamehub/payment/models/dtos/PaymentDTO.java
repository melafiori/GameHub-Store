package com.gamehub.payment.models.dtos;

import lombok.Data;

@Data
public class PaymentDTO {
    private Long ordenId;
    private Long usuarioId;
    private Double monto;
    private String metodo;
}
