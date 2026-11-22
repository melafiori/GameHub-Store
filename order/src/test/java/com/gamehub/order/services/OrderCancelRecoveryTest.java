package com.gamehub.order.services;
import com.gamehub.order.clients.InventoryClient;
import com.gamehub.order.clients.PaymentClient;
import com.gamehub.order.models.OrderDetalle;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class OrderCancelRecoveryTest {
 @Mock OrderTransitionService transitions; @Mock PaymentClient payments; @Mock InventoryClient inventory; @InjectMocks OrderServiceImpl service;
 @Test void retryResumesAfterStockResponseLost() {
  String secret="0123456789abcdef0123456789abcdef";ReflectionTestUtils.setField(service,"paymentSecret",secret);ReflectionTestUtils.setField(service,"inventorySecret",secret);
  OrderDetalle pending=new OrderDetalle();pending.setOrderId(4L);pending.setEstado("ANULACION_PENDIENTE");pending.setTotal(1000d);
  OrderDetalle done=new OrderDetalle();done.setOrderId(4L);done.setEstado("ANULADA");
  when(transitions.beginCancel(4L)).thenReturn(pending);when(payments.cancel(4L,secret)).thenReturn(new PaymentClient.Response(1L,4L,BigDecimal.valueOf(1000d),"ANULADO"));
  when(inventory.returnStock("order-4",secret)).thenThrow(new IllegalStateException("respuesta perdida")).thenReturn(Map.of("estado","DEVUELTO"));when(transitions.finishCancel(4L)).thenReturn(done);
  assertThatThrownBy(()->service.anularPago(4L)).isInstanceOf(IllegalStateException.class);verify(transitions,never()).finishCancel(anyLong());
  assertThat(service.anularPago(4L).getEstado()).isEqualTo("ANULADA");verify(inventory,times(2)).returnStock("order-4",secret);
 }
}
