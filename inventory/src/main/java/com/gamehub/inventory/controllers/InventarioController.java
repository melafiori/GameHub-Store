package com.gamehub.inventory.controllers;

import com.gamehub.inventory.models.Inventario;
import com.gamehub.inventory.models.dtos.InventarioResponseDto;
import com.gamehub.inventory.repositories.InventarioRepository;
import com.gamehub.inventory.services.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@Validated
@Tag(name = "Inventory V1", description = "Metodos CRUD para gestión de inventario.")

public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private InventarioRepository inventarioRepository;

    @GetMapping
    @Operation(summary = "Obtener inventario", description = "Obtiene todos los registros del inventario.")
    @ApiResponse(responseCode = "200", description = "Inventario obtenido correctamente.")
    public ResponseEntity<List<InventarioResponseDto>> getAll() {
        return ResponseEntity.ok(this.inventarioService.findAll());
    }

    @GetMapping("/check")
    @Operation(summary = "Verificar stock", description = "Verifica si existe stock suficiente para un producto.")
    @ApiResponse(responseCode = "200", description = "Verificación realizada correctamente.")
    public ResponseEntity<Boolean> checkStock(

            @Parameter(description = "ID del producto.")
            @RequestParam("productId") Long productId,

            @Parameter(description = "Cantidad solicitada.")
            @RequestParam("cantidad") Integer cantidad) {

        return inventarioRepository.findById(productId)
                .map(inv -> ResponseEntity.ok(inv.getStockDisponible() >= cantidad))
                .orElse(ResponseEntity.ok(false));
    }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar inventario por ID", description = "Obtiene un registro de inventario mediante su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado."),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado.")
    })
    public ResponseEntity<?> findById(
            @Parameter(description = "ID del inventario.")
            @PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Registrar inventario", description = "Registra un nuevo inventario en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventario registrado correctamente."),
            @ApiResponse(responseCode = "400", description = "Datos inválidos.")
    })
    public ResponseEntity<Inventario> save(
            @Parameter(description = "Datos del inventario a registrar.")
            @Valid @RequestBody Inventario inventario) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.inventarioService.save(inventario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar inventario", description = "Actualiza la información de un inventario existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario actualizado correctamente."),
            @ApiResponse(responseCode = "404", description = "Error al actualizar inventario. Inventario no encontrado.")
    })
    public ResponseEntity<Inventario> update(
            @Parameter(description = "ID del inventario.")
            @PathVariable Long id,

            @Parameter(description = "Nuevos datos del inventario.")
            @RequestBody Inventario inventario) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.inventarioService.updateById(id, inventario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar inventario", description = "Elimina un registro de inventario mediante su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inventario eliminado correctamente."),
            @ApiResponse(responseCode = "404", description = "Error al eliminar inventario. Inventario no encontrado.")
    })
    public ResponseEntity<Inventario> delete(
            @Parameter(description = "ID del inventario.")
            @PathVariable Long id) {
        this.inventarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
