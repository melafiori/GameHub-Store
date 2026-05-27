package com.gamehub.inventory.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "inventory")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id", nullable = false)
    private Long inventoryId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "stock_disponible", nullable = false)
    private int stockDisponible;

    @Column(name = "stock_reservado")
    private int stockReservado;

    @Column(name = "stock_minimo",  nullable = false)
    private int stockMinimo;

    @Column(name = "ubicacion", nullable = false)
    private String ubicacion;

    @Embedded
    private Audit audit = new Audit();

}
