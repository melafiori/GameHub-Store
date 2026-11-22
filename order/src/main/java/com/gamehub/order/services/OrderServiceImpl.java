package com.gamehub.order.services;

import com.gamehub.order.clients.InventoryClient;
import com.gamehub.order.clients.ProductClient;
import com.gamehub.order.exceptions.OrderException;
import com.gamehub.order.models.Order;
import com.gamehub.order.models.OrderDetalle;
import com.gamehub.order.models.dtos.OrderDTO;
import com.gamehub.order.models.dtos.OrderRequestDTO;
import com.gamehub.order.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.TreeMap;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service

public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryClient inventoryClient;

    @Autowired
    private ProductClient productClient;

    @Value("${INVENTORY_SERVICE_SECRET:}")
    private String inventorySecret;

    @Autowired private com.gamehub.order.clients.PromotionClient promotionClient;
    @Value("${PROMOTION_SERVICE_SECRET:}") private String promotionSecret;
    @Autowired private com.gamehub.order.clients.PaymentClient paymentClient;
    @Value("${PAYMENT_SERVICE_SECRET:}") private String paymentSecret;
    @Autowired private OrderTransitionService transitions;
    public OrderDetalle anularPago(Long id) {
        OrderDetalle o=transitions.beginCancel(id);
        if ("ANULADA".equals(o.getEstado())) return o;
        var payment=paymentClient.cancel(id,paymentSecret);
        if (!"ANULADO".equals(payment.estado()) || !id.equals(payment.orderId()) || BigDecimal.valueOf(o.getTotal()).compareTo(payment.monto()) != 0) throw new IllegalStateException("Anulacion de pago no confirmada");
        var stock=inventoryClient.returnStock("order-"+id,inventorySecret);
        if (!"DEVUELTO".equals(stock.get("estado"))) throw new IllegalStateException("Devolucion de stock no confirmada");
        return transitions.finishCancel(id);
    }
    public List<OrderDetalle> bandeja(String estado) {
        if (estado == null || estado.isBlank()) return orderRepository.findAllByOrderByOrderIdDesc();
        if (!java.util.Set.of("RESERVA_PENDIENTE", "CUPON_PENDIENTE", "REVERSION_PENDIENTE", "PENDIENTE_DE_PAGO", "PAGADA", "RECHAZADA", "ANULACION_PENDIENTE", "ANULADA", "PENDIENTE", "PROCESADA").contains(estado))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Estado no valido");
        return orderRepository.findByEstadoOrderByOrderIdDesc(estado);
    }
    public List<OrderDetalle> ordenesPagadas() { return orderRepository.findByEstadoOrderByOrderIdDesc("PAGADA"); }
    public List<OrderDetalle> historialPropio(Long customerId) {
        return orderRepository.findByCustomerIdOrderByOrderIdDesc(customerId);
    }
    public OrderDetalle confirmarPagoOperador(Long orderId) {
        OrderDetalle order = orderRepository.findById(orderId)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Orden no encontrada"));
        if (order.getCustomerId() == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Orden sin cliente");
        return pagar(order.getCustomerId(), orderId);
    }
    public OrderDetalle pagar(Long customerId, Long orderId) {
        OrderDetalle order=orderRepository.findByOrderIdAndCustomerId(orderId,customerId)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        if ("PAGADA".equals(order.getEstado())) return order;
        if (!"PENDIENTE_DE_PAGO".equals(order.getEstado())) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,"Orden no pendiente de pago");
        if (paymentSecret.getBytes(StandardCharsets.UTF_8).length < 32) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,"Secreto de pagos no configurado");
        var result=paymentClient.pay(orderId,paymentSecret,new com.gamehub.order.clients.PaymentClient.Request(BigDecimal.valueOf(order.getTotal())));
        if (!"APROBADO".equals(result.estado()) || !orderId.equals(result.orderId()) || BigDecimal.valueOf(order.getTotal()).compareTo(result.monto())!=0)
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,"Pago no aprobado o monto incorrecto");
        order.setEstado("PAGADA"); return orderRepository.saveAndFlush(order);
    }
    // customerId debe proceder de una identidad ya autenticada por la capa HTTP.
    public OrderDetalle crearPendiente(Long customerId, String key, Map<Long, Integer> items) { return crearPendiente(customerId,key,items,null); }

    public OrderDetalle crearPendiente(Long customerId, String key, Map<Long, Integer> items, com.gamehub.order.models.ShippingAddress direccion) {
        return crearPendiente(customerId, key, items, direccion, null);
    }
    public OrderDetalle crearPendiente(Long customerId, String key, Map<Long, Integer> items, com.gamehub.order.models.ShippingAddress direccion, String codigoCupon) {
        var snapshot = direccion == null ? null : direccion.normalized();
        String coupon = codigoCupon == null || codigoCupon.isBlank() ? null : codigoCupon.trim().toUpperCase(java.util.Locale.ROOT);
        if (coupon != null && coupon.length() > 100) throw new IllegalArgumentException("Cupon invalido");
        if (customerId == null || customerId <= 0 || key == null || key.isBlank() || key.length() > 128 ||
                items == null || items.isEmpty() || items.entrySet().stream().anyMatch(e ->
                e.getKey() == null || e.getKey() <= 0 || e.getValue() == null || e.getValue() <= 0))
            throw new IllegalArgumentException("Cliente, clave y carrito deben ser válidos");
        Map<Long, Integer> sorted = new TreeMap<>(items);
        String fingerprint;
        try {
            fingerprint = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest((sorted.toString() + (snapshot == null ? "" : snapshot.fingerprintPart()) + (coupon == null ? "" : "|cup:" + coupon)).getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
        OrderDetalle order = orderRepository.findByCustomerIdAndIdempotencyKey(customerId, key).orElse(null);
        if (order == null) {
            order = new OrderDetalle(); order.setShippingStarted(false);
            order.setCustomerId(customerId); order.setIdempotencyKey(key); order.setCartFingerprint(fingerprint);
            order.setDireccionDespacho(snapshot); order.setCodigoCupon(coupon);
            order.setUserEmail("cliente-" + customerId); order.setEstado("RESERVA_PENDIENTE");
            BigDecimal total = BigDecimal.ZERO;
            List<Order> details = new ArrayList<>();
            for (var entry : sorted.entrySet()) {
                Object body = productClient.getProductById(entry.getKey()).getBody();
                if (!(body instanceof Map<?, ?> product) || !"ACTIVO".equalsIgnoreCase(String.valueOf(product.get("estado"))) ||
                        !(product.get("precioVigente") instanceof Number price)) throw new IllegalArgumentException("Producto no disponible");
                BigDecimal unit = new BigDecimal(price.toString());
                if (unit.signum() <= 0) throw new IllegalArgumentException("Precio inválido");
                Order detail = new Order(); detail.setProductId(entry.getKey()); detail.setCantidad(entry.getValue());
                detail.setPrecioUnitario(unit.doubleValue()); details.add(detail);
                total = total.add(unit.multiply(BigDecimal.valueOf(entry.getValue())));
            }
            order.setItems(details); order.setSubtotal(total.doubleValue()); order.setDescuento(0.0); order.setTotal(total.doubleValue());
            try { order = orderRepository.saveAndFlush(order); }
            catch (org.springframework.dao.DataIntegrityViolationException conflict) {
                throw new IllegalStateException("Clave ya utilizada; reintenta la solicitud", conflict);
            }
        } else if (!fingerprint.equals(order.getCartFingerprint())) {
            throw new IllegalStateException("La clave ya se usó con otro carrito");
        }
        if ("RESERVA_PENDIENTE".equals(order.getEstado())) {
            if (inventorySecret == null || inventorySecret.getBytes(StandardCharsets.UTF_8).length < 32) throw new IllegalStateException("INVENTORY_SERVICE_SECRET no configurado");
            try {
                Map<String,String> result=inventoryClient.reserve("order-"+order.getOrderId(),inventorySecret,sorted);
                if (!"RESERVADO".equals(result.get("estado"))) throw new IllegalStateException("Reserva no confirmada");
                order.setEstado(coupon == null ? "PENDIENTE_DE_PAGO" : "CUPON_PENDIENTE"); order=orderRepository.saveAndFlush(order);
            } catch (feign.FeignException.Conflict e) {order.setEstado("RECHAZADA"); order.setMotivoRechazo("Stock insuficiente"); return orderRepository.saveAndFlush(order);}
        }
        if ("CUPON_PENDIENTE".equals(order.getEstado())) {
            if (promotionSecret == null || promotionSecret.getBytes(StandardCharsets.UTF_8).length < 32) throw new IllegalStateException("PROMOTION_SERVICE_SECRET no configurado");
            try {
                var quote=promotionClient.redeem("order-"+order.getOrderId(),promotionSecret,new com.gamehub.order.clients.PromotionClient.Request(order.getCodigoCupon(),BigDecimal.valueOf(order.getSubtotal())));
                if(!order.getCodigoCupon().equals(quote.codigo()) || quote.descuento().signum()<0 || quote.total().compareTo(BigDecimal.valueOf(order.getSubtotal()).subtract(quote.descuento()))!=0 || quote.total().signum()<0) throw new IllegalStateException("Respuesta de cupon invalida");
                order.setDescuento(quote.descuento().doubleValue());order.setTotal(quote.total().doubleValue());order.setEstado("PENDIENTE_DE_PAGO");return orderRepository.saveAndFlush(order);
            } catch (feign.FeignException.BadRequest e) {order.setEstado("REVERSION_PENDIENTE");order.setMotivoRechazo("Cupon no disponible o fuera de vigencia");order=orderRepository.saveAndFlush(order);}
        }
        if ("REVERSION_PENDIENTE".equals(order.getEstado())) {
            Map<String,String> restored=inventoryClient.returnStock("order-"+order.getOrderId(),inventorySecret);
            if(!"DEVUELTO".equals(restored.get("estado"))) throw new IllegalStateException("Devolucion de stock pendiente");
            order.setEstado("RECHAZADA");return orderRepository.saveAndFlush(order);
        }
        return order;
    }



    @Override
    @Transactional // Asegura que si falla el descuento de stock de un producto, se cancele toda la compra
    public OrderDetalle crearOrden(OrderRequestDTO orderRequestDto) {
        OrderDetalle orden = new OrderDetalle();
        orden.setUserEmail(orderRequestDto.getUserEmail());
        orden.setEstado("PENDIENTE");

        double totalOrden = 0.0;
        List<Order> listaItems = new ArrayList<>();

        // 1. Validar disponibilidad de TODO el carrito antes de procesar o descontar nada
        for (OrderDTO itemDto : orderRequestDto.getItems()) {
            boolean tieneStock = inventoryClient.checkStock(itemDto.getProductId(), itemDto.getCantidad());
            if (!tieneStock) {
                throw new RuntimeException("Stock insuficiente para el producto con ID: " + itemDto.getProductId());
            }
        }

        // 2. Si todo tiene stock, construimos los detalles de la orden y calculamos precios
        for (OrderDTO itemDto : orderRequestDto.getItems()) {
            // Consultamos al catálogo de productos su precio actual
            double precioUnitario = 0.0;
            try {
                ResponseEntity<Object> response = productClient.getProductById(itemDto.getProductId());
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    // Mapeo dinámico del precio desde el objeto genérico retornado por product-service
                    Map<String, Object> producto = (Map<String, Object>) response.getBody();
                    precioUnitario = ((Number) producto.get("precio")).doubleValue();
                } else {
                    throw new RuntimeException("El producto no existe en el catálogo maestro.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Error al conectar con el catálogo de productos: " + e.getMessage());
            }

            Order item = new Order();
            item.setProductId(itemDto.getProductId());
            item.setCantidad(itemDto.getCantidad());
            item.setPrecioUnitario(precioUnitario);
            listaItems.add(item);

            totalOrden += (precioUnitario * itemDto.getCantidad());

            inventoryClient.reduceStock(itemDto.getProductId(), itemDto.getCantidad());
        }

        // 4. Consolidamos la cabecera de la orden
        orden.setItems(listaItems);
        orden.setTotal(totalOrden);
        orden.setEstado("PROCESADA");

        return orderRepository.save(orden);
    }

    @Override
    public List<OrderDetalle> obtenerHistorialPorEmail(String email) {
        return orderRepository.findByUserEmail(email);
    }
}
