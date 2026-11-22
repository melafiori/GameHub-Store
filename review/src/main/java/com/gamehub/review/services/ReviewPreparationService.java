package com.gamehub.review.services;

import com.gamehub.review.clients.OrderClient;
import com.gamehub.review.models.Review;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Prepares a review only. No write endpoint is enabled until the uniqueness rule is confirmed. */
@Service
public class ReviewPreparationService {
    private final OrderClient orders;
    public ReviewPreparationService(OrderClient orders) { this.orders = orders; }

    public Review prepare(Jwt jwt, Long orderId, Long productId, Integer score, String comment) {
        if (jwt == null || !"CLIENTE".equals(jwt.getClaimAsString("role")))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Se requiere una cuenta cliente");
        if (orderId == null || orderId <= 0 || productId == null || productId <= 0 ||
                score == null || score < 1 || score > 5 || comment == null ||
                comment.isBlank() || comment.trim().length() > 1000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de reseña inválidos");
        Long customerId;
        try { customerId = Long.valueOf(jwt.getSubject()); }
        catch (RuntimeException e) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identidad inválida"); }
        var order = orders.myOrders("Bearer " + jwt.getTokenValue()).stream()
                .filter(o -> orderId.equals(o.id())).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
        if (!"PAGADA".equals(order.estado()) || order.items() == null ||
                order.items().stream().noneMatch(i -> productId.equals(i.productId())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La orden debe estar pagada e incluir el producto");
        Review review = new Review();
        review.setUserId(customerId); review.setOrderId(orderId); review.setProductId(productId);
        review.setPuntuacion(score); review.setComentario(comment.trim());
        return review;
    }
}
