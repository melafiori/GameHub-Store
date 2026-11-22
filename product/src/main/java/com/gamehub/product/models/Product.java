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
    @jakarta.validation.constraints.Positive(message = "El precio debe ser mayor que cero")
    private double precio;

    @Column(name = "category_id", nullable = false)
    @jakarta.validation.constraints.NotNull(message = "La categoria es obligatoria")
    private Long categoryId;

    @Column(name = "product_descripcion", nullable = false)
    private String descripcion;

    @Column(name = "product_imagen", length = 2048)
    private String imagen;

    @Column(name = "product_especificaciones", length = 8000)
    private String especificaciones;

    @Column(name = "product_estado", nullable = false)
    private String estado;

    @jakarta.persistence.Transient
    public double getPrecioVigente() {
        if (sku != null && java.util.Set.of("GH-ORIGINAL-0002", "GH-ORIGINAL-0004", "GH-ORIGINAL-0019", "GH-ORIGINAL-0029").contains(sku))
            return java.math.BigDecimal.valueOf(precio).multiply(new java.math.BigDecimal("0.90")).setScale(0, java.math.RoundingMode.HALF_UP).doubleValue();
        return precio;
    }
    @Embedded
    private Audit audit = new Audit();
}
