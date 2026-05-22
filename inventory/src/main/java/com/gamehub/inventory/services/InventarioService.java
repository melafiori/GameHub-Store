package com.gamehub.inventory.services;

import com.gamehub.inventory.models.Inventario;

import java.util.List;

public interface InventarioService {

    List<Inventario> getAll();
    Inventario getById(Long id);
    Inventario getByName(String name);
    Inventario save(Inventario inventario);
    Inventario updateById(Long id, Inventario inventario);
    void deleteById(Long id);

}
