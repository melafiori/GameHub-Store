package com.gamehub.order.services;
import com.gamehub.order.models.OrderDetalle;
import com.gamehub.order.repositories.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class OrderTransitionService {
 private final OrderRepository orders;
 public OrderTransitionService(OrderRepository orders) {this.orders=orders;}
 private OrderDetalle lock(Long id) {return orders.lockById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Orden no encontrada"));}
 @Transactional public OrderDetalle beginShipping(Long id) {
  OrderDetalle o=lock(id); if(!"PAGADA".equals(o.getEstado())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Orden no disponible para despacho");
  o.setShippingStarted(true);return orders.saveAndFlush(o);
 }
 @Transactional public OrderDetalle beginCancel(Long id) {
  OrderDetalle o=lock(id); if("ANULADA".equals(o.getEstado()) || "ANULACION_PENDIENTE".equals(o.getEstado())) return o;
  if(!"PAGADA".equals(o.getEstado())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Orden no pagada");
  if(!Boolean.FALSE.equals(o.getShippingStarted())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Despacho iniciado o no verificado");
  o.setEstado("ANULACION_PENDIENTE");return orders.saveAndFlush(o);
 }
 @Transactional public OrderDetalle finishCancel(Long id) {
  OrderDetalle o=lock(id); if("ANULADA".equals(o.getEstado())) return o;
  if(!"ANULACION_PENDIENTE".equals(o.getEstado())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Anulacion no iniciada");
  o.setEstado("ANULADA");return orders.saveAndFlush(o);
 }
}
