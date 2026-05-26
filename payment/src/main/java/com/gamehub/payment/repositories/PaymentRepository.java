package com.gamehub.payment.repositories;

import com.gamehub.payment.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByOrderId(Long orderId);
    List<Pago> findByUserId(Long userId);
    List<Pago> findByEstado(String estado);
    Optional<Pago> findByCodigoTransaction(String codigoTransaction);
    boolean existsByOrderIdAndEstado(Long orderId, String estado);
}
