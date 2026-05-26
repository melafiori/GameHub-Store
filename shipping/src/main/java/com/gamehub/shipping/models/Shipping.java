package com.gamehub.shipping.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shippings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipping extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "shipping_id")
    private Long shippingId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "direccion",nullable = false)
    private String direccion;

    @Column(name = "transportista",nullable = false)
    private String transportista;

    @Column(name = "tracking",unique = true)
    private String tracking;

    @Column(name = "estado",nullable = false)
    private String estado;

    @Column(name = "fecha_envio",nullable = false)
    private LocalDateTime fechaEnvio;

    @Embedded
    private Audit audit = new Audit();
}
