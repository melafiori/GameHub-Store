package com.gamehub.order.services;
import com.gamehub.order.models.OrderDetalle;
import com.gamehub.order.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:orderrace;DB_CLOSE_DELAY=-1","spring.jpa.hibernate.ddl-auto=create-drop","eureka.client.enabled=false","JWT_SECRET=0123456789abcdef0123456789abcdef"})
class OrderTransitionRaceTest {
 @Autowired OrderRepository orders; @Autowired OrderTransitionService transitions;
 @Test void onlyOneOfCancelAndShippingWins() throws Exception {
  OrderDetalle o=new OrderDetalle();o.setCustomerId(1L);o.setUserEmail("test");o.setTotal(1000d);o.setEstado("PAGADA");o.setShippingStarted(false);Long id=orders.saveAndFlush(o).getOrderId();
  var pool=Executors.newFixedThreadPool(2);var gate=new CountDownLatch(1);
  try {Callable<Boolean> cancel=()->{gate.await();try{transitions.beginCancel(id);return true;}catch(org.springframework.web.server.ResponseStatusException e){return false;}};
   Callable<Boolean> shipping=()->{gate.await();try{transitions.beginShipping(id);return true;}catch(org.springframework.web.server.ResponseStatusException e){return false;}};
   var a=pool.submit(cancel);var b=pool.submit(shipping);gate.countDown();assertThat((a.get()?1:0)+(b.get()?1:0)).isEqualTo(1);
   var saved=orders.findById(id).orElseThrow();assertThat("ANULACION_PENDIENTE".equals(saved.getEstado()) ^ Boolean.TRUE.equals(saved.getShippingStarted())).isTrue();
  } finally {pool.shutdownNow();}
 }
}
