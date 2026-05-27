package com.gamehub.promotion.controllers;

import com.gamehub.promotion.models.Promotion;
import com.gamehub.promotion.services.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping
    public List<Promotion> findAll() {
        return promotionService.findAll();
    }

    @GetMapping("/{id}")
    public Promotion findById(@PathVariable Long id) {
        return promotionService.findById(id);
    }

    @PostMapping
    public Promotion save(@Valid @RequestBody Promotion promotion) {
        return promotionService.save(promotion);
    }

    @PutMapping("/{id}")
    public Promotion updateById(@PathVariable Long id, @RequestBody Promotion promotion) {
        return promotionService.updateById(id, promotion);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        promotionService.deleteById(id);
    }

    @GetMapping("/code/{code}")
    public Promotion findByCode(@PathVariable String code) {
        return promotionService.findByCode(code);
    }

    @GetMapping("/estado/{estado}")
    public List<Promotion> findByEstado(@PathVariable String estado) {
        return promotionService.findByEstado(estado);
    }
}
