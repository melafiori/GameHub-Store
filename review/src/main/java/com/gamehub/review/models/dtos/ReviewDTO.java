package com.gamehub.review.models.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ReviewDTO {
    private Long userId;
    private Long productId;
    private Long ordenId;
    @Min(1)
    @Max(5)
    private Integer puntuacion;
    private String comentario;
}
