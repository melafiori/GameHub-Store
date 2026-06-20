package com.gamehub.shipping.controllers;

import com.gamehub.shipping.models.Shipping;
import com.gamehub.shipping.models.dtos.ShippingDTO;
import com.gamehub.shipping.services.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shippings")
@Validated
@Tag(name="Shipping V1", description = "Metodos CRUD para la gestión de envíos.")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @GetMapping
    @Operation(
            summary = "Listado de todos los envíos",
            description = "Se devuelve una lista de todos los envíos que se encuentran en la BD"
    )
    @ApiResponse(responseCode = "200", description = "Operación exitosa.")
    public ResponseEntity<List<Shipping>> findAll() {
        return ResponseEntity.ok(shippingService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Busqueda de un envío",
            description = "Se devuelve un envío, en caso contrario devuelve una excepción."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Envío encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Shipping.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Envío no se encuentra en la BD"
            )
    })
    public ResponseEntity<Shipping> findById(@PathVariable Long id) {
        return ResponseEntity.ok(shippingService.findById(id));

    }

    @PostMapping
    @Operation(summary = "Guardado de Envío", description = "Esta es la forma de guardar un envío.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Envío a crear", required = true,
            content = @Content(schema = @Schema(implementation = ShippingDTO.class))
    )
    public ResponseEntity<Shipping> save(@Valid @RequestBody Shipping shipping) {
        return ResponseEntity.ok(shippingService.save(shipping));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualización de envíos", description = "Se actualizan los datos de un envío determinado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Envío actualizado."),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado.")
    })
    public Shipping updateById(
            @Parameter(description = "Id del envío a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Shipping shipping) {
        return shippingService.updateById(id, shipping);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminación de envíos", description = "Se elimina un envío determinado.")
    @ApiResponse(responseCode = "204", description = "Envío eliminado")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Id del envío a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        this.shippingService.deleteById(id);
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/order/{orderId}")
    @Operation(
            summary = "Busqueda de un envío por ID de pedidos",
            description = "Devuelve una lista de pedidos que compartan el mismo envío."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido(s) encontrado(s)"),
            @ApiResponse(responseCode = "404", description = "Pedido(s) no encontrado(s)")
    })
    public ResponseEntity<List<Shipping>> findByOrderId(
            @Parameter(description = "Id del pedido", required = true, example = "1")
            @PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.findByOrderId(orderId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(
            summary = "Busqueda de envíos por su estado.",
            description = "Devuelve una lista de envíos que tengan un estado determinado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Envío(s) encontrado(s)"),
            @ApiResponse(responseCode = "404", description = "Envío(s) no encontrado(s)")
    })
    public ResponseEntity<List<Shipping>> findByEstado(
            @Parameter(description = "Estado del pedido", required = true, example = "Pendiente")
            @PathVariable String estado) {
        return ResponseEntity.ok(shippingService.findByEstado(estado));
    }
}
