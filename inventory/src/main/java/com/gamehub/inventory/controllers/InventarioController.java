package com.gamehub.inventory.controllers;

import com.gamehub.inventory.models.Inventario;
import com.gamehub.inventory.repositories.InventarioRepository;
import com.gamehub.inventory.services.InventarioService;
import feign.Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@Validated
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<Inventario>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.inventarioService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> getById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.inventarioService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Inventario> save(@Valid @RequestBody Inventario inventario) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.inventarioService.save(inventario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventario> update(@PathVariable Long id, @RequestBody Inventario inventario) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.inventarioService.updateById(id, inventario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Inventario> delete(@PathVariable Long id) {
        this.inventarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }



}
