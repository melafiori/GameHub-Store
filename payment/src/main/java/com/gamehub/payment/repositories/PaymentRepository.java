package com.gamehub.payment.repositories;

import com.gamehub.payment.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByOrdenId(Long ordenId);
    List<Pago> findByUserId(Long userd);
    List<Pago> findByEstado(String estado);
    Optional<Pago> findByCodigoTransaccion(String codigoTransaccion);

    boolean existsByOrdenIdAndEstado(Long ordenId, String estado);
}
