package com.gamehub.order.services;

import com.gamehub.order.clients.PaymentClient;
import com.gamehub.order.models.OrderDetalle;
import com.gamehub.order.repositories.OrderRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperatorPaymentConfirmationTest {
    @Mock OrderRepository orders;
    @Mock PaymentClient payment;
    @InjectMocks OrderServiceImpl service;
    @Test void operatorUsesStoredTotalAndRetryDoesNotCreateAnotherPayment() {
        String secret="0123456789abcdef0123456789abcdef";
        ReflectionTestUtils.setField(service,"paymentSecret",secret);
        OrderDetalle order=new OrderDetalle(); order.setOrderId(8L); order.setCustomerId(4L);
        order.setEstado("PENDIENTE_DE_PAGO"); order.setTotal(1200.0);
        when(orders.findById(8L)).thenReturn(Optional.of(order));
        when(orders.findByOrderIdAndCustomerId(8L,4L)).thenReturn(Optional.of(order));
        when(payment.pay(8L,secret,new PaymentClient.Request(BigDecimal.valueOf(1200.0))))
            .thenReturn(new PaymentClient.Response(7L,8L,BigDecimal.valueOf(1200.0),"APROBADO"));
        when(orders.saveAndFlush(order)).thenReturn(order);
        assertThat(service.confirmarPagoOperador(8L).getEstado()).isEqualTo("PAGADA");
        assertThat(service.confirmarPagoOperador(8L).getEstado()).isEqualTo("PAGADA");
        verify(payment,times(1)).pay(8L,secret,new PaymentClient.Request(BigDecimal.valueOf(1200.0)));
    }
}
