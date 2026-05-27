package com.gamehub.notification.services;

import com.gamehub.notification.models.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> findAll();
    Notification findById(Long id);
    Notification save(Notification notification);
    void deleteById (Long id);
    List<Notification> findByUsuarioId(Long usuarioId);
    List<Notification> findByLeida(Boolean leida);
    Notification updateById(Long id, Notification notification);
}
