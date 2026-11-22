package com.gamehub.warranty.controllers;

import com.gamehub.warranty.models.Warranty;
import com.gamehub.warranty.models.dtos.WarrantyDTO;
import com.gamehub.warranty.services.WarrantyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/warranties")
@Validated
@Tag(name="Warranty V1", description = "Metodos CRUD para gestión de garantías.")
public class WarrantyController {

    @Autowired
    private WarrantyService warrantyService;

    @GetMapping
    @Operation(
            summary = "Listado de todas las garantías.",
            description = "Devuelve una lista con las garantías que se encuentren en la tabla Garantías."
    )
    @ApiResponse(responseCode = "200", description = "Operación exitosa.")
    public ResponseEntity<List<Warranty>> findAll() {
        return ResponseEntity.ok(warrantyService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Busqueda de una garnatía.",
            description = "Devuelve una garantía, en caso contrario devuelve una excepción."
    )
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "Garantía encontrada",
                    content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Warranty.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404", description = "Garantía no se encuentra en la BD"
            )
    })
    public ResponseEntity<Warranty> findById(@PathVariable Long id) {
        return ResponseEntity.ok(warrantyService.findById(id));
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<java.util.Map<String,String>> requestError(org.springframework.web.server.ResponseStatusException error) {
        return ResponseEntity.status(error.getStatusCode()).body(java.util.Map.of("error", error.getReason() == null ? "Solicitud invalida" : error.getReason()));
    }
    @Autowired private com.gamehub.warranty.services.WarrantyRequestService requestService;
    @Autowired private com.gamehub.warranty.repositories.WarrantyRepository repository;
    @GetMapping("/orders/{orderId}/eligibility")
    @Operation(summary="Consultar elegibilidad de garantia para una orden propia")
    public com.gamehub.warranty.services.WarrantyRequestService.Eligibility eligibility(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt, @PathVariable Long orderId) { return requestService.eligibility(jwt, orderId); }
    public record CreateRequest(Long orderId, Long productId, String motivo) {}
    @PostMapping
    @Operation(summary="Solicitar garantia para una orden propia entregada")
    public ResponseEntity<Warranty> create(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt, @RequestBody CreateRequest request) {
        return ResponseEntity.status(201).body(requestService.create(jwt, request.orderId(), request.productId(), request.motivo()));
    }
    @GetMapping("/me")
    @Operation(summary="Consultar garantias propias")
    public List<Warranty> mine(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        return repository.findByUserId(Long.valueOf(jwt.getSubject()));
    }

    public record ResolutionRequest(String estado, String resolucion) {}
    @PatchMapping("/{id}/resolution")
    @Operation(summary="Resolver una garantia", description="Exige una resolucion para cerrar, aprobar o rechazar.")
    public ResponseEntity<?> resolve(@PathVariable Long id, @RequestBody ResolutionRequest request) {
        if (request.estado() == null || !java.util.Set.of("CERRADA", "APROBADA", "RECHAZADA").contains(request.estado()) || request.resolucion() == null || request.resolucion().isBlank())
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Debe ingresar un estado final y una resolucion"));
        try { Warranty changes = new Warranty(); changes.setEstado(request.estado()); changes.setResolution(request.resolucion().trim()); return ResponseEntity.ok(warrantyService.updateById(id, changes)); }
        catch (com.gamehub.warranty.exceptions.WarrantyException e) { return ResponseEntity.status(404).body(java.util.Map.of("error", e.getMessage())); }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualización de garantías.", description = "Se actualizan los datos de una garantía en la BD.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "Garantía actualizada."),
            @ApiResponse(responseCode = "404", description = "Garantía no se encuentra en la BD.")
    })
    public ResponseEntity<Warranty> updateById(@Parameter(description = "Id de la garantía a actualizar", required = true, example = "1")
                                                @PathVariable Long id, @RequestBody Warranty warranty) {
        return ResponseEntity.ok(warrantyService.updateById(id, warranty));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminación de Garantías", description = "Se elimina una garantía de la BD.")
    @ApiResponse(responseCode = "204", description = "Garantía eliminada.")
    public ResponseEntity<Void> deleteById(@Parameter(description = "Id de la garantía a eliminar", required = true, example = "1")
                                               @PathVariable Long id) {
        this.warrantyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener garantías de cierto usuario", description = "Devuelve garantías asociadas a un usuario determinado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Garantía encontrada."),
            @ApiResponse(responseCode = "404", description = "Garantía no encontrada.")

    })
    public ResponseEntity<List<Warranty>> findByUserId(
            @Parameter(description = "Id del usuario", required = true, example = "1")
            @PathVariable Long userId) {

        return ResponseEntity.ok(warrantyService.findByUserId(userId));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Obtener garantías de cierto producto", description = "Devuelve garantías asociadas a un producto determinado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Garantía encontrada."),
            @ApiResponse(responseCode = "404", description = "Garantía no encontrada.")
    })
    public ResponseEntity<List<Warranty>> findByProductId(
            @Parameter(description = "Id del producto", required = true, example = "1")
            @PathVariable Long productId) {

        return ResponseEntity.ok(warrantyService.findByProductId(productId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener estado de una garantía", description = "Devuelve el estado de cierta garantía determinada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Garantía encontrada."),
            @ApiResponse(responseCode = "404", description = "Garantía no encontrada.")
    })
    public List<Warranty> findByEstado(@PathVariable String estado) {
        return warrantyService.findByEstado(estado);
    }
}
