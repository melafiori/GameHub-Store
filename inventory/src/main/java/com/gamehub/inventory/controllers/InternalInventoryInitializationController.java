package com.gamehub.inventory.controllers;

import com.gamehub.inventory.models.Inventario;
import com.gamehub.inventory.repositories.InventarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@RestController
@RequestMapping("/internal/inventory-initialization")
public class InternalInventoryInitializationController {
 private final InventarioRepository repository; private final String secret;
 public InternalInventoryInitializationController(InventarioRepository repository,@Value("${INVENTORY_SERVICE_SECRET:}") String secret){this.repository=repository;this.secret=secret;}
 @PutMapping("/{productId}")
 public Inventario initialize(@PathVariable Long productId,@RequestHeader(value="X-Service-Secret",required=false) String supplied,@RequestBody Map<String,Object> body){
  if(secret.getBytes(StandardCharsets.UTF_8).length<32 || supplied==null || !MessageDigest.isEqual(secret.getBytes(StandardCharsets.UTF_8),supplied.getBytes(StandardCharsets.UTF_8))) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
  var stock=body.get("stockInicial");
  if(productId<=0 || !(stock instanceof Integer || stock instanceof Long) || ((Number)stock).longValue()<0 || ((Number)stock).longValue()>Integer.MAX_VALUE) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Stock inicial debe ser un entero no negativo");
  var found=repository.findByProductId(productId); if(found.isPresent())return found.get();
  Inventario inventory=new Inventario();inventory.setProductId(productId);inventory.setStockDisponible(((Number)stock).intValue());inventory.setStockReservado(0);inventory.setStockMinimo(0);inventory.setUbicacion("Catalogo");
  try{return repository.saveAndFlush(inventory);}catch(DataIntegrityViolationException e){return repository.findByProductId(productId).orElseThrow(() -> e);}
 }
}
