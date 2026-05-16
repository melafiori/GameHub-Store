package com.gamehub.shipping.repositories;

import com.gamehub.shipping.models.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {
    List<Shipping> findByOrderId(Long orderId);
    List<Shipping> findByEstado(String estado);
    Optional<Shipping> findByTracking(String tracking);
}
