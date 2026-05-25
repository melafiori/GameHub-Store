package com.gamehub.shipping.services;

import com.gamehub.shipping.exceptions.ShippingException;
import com.gamehub.shipping.models.Shipping;
import com.gamehub.shipping.repositories.ShippingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingServiceImpl implements ShippingService{
    private final ShippingRepository shippingRepository;

    @Transactional
    @Override
    public Shipping save(Shipping shipping) {

        if(shipping.getTracking() != null &&
                shippingRepository.findByTracking(
                        shipping.getTracking()).isPresent()) {

            throw new ShippingException(
                    "El tracking ya existe");
        }

        shipping.setEstado("PREPARANDO");
        shipping.setFechaEnvio(LocalDateTime.now());

        return shippingRepository.save(shipping);
    }

    @Transactional
    @Override
    public Shipping updateById(Long id, Shipping shipping) {

        Shipping currentShipping = this.findById(id);

        if(shipping.getTracking() != null &&
                !shipping.getTracking().equals(
                        currentShipping.getTracking())) {

            if(shippingRepository.findByTracking(
                    shipping.getTracking()).isPresent()) {
                throw new ShippingException("El tracking ya existe");
            }

            currentShipping.setTracking(
                    shipping.getTracking());
        }

        if(shipping.getEstado().equals("ENTREGADO") && shipping.getFechaEntrega() == null) {
            throw new ShippingException("No se puede entregar sin fecha de entrega");
        }

        currentShipping.setEstado(shipping.getEstado());
        currentShipping.setFechaEntrega(
                shipping.getFechaEntrega());

        return shippingRepository.save(currentShipping);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Shipping shipping = this.findById(id);
        shipping.setEstado("CANCELADO");
        shippingRepository.save(shipping);
    }

    @Transactional
    @Override
    public Shipping findById(Long id) {
        return shippingRepository.findById(id).orElseThrow(
                () -> new ShippingException("Despacho no encontrado"));
    }

    @Transactional
    @Override
    public List<Shipping> findAll() {
        return shippingRepository.findAll();
    }

    @Transactional
    @Override
    public List<Shipping> findByOrderId(Long orderId) {
        return shippingRepository.findByOrderId(orderId);
    }

    @Transactional
    @Override
    public List<Shipping> findByEstado(String estado) {
        return shippingRepository.findByEstado(estado);
    }
}
