package com.gamehub.inventory.repositories;

import com.gamehub.inventory.models.StockReservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface StockReservationRepository extends JpaRepository<StockReservation, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from StockReservation r where r.orderKey = :key")
    Optional<StockReservation> findForUpdate(@Param("key") String key);
}
