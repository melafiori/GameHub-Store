package com.gamehub.review.models.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ReviewDTO {
    private Long userId;
    private Long productId;
    private Long orderId;
    @Min(1)
    @Max(5)
    private Integer puntuacion;
    private String comentario;
    private String estado;
}
