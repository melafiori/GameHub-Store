package com.gamehub.category.controllers;

import com.gamehub.category.models.Category;
import com.gamehub.category.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Validated
@Tag(name = "Categories V1", description = "Metodos CRUD para la gestión de categorías")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @Operation(
            summary = "Listado de todas las categorías",
            description = "Se devuelve una lista con las categorías encontradas en la BdD"
    )
    @ApiResponse(responseCode = "200", description = "Operación exitosa.")
    public ResponseEntity<List<Category>> getAll() {
        List<Category> categorias = this.categoryService.findAll();
        return ResponseEntity.ok(categorias);
    }

    @Operation(
            summary = "Busqueda de una categoría",
            description = "Devuelve una categoría, o en caso contrario, una excepción"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoría encontrada",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Category.class))
            ),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(
            @Parameter(description = "Id de categoría a buscar")
            @PathVariable Long id) {
        Category categoria = this.categoryService.findById(id);
        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    @Operation(summary = "Guardado de categoría", description = "Esta es la forma de guardar una categoría nueva.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Categoría a guardar", required = true,
            content =  @Content(schema = @Schema(implementation = Category.class))
    )
    public ResponseEntity<Category> create(@Valid @RequestBody Category category) {
        Category nuevaCategoria = this.categoryService.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualización de categoría", description = "Se actualizan los datos de una categoría en la BdD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada."),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada.")
    })
    public ResponseEntity<Category> update(
            @Parameter(description = "Id de la categoría a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody Category category) {
        Category categoriaActualizada = this.categoryService.updateById(id, category);
        return ResponseEntity.ok(categoriaActualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminación de una categoría", description = "Se elimina una categoría de la BdD")
    @ApiResponse(responseCode = "204", description = "Categoría eliminada.")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Id de la categoría a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        this.categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
