package com.gamehub.auth.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@Table(name = "auth")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Auth {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id", nullable = false)
    private Long authId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "hash_pswd", nullable = false)
    private String passwordHash;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Embedded
    private Audit audit = new Audit();
}
