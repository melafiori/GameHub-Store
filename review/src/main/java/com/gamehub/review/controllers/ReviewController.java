package com.gamehub.review.controllers;

import com.gamehub.review.models.Review;
import com.gamehub.review.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public List<Review> findAll() {
        return reviewService.findAll();
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    @PostMapping
    public Review save(@Valid @RequestBody Review review) {
        return reviewService.save(review);
    }

    @PutMapping("/{id}")
    public Review updateById(
            @PathVariable Long id,
            @RequestBody Review review) {

        return reviewService.updateById(id, review);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        reviewService.deleteById(id);
    }

    @GetMapping("/producto/{productoId}")
    public List<Review> findByProductId(
            @PathVariable Long productId) {

        return reviewService.findByProductId(productId);
    }

    @GetMapping("/user/{userId}")
    public List<Review> findByUserId(
            @PathVariable Long userId) {

        return reviewService.findByUserId(userId);
    }
}
