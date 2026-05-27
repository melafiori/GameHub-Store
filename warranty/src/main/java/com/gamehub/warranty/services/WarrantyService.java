package com.gamehub.warranty.services;

import com.gamehub.warranty.models.Warranty;

import java.util.List;

public interface WarrantyService {

    List<Warranty> findAll();
    Warranty findById(Long id);
    Warranty save(Warranty warranty);
    Warranty updateById(Long id, Warranty warranty);
    void deleteById(Long id);
    List<Warranty> findByUserId(Long userId);
    List<Warranty> findByProductId(Long productId);
    List<Warranty> findByEstado(String estado);
}
