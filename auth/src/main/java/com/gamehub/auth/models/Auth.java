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

    @Column(name = "auth_email", nullable = false)
    private String email;

    @Column(name = "auth_hash_pswd", nullable = false)
    private String passwordHash;

    @Column(name = "auth_rol", nullable = false)
    private String rol;

    @Column(name = "auth_estado", nullable = false)
    private String estado;

    // fechaCreación x audit o como variable local?

    @Embedded
    private Audit audit = new Audit();
}
