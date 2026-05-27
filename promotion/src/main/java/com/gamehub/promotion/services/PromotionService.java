package com.gamehub.promotion.services;

import com.gamehub.promotion.models.Promotion;

import java.util.List;

public interface PromotionService {
    List<Promotion> findAll();
    Promotion findById(Long id);
    Promotion save(Promotion promotion);
    Promotion updateById(Long id, Promotion promotion);
    void deleteById(Long id);
    Promotion findByCode(String code);
    List<Promotion> findByEstado(String estado);
}
