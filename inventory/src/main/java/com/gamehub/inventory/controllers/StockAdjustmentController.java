package com.gamehub.inventory.controllers;
import com.gamehub.inventory.models.StockAdjustment;import com.gamehub.inventory.repositories.StockAdjustmentRepository;import com.gamehub.inventory.services.StockAdjustmentService;import org.springframework.security.core.annotation.AuthenticationPrincipal;import org.springframework.security.oauth2.jwt.Jwt;import org.springframework.web.bind.annotation.*;import java.util.List;
@RestController @RequestMapping("/api/v1/inventory/adjustments") public class StockAdjustmentController {
 private final StockAdjustmentService service;private final StockAdjustmentRepository repo;
 public StockAdjustmentController(StockAdjustmentService s,StockAdjustmentRepository r){service=s;repo=r;}
 public record Request(Integer delta,String motivo){}
 @PostMapping("/{productId}") @io.swagger.v3.oas.annotations.Operation(summary="Ajustar stock, solo operador o administrador",description="Idempotency-Key identifica el intento. Delta positivo suma y negativo resta.")
 public StockAdjustment adjust(@PathVariable Long productId,@RequestHeader("Idempotency-Key") String key,@AuthenticationPrincipal Jwt jwt,@RequestBody Request r){if(r.delta()==null)throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Delta requerido");Long actor;try{actor=Long.valueOf(jwt.getSubject());}catch(RuntimeException ex){throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED);}return service.adjust(productId,actor,key,r.delta(),r.motivo());}
 @GetMapping("/{productId}") @io.swagger.v3.oas.annotations.Operation(summary="Consultar historial de ajustes") public List<StockAdjustment> history(@PathVariable Long productId){return repo.findByProductIdOrderByIdDesc(productId);}
}
