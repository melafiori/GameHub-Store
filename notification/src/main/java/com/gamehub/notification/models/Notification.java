package com.gamehub.notification.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue
    @Column(name = "notification_id")
    private Long notificationID;

    @Column(name = "usuario_id",nullable = false)
    private Long usuarioId;

    @Column(name = "tipo",nullable = false)
    private String tipo;

    @Column(name = "mensaje",nullable = false)
    private String mensaje;

    @Column(name = "leida",nullable = false)
    private Boolean leida = false;

    @Column(name = "fecha_envio",nullable = false)
    private LocalDateTime fechaEnvio;

    @Embedded
    private Audit audit = new Audit();

}
