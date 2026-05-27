package com.gamehub.order.services;

import com.gamehub.order.clients.InventoryClient;
import com.gamehub.order.clients.ProductClient;
import com.gamehub.order.exceptions.OrderException;
import com.gamehub.order.models.Order;
import com.gamehub.order.models.OrderDetalle;
import com.gamehub.order.models.dtos.OrderDTO;
import com.gamehub.order.models.dtos.OrderRequestDTO;
import com.gamehub.order.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service

public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryClient inventoryClient;

    @Autowired
    private ProductClient productClient;



    @Override
    @Transactional // Asegura que si falla el descuento de stock de un producto, se cancele toda la compra
    public OrderDetalle crearOrden(OrderRequestDTO orderRequestDto) {
        OrderDetalle orden = new OrderDetalle();
        orden.setUserEmail(orderRequestDto.getUserEmail());
        orden.setEstado("PENDIENTE");

        double totalOrden = 0.0;
        List<Order> listaItems = new ArrayList<>();

        // 1. Validar disponibilidad de TODO el carrito antes de procesar o descontar nada
        for (OrderDTO itemDto : orderRequestDto.getItems()) {
            boolean tieneStock = inventoryClient.checkStock(itemDto.getProductId(), itemDto.getCantidad());
            if (!tieneStock) {
                throw new RuntimeException("Stock insuficiente para el producto con ID: " + itemDto.getProductId());
            }
        }

        // 2. Si todo tiene stock, construimos los detalles de la orden y calculamos precios
        for (OrderDTO itemDto : orderRequestDto.getItems()) {
            // Consultamos al catálogo de productos su precio actual
            double precioUnitario = 0.0;
            try {
                ResponseEntity<Object> response = productClient.getProductById(itemDto.getProductId());
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    // Mapeo dinámico del precio desde el objeto genérico retornado por product-service
                    Map<String, Object> producto = (Map<String, Object>) response.getBody();
                    precioUnitario = ((Number) producto.get("precio")).doubleValue();
                } else {
                    throw new RuntimeException("El producto no existe en el catálogo maestro.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Error al conectar con el catálogo de productos: " + e.getMessage());
            }

            Order item = new Order();
            item.setProductId(itemDto.getProductId());
            item.setCantidad(itemDto.getCantidad());
            item.setPrecioUnitario(precioUnitario);
            listaItems.add(item);

            totalOrden += (precioUnitario * itemDto.getCantidad());

            inventoryClient.reduceStock(itemDto.getProductId(), itemDto.getCantidad());
        }

        // 4. Consolidamos la cabecera de la orden
        orden.setItems(listaItems);
        orden.setTotal(totalOrden);
        orden.setEstado("PROCESADA");

        return orderRepository.save(orden);
    }

    @Override
    public List<OrderDetalle> obtenerHistorialPorEmail(String email) {
        return orderRepository.findByUserEmail(email);
    }
}
