package com.gamehub.order.repositories;

import com.gamehub.order.models.Order;
import com.gamehub.order.models.OrderDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderDetalle, Long> {
    // Para que un usuario pueda ver su historial de compras
    List<OrderDetalle> findByUserEmail(String userEmail);
}