package com.gamehub.payment.models.dtos;
import lombok.Data;

@Data
public class PaymentDTO {
    private Long orderId;
    private Long userId;
    private Double monto;
    private String metodo;
    private String estado;
}
