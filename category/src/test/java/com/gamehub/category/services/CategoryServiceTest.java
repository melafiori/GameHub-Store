package com.gamehub.category.services;

import com.gamehub.category.exceptions.CategoryException;
import com.gamehub.category.models.Category;
import com.gamehub.category.repositories.CategoryRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import feign.FeignException;
import net.datafaker.Faker;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;


    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category categoryPrueba;
    private List<Category> categoryList = new ArrayList<>();

    /**
     * Preparar categorias de prueba
     */

    @BeforeEach
    public void setUp() {
        this.categoryPrueba = new Category();
        this.categoryPrueba.setCategoryId(1L);
        this.categoryPrueba.setNombre("Categoria de prueba");
        this.categoryPrueba.setEstado("ACTIVO");
        this.categoryPrueba.setDescripcion("Prueba de categoria");
        this.categoryList.add(categoryPrueba);

        Faker faker = new Faker(Locale.of("es", "CL"));
        for (int i = 0; i < 50; i++) {
            Category category = new Category();
            category.setCategoryId((long) (i + 2));
            category.setNombre(faker.lorem().word());
            category.setEstado("ACTIVO");
            category.setDescripcion(faker.lorem().sentence());
            categoryList.add(category);
        }
    }

    @Test
    @DisplayName("Debe buscar una categoria por su id")
    public void shouldFindCategoriaById() {
        Long id = 1L;
        when(this.categoryRepository.findById(id)).thenReturn(Optional.of(this.categoryPrueba));

        Category result = this.categoryService.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo("ACTIVO");
        verify(categoryRepository, times(1)).findById(id);
    }


    /** Verifica que se lanza excepción al buscar un id inexistente. */
    @Test
    @DisplayName("Debe lanzar una excepción al buscar una categoria con id inexistente")
    public void shouldNotFindCategoryById() {
        Long id =999L;
        when(this.categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.categoryService.findById(id))
                .isInstanceOf(CategoryException.class)
                .hasMessage("La categoría con id " + id + " no existe.");
        verify(categoryRepository, times(1)).findById(id);
    }

    /** Verifica la actualización de una categoria existente */
    @Test
    @DisplayName("Debe actualizar una categoria existente")
    public void shouldUpdateCategoria() {
        Long id = 1L;
        Category cambios = new Category();
        cambios.setNombre("Actualizado");
        cambios.setEstado("INACTIVO");
        cambios.setCategoryId(10L);
        cambios.setDescripcion("Categoria actualizada");

        when(this.categoryRepository.findById(id)).thenReturn(Optional.of(this.categoryPrueba));
        when(this.categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = this.categoryService.updateById(id, cambios);

        assertThat(result.getNombre()).isEqualTo("Actualizado");
        assertThat(result.getEstado()).isEqualTo("INACTIVO");
        assertThat(result.getDescripcion()).isEqualTo("Categoria actualizada");
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    /** Verifica que se lanza excepción al actualizar categoria inexistente */
    @Test
    @DisplayName("Debe lanzar una excepción al actualizar una categoría inexistente")
    public void shouldNotUpdateCategoryWhenNotExists() {
        Long id = 9999L;
        when(this.categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> this.categoryService.updateById(id, this.categoryPrueba))
                .isInstanceOf(CategoryException.class)
                .hasMessage("La categoria con id " + this.categoryPrueba.getCategoryId() + " no existe");
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    /** Verifica la eliminación de una categoría por id */
    @Test
    @DisplayName("Debe eliminar una atención por su id")
    public void shouldDeleteCategoriaById() {
        Long id = 1L;

        this.categoryService.deleteById(id);

        verify(categoryRepository, times(1)).deleteById(id);
    }
}
