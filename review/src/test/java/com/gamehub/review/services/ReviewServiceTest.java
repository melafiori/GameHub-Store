package com.gamehub.review.services;

import com.gamehub.review.exceptions.ReviewException;
import com.gamehub.review.models.Review;
import com.gamehub.review.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review reviewPrueba;
    private List<Review> reviewList = new ArrayList<>();

    @BeforeEach
    public void setUp() {

        reviewPrueba = new Review();
        reviewPrueba.setResenaId(1L);
        reviewPrueba.setUserId(10L);
        reviewPrueba.setProductId(20L);
        reviewPrueba.setOrderId(30L);
        reviewPrueba.setPuntuacion(5);
        reviewPrueba.setComentario("Excelente producto");
        reviewPrueba.setEstado("ACTIVA");
        reviewPrueba.setFecha(LocalDateTime.now());

        reviewList.add(reviewPrueba);

        for (int i = 0; i < 20; i++) {
            Review review = new Review();
            review.setResenaId((long) (i + 2));
            review.setUserId((long) i);
            review.setProductId((long) i);
            review.setOrderId((long) i);
            review.setPuntuacion(4);
            review.setComentario("Comentario " + i);
            review.setEstado("ACTIVA");
            review.setFecha(LocalDateTime.now());

            reviewList.add(review);
        }
    }

    @Test
    @DisplayName("Debe buscar una reseña por id")
    public void shouldFindReviewById() {
        Long id = 1L;

        when(reviewRepository.findById(id)).thenReturn(Optional.of(reviewPrueba));
        Review result = reviewService.findById(id);
        assertThat(result).isNotNull();
        assertThat(result.getComentario()).isEqualTo("Excelente producto");
        verify(reviewRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar una reseña inexistente")
    public void shouldNotFindReviewById() {
        Long id = 999L;

        when(reviewRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.findById(id)).isInstanceOf(ReviewException.class).hasMessage("Reseña no encontrada");
        verify(reviewRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe guardar una reseña correctamente")
    public void shouldSaveReview() {

        when(reviewRepository.existsByUserIdAndProductIdAndOrderId(
                reviewPrueba.getUserId(),
                reviewPrueba.getProductId(),
                reviewPrueba.getOrderId()))
                .thenReturn(false);

        when(reviewRepository.save(any(Review.class)))
                .thenReturn(reviewPrueba);

        Review result = reviewService.save(reviewPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo("ACTIVA");

        verify(reviewRepository, times(1))
                .existsByUserIdAndProductIdAndOrderId(
                        reviewPrueba.getUserId(),
                        reviewPrueba.getProductId(),
                        reviewPrueba.getOrderId());

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar una reseña duplicada")
    public void shouldNotSaveDuplicatedReview() {

        when(reviewRepository.existsByUserIdAndProductIdAndOrderId(
                reviewPrueba.getUserId(),
                reviewPrueba.getProductId(),
                reviewPrueba.getOrderId()))
                .thenReturn(true);

        assertThatThrownBy(() -> reviewService.save(reviewPrueba))
                .isInstanceOf(ReviewException.class)
                .hasMessage("Ya existe una reseña para esta compra");

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Debe actualizar una reseña existente")
    public void shouldUpdateReview() {

        Long id = 1L;

        Review cambios = new Review();
        cambios.setComentario("Comentario actualizado");
        cambios.setPuntuacion(3);

        when(reviewRepository.findById(id)).thenReturn(Optional.of(reviewPrueba));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Review result = reviewService.updateById(id, cambios);
        assertThat(result.getComentario()).isEqualTo("Comentario actualizado");
        assertThat(result.getPuntuacion()).isEqualTo(3);

        verify(reviewRepository, times(1)).findById(id);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("Debe eliminar lógicamente una reseña")
    public void shouldDeleteReview() {
        Long id = 1L;

        when(reviewRepository.findById(id)).thenReturn(Optional.of(reviewPrueba));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reviewService.deleteById(id);
        verify(reviewRepository, times(1)).findById(id);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("Debe listar todas las reseñas")
    public void shouldFindAllReviews() {

        when(reviewRepository.findAll()).thenReturn(reviewList);
        List<Review> result = reviewService.findAll();
        assertThat(result).hasSize(21);
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar reseñas por producto")
    public void shouldFindReviewsByProductId() {
        Long productId = 20L;

        when(reviewRepository.findByProductId(productId)).thenReturn(List.of(reviewPrueba));
        List<Review> result = reviewService.findByProductId(productId);
        assertThat(result).hasSize(1);
        verify(reviewRepository, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("Debe buscar reseñas por usuario")
    public void shouldFindReviewsByUserId() {
        Long userId = 10L;

        when(reviewRepository.findByUserId(userId)).thenReturn(List.of(reviewPrueba));
        List<Review> result = reviewService.findByUserId(userId);
        assertThat(result).hasSize(1);
        verify(reviewRepository, times(1)).findByUserId(userId);
    }
}