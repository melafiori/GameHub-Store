package com.gamehub.promotion.models.dtos;

import java.time.LocalDateTime;

public class PromotionDTO {
    private String code;
    private String tipo;
    private Double valor;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Double montoMinimo;
    private Integer usosMaximos;
    private String estado;
}
