package com.gamehub.user.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "direcciones")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "direccion_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank(message = "La comuna es obligatoria")
    @Column(name = "comuna", nullable = false)
    private String comuna;

    @NotBlank(message = "La ciudad es obligatoria")
    @Column(name = "ciudad", nullable = false)
    private String ciudad;

    @NotBlank(message = "La calle es obligatoria")
    @Column(name = "calle", nullable = false)
    private String calle;

    @NotNull(message = "El número es obligatorio")
    @Column(name = "numero", nullable = false)
    private Integer numero;
}
