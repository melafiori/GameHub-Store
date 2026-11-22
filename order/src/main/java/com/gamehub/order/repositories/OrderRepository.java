package com.gamehub.order.repositories;

import com.gamehub.order.models.Order;
import com.gamehub.order.models.OrderDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderDetalle, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "items")
    List<OrderDetalle> findByCustomerIdOrderByOrderIdDesc(Long customerId);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select o from OrderDetalle o where o.orderId = :id")
    Optional<OrderDetalle> lockById(@org.springframework.data.repository.query.Param("id") Long id);
    Optional<OrderDetalle> findByOrderIdAndCustomerId(Long orderId, Long customerId);
    List<OrderDetalle> findByEstadoOrderByOrderIdDesc(String estado);
    List<OrderDetalle> findAllByOrderByOrderIdDesc();
    // Para que un usuario pueda ver su historial de compras
    List<OrderDetalle> findByUserEmail(String userEmail);
    Optional<OrderDetalle> findByCustomerIdAndIdempotencyKey(Long customerId, String idempotencyKey);
}
