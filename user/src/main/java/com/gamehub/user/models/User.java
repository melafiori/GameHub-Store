package com.gamehub.user.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_nombre", nullable = false)
    @NotBlank(message = "El campo nombre del usuario no puede estar vacío.")
    private String nombre;

    @Email
    @Column(name = "user_email", nullable = false)
    @NotBlank(message = "El campo email del usuario no puede estar vacío.")
    private String email;

    @Column(name = "user_telefono", nullable = false)
    @NotBlank(message = "El campo telefono del usuario no puede estar vacío.")
    private String telefono;

    @Column(name = "user_rol", nullable = false)
    @NotBlank(message = "El campo rol del usuario no puede estar vacío.")
    private String rol;

    @Column(name = "user_estado", nullable = false)
    @NotBlank(message = "El campo estado del usuario no puede estar vacío.")
    private String estado;

    @Embedded
    private Audit audit = new Audit();
}
