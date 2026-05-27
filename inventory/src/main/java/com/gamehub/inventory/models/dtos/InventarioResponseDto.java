package com.gamehub.inventory.models.dtos;

import com.gamehub.inventory.models.Audit;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class InventarioResponseDto {
    private Long inventoryId;
    private Long productId;
    private int stockDisponible;
    private int stockReservado;
    private int stockMinimo;
    private String ubicacion;
    private Audit audit;

    private ProductDto product;
}