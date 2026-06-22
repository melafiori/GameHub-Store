package com.gamehub.payment.services;

import com.gamehub.payment.models.Payment;
import com.gamehub.payment.repositories.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Debe aprobar un pago válido")
    public void shouldApprovePayment() {

        Payment payment = new Payment();
        payment.setOrderId(1L);
        payment.setMonto(50000.0);
        payment.setMetodoPago("TARJETA");

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.procesarPago(payment);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo("APROBADO");
        assertThat(result.getTransactionId()).startsWith("TX-OK-");

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe rechazar un pago con monto inválido")
    public void shouldRejectPayment() {

        Payment payment = new Payment();
        payment.setOrderId(1L);
        payment.setMonto(0.0);
        payment.setMetodoPago("TARJETA");

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Payment result = paymentService.procesarPago(payment);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo("RECHAZADO");
        assertThat(result.getTransactionId()).startsWith("TX-FAILED-");
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}
