package com.gamehub.inventory.repositories;

import com.gamehub.inventory.models.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventario i where i.productId = :id")
    Optional<Inventario> lockProduct(@Param("id") Long id);
    Optional<Inventario> findByProductId(Long productId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Inventario i set i.stockDisponible = i.stockDisponible - :cantidad " +
            "where i.productId = :productId and i.stockDisponible >= :cantidad")
    int descontarSiDisponible(@Param("productId") Long productId, @Param("cantidad") int cantidad);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Inventario i set i.stockDisponible = i.stockDisponible + :cantidad where i.productId = :productId")
    int devolver(@Param("productId") Long productId, @Param("cantidad") int cantidad);
}
