package com.gamehub.inventory.repositories;

import com.gamehub.inventory.models.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByName(String name);
}
