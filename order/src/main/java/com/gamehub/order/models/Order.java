package com.gamehub.order.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {

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

    @Transient
    private List<DetalleOrder> detalles;

    @Embedded
    private Audit audit = new Audit();
}
