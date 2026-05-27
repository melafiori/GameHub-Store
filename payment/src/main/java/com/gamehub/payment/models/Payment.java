package com.gamehub.payment.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@Getter @Setter
@ToString
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Double monto;

    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago; // Ej: Tarj de credito, paypal, debito, etc.

    @Column(nullable = false)
    private String estado; // Ej: Pendiente, Aprobado, etc.

    private String transactionId; // ID ssimulado

    @Embedded
    private Audit audit = new Audit();
}