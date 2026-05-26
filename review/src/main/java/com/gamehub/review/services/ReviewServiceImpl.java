package com.gamehub.review.services;

import com.gamehub.review.exceptions.ReviewException;
import com.gamehub.review.models.Review;
import com.gamehub.review.repositories.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{
    private final ReviewRepository reviewRepository;

    @Transactional
    @Override
    public Review save(Review review) {
        if(reviewRepository.existsByUserIdAndProductIdAndOrderId(
                        review.getUserId(),
                        review.getProductId(),
                        review.getOrderId())) {
            throw new ReviewException("Ya existe una reseña para esta compra");
        }
        review.setEstado("ACTIVA");
        review.setFecha(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Transactional
    @Override
    public Review updateById(Long id, Review review) {

        Review currentReview = this.findById(id);

        currentReview.setComentario(review.getComentario());
        currentReview.setPuntuacion(review.getPuntuacion());

        return reviewRepository.save(currentReview);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {

        Review review = this.findById(id);
        review.setEstado("ELIMINADA");

        reviewRepository.save(review);
    }

    @Transactional
    @Override
    public Review findById(Long id) {
        return reviewRepository.findById(id).orElseThrow(
                () -> new ReviewException("Reseña no encontrada"));
    }

    @Transactional
    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Transactional
    @Override
    public List<Review> findByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Transactional
    @Override
    public List<Review> findByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }
}
