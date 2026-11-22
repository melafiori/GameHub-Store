package com.gamehub.warranty.services;

import com.gamehub.warranty.clients.OrderClient;
import com.gamehub.warranty.clients.ShippingClient;
import com.gamehub.warranty.models.Warranty;
import com.gamehub.warranty.repositories.WarrantyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class WarrantyRequestService {
 private final OrderClient orders; private final ShippingClient shippings; private final WarrantyRepository repository; private final int days;
 public WarrantyRequestService(OrderClient orders, ShippingClient shippings, WarrantyRepository repository, @Value("${WARRANTY_PERIOD_DAYS:180}") int days) {
  if(days < 1) throw new IllegalArgumentException("WARRANTY_PERIOD_DAYS debe ser positivo");
  this.orders=orders;this.shippings=shippings;this.repository=repository;this.days=days;
 }
 public static boolean withinPeriod(LocalDate delivered, LocalDate today, int days) {
  return delivered != null && !today.isBefore(delivered) && !today.isAfter(delivered.plusDays(days));
 }
 public Warranty create(Jwt jwt, Long orderId, Long productId, String reason) {
  if(orderId == null || orderId <= 0 || productId == null || productId <= 0 || reason == null || reason.isBlank() || reason.trim().length()>255)
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Motivo y producto son obligatorios");
  String auth="Bearer "+jwt.getTokenValue();
  var order=orders.myOrders(auth).stream().filter(o -> orderId.equals(o.id())).findFirst()
   .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Orden no encontrada"));
  if(!"PAGADA".equals(order.estado()) || order.items()==null || order.items().stream().noneMatch(i -> productId.equals(i.productId())))
   throw new ResponseStatusException(HttpStatus.FORBIDDEN,"La orden debe estar pagada e incluir el producto");
  var eligibility=deliveryEligibility(orderId,auth);
  if (!eligibility.elegible()) throw new ResponseStatusException(HttpStatus.CONFLICT,eligibility.motivo());
  Warranty w=new Warranty();w.setUserId(Long.valueOf(jwt.getSubject()));w.setOrderId(orderId);w.setProductId(productId);w.setMotivo(reason.trim());w.setEstado("PENDIENTE");w.setFechaSolicitud(LocalDateTime.now());
  return repository.save(w);
 }
 public record Eligibility(boolean elegible, String motivo) {}
 private Eligibility deliveryEligibility(Long orderId, String auth) {
  var shipping=shippings.byOrder(orderId,auth);
  if(shipping==null || !"ENTREGADO".equals(shipping.estado()) || shipping.fechaEntrega()==null)
   return new Eligibility(false,"La orden no tiene una fecha de entrega confirmada");
  if(!withinPeriod(shipping.fechaEntrega().toLocalDate(),LocalDate.now(),days))
   return new Eligibility(false,"La garantia esta fuera de plazo");
  return new Eligibility(true,null);
 }
 public Eligibility eligibility(Jwt jwt, Long orderId) {
  if(orderId == null || orderId <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Orden invalida");
  String auth="Bearer "+jwt.getTokenValue();
  var order=orders.myOrders(auth).stream().filter(o -> orderId.equals(o.id())).findFirst()
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Orden no encontrada"));
  if(!"PAGADA".equals(order.estado()) || order.items()==null || order.items().isEmpty())
   return new Eligibility(false,"La orden debe estar pagada y contener productos");
  return deliveryEligibility(orderId,auth);
 }

}
