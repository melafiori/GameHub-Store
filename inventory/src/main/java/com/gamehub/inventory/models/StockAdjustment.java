package com.gamehub.inventory.models;
import jakarta.persistence.*;import lombok.*;import java.time.LocalDateTime;
@Entity @Table(name="stock_adjustments",uniqueConstraints=@UniqueConstraint(columnNames={"product_id","actor_id","request_key"})) @Getter @Setter
public class StockAdjustment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="product_id",nullable=false) private Long productId;
 @Column(name="actor_id",nullable=false) private Long actorId;
 @Column(name="request_key",nullable=false,length=128) private String requestKey;
 @Column(nullable=false) private Integer delta;
 @Column(nullable=false,length=500) private String motivo;
 @Column(nullable=false) private Integer stockAnterior;
 @Column(nullable=false) private Integer stockPosterior;
 @Column(nullable=false) private LocalDateTime fecha;
}
