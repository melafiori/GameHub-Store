package com.gamehub.order.controllers;
import com.gamehub.order.services.OrderServiceImpl;
import com.gamehub.order.models.OrderDetalle;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderServiceImpl service;
    public OrderController(OrderServiceImpl service) { this.service = service; }
    public record CartRequest(@NotEmpty Map<Long, Integer> items, @Valid @jakarta.validation.constraints.NotNull com.gamehub.order.models.ShippingAddress direccionDespacho, String codigoCupon) {}
    public record OrderResponse(Long id, String estado, Double total, Double subtotal, Double descuento, String codigoCupon, String motivoRechazo) {}
    public record ItemResponse(Long productId, Integer cantidad, Double precioUnitario) {}
    public record HistoryResponse(Long id, String estado, Double total, java.util.List<ItemResponse> items, com.gamehub.order.models.ShippingAddress direccionDespacho, String codigoCupon, Double subtotal, Double descuento) {}
    public record PaidOrder(Long id, Double total, com.gamehub.order.models.ShippingAddress direccionDespacho) {}
    public record OperatorOrder(Long id, java.time.LocalDateTime fecha, Double total, String estado, Boolean despachoIniciado) {}
    @GetMapping("/operator")
    @io.swagger.v3.oas.annotations.Operation(summary="Bandeja de ordenes", description="OPERADOR o ADMINISTRADOR. Filtro opcional por estado exacto.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="200", description="Ordenes obtenidas"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="400", description="Estado invalido"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="403", description="Rol no autorizado")})
    public java.util.List<OperatorOrder> operatorOrders(@RequestParam(required=false) String estado) {
        return service.bandeja(estado).stream().map(o -> new OperatorOrder(o.getOrderId(), o.getAudit() == null ? null : o.getAudit().getCreatedAt(), o.getTotal(), o.getEstado(), o.getShippingStarted())).toList();
    }
    @GetMapping("/paid")
    public java.util.List<PaidOrder> paidOrders() {
        return service.ordenesPagadas().stream().map(o -> new PaidOrder(o.getOrderId(), o.getTotal(), o.getDireccionDespacho())).toList();
    }
    @GetMapping("/me")
    public java.util.List<HistoryResponse> history(@AuthenticationPrincipal Jwt jwt) {
        return service.historialPropio(Long.valueOf(jwt.getSubject())).stream().map(o -> new HistoryResponse(o.getOrderId(),o.getEstado(),o.getTotal(),o.getItems().stream().map(i -> new ItemResponse(i.getProductId(),i.getCantidad(),i.getPrecioUnitario())).toList(),o.getDireccionDespacho(),o.getCodigoCupon(),o.getSubtotal(),o.getDescuento())).toList();
    }
    @PostMapping("/{id}/payment/cancel")
    @io.swagger.v3.oas.annotations.Operation(summary="Anular pago de orden pagada sin despacho")
    public ResponseEntity<?> cancelPayment(@PathVariable Long id) {
        try {var o=service.anularPago(id);return ResponseEntity.ok(new OrderResponse(o.getOrderId(),o.getEstado(),o.getTotal(),o.getSubtotal(),o.getDescuento(),o.getCodigoCupon(),o.getMotivoRechazo()));}
        catch (feign.FeignException e) {return ResponseEntity.status(503).body(Map.of("error","Anulacion pendiente; reintente"));}
    }
    @PostMapping("/{id}/payment/confirm")
    @io.swagger.v3.oas.annotations.Operation(summary = "Confirmar pago simulado de una orden", description = "OPERADOR o ADMINISTRADOR; utiliza el total persistido.")
    public ResponseEntity<?> confirmPayment(@PathVariable Long id) {
        try {
            var order = service.confirmarPagoOperador(id);
            return ResponseEntity.ok(new OrderResponse(order.getOrderId(),order.getEstado(),order.getTotal(),order.getSubtotal(),order.getDescuento(),order.getCodigoCupon(),order.getMotivoRechazo()));
        } catch (feign.FeignException e) { return ResponseEntity.status(503).body(Map.of("error", "Pago no disponible; reintente")); }
    }
    @PostMapping("/{id}/payment")
    public ResponseEntity<?> pay(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        try { var order=service.pagar(Long.valueOf(jwt.getSubject()),id); return ResponseEntity.ok(new OrderResponse(order.getOrderId(),order.getEstado(),order.getTotal(),order.getSubtotal(),order.getDescuento(),order.getCodigoCupon(),order.getMotivoRechazo())); }
        catch (feign.FeignException e) { return ResponseEntity.status(503).body(Map.of("error","Pago no disponible; reintente")); }
    }
    @PostMapping
    @io.swagger.v3.oas.annotations.Operation(summary="Crear orden pendiente", description="Calcula el total del catalogo y valida el cupon; Idempotency-Key identifica el intento.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="201", description="Orden creada o recuperada"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="400", description="Carrito o direccion invalidos"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="401", description="JWT ausente o invalido"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="403", description="Rol no autorizado"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="409", description="Conflicto de idempotencia o stock"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="503", description="Servicio no disponible")})
    public ResponseEntity<?> checkout(@io.swagger.v3.oas.annotations.Parameter(hidden=true) @AuthenticationPrincipal Jwt jwt, @RequestHeader("Idempotency-Key") String key, @Valid @RequestBody CartRequest cart) {
        try {
            Long customerId = Long.valueOf(jwt.getSubject());
            OrderDetalle order = service.crearPendiente(customerId, key, cart.items(), cart.direccionDespacho(), cart.codigoCupon());
            var response = new OrderResponse(order.getOrderId(), order.getEstado(), order.getTotal(), order.getSubtotal(), order.getDescuento(), order.getCodigoCupon(), order.getMotivoRechazo());
            return ResponseEntity.status("RECHAZADA".equals(order.getEstado()) ? 409 : 201).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        } catch (feign.FeignException e) {
            return ResponseEntity.status(503).body(Map.of("error", "Servicio no disponible; reintente con la misma clave"));
        }
    }
}
