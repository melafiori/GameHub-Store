package com.gamehub.notification.repositories;

import com.gamehub.notification.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByUsuarioId(Long usuarioId);

    List<Notification> findByLeida(Boolean leida);
}
