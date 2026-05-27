package com.gamehub.notification.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long notificationId;
    private Long usuarioId;
    private String tipo;
    private String mensaje;
    private Boolean leida;
    private LocalDateTime fechaEnvio;

}
