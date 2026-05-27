package com.gamehub.product.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name ="productos")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_sku", nullable = false)
    @NotBlank(message = "El campo SKU no puede estar vacío.")
    private String sku;


    @Column(name = "product_nombre", nullable = false)
    @NotBlank(message = "El campo nombre del producto no puede ser vacío.")
    private String nombre;

    @Column(name = "product_marca", nullable = false)
    @NotBlank(message = "El campo marca del producto no puede ser vacío.")
    private String marca;

    @Column(name ="product_modelo", nullable = false)
    @NotBlank(message = "El campo modelo del producto no puede ser vacío.")
    private String modelo;

    @Column(name = "product_precio", nullable = false)
    private double precio;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "product_descripcion", nullable = false)
    private String descripcion;

    @Column(name = "product_estado", nullable = false)
    private String estado;

    @Embedded
    private Audit audit = new Audit();
}
