package com.gamehub.promotion.services;
import com.gamehub.promotion.models.*;
import com.gamehub.promotion.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.LocalDateTime;
import java.util.Locale;
@Service
public class CouponService {
    private final PromotionRepository promotions; private final CouponUseRepository uses;
    public CouponService(PromotionRepository promotions,CouponUseRepository uses){this.promotions=promotions;this.uses=uses;}
    public record Quote(String codigo, BigDecimal subtotal, BigDecimal descuento, BigDecimal total) {}
    public static String code(String code){if(code==null||code.isBlank()||code.length()>100)throw new IllegalArgumentException("Cupon invalido");return code.trim().toUpperCase(Locale.ROOT);}
    private Quote calculate(Promotion p,BigDecimal subtotal) {
        if(subtotal==null || subtotal.signum()<=0 || subtotal.scale()>2) throw new IllegalArgumentException("Subtotal invalido");
        var now=LocalDateTime.now();
        if(!"ACTIVA".equalsIgnoreCase(p.getEstado())) throw new IllegalArgumentException("Cupon invalido o inactivo");
        if(p.getFechaInicio()==null||p.getFechaFin()==null||now.isBefore(p.getFechaInicio())||now.isAfter(p.getFechaFin())) throw new IllegalArgumentException("Cupon fuera de vigencia");
        if(p.getUsosMaximos()==null||p.getUsosActuales()==null||p.getUsosActuales()>=p.getUsosMaximos()) throw new IllegalArgumentException("Cupon sin usos disponibles");
        if(p.getMontoMinimo()==null||subtotal.compareTo(BigDecimal.valueOf(p.getMontoMinimo()))<0) throw new IllegalArgumentException("No se cumple el monto minimo");
        if(!"PORCENTAJE".equalsIgnoreCase(p.getTipo())||p.getValor()==null||p.getValor()<=0||p.getValor()>100) throw new IllegalArgumentException("Cupon invalido");
        BigDecimal descuento=subtotal.multiply(BigDecimal.valueOf(p.getValor())).divide(BigDecimal.valueOf(100));
        if(p.getMaximoDescuento()!=null) {if(p.getMaximoDescuento()<0)throw new IllegalArgumentException("Cupon invalido");descuento=descuento.min(BigDecimal.valueOf(p.getMaximoDescuento()));}
        descuento=descuento.min(subtotal).setScale(0,RoundingMode.HALF_UP);return new Quote(p.getCode(),subtotal,descuento,subtotal.subtract(descuento));
    }
    @Transactional(readOnly=true) public Quote preview(String code,BigDecimal subtotal){String c=code(code);return calculate(promotions.findByCode(c).orElseThrow(()->new IllegalArgumentException("Cupon invalido")),subtotal);}
    @Transactional public Quote redeem(String orderKey,String input,BigDecimal subtotal){
        if(orderKey==null||orderKey.isBlank()||orderKey.length()>128)throw new IllegalArgumentException("Orden invalida");
        String c=code(input);var p=promotions.lockByCode(c).orElseThrow(()->new IllegalArgumentException("Cupon invalido"));
        var existing=uses.findById(orderKey);
        if(existing.isPresent()) {var u=existing.get();if(!u.getCode().equals(c)||u.getSubtotal().compareTo(subtotal)!=0)throw new IllegalArgumentException("Orden con otro cupon");return new Quote(c,subtotal,u.getDescuento(),subtotal.subtract(u.getDescuento()));}
        Quote quote=calculate(p,subtotal);p.setUsosActuales(p.getUsosActuales()+1);promotions.save(p);
        var u=new CouponUse();u.setOrderKey(orderKey);u.setCode(c);u.setSubtotal(subtotal);u.setDescuento(quote.descuento());uses.saveAndFlush(u);return quote;
    }
}
