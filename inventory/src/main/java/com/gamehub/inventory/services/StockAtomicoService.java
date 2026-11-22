package com.gamehub.inventory.services;

import com.gamehub.inventory.repositories.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockAtomicoService {
    private final InventarioRepository repository;
    public StockAtomicoService(InventarioRepository repository) { this.repository = repository; }

    @Transactional
    public boolean descontar(Long productId, int cantidad) {
        if (productId == null || cantidad <= 0) throw new IllegalArgumentException("Producto y cantidad deben ser válidos");
        int filas = repository.descontarSiDisponible(productId, cantidad);
        if (filas > 1) throw new IllegalStateException("Existe más de un inventario para el producto");
        return filas == 1;
    }
}
