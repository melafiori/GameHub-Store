package com.gamehub.review.repositories;

import com.gamehub.review.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    List<Review> findByUserId(Long userId);
    boolean existsByUserIdAndProductIdAndOrderId(Long userId, Long productId, Long orderId);
}
