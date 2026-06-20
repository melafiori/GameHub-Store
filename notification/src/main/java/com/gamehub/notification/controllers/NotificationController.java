package com.gamehub.notification.controllers;

import com.gamehub.notification.models.Notification;
import com.gamehub.notification.models.dtos.NotificationDTO;
import com.gamehub.notification.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@Validated
@Tag(name="Notifications V1", description = "Metodos CRUD para la gestión de notificaciones.")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Listado de todas las notificaciones", description = "Devuelve una lista con todas las notificaciones registradas.")
    @ApiResponse(responseCode = "200", description = "Notificaciones obtenidas correctamente")
    public ResponseEntity<List<Notification>> findAll() {
        return ResponseEntity.ok(notificationService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busqueda de una notificación", description = "Devuelve una notificación por ID, si no, devuelve una excepción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación encontrada"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    public ResponseEntity<Notification> findById(
            @Parameter(description = "Id de la notificación a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Guardado de notificación", description = "Esta es la forma de guardar una notificación")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Notificación a crear", required = true,
            content = @Content(schema = @Schema(implementation = NotificationDTO.class))
    )
    public ResponseEntity<Notification> save(@RequestBody Notification notification) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.save(notification));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualización de notificación", description = "Se actualizan los datos de una notificación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación actualizada"),
            @ApiResponse(responseCode = "404", description = "Notificación no se encuentra en la BD")
    })
    public ResponseEntity<Notification> updateById(
            @Parameter(description = "Id de la notificación a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.updateById(id,notification));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminación de notificación", description = "Se elimina una notificación de la BD")
    @ApiResponse(responseCode = "204", description = "Notificación eliminada")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Id de la notificación a eliminar.", required = true, example = "1")
            @PathVariable Long id) {
        notificationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Busqueda de notificaciones por usuario", description = "Devuelve una lista de notificaciones asociadas a un usuario determinado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificaciones encontradas"),
            @ApiResponse(responseCode = "404", description = "Notificaciones no encontradas")
    })
    public ResponseEntity<List<Notification>> findByUsuarioId(
            @Parameter(description = "Id del usuario", required = true, example = "1")
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificationService.findByUsuarioId(usuarioId));
    }

    @GetMapping("/leidas/{leida}")
    @Operation(summary = "Busqueda de notificación por estado leído/no leido", description = "Devuelve una lista de notificaciónes determinadas por su estado de lectura")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificaciones encontradas"),
            @ApiResponse(responseCode = "404", description = "Notificaciones no encontradas")
    })
    public ResponseEntity<List<Notification>> findByLeida(
            @Parameter(description = "Estado leído a buscar", required = true, example = "True")
            @PathVariable Boolean leida) {
        return ResponseEntity.ok(notificationService.findByLeida(leida));
    }
}