package com.gamehub.notification.services;

import com.gamehub.notification.exceptions.NotificationException;
import com.gamehub.notification.models.Notification;
import com.gamehub.notification.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Override
    public Notification findById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationException("Notificación no encontrada con id: " + id));
    }

    @Override
    public Notification save(Notification notification) {
        notification.setFechaEnvio(LocalDateTime.now());
        notification.setLeida(false);
        return notificationRepository.save(notification);
    }

    @Override
    public Notification updateById(Long id, Notification notification) {
        Notification existing = findById(id);
        existing.setTipo(notification.getTipo());
        existing.setMensaje(notification.getMensaje());
        existing.setLeida(notification.getLeida());
        return notificationRepository.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        findById(id);
        notificationRepository.deleteById(id);
    }

    @Override
    public List<Notification> findByUsuarioId(Long usuarioId) {
        return notificationRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<Notification> findByLeida(Boolean leida) {
        return notificationRepository.findByLeida(leida);
    }

}
