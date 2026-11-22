package com.gamehub.review.controllers;

import com.gamehub.review.models.Review;
import com.gamehub.review.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@Validated
@Tag(name = "Review V1", description = "Métodos CRUD para gestión de reseñas.")

public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Obtener todas las reseñas", description = "Retorna una lista con todas las reseñas registradas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reseñas obtenidas correctamente")
    })
    public List<Review> findAll() {
        return reviewService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reseña por ID", description = "Obtiene una reseña específica mediante su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reseñea encontrada"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    public Review findById(
            @Parameter(description = "ID de la reseña")
            @PathVariable Long id) {
        return reviewService.findById(id);
    }

    @Autowired private ReviewPreparationService preparation;
    @Autowired private com.gamehub.review.repositories.ReviewRepository repository;
    public record CreateReview(Long orderId, Long productId, Integer puntuacion, String comentario) {}

    @PostMapping
    @Operation(summary = "Crear una reseña por orden pagada")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Reseña creada"), @ApiResponse(responseCode = "409", description = "Orden ya reseñada")})
    public org.springframework.http.ResponseEntity<?> create(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt, @RequestBody CreateReview request) {
        Review review = preparation.prepare(jwt, request.orderId(), request.productId(), request.puntuacion(), request.comentario());
        review.setEstado("ACTIVA"); review.setFecha(java.time.LocalDateTime.now());
        try {
            Review saved = repository.saveAndFlush(review);
            return org.springframework.http.ResponseEntity.status(201).body(new PublicReview(saved.getResenaId(), saved.getPuntuacion(), saved.getComentario(), saved.getFecha()));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return org.springframework.http.ResponseEntity.status(409).body(java.util.Map.of("error", "Esta orden ya fue reseñada"));
        }
    }

    public record VisibilityRequest(Boolean visible) {}

    @PatchMapping("/{id}/visibility")
    @Operation(summary = "Ocultar o publicar una rese?a", description = "Solo ADMINISTRADOR. Conserva la rese?a y su v?nculo con la orden.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Visibilidad actualizada"), @ApiResponse(responseCode = "400", description = "Se requiere visible"), @ApiResponse(responseCode = "403", description = "Rol no autorizado"), @ApiResponse(responseCode = "404", description = "Rese?a no encontrada")})
    public Review changeVisibility(@PathVariable Long id, @RequestBody VisibilityRequest request) {
        if (request.visible() == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Debe indicar visible");
        Review review = repository.findById(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Rese?a no encontrada"));
        review.setEstado(request.visible() ? "ACTIVA" : "OCULTA");
        return repository.save(review);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar reseña", description = "Actualiza la información de una reseña existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reseña actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    public Review updateById(
            @Parameter(description = "ID de la reseña a actualizar")
            @PathVariable Long id,

            @Parameter(description = "Nuevos datos de la reseña")
            @RequestBody Review review) {
        return reviewService.updateById(id, review);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reseña", description = "Elimina una reseña mediante su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reseña eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    public void deleteById(
            @Parameter(description = "ID de la reseña a eliminar")
            @PathVariable Long id) {
        reviewService.deleteById(id);
    }

    public record PublicReview(Long resenaId, Integer puntuacion, String comentario, java.time.LocalDateTime fecha) {}

    @GetMapping("/product/{productId}")
    @Operation(summary = "Consultar reseñas activas de un producto")
    public List<PublicReview> publicReviews(@PathVariable Long productId) {
        return reviewService.findByProductId(productId).stream()
                .filter(r -> "ACTIVA".equals(r.getEstado()))
                .map(r -> new PublicReview(r.getResenaId(), r.getPuntuacion(), r.getComentario(), r.getFecha()))
                .toList();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Buscar reseñas por usuario", description = "Obtiene todas las reseñas realizadas por un usuario específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reseñaas encontradas correctamente")
    })
    public List<Review> findByUserId(
            @Parameter(description = "ID del usuario")
            @PathVariable Long userId) {
        return reviewService.findByUserId(userId);
    }
}
