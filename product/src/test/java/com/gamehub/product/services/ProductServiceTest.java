package com.gamehub.product.services;


import com.gamehub.product.clients.CategoryClient;
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

import static org.assertj.core.api.Assertions.assertThat;
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


}
