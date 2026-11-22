package com.gamehub.promotion.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
@Entity @Table(name="coupon_uses") @Getter @Setter
public class CouponUse {
    @Id @Column(length=128) private String orderKey;
    @Column(nullable=false) private String code;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal subtotal;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal descuento;
}
