package com.gamehub.inventory.services;

import com.gamehub.inventory.clients.ProductClient;
import com.gamehub.inventory.exceptions.InventoryException;
import com.gamehub.inventory.models.Inventario;
import com.gamehub.inventory.models.dtos.InventarioResponseDto;
import com.gamehub.inventory.models.dtos.ProductDto;
import com.gamehub.inventory.repositories.InventarioRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioServiceImpl implements  InventarioService {

    @Autowired
    InventarioRepository inventoryRepository;

    @Autowired
    ProductClient productClient;

    @Override
    public List<InventarioResponseDto> findAll() {
        return inventoryRepository.findAll().stream().map(inventario -> {
            InventarioResponseDto dto = new InventarioResponseDto();
            dto.setInventoryId(inventario.getInventoryId());
            dto.setStockDisponible(inventario.getStockDisponible());
            dto.setUbicacion(inventario.getUbicacion());
            dto.setAudit(inventario.getAudit());

            try {
                ProductDto product = productClient.getProductById(inventario.getProductId());
                dto.setProduct(product);
            } catch (Exception e) {
                dto.setProduct(null);
            }

            return dto;
        }).collect(Collectors.toList());
    }
    @Override
    public Inventario getById(Long id) {
        return this.inventoryRepository.findById(id).orElseThrow(
                () -> new InventoryException("Stock con id "+ id+ " no encontrado.")
        );
    }



    @Override
    public Inventario save(Inventario inventario) {
        try {

            this.productClient.getProductById(inventario.getProductId());
        } catch (FeignException e) {
            throw new InventoryException("No se puede registrar inventario: El producto asociado no existe.");
        }
        return this.inventoryRepository.save(inventario);
    }

    @Override
    public Inventario updateById(Long id, Inventario inventario) {
        return this.inventoryRepository.findById(id).map( element -> {
            element.setStockDisponible(inventario.getStockDisponible());
            element.setUbicacion(inventario.getUbicacion());
            element.setStockReservado(inventario.getStockReservado());
            return this.inventoryRepository.save(element);
        }).orElseThrow(
                () -> new InventoryException("Stock con id "+ id+ " no existente.")
        );
    }

    @Override
    public void deleteById(Long id) {
        this.inventoryRepository.deleteById(id);
    }
}
