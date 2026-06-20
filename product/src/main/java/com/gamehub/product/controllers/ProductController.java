package com.gamehub.product.controllers;

import com.gamehub.product.models.Product;
import com.gamehub.product.models.dtos.ProductDetalleDto;
import com.gamehub.product.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Validated
@Tag(name="Products V1", description = "Metodos CRUD para la gestión de productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "Listado de todos los productos", description = "Devuelve una lista con todos los productos registrados.")
    @ApiResponse(responseCode = "200", description = "Productos obtenidos correctamente")
    public ResponseEntity<List<ProductDetalleDto>> getAll() {
        return ResponseEntity.ok(this.productService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busqueda de un producto", description = "Devuelve un producto, en caso contrario devuelve una excepción.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDetalleDto.class)

                    )),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Product> getById(
            @Parameter(description = "ID del producto a buscar", required = true, example = "1")
            @PathVariable @Valid Long id) {
        return ResponseEntity.ok(this.productService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear un producto", description = "Crea un nuevo producto en la base de datos.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Producto a crear", required = true,
            content = @Content(schema = @Schema(implementation = ProductDetalleDto.class))
    )
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        Product nuevoProducto = this.productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto", description = "Actualiza un producto existente en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Product> update(
            @Parameter(description = "ID del producto a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody Product product)
    {
        Product productoActualizado = this.productService.updateById(id, product);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto", description = "Elimina un producto de la base de datos.")
    @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del producto a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        this.productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
