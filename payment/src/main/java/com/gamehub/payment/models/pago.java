package com.gamehub.payment.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pago_id", nullable = false)
    private Long id;

    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "monto",nullable = false)
    private Double monto;

    @Column(name = "metodo",nullable = false)
    private String metodo;

    @Column(name = "estado",nullable = false)
    private String estado;

    @Column(name = "codigo_transaction", unique = true)
    private String codigoTransaction;

    @Column(name = "fecha", unique = true)
    private LocalDateTime fecha;

    @Embedded
    private Audit audit = new Audit();
}
