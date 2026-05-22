package com.gamehub.inventory.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "mov_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mov_inv_id", nullable = false)
    private long movInventarioId;

    @Column(name = "producto_id", nullable = false)
    private long productoId;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Embedded
    private Audit audit = new Audit();
}
