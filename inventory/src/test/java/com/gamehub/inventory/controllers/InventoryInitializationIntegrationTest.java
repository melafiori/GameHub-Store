package com.gamehub.inventory.controllers;

import com.gamehub.inventory.repositories.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:initinventorytest;DB_CLOSE_DELAY=-1","spring.jpa.hibernate.ddl-auto=create-drop","eureka.client.enabled=false","INVENTORY_SERVICE_SECRET=0123456789abcdef0123456789abcdef"})
class InventoryInitializationIntegrationTest {
 @Autowired InternalInventoryInitializationController controller;
 @Autowired InventarioRepository repository;
 private final String secret="0123456789abcdef0123456789abcdef";
 @Test void retryPreservesChangedStockAndRejectsInvalidValues() {
  var initial=controller.initialize(991L,secret,Map.of("stockInicial",3));assertThat(initial.getStockDisponible()).isEqualTo(3);
  initial.setStockDisponible(1);repository.saveAndFlush(initial);
  assertThat(controller.initialize(991L,secret,Map.of("stockInicial",3)).getStockDisponible()).isEqualTo(1);
  assertThat(repository.findAll().stream().filter(i->i.getProductId().equals(991L)).count()).isEqualTo(1);
  assertThatThrownBy(()->controller.initialize(992L,secret,Map.of("stockInicial",1.5))).isInstanceOf(ResponseStatusException.class).hasMessageContaining("400");
  assertThatThrownBy(()->controller.initialize(992L,"incorrecto",Map.of("stockInicial",3))).isInstanceOf(ResponseStatusException.class).hasMessageContaining("401");
 }
 @Test void concurrentInitializationCreatesOneInventory() throws Exception {
  var pool=Executors.newFixedThreadPool(2);var gate=new CountDownLatch(1);
  try {var a=pool.submit(()->{gate.await();return controller.initialize(993L,secret,Map.of("stockInicial",3));});var b=pool.submit(()->{gate.await();return controller.initialize(993L,secret,Map.of("stockInicial",3));});gate.countDown();assertThat(a.get().getInventoryId()).isEqualTo(b.get().getInventoryId());assertThat(repository.findAll().stream().filter(i->i.getProductId().equals(993L)).count()).isEqualTo(1);}
  finally {pool.shutdownNow();}
 }
}
