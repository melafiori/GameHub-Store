package com.gamehub.inventory.services;

import com.gamehub.inventory.models.Inventario;
import com.gamehub.inventory.models.dtos.InventarioResponseDto;

import java.util.List;

public interface InventarioService {

    List<InventarioResponseDto> findAll();
    Inventario getById(Long id);
    Inventario save(Inventario inventario);
    Inventario updateById(Long id, Inventario inventario);
    void deleteById(Long id);

}
