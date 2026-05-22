package com.gamehub.inventory.services;

import com.gamehub.inventory.exceptions.InventarioException;
import com.gamehub.inventory.models.Inventario;
import com.gamehub.inventory.repositories.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioServiceImpl implements  InventarioService {

    @Autowired
    InventarioRepository inventarioRepository;

    @Override
    public List<Inventario> getAll() {
        return this.inventarioRepository.findAll();
    }

    @Override
    public Inventario getById(Long id) {
        return this.inventarioRepository.findById(id).orElseThrow(
                () -> new InventarioException("Stock con id "+ id+ " no encontrado.")
        );
    }

    @Override
    public Inventario getByName(String name) {
        return this.inventarioRepository.findByName(name).orElseThrow(
                () -> new InventarioException("Stock no encontrado.")
        );
    }

    @Override
    public Inventario save(Inventario inventario) {
//        if (this.inventarioRepository.findById(inventario.getInventoryId()).isPresent()) {
//            throw new InventarioException("Stock ya existente.");
//        }
//         Es un inventario, el stock se repite?? o se agrega un contador?

        return this.inventarioRepository.save(inventario);
    }

    @Override
    public Inventario updateById(Long id, Inventario inventario) {
        return this.inventarioRepository.findById(id).map( element -> {
            element.setStockDisponible(inventario.getStockDisponible());
            element.setUbicacion(inventario.getUbicacion());
            element.setStockReservado(inventario.getStockReservado());
            return this.inventarioRepository.save(element);
        }).orElseThrow(
                () -> new InventarioException("Stock con id "+ id+ " no existente.")
        );
    }

    @Override
    public void deleteById(Long id) {
        this.inventarioRepository.deleteById(id);
    }
}
