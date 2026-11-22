package com.gamehub.category.controllers;
import com.gamehub.category.clients.ProductCatalogClient;
import com.gamehub.category.models.Category;
import com.gamehub.category.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CategoryDeactivationTest {
 @Mock ProductCatalogClient products; @Mock CategoryService service; @InjectMocks CategoryController controller;
 @Test void associatedProductBlocksDeactivation() {
  when(products.products()).thenReturn(List.of(new ProductCatalogClient.Item(new ProductCatalogClient.CategoryRef(3L))));
  assertThatThrownBy(()->controller.delete(3L)).isInstanceOf(ResponseStatusException.class).hasMessageContaining("409");
  verify(service,never()).updateById(any(),any());
 }
 @Test void emptyCategoryIsDisabled() {
  var c=new Category(); c.setEstado("ACTIVO"); when(products.products()).thenReturn(List.of()); when(service.findById(4L)).thenReturn(c);
  assertThat(controller.delete(4L).getStatusCode().value()).isEqualTo(204); assertThat(c.getEstado()).isEqualTo("INACTIVO"); verify(service).updateById(4L,c);
 }
}
