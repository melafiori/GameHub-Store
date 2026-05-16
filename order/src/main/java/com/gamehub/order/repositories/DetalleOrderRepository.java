package com.gamehub.order.repositories;

import com.gamehub.order.models.DetalleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleOrderRepository extends JpaRepository<DetalleOrder, Long> {
    List<DetalleOrder> findByOrderId(Long orderId);
}
