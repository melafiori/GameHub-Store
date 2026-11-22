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
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;

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

    @Mock
    private com.gamehub.order.clients.PromotionClient promotionClient;

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
        ReflectionTestUtils.setField(orderService, "inventorySecret", "0123456789abcdef0123456789abcdef");
        ReflectionTestUtils.setField(orderService, "promotionSecret", "0123456789abcdef0123456789abcdef");
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

    @Test
    void orderUsesCurrentPriceAndKeepsItOnRetry() {
        when(orderRepository.findByCustomerIdAndIdempotencyKey(7L, "oferta")).thenReturn(Optional.empty());
        when(productClient.getProductById(2L)).thenReturn(ResponseEntity.ok(Map.of("precio", 999990, "precioVigente", 899991, "estado", "ACTIVO")));
        when(orderRepository.saveAndFlush(any(OrderDetalle.class))).thenAnswer(call -> { OrderDetalle order = call.getArgument(0); order.setOrderId(44L); return order; });
        when(inventoryClient.reserve(eq("order-44"), anyString(), anyMap())).thenReturn(Map.of("estado", "RESERVADO"));
        var order = orderService.crearPendiente(7L, "oferta", Map.of(2L, 2));
        assertThat(order.getSubtotal()).isEqualTo(1799982);
        assertThat(order.getItems().get(0).getPrecioUnitario()).isEqualTo(899991);
        when(orderRepository.findByCustomerIdAndIdempotencyKey(7L, "oferta")).thenReturn(Optional.of(order));
        assertThat(orderService.crearPendiente(7L, "oferta", Map.of(2L, 2)).getSubtotal()).isEqualTo(1799982);
        verify(productClient, times(1)).getProductById(2L);
    }

    @Test
    void retryAfterLostResponseReusesPersistedOrderId() {
        when(orderRepository.findByCustomerIdAndIdempotencyKey(7L, "clave-1")).thenReturn(Optional.empty());
        when(productClient.getProductById(10L)).thenReturn(ResponseEntity.ok(Map.of("precio", 1000, "precioVigente", 1000, "estado", "ACTIVO")));
        when(orderRepository.saveAndFlush(any(OrderDetalle.class))).thenAnswer(call -> {
            OrderDetalle order = call.getArgument(0); order.setOrderId(42L); return order;
        });
        when(inventoryClient.reserve(eq("order-42"), anyString(), anyMap()))
                .thenThrow(new RuntimeException("respuesta perdida")).thenReturn(Map.of("estado", "RESERVADO"));
        assertThatThrownBy(() -> orderService.crearPendiente(7L, "clave-1", Map.of(10L, 2)))
                .hasMessage("respuesta perdida");
        var saved = org.mockito.ArgumentCaptor.forClass(OrderDetalle.class);
        verify(orderRepository).saveAndFlush(saved.capture());
        OrderDetalle pending = saved.getValue();
        assertThat(pending.getEstado()).isEqualTo("RESERVA_PENDIENTE");
        when(orderRepository.findByCustomerIdAndIdempotencyKey(7L, "clave-1")).thenReturn(Optional.of(pending));
        assertThatThrownBy(() -> orderService.crearPendiente(7L, "clave-1", Map.of(10L, 3)))
                .isInstanceOf(IllegalStateException.class);
        OrderDetalle recovered = orderService.crearPendiente(7L, "clave-1", Map.of(10L, 2));
        assertThat(recovered.getOrderId()).isEqualTo(42L);
        assertThat(recovered.getEstado()).isEqualTo("PENDIENTE_DE_PAGO");
        assertThat(recovered.getTotal()).isEqualTo(2000);
        verify(inventoryClient, times(2)).reserve(eq("order-42"), anyString(), eq(Map.of(10L, 2)));
    }

    @Test void stockFailureDoesNotConsumeCoupon() {
        when(orderRepository.findByCustomerIdAndIdempotencyKey(7L,"stock-cupons")).thenReturn(Optional.empty());
        when(productClient.getProductById(10L)).thenReturn(ResponseEntity.ok(Map.of("precio",1000,"precioVigente",1000,"estado","ACTIVO")));
        when(orderRepository.saveAndFlush(any(OrderDetalle.class))).thenAnswer(call -> {OrderDetalle order=call.getArgument(0);order.setOrderId(55L);return order;});
        when(inventoryClient.reserve(anyString(),anyString(),anyMap())).thenThrow(mock(feign.FeignException.Conflict.class));
        var order=orderService.crearPendiente(7L,"stock-cupons",Map.of(10L,2),new com.gamehub.order.models.ShippingAddress("R","C","D"),"NEON10");
        assertThat(order.getEstado()).isEqualTo("RECHAZADA");
        verifyNoInteractions(promotionClient);
    }

    @Test
    void addressIsSnapshottedAndChangedRetryConflicts() {
        var address = new com.gamehub.order.models.ShippingAddress("Metropolitana", "Santiago", "Calle 123");
        when(orderRepository.findByCustomerIdAndIdempotencyKey(7L, "direccion"))
            .thenReturn(Optional.empty());
        when(productClient.getProductById(10L)).thenReturn(ResponseEntity.ok(Map.of("precio", 1000, "precioVigente", 1000, "estado", "ACTIVO")));
        when(orderRepository.saveAndFlush(any(OrderDetalle.class))).thenAnswer(call -> {
            OrderDetalle order = call.getArgument(0); order.setOrderId(42L); return order;
        });
        when(inventoryClient.reserve(eq("order-42"), anyString(), anyMap())).thenReturn(Map.of("estado", "RESERVADO"));
        OrderDetalle order = orderService.crearPendiente(7L, "direccion", Map.of(10L, 1), address);
        address.setDireccion("otra direccion");
        assertThat(order.getDireccionDespacho().getDireccion()).isEqualTo("Calle 123");
        when(orderRepository.findByCustomerIdAndIdempotencyKey(7L, "direccion")).thenReturn(Optional.of(order));
        var original = new com.gamehub.order.models.ShippingAddress("Metropolitana", "Santiago", "Calle 123");
        assertThat(orderService.crearPendiente(7L, "direccion", Map.of(10L, 1), original).getOrderId()).isEqualTo(42L);
        assertThatThrownBy(() -> orderService.crearPendiente(7L, "direccion", Map.of(10L, 1), address)).isInstanceOf(IllegalStateException.class);
        verify(inventoryClient, times(1)).reserve(anyString(), anyString(), anyMap());
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
