package com.gamehub.inventory.services;

import com.gamehub.inventory.clients.ProductClient;
import com.gamehub.inventory.exceptions.InventoryException;
import com.gamehub.inventory.models.Inventario;
import com.gamehub.inventory.models.dtos.InventarioResponseDto;
import com.gamehub.inventory.models.dtos.ProductDto;
import com.gamehub.inventory.repositories.InventarioRepository;
import com.github.javafaker.Faker;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
    class InventarioServiceTest {

    @Mock
    private InventarioRepository inventoryRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Faker faker;
    private Inventario inventario;
    private ProductDto productDto;
    private Long productId;
    private Long inventoryId;

    @BeforeEach
    public void setUp() {
        this.faker = new Faker();

        this.productId = this.faker.number().randomNumber(5, true);
        this.inventoryId = this.faker.number().randomNumber(5, true);

        this.inventario = new Inventario();
        this.inventario.setInventoryId(this.inventoryId);
        this.inventario.setProductId(this.productId);
        this.inventario.setStockDisponible(this.faker.number().numberBetween(1, 200));
        this.inventario.setStockReservado(this.faker.number().numberBetween(0, 50));
        this.inventario.setUbicacion(this.faker.address().city());

        this.productDto = new ProductDto();
        this.productDto.setProductId(this.productId);
        this.productDto.setNombre(this.faker.commerce().productName());
        this.productDto.setMarca(this.faker.company().name());
        this.productDto.setModelo(this.faker.bothify("MOD-####"));
        this.productDto.setPrecio(this.faker.number().randomDouble(2, 1000, 500000));
        this.productDto.setEstado("DISPONIBLE");
    }

    /** Verifica que findAll retorna la lista de inventarios con su producto asociado. */
    @Test
    @DisplayName("Debe retornar la lista de inventarios con su producto asociado")
    public void shouldFindAllWithProduct() {
        when(this.inventoryRepository.findAll()).thenReturn(List.of(this.inventario));
        when(this.productClient.getProductById(this.productId)).thenReturn(this.productDto);

        List<InventarioResponseDto> result = this.inventarioService.findAll();

        assertThat(result).hasSize(1);
        InventarioResponseDto dto = result.get(0);
        assertThat(dto.getInventoryId()).isEqualTo(this.inventario.getInventoryId());
        assertThat(dto.getStockDisponible()).isEqualTo(this.inventario.getStockDisponible());
        assertThat(dto.getUbicacion()).isEqualTo(this.inventario.getUbicacion());
        assertThat(dto.getProduct()).isNotNull();
        assertThat(dto.getProduct().getNombre()).isEqualTo(this.productDto.getNombre());

        verify(inventoryRepository, times(1)).findAll();
        verify(productClient, times(1)).getProductById(this.productId);
    }

    /** Verifica que findAll retorna producto null cuando falla la llamada a ProductClient. */
    @Test
    @DisplayName("Debe retornar producto null cuando falla la llamada a ProductClient")
    public void shouldFindAllWithNullProductWhenFeignFails() {
        when(this.inventoryRepository.findAll()).thenReturn(List.of(this.inventario));
        when(this.productClient.getProductById(this.productId)).thenThrow(FeignException.class);

        List<InventarioResponseDto> result = this.inventarioService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProduct()).isNull();

        verify(productClient, times(1)).getProductById(this.productId);
    }

    /** Verifica que findAll retorna una lista vacia cuando no hay registros. */
    @Test
    @DisplayName("Debe retornar una lista vacia cuando no hay registros")
    public void shouldFindAllReturnEmptyListWhenNoRecords() {
        when(this.inventoryRepository.findAll()).thenReturn(List.of());

        List<InventarioResponseDto> result = this.inventarioService.findAll();

        assertThat(result).isEmpty();
        verify(productClient, never()).getProductById(anyLong());
    }

    /** Verifica la busqueda exitosa de un inventario por id. */
    @Test
    @DisplayName("Debe buscar un inventario por id")
    public void shouldFindInventarioById() {
        when(this.inventoryRepository.findById(this.inventoryId)).thenReturn(Optional.of(this.inventario));

        Inventario result = this.inventarioService.getById(this.inventoryId);

        assertThat(result).isNotNull();
        assertThat(result.getInventoryId()).isEqualTo(this.inventoryId);
        verify(inventoryRepository, times(1)).findById(this.inventoryId);
    }

    /** Verifica que se lanza excepcion al buscar un id inexistente. */
    @Test
    @DisplayName("Debe lanzar excepcion al buscar un inventario con id inexistente")
    public void shouldNotFindInventarioById() {
        Long idInexistente = this.faker.number().randomNumber(6, true);
        when(this.inventoryRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.inventarioService.getById(idInexistente))
                .isInstanceOf(InventoryException.class)
                .hasMessage("Stock con id " + idInexistente + " no encontrado.");

        verify(inventoryRepository, times(1)).findById(idInexistente);
    }

    /** Verifica que se guarda un inventario cuando el producto asociado existe. */
    @Test
    @DisplayName("Debe guardar un inventario cuando el producto existe")
    public void shouldSaveInventario() {
        when(this.productClient.getProductById(this.productId)).thenReturn(this.productDto);
        when(this.inventoryRepository.save(this.inventario)).thenReturn(this.inventario);

        Inventario result = this.inventarioService.save(this.inventario);

        assertThat(result).isNotNull();
        assertThat(result.getInventoryId()).isEqualTo(this.inventario.getInventoryId());
        verify(productClient, times(1)).getProductById(this.productId);
        verify(inventoryRepository, times(1)).save(this.inventario);
    }

    /** Verifica que se lanza excepcion al guardar un inventario con producto inexistente. */
    @Test
    @DisplayName("Debe lanzar excepcion al guardar un inventario con producto inexistente")
    public void shouldNotSaveInventarioWhenProductNotExists() {
        when(this.productClient.getProductById(this.productId)).thenThrow(FeignException.class);

        assertThatThrownBy(() -> this.inventarioService.save(this.inventario))
                .isInstanceOf(InventoryException.class)
                .hasMessage("No se puede registrar inventario: El producto asociado no existe.");

        verify(inventoryRepository, never()).save(any(Inventario.class));
    }

    /** Verifica la actualizacion de un inventario existente. */
    @Test
    @DisplayName("Debe actualizar un inventario existente")
    public void shouldUpdateInventario() {
        Inventario cambios = new Inventario();
        cambios.setStockDisponible(100);
        cambios.setStockReservado(10);
        String nuevaUbicacion = this.faker.address().city();
        cambios.setUbicacion(nuevaUbicacion);

        when(this.inventoryRepository.findById(this.inventoryId)).thenReturn(Optional.of(this.inventario));
        when(this.inventoryRepository.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inventario result = this.inventarioService.updateById(this.inventoryId, cambios);

        assertThat(result.getStockDisponible()).isEqualTo(100);
        assertThat(result.getStockReservado()).isEqualTo(10);
        assertThat(result.getUbicacion()).isEqualTo(nuevaUbicacion);
        verify(inventoryRepository, times(1)).findById(this.inventoryId);
        verify(inventoryRepository, times(1)).save(any(Inventario.class));
    }

    /** Verifica que se lanza excepcion al actualizar un inventario inexistente. */
    @Test
    @DisplayName("Debe lanzar excepcion al actualizar un inventario inexistente")
    public void shouldNotUpdateInventarioWhenNotExists() {
        Long idInexistente = this.faker.number().randomNumber(6, true);
        when(this.inventoryRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.inventarioService.updateById(idInexistente, this.inventario))
                .isInstanceOf(InventoryException.class)
                .hasMessage("Stock con id " + idInexistente + " no existente.");

        verify(inventoryRepository, never()).save(any(Inventario.class));
    }

    /** Verifica la eliminacion de un inventario por id. */
    @Test
    @DisplayName("Debe eliminar un inventario por su id")
    public void shouldDeleteInventarioById() {
        doNothing().when(this.inventoryRepository).deleteById(this.inventoryId);

        this.inventarioService.deleteById(this.inventoryId);

        verify(inventoryRepository, times(1)).deleteById(this.inventoryId);
    }
}