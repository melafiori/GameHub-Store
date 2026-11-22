package com.gamehub.payment;
import com.gamehub.payment.controllers.PaymentController;
import com.gamehub.payment.models.Payment;
import com.gamehub.payment.repositories.PaymentRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
class PaymentCancellationTest {
 @Test void repeatedCancellationKeepsOnePayment() {
  var repo=mock(PaymentRepository.class); var controller=new PaymentController(repo,"0123456789abcdef0123456789abcdef");
  var payment=new Payment();payment.setId(1L);payment.setOrderId(2L);payment.setMonto(1000.0);payment.setEstado("APROBADO");
  when(repo.lockByOrderId(2L)).thenReturn(Optional.of(payment));when(repo.saveAndFlush(payment)).thenReturn(payment);
  assertThat(controller.cancel(2L,"0123456789abcdef0123456789abcdef").estado()).isEqualTo("ANULADO");
  assertThat(controller.cancel(2L,"0123456789abcdef0123456789abcdef").estado()).isEqualTo("ANULADO");verify(repo,times(1)).saveAndFlush(payment);
 }
}
