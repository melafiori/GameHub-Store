package com.gamehub.inventory.services;
import com.gamehub.inventory.models.Inventario;import com.gamehub.inventory.repositories.*;import org.junit.jupiter.api.Test;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.test.context.SpringBootTest;import java.util.Map;import java.util.concurrent.*;import static org.assertj.core.api.Assertions.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:stockadjust;DB_CLOSE_DELAY=-1","eureka.client.enabled=false","JWT_SECRET=0123456789abcdef0123456789abcdef"})
class StockAdjustmentIntegrationTest {
 @Autowired StockAdjustmentService adjustments;@Autowired StockReservationService reservations;@Autowired InventarioRepository inventory;@Autowired StockAdjustmentRepository moves;
 @Test void repeatSameKeyAppliesOnce() {
  Inventario i=new Inventario();i.setProductId(992L);i.setStockDisponible(3);i.setStockMinimo(0);i.setUbicacion("test");inventory.saveAndFlush(i);
  var first=adjustments.adjust(992L,1L,"same",2,"count");var retry=adjustments.adjust(992L,1L,"same",2,"count");
  assertThat(retry.getId()).isEqualTo(first.getId());assertThat(inventory.findByProductId(992L).orElseThrow().getStockDisponible()).isEqualTo(5);assertThat(moves.findByProductIdOrderByIdDesc(992L)).hasSize(1);
  assertThatThrownBy(()->adjustments.adjust(992L,1L,"other",-6,"test")).isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
 }
 @Test void retryAndCompetingReservationNeverOversell() throws Exception {
  Inventario i=new Inventario();i.setProductId(991L);i.setStockDisponible(1);i.setStockMinimo(0);i.setUbicacion("test");inventory.saveAndFlush(i);
  var pool=Executors.newFixedThreadPool(2);var gate=new CountDownLatch(1);
  try{Callable<Boolean> adjust=()->{gate.await();try{adjustments.adjust(991L,1L,"a",-1,"test");return true;}catch(RuntimeException e){return false;}};Callable<Boolean> reserve=()->{gate.await();try{reservations.reservar("stockadjust-991",Map.of(991L,1));return true;}catch(RuntimeException e){return false;}};var a=pool.submit(adjust);var b=pool.submit(reserve);gate.countDown();boolean success=a.get();assertThat((success?1:0)+(b.get()?1:0)).isEqualTo(1);assertThat(inventory.findByProductId(991L).orElseThrow().getStockDisponible()).isZero();if(success){var first=adjustments.adjust(991L,1L,"a",-1,"test");assertThat(first.getStockPosterior()).isZero();assertThat(moves.findByProductIdOrderByIdDesc(991L)).hasSize(1);}}
  finally{pool.shutdownNow();}
 }
}
