package com.gamehub.inventory.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Entity
@Table(name = "stock_reservations")
@Getter @Setter
public class StockReservation {
    @Id
    @Column(length = 128)
    private String orderKey;
    @Column(nullable = false)
    private String estado;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "stock_reservation_items", joinColumns = @JoinColumn(name = "order_key"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "cantidad", nullable = false)
    private Map<Long, Integer> items;
}
