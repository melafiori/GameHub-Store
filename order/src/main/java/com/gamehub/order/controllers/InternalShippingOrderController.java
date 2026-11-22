package com.gamehub.order.controllers;
import com.gamehub.order.repositories.OrderRepository;
import com.gamehub.order.models.ShippingAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
@RestController
@RequestMapping("/internal/shipping-orders")
public class InternalShippingOrderController {
    private final OrderRepository orders; private final String secret;
    public InternalShippingOrderController(OrderRepository orders, @Value("${SHIPPING_SERVICE_SECRET:}") String secret) {this.orders=orders;this.secret=secret;}
    @org.springframework.beans.factory.annotation.Autowired private com.gamehub.order.services.OrderTransitionService transitions;
    public record Snapshot(Long id,Long customerId,String estado,ShippingAddress direccionDespacho) {}
    @PutMapping("/{id}/start") public Snapshot start(@PathVariable Long id,@RequestHeader(value="X-Service-Secret",required=false) String supplied) {
        byte[] expected=secret.getBytes(StandardCharsets.UTF_8);
        if(expected.length<32 || supplied==null || !MessageDigest.isEqual(expected,supplied.getBytes(StandardCharsets.UTF_8))) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        var order=transitions.beginShipping(id);
        return new Snapshot(order.getOrderId(),order.getCustomerId(),order.getEstado(),order.getDireccionDespacho());
    }
    @GetMapping("/{id}") public Snapshot get(@PathVariable Long id,@RequestHeader(value="X-Service-Secret",required=false) String supplied) {
        byte[] expected=secret.getBytes(StandardCharsets.UTF_8);
        if(expected.length<32 || supplied==null || !MessageDigest.isEqual(expected,supplied.getBytes(StandardCharsets.UTF_8))) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        var order=orders.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new Snapshot(order.getOrderId(),order.getCustomerId(),order.getEstado(),order.getDireccionDespacho());
    }
}
