package com.gamehub.order.models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Order extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    @Column(name = "descuento", nullable = false)
    private Double descuento;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name= "fecha", nullable = false)
    private LocalDateTime fecha;
}
