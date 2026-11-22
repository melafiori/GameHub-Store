package com.gamehub.promotion.services;
import com.gamehub.promotion.models.Promotion;
import com.gamehub.promotion.repositories.PromotionRepository;
import com.gamehub.promotion.repositories.CouponUseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:cuponitest;DB_CLOSE_DELAY=-1","eureka.client.enabled=false"})
class CouponIntegrationTest {
    @Autowired CouponService coupons;
    @Autowired PromotionRepository promotions;
    @Autowired CouponUseRepository uses;
    private Promotion create(String code,int max,boolean expired){
        var p=new Promotion();p.setCode(code);p.setTipo("PORCENTAJE");p.setValor(20.0);p.setMontoMinimo(0.0);p.setMaximoDescuento(100.0);p.setUsosMaximos(max);p.setUsosActuales(0);p.setEstado("ACTIVA");p.setFechaInicio(LocalDateTime.now().minusDays(3));p.setFechaFin(LocalDateTime.now().plusDays(expired?-1:3));return promotions.saveAndFlush(p);
    }
    @Test void lastUseIsAtomicAndRetryIsIdempotent() throws Exception {
        create("ULTIMO",1,false);var pool=Executors.newFixedThreadPool(2);var start=new CountDownLatch(1);
        try {
            Callable<String> a=()->{start.await();try{coupons.redeem("order-a","ULTIMO",new BigDecimal("1000"));return "a";}catch(IllegalArgumentException e){return "rejected";}};
            Callable<String> b=()->{start.await();try{coupons.redeem("order-b","ULTIMO",new BigDecimal("1000"));return "b";}catch(IllegalArgumentException e){return "rejected";}};
            var x=pool.submit(a);var y=pool.submit(b);start.countDown();String first=x.get(),second=y.get();
            assertThat(java.util.List.of(first,second)).contains("rejected");String winner=first.equals("rejected")?second:first;
            var quote=coupons.redeem("order-"+winner,"ULTIMO",new BigDecimal("1000"));assertThat(quote.descuento()).isEqualByComparingTo("100");
            assertThat(promotions.findByCode("ULTIMO").orElseThrow().getUsosActuales()).isEqualTo(1);
            assertThat(uses.findAll().stream().filter(u->u.getCode().equals("ULTIMO"))).hasSize(1);
            assertThatThrownBy(()->coupons.preview("ULTIMO",BigDecimal.TEN)).hasMessageContaining("usos");
        } finally {pool.shutdownNow();}
    }
    @Test void roundsAfterCapAndPersistsSameQuoteOnRetry() {
        var p=create("ROUND10",5,false);p.setValor(10.0);p.setMaximoDescuento(50000.0);promotions.saveAndFlush(p);
        var subtotal=new BigDecimal("36891");
        var preview=coupons.preview("ROUND10",subtotal);
        assertThat(preview.descuento()).isEqualByComparingTo("3689");
        assertThat(preview.total()).isEqualByComparingTo("33202");
        assertThat(promotions.findByCode("ROUND10").orElseThrow().getUsosActuales()).isZero();
        var redeem=coupons.redeem("round-order","ROUND10",subtotal);
        assertThat(redeem.descuento()).isEqualByComparingTo(preview.descuento());
        assertThat(coupons.redeem("round-order","ROUND10",subtotal).total()).isEqualByComparingTo("33202");
        assertThat(coupons.preview("ROUND10",new BigDecimal("105")).descuento()).isEqualByComparingTo("11");
        assertThat(coupons.preview("ROUND10",new BigDecimal("899991")).descuento()).isEqualByComparingTo("50000");
    }
    @Test void invalidAndExpiredAreRejectedWithoutConsumption(){
        create("VENCIDO",3,true);
        assertThatThrownBy(()->coupons.preview("NOEXISTE",BigDecimal.TEN)).hasMessageContaining("invalido");
        assertThatThrownBy(()->coupons.redeem("order-v","VENCIDO",BigDecimal.TEN)).hasMessageContaining("vigencia");
        assertThat(promotions.findByCode("VENCIDO").orElseThrow().getUsosActuales()).isZero();
    }
}
