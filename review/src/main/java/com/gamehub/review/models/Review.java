package com.gamehub.review.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resenaId;

    @Column(name = "review_puntuacion", nullable = false)
    @NotBlank(message = "El campo puntuacion de review no puede estar vacío.")
    private double puntuacion;

    @Column(name = "review_comentario")
    private String comentario;

    @Column(name = "review_estado", nullable = false)
    @NotBlank(message = "El campo estado de review no puede estar vacío.")
    private String estado;

    @Column(name = "review_fecha", nullable = false)
    @NotBlank(message = "El campo fecha de review no puede estar vacío.")
    private LocalDateTime fecha;

    @Embedded
    private Audit audit = new Audit();

    //Hay que hacer como column userId, productoId, ordenId

}
