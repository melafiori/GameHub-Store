package com.gamehub.warranty.repositories;

import com.gamehub.warranty.models.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarrantyRepository extends JpaRepository<Warranty, Long> {

    List<Warranty> findByUserId(Long userId);
    List<Warranty> findByProductId(Long productId);
    List<Warranty> findByEstado(String estado);
}
