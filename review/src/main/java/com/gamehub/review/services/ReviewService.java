package com.gamehub.review.services;

import com.gamehub.review.models.Review;

import java.util.List;

public interface ReviewService {
    List<Review> findAll();
    Review findById(Long id);
    Review save(Review review);
    Review updateById(Long id, Review review);
    void deleteById(Long id);
    List<Review> findByProductId(Long productId);
    List<Review> findByUserId(Long userId);
}
