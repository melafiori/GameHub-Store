package com.gamehub.promotion.controllers;

import com.gamehub.promotion.models.Promotion;
import com.gamehub.promotion.services.PromotionService;
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
@RequestMapping("/api/v1/promotions")
@Validated
@Tag(name = "Promotion V1", description = "Métodos CRUD para gestión de promociones.")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @GetMapping
    @Operation(summary = "Obtener todas las promociones", description = "Obtiene una lista de todas las promociones registradas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promociones obtenidas correctamente.")
    })
    public List<Promotion> findAll() {
        return promotionService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar promoción por ID", description = "Obtiene una promoción mediante su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promoción encontrada."),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada.")
    })
    public Promotion findById(
            @Parameter(description = "ID de la promoción")
            @PathVariable Long id) {
        return promotionService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Registar promoción", description = "Registra una nueva promoción en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Promoción registrada correctamente."),
            @ApiResponse(responseCode = "400", description = "Datos inválidos.")
    })
    public Promotion save(
            @Parameter(description = "Datos de la promoción a registrar.")
            @Valid @RequestBody Promotion promotion) {
        return promotionService.save(promotion);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar promoción", description = "Actualiiza la información de una promoción existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promoción actualizada correctamente."),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada.")
    })
    public Promotion updateById(
            @Parameter(description = "ID de la promoción.")
            @PathVariable Long id,

            @Parameter(description = "Nuevos datos de la promoción.")
            @RequestBody Promotion promotion) {
        return promotionService.updateById(id, promotion);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar promoción", description = "Elimina una promoción mediante su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Promoción eliminada correctamente."),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada.")
    })
    public void deleteById(
            @Parameter(description = "ID de la promoción.")
            @PathVariable Long id) {
        promotionService.deleteById(id);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Buscar promoción por código", description = "Obtiene una promoción utilizando su código.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promoción encontrada"),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada.")
    })
    public Promotion findByCode(
            @Parameter(description = "Código de la promoción")
            @PathVariable String code) {
        return promotionService.findByCode(code);
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Buscar pormocioón por estado", description = "Obtiene las promociones según su estado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promociones obtenidas correctamente.")
    })
    public List<Promotion> findByEstado(
            @Parameter(description = "Estado de la promoción.")
            @PathVariable String estado) {
        return promotionService.findByEstado(estado);
    }
}
