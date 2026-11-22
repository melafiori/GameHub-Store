package com.gamehub.shipping.controllers;
import com.gamehub.shipping.clients.OrderClient;
import com.gamehub.shipping.models.Shipping;
import com.gamehub.shipping.repositories.ShippingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.util.*;
@RestController
@RequestMapping("/api/v1/shippings")
public class ShippingController {
    private final ShippingRepository repository; private final OrderClient orders; private final String secret;
    public ShippingController(ShippingRepository repository,OrderClient orders,@Value("${SHIPPING_SERVICE_SECRET:}") String secret){this.repository=repository;this.orders=orders;this.secret=secret;}
    public record StatusRequest(String estado) {}
    @GetMapping public List<Shipping> findAll(){return repository.findAll();}
    @PostMapping("/orders/{orderId}") public ResponseEntity<Shipping> create(@PathVariable Long orderId){
        if(secret.getBytes(StandardCharsets.UTF_8).length<32) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        OrderClient.Snapshot order;
        try { order=orders.start(orderId,secret); } catch(feign.FeignException.Conflict e) {throw new ResponseStatusException(HttpStatus.CONFLICT,"Orden no disponible para despacho");}
        if(!"PAGADA".equals(order.estado()) || order.direccionDespacho()==null || order.customerId()==null) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        var existing=repository.findFirstByOrderId(orderId); if(existing.isPresent()) return ResponseEntity.ok(existing.get());
        var address=order.direccionDespacho(); var shipping=new Shipping();
        shipping.setOrderId(orderId);shipping.setUserId(order.customerId());shipping.setDireccion(address.direccion()+", "+address.comuna()+", "+address.region());
        shipping.setTransportista("Por asignar");shipping.setTracking("GH-"+UUID.randomUUID());shipping.setEstado("PENDIENTE");shipping.setFechaEnvio(LocalDateTime.now());
        try{return ResponseEntity.status(HttpStatus.CREATED).body(repository.saveAndFlush(shipping));}
        catch(DataIntegrityViolationException e){return ResponseEntity.ok(repository.findFirstByOrderId(orderId).orElseThrow(() -> e));}
    }
    @GetMapping("/order/{orderId}") public ResponseEntity<Shipping> byOrder(@PathVariable Long orderId,@AuthenticationPrincipal Jwt jwt){
        var shipping=repository.findFirstByOrderId(orderId); if(shipping.isEmpty()) return ResponseEntity.noContent().build();
        var role=jwt.getClaimAsString("role");
        if("CLIENTE".equals(role) && !Objects.equals(shipping.get().getUserId().toString(),jwt.getSubject())) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(shipping.get());
    }
    @PatchMapping("/{id}/status") public Shipping change(@PathVariable Long id,@RequestBody StatusRequest request){
        Shipping shipping=repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var next=Map.of("PENDIENTE","EN_PREPARACION","EN_PREPARACION","EN_CAMINO","EN_CAMINO","ENTREGADO");
        if(request.estado()==null || (!request.estado().equals(shipping.getEstado()) && !request.estado().equals(next.get(shipping.getEstado())))) throw new ResponseStatusException(HttpStatus.CONFLICT,"Transicion no permitida");
        if ("ENTREGADO".equals(request.estado()) && !"ENTREGADO".equals(shipping.getEstado())) shipping.setFechaEntrega(LocalDateTime.now());
        shipping.setEstado(request.estado()); return repository.saveAndFlush(shipping);
    }
}
