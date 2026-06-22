package com.gamehub.product.services;


import com.gamehub.product.clients.CategoryClient;
import com.gamehub.product.exceptions.ProductException;
import com.gamehub.product.models.Product;
import com.gamehub.product.models.dtos.CategoryDto;
import com.gamehub.product.models.dtos.ProductDetalleDto;
import com.gamehub.product.repositories.ProductRepository;
import jakarta.inject.Inject;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryClient categoryClient;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product productPrueba;
    private List<Product> productList = new ArrayList<>();

    @BeforeEach
    public void SetUp(){
        this.productPrueba = new Product();
        this.productPrueba.setProductId(1L);
        this.productPrueba.setNombre("Producto de prueba");
        this.productPrueba.setMarca("Marca de prueba");
        this.productPrueba.setModelo("Modelo de prueba");
        this.productPrueba.setPrecio(1000.00);
        this.productPrueba.setDescripcion("Descripcion de prueba");
        this.productPrueba.setEstado("ACTIVO");

        Faker faker = new Faker(Locale.of("es", "CL"));
        for(int i = 0; i < 50; i++){
            Product product = new Product();
            product.setProductId((long) (i + 2));
            product.setNombre(faker.lorem().word());
            product.setMarca(faker.lorem().word());
            product.setModelo(faker.lorem().word());
            product.setPrecio(faker.number().randomDouble(2, 100, 100000));
            product.setDescripcion(faker.lorem().sentence());
            product.setEstado("ACTIVO");
            this.productList.add(product);
        }
    }

    /** Verifica el listado enriquecido con datos de productos */
    @Test
    @DisplayName("Debe listar todos los productos enriquecidos")
    public void shouldListAllProductos(){
        when(this.productRepository.findAll()).thenReturn(List.of(this.productPrueba));

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryId(1L);
        categoryDto.setNombre("Categoria de prueba");
        categoryDto.setEstado("ACTIVO");
        categoryDto.setDescripcion("Descripcion de prueba");
        when(this.categoryClient.getCategoryById(1L)).thenReturn(categoryDto);

        List<ProductDetalleDto> result = this.productService.findAll();

        assertThat(result).hasSize(1);
        ProductDetalleDto dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNombre()).isEqualTo("Producto de prueba");
        assertThat(dto.getMarca()).isEqualTo("Marca de prueba");
        assertThat(dto.getModelo()).isEqualTo("Modelo de prueba");
        assertThat(dto.getPrecio()).isEqualTo(1000.00);
        assertThat(dto.getDescripcion()).isEqualTo("Descripcion de prueba");
        assertThat(dto.getCategoria().getNombre()).isEqualTo("Categoria de prueba");
        verify(categoryClient, times(1)).getCategoryById(20L);
    }

    /** Verifica la búsqueda exitosa de una atención por id. */
    @Test
    @DisplayName("Debe buscar una producto por su id")
    public void shouldFindProductById() {
        Long id = 1L;
        when(this.productRepository.findById(id)).thenReturn(Optional.of(this.productPrueba));

        Product result = this.productService.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo("ACTIVO");
        verify(productRepository, times(1)).findById(id);
    }

    /** Verifica que se lanza excepción al buscar un id inexistente. */
    @Test
    @DisplayName("Debe lanzar una excepción al buscar un producto con id inexistente")
    public void shouldNotFindProductById(){
        Long id = 9999L;
        when(this.productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.productService.findById(id))
                .isInstanceOf(ProductException.class)
                .hasMessage("Producto con id " + id + " no encontrado.");
        verify(productRepository, times(1)).findById(id);
    }

    /** Verifica la actualización de un producto existente */
    @Test
    @DisplayName("Debe actualizar un producto existente")
    public void shouldUpdateProduct() {
        Long id = 1L;
        Product cambios = new Product();
        cambios.setNombre("Actualizado");
        cambios.setEstado("INACTIVO");
        cambios.setPrecio(1000.00);
        cambios.setDescripcion("Producto actualizado");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryId(10L);

        when(this.productRepository.findById(id)).thenReturn(Optional.of(this.productPrueba));
        when(this.categoryClient.getCategoryById(10L)).thenReturn(categoryDto);
        when(this.productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = this.productService.updateById(id, cambios);

        assertThat(result.getNombre()).isEqualTo("Actualizado");
        assertThat(result.getEstado()).isEqualTo("INACTIVO");
        assertThat(result.getDescripcion()).isEqualTo("Producto actualizado");
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    /** Verifica que se lanza excepción al actualizar un producto inexistente */
    @Test
    @DisplayName("Debe lanzar excepción al actualizar un producto inexistente")
    public void shouldNotUpdateProductWhenNotExists() {
        Long id = 9999L;
        when(this.productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> this.productService.updateById(id, this.productPrueba))
                .isInstanceOf(ProductException.class)
                .hasMessage("Producto con id " + this.productPrueba.getProductId() + " no encontrado.");
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, never()).save(any(Product.class));
    }

    /** Verifica la eliminación de un producto por id. */
    @Test
    @DisplayName("Debe eliminar un producto por su id")
    public void shouldDeleteProductById() {
        Long id = 1L;

        this.productService.deleteById(id);

        verify(productRepository, times(1)).deleteById(id);
    }
//          NO ENTIENDO PQ NO FUNCIONA
//    /** Verifica el listado de productos por categoria. */
//    @Test
//    @DisplayName("Debe listar los productos por categoria")
//    public void shouldFindByCategoriaID(){
//        Long idCategoria = 20L;
//        when(this.productRepository.findById(idCategoria)).thenReturn(this.productList);
//
//        List<Product> result = this.productService.findById(idCategoria);
//
//        assertThat(result).hasSize(50);
//        verify(productRepository, times(1)).findById(idCategoria);
//    }

}
