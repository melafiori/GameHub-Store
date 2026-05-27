package com.gamehub.promotion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long promotionId;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "valor", nullable = false)
    private Double valor;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "monto_minimo", nullable = false)
    private Double montoMinimo;

    @Column(name = "usos_maximos", nullable = false)
    private Integer usosMaximos;

    @Column(name = "usos_actuales")
    private Integer usosActuales;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Embedded
    private Audit audit = new Audit();
}
