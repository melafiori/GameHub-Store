package com.gamehub.order.services;

import com.gamehub.order.clients.InventoryClient;
import com.gamehub.order.clients.ProductClient;
import com.gamehub.order.exceptions.OrderException;
import com.gamehub.order.models.OrderDetalle;
import com.gamehub.order.models.dtos.OrderDTO;
import com.gamehub.order.models.dtos.OrderRequestDTO;
import com.gamehub.order.repositories.OrderRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Faker faker;
    private OrderRequestDTO orderRequestDto;
    private OrderDTO orderDto;
    private Map<String, Object> productoMap;
    private Double precioProducto;
    private Long productId;
    private String userEmail;

    @BeforeEach
    public void setUp() {
        this.faker = new Faker();

        this.productId = this.faker.number().randomNumber(5, true);
        this.userEmail = this.faker.internet().emailAddress();
        this.precioProducto = this.faker.number().randomDouble(2, 1000, 500000);

        this.orderDto = new OrderDTO();
        this.orderDto.setProductId(this.productId);
        this.orderDto.setCantidad(this.faker.number().numberBetween(1, 5));

        this.orderRequestDto = new OrderRequestDTO();
        this.orderRequestDto.setUserEmail(this.userEmail);
        this.orderRequestDto.setItems(List.of(this.orderDto));

        // Simula el Map<String,Object> que devuelve product-service como body genérico
        this.productoMap = Map.of(
                "productId", this.productId,
                "nombre", this.faker.commerce().productName(),
                "precio", this.precioProducto
        );
    }

    /** Verifica que se crea una orden exitosamente cuando hay stock y el producto existe. */
    @Test
    @DisplayName("Debe crear una orden cuando hay stock disponible y el producto existe")
    public void shouldCrearOrden() {
        when(this.inventoryClient.checkStock(this.productId, this.orderDto.getCantidad())).thenReturn(true);
        when(this.productClient.getProductById(this.productId))
                .thenReturn(ResponseEntity.ok(this.productoMap));
        when(this.orderRepository.save(any(OrderDetalle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDetalle result = this.orderService.crearOrden(this.orderRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo("PROCESADA");
        assertThat(result.getUserEmail()).isEqualTo(this.userEmail);
        assertThat(result.getTotal()).isEqualTo(this.precioProducto * this.orderDto.getCantidad());

        verify(inventoryClient, times(1)).checkStock(this.productId, this.orderDto.getCantidad());
        verify(productClient, times(1)).getProductById(this.productId);
        verify(inventoryClient, times(1)).reduceStock(this.productId, this.orderDto.getCantidad());
        verify(orderRepository, times(1)).save(any(OrderDetalle.class));
    }

    /** Verifica que se lanza excepcion cuando no hay stock suficiente para un producto. */
    @Test
    @DisplayName("Debe lanzar excepcion cuando no hay stock suficiente")
    public void shouldNotCrearOrdenWhenStockInsuficiente() {
        when(this.inventoryClient.checkStock(this.productId, this.orderDto.getCantidad())).thenReturn(false);

        assertThatThrownBy(() -> this.orderService.crearOrden(this.orderRequestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock insuficiente para el producto con ID: " + this.productId);

        verify(productClient, never()).getProductById(anyLong());
        verify(inventoryClient, never()).reduceStock(anyLong(), any());
        verify(orderRepository, never()).save(any(OrderDetalle.class));
    }

    /** Verifica que se lanza excepcion cuando el producto asociado no existe en el catálogo. */
    @Test
    @DisplayName("Debe lanzar excepcion cuando el producto no existe")
    public void shouldNotCrearOrdenWhenProductNotExists() {
        when(this.inventoryClient.checkStock(this.productId, this.orderDto.getCantidad())).thenReturn(true);
        when(this.productClient.getProductById(this.productId))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        assertThatThrownBy(() -> this.orderService.crearOrden(this.orderRequestDto))
                .isInstanceOf(RuntimeException.class);

        verify(inventoryClient, never()).reduceStock(anyLong(), any());
        verify(orderRepository, never()).save(any(OrderDetalle.class));
    }

    /** Verifica que se retorna el historial de ordenes de un usuario por su email. */
    @Test
    @DisplayName("Debe retornar el historial de ordenes por email")
    public void shouldObtenerHistorialPorEmail() {
        OrderDetalle orden = new OrderDetalle();
        orden.setUserEmail(this.userEmail);
        orden.setEstado("PROCESADA");

        when(this.orderRepository.findByUserEmail(this.userEmail)).thenReturn(List.of(orden));

        List<OrderDetalle> result = this.orderService.obtenerHistorialPorEmail(this.userEmail);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserEmail()).isEqualTo(this.userEmail);

        verify(orderRepository, times(1)).findByUserEmail(this.userEmail);
    }

    /** Verifica que se retorna una lista vacia cuando el usuario no tiene ordenes. */
    @Test
    @DisplayName("Debe retornar una lista vacia cuando el usuario no tiene ordenes")
    public void shouldReturnEmptyListWhenNoOrders() {
        when(this.orderRepository.findByUserEmail(this.userEmail)).thenReturn(List.of());

        List<OrderDetalle> result = this.orderService.obtenerHistorialPorEmail(this.userEmail);

        assertThat(result).isEmpty();
        verify(orderRepository, times(1)).findByUserEmail(this.userEmail);
    }
}