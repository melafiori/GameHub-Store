package com.gamehub.review.controllers;

import com.gamehub.review.models.Review;
import com.gamehub.review.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    @Operation(summary = "Crear reseña", description = "Registra una nueva reseña para un producto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reseña creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public Review save(
            @Parameter(description = "Datos de la reseña a registrar")
            @Valid @RequestBody Review review) {
        return reviewService.save(review);
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

    @GetMapping("/product/{productId}")
    @Operation(summary = "Buscar reseñas por producto", description = "Obtiene todas las reseñas asociadas a un producto específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reseñas encontradas correctamente")
    })
    public List<Review> findByProductId(
            @Parameter(description = "ID del producto")
            @PathVariable Long productId) {
        return reviewService.findByProductId(productId);
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
