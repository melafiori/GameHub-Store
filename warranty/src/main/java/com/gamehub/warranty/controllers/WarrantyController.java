package com.gamehub.warranty.controllers;

import com.gamehub.warranty.models.Warranty;
import com.gamehub.warranty.services.WarrantyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warranties")
@RequiredArgsConstructor
public class WarrantyController {
    private final WarrantyService warrantyService;

    @GetMapping
    public List<Warranty> findAll() {
        return warrantyService.findAll();
    }

    @GetMapping("/{id}")
    public Warranty findById(@PathVariable Long id) {
        return warrantyService.findById(id);
    }

    @PostMapping
    public Warranty save(@Valid @RequestBody Warranty warranty) {
        return warrantyService.save(warranty);
    }

    @PutMapping("/{id}")
    public Warranty updateById(@PathVariable Long id, @RequestBody Warranty warranty) {
        return warrantyService.updateById(id, warranty);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        warrantyService.deleteById(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Warranty> findByUserId(
            @PathVariable Long userId) {

        return warrantyService.findByUserId(userId);
    }

    @GetMapping("/producto/{productoId}")
    public List<Warranty> findByProductId(@PathVariable Long productId) {

        return warrantyService.findByProductId(productId);
    }

    @GetMapping("/estado/{estado}")
    public List<Warranty> findByEstado(@PathVariable String estado) {
        return warrantyService.findByEstado(estado);
    }
}
