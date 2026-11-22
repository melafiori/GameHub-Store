package com.gamehub.auth.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth")
@Getter
@Setter
@NoArgsConstructor
public class Auth {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id", nullable = false)
    private Long authId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "hash_pswd", nullable = false)
    private String passwordHash;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "profile_id")
    private Long profileId;


    @Embedded
    private Audit audit = new Audit();
}
