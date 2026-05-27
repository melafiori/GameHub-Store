package com.gamehub.notification.controllers;

import com.gamehub.notification.models.Notification;
import com.gamehub.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> findAll() {
        return ResponseEntity.ok(notificationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> findById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Notification> save(@RequestBody Notification notification) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.save(notification));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateById(@PathVariable Long id, @RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.updateById(id,notification));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        notificationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Notification>> findByUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificationService.findByUsuarioId(usuarioId));
    }

    @GetMapping("/leidas/{leida}")
    public ResponseEntity<List<Notification>> findByLeida(@PathVariable Boolean leida) {
        return ResponseEntity.ok(notificationService.findByLeida(leida));
    }
}