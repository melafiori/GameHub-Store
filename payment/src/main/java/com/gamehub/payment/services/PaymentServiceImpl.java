package com.gamehub.payment.services;

import com.gamehub.payment.exceptions.PaymentException;
import com.gamehub.payment.models.Pago;
import com.gamehub.payment.repositories.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    @Override
    public Pago save(Pago pago) {
        boolean pagoExistente = paymentRepository.existsByOrderIdAndEstado(pago.getOrdenId(), "APROBADO");
        if (pagoExistente) {
                throw new PaymentException("La orden ya tiene un pago aprobado.");
        }
        if(pago.getCodigoTransaction() != null && paymentRepository.findByCodigoTransaction(pago.getCodigoTransaction()).isPresent()) {
            throw new PaymentException("Código de transacción duplicado.");
        }

        pago.setEstado("PENDIENTE");
        pago.setFecha(LocalDateTime.now());
        return paymentRepository.save(pago);
    }

    @Transactional
    @Override
    public Pago updateById(Long id, Pago pago) {
        Pago currentPago = this.findById(id);
        currentPago.setEstado(pago.getEstado());
        if(pago.getCodigoTransaction() != null && !pago.getCodigoTransaction().equals(currentPago.getCodigoTransaction())) {
            if(paymentRepository.findByCodigoTransaction(pago.getCodigoTransaction()).isPresent()) {

                throw new PaymentException("Código de transacción duplicado.");
            }

            currentPago.setCodigoTransaction(pago.getCodigoTransaction());
        }
        return paymentRepository.save(currentPago);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Pago pago = this.findById(id);
        pago.setEstado("ANULADO");
        paymentRepository.save(pago);
    }

    @Transactional
    @Override
    public Pago findById(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new PaymentException("No existe el pago con el id: " + id));
    }

    @Transactional
    @Override
    public List<Pago> findAll() {
        return paymentRepository.findAll();
    }

    @Transactional
    @Override
    public List<Pago> findByOrderId(Long ordenId) {
        return paymentRepository.findByOrderId(ordenId);
    }

    @Transactional
    @Override
    public List<Pago> findByUserId(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Transactional
    @Override
    public List<Pago> findByEstado(String estado) {
        return paymentRepository.findByEstado(estado);
    }

}
