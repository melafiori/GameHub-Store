package com.gamehub.review.services;

import com.gamehub.review.controllers.ReviewController;
import com.gamehub.review.models.Review;
import com.gamehub.review.repositories.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:reviewvisibility;DB_CLOSE_DELAY=-1", "spring.jpa.hibernate.ddl-auto=create-drop", "eureka.client.enabled=false", "JWT_SECRET=0123456789abcdef0123456789abcdef"})
class ReviewVisibilityTest {
    @Autowired ReviewRepository repository;
    @Autowired ReviewController controller;
    @Test void visibilityPreservesReviewAndOrderAndPublicResults() {
        Review r = new Review(); r.setOrderId(87654L); r.setProductId(456L); r.setUserId(1L);
        r.setEstado("ACTIVA"); r.setFecha(LocalDateTime.now()); r.setPuntuacion(4); r.setComentario("Buen producto");
        r = repository.saveAndFlush(r); Long id = r.getResenaId();
        assertThat(controller.publicReviews(456L)).hasSize(1);
        controller.changeVisibility(id, new ReviewController.VisibilityRequest(false));
        assertThat(controller.publicReviews(456L)).isEmpty();
        assertThat(repository.findById(id).orElseThrow().getOrderId()).isEqualTo(87654L);
        controller.changeVisibility(id, new ReviewController.VisibilityRequest(true));
        assertThat(controller.publicReviews(456L)).hasSize(1);
        assertThat(repository.findAll().stream().filter(x -> x.getOrderId().equals(87654L))).hasSize(1);
    }
}
