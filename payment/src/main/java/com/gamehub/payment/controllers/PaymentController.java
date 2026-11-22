package com.gamehub.payment.controllers;
import com.gamehub.payment.models.Payment;
import com.gamehub.payment.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.math.BigDecimal;
import java.util.UUID;
@RestController
@RequestMapping("/internal/payments")
public class PaymentController {
    private final PaymentRepository payments;
    private final String secret;
    public PaymentController(PaymentRepository payments, @Value("${PAYMENT_SERVICE_SECRET:}") String secret) { this.payments=payments; this.secret=secret; }
    public record Request(BigDecimal monto) {}
    public record Response(Long id, Long orderId, BigDecimal monto, String estado) {}
    @PutMapping("/{orderId}/cancel")
    @org.springframework.transaction.annotation.Transactional
    public Response cancel(@PathVariable Long orderId, @RequestHeader(value="X-Service-Secret", required=false) String supplied) {
        byte[] expected=secret.getBytes(StandardCharsets.UTF_8);
        if(expected.length<32 || supplied==null || !MessageDigest.isEqual(expected,supplied.getBytes(StandardCharsets.UTF_8))) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Payment payment=payments.lockByOrderId(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Pago no encontrado"));
        if(!"APROBADO".equals(payment.getEstado()) && !"ANULADO".equals(payment.getEstado())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Pago no aprobado");
        if("APROBADO".equals(payment.getEstado())) { payment.setEstado("ANULADO"); payment=payments.saveAndFlush(payment); }
        return new Response(payment.getId(),payment.getOrderId(),BigDecimal.valueOf(payment.getMonto()),payment.getEstado());
    }
    @PutMapping("/{orderId}")
    public Response pay(@PathVariable Long orderId, @RequestHeader(value="X-Service-Secret", required=false) String supplied, @RequestBody Request request) {
        byte[] expected=secret.getBytes(StandardCharsets.UTF_8);
        if(expected.length<32 || supplied==null || !MessageDigest.isEqual(expected,supplied.getBytes(StandardCharsets.UTF_8))) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        if(orderId<=0 || request.monto()==null || request.monto().signum()<0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Payment payment=payments.findByOrderId(orderId).orElse(null);
        if(payment==null) {
            payment=new Payment(); payment.setOrderId(orderId); payment.setMonto(request.monto().doubleValue());
            payment.setMetodoPago("SIMULADO"); payment.setEstado("APROBADO"); payment.setTransactionId(UUID.randomUUID().toString());
            try { payment=payments.saveAndFlush(payment); }
            catch(DataIntegrityViolationException e) { payment=payments.findByOrderId(orderId).orElseThrow(() -> e); }
        }
        if(BigDecimal.valueOf(payment.getMonto()).compareTo(request.monto())!=0) throw new ResponseStatusException(HttpStatus.CONFLICT,"Monto distinto para esta orden");
        return new Response(payment.getId(),payment.getOrderId(),BigDecimal.valueOf(payment.getMonto()),payment.getEstado());
    }
}
