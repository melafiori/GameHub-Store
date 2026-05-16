package com.gamehub.review.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Review extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long resenaId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @Column(name = "review_puntuacion", nullable = false)
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer puntuacion;

    @Column(name = "review_comentario")
    private String comentario;

    @Column(name = "review_estado", nullable = false)
    @NotBlank(message = "El campo estado de review no puede estar vacío.")
    private String estado;

    @Embedded
    private Audit audit = new Audit();

}
