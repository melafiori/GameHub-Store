package com.gamehub.order.services;

import com.gamehub.order.exceptions.OrderException;
import com.gamehub.order.models.Order;
import com.gamehub.order.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.List;

@Data
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public Order save(Order order) {

        order.setEstado("PENDIENTE");
        order.setFecha(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Transactional
    @Override
    public Order updateById(Long id, Order order) {

        Order currentOrder = this.findById(id);

        if(currentOrder.getEstado().equals("PAGADA")) {
            throw new OrderException(
                    "No se puede modificar una orden pagada"
            );
        }

        currentOrder.setEstado(order.getEstado());
        currentOrder.setSubtotal(order.getSubtotal());
        currentOrder.setDescuento(order.getDescuento());
        currentOrder.setTotal(order.getTotal());

        return orderRepository.save(currentOrder);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Order order = this.findById(id);

        if(order.getEstado().equals("PAGADA")) {
            throw new OrderException("No se puede cancelar una orden pagada");
        }

        order.setEstado("CANCELADA");
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new OrderException("Orden no encontrada"));
    }

    @Transactional
    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional
    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    @Override
    public List<Order> findByEstado(String estado) {
        return orderRepository.findByEstado(estado);
    }
}
