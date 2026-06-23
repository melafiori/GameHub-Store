package com.gamehub.notification.services;

import com.gamehub.notification.exceptions.NotificationException;
import com.gamehub.notification.models.Notification;
import com.gamehub.notification.repositories.NotificationRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notificationPrueba;
    private List<Notification> notificationList = new ArrayList<>();

    @BeforeEach
    public void setUp() {

        this.notificationPrueba = new Notification();
        this.notificationPrueba.setNotificationID(1L);
        this.notificationPrueba.setUsuarioId(10L);
        this.notificationPrueba.setTipo("ALERTA");
        this.notificationPrueba.setMensaje("Tienes una nueva oferta disponible");
        this.notificationPrueba.setLeida(false);
        this.notificationPrueba.setFechaEnvio(LocalDateTime.now());

        notificationList.add(notificationPrueba);

        Faker faker = new Faker(Locale.of("es", "CL"));

        for (int i = 0; i < 20; i++) {
            Notification notification = new Notification();
            notification.setNotificationID((long) (i + 2));
            notification.setUsuarioId((long) (2000 + i));
            notification.setTipo("INFO");
            notification.setMensaje(faker.lorem().sentence());
            notification.setLeida(false);
            notification.setFechaEnvio(LocalDateTime.now());

            notificationList.add(notification);
        }
    }

    @Test
    @DisplayName("Debe buscar todas las notificaciones")
    public void shouldFindAllNotifications() {

        when(notificationRepository.findAll()).thenReturn(notificationList);
        List<Notification> result = notificationService.findAll();
        assertThat(result).isNotNull();
        assertThat(result).hasSize(21);
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar una notificación por id")
    public void shouldFindNotificationById() {
        Long id = 1L;

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notificationPrueba));
        Notification result = notificationService.findById(id);
        assertThat(result).isNotNull();
        assertThat(result.getTipo()).isEqualTo("ALERTA");
        verify(notificationRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar una notificación inexistente")
    public void shouldNotFindNotificationById() {
        Long id = 999L;

        when(notificationRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> notificationService.findById(id))
                .isInstanceOf(NotificationException.class)
                .hasMessage("Notificación no encontrada con id: " + id);
        verify(notificationRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe guardar una notificación")
    public void shouldSaveNotification() {

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Notification result = notificationService.save(notificationPrueba);
        assertThat(result).isNotNull();
        assertThat(result.getLeida()).isFalse();
        assertThat(result.getFechaEnvio()).isNotNull();
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Debe actualizar una notificación")
    public void shouldUpdateNotification() {
        Long id = 1L;

        Notification cambios = new Notification();
        cambios.setTipo("URGENTE");
        cambios.setMensaje("Mensaje actualizado");
        cambios.setLeida(true);

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notificationPrueba));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Notification result = notificationService.updateById(id, cambios);

        assertThat(result.getTipo()).isEqualTo("URGENTE");
        assertThat(result.getMensaje()).isEqualTo("Mensaje actualizado");
        assertThat(result.getLeida()).isTrue();

        verify(notificationRepository, times(1)).findById(id);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar una notificación inexistente")
    public void shouldNotUpdateNotificationWhenNotFound() {
        Long id = 999L;

        Notification cambios = new Notification();
        cambios.setTipo("URGENTE");

        when(notificationRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> notificationService.updateById(id, cambios))
                .isInstanceOf(NotificationException.class)
                .hasMessage("Notificación no encontrada con id: " + id);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Debe eliminar una notificación")
    public void shouldDeleteNotification() {
        Long id = 1L;

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notificationPrueba));
        doNothing().when(notificationRepository).deleteById(id);

        notificationService.deleteById(id);
        verify(notificationRepository, times(1)).findById(id);
        verify(notificationRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar una notificación inexistente")
    public void shouldNotDeleteNotificationWhenNotFound() {
        Long id = 999L;

        when(notificationRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> notificationService.deleteById(id))
                .isInstanceOf(NotificationException.class)
                .hasMessage("Notificación no encontrada con id: " + id);
        verify(notificationRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Debe buscar notificaciones por usuario")
    public void shouldFindNotificationsByUsuarioId() {
        Long usuarioId = 10L;

        when(notificationRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(notificationPrueba));
        List<Notification> result = notificationService.findByUsuarioId(usuarioId);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsuarioId()).isEqualTo(usuarioId);
        verify(notificationRepository, times(1)).findByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Debe buscar notificaciones por estado de lectura")
    public void shouldFindNotificationsByLeida() {

        when(notificationRepository.findByLeida(false)).thenReturn(List.of(notificationPrueba));
        List<Notification> result = notificationService.findByLeida(false);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLeida()).isFalse();
        verify(notificationRepository, times(1)).findByLeida(false);
    }
}