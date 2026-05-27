package com.gamehub.auth.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cuenta_acceso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaAcceso{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuenta_id")
    private Long cuentaId;

    @Email(message = "Correo inválido")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Embedded
    private Audit audit = new Audit();
}
