package com.gamehub.inventory.repositories;
import com.gamehub.inventory.models.StockAdjustment;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment,Long> {
 Optional<StockAdjustment> findByProductIdAndActorIdAndRequestKey(Long productId,Long actorId,String requestKey);
 List<StockAdjustment> findByProductIdOrderByIdDesc(Long productId);
}
