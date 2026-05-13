package com.gamehub.category.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name="category_name",  nullable = false, unique = true)
    @NotBlank(message = "El campo nombre no puede estar vacío.")
    private String nombre;

    @Column(name = "category_descripcion")
    private String descripcion;

    @Column(name = "category_estado")
    private String estado;

}
