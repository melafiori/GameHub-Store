package com.gamehub.order.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_detalle")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrderDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "user_email", nullable = false)
    private String userEmail; // Quién compra (Validado contra puerto 8004/8005)

    @Column(nullable = false)
    private String estado; // PENDIENTE, PROCESADA, RECHAZADA

    @Column(nullable = false)
    private Double total;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Ojalá saber que hace esto
    @JoinColumn(name = "order_id") // Crea la llave foránea en la tabla order_items
    private List<Order> items = new ArrayList<>();

    @Embedded
    private Audit audit = new Audit();

    @PrePersist
    public void prePersist() {
        if (this.audit == null) this.audit = new Audit();
        this.audit.setCreatedAt(LocalDateTime.now());
    }

    @PreUpdate
    public void preUpdate() {
        if (this.audit == null) this.audit = new Audit();
        this.audit.setUpdatedAt(LocalDateTime.now());
    }
}