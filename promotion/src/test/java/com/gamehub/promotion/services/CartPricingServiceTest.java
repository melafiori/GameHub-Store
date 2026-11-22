package com.gamehub.promotion.services;
import com.gamehub.promotion.clients.ProductClient;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
class CartPricingServiceTest {
    @Test void subtotalUsesCurrentPricesAndQuantities() {
        ProductClient products = mock(ProductClient.class);
        when(products.find(2L)).thenReturn(Map.of("estado","ACTIVO","precio",999990,"precioVigente",899991));
        when(products.find(3L)).thenReturn(Map.of("estado","ACTIVO","precio",1000,"precioVigente",1000));
        assertThat(new CartPricingService(products).subtotal(Map.of(2L,2,3L,1))).isEqualByComparingTo(new BigDecimal("1800982"));
    }
    @Test void rejectsInvalidQuantitiesAndMissingCurrentPrice() {
        ProductClient products = mock(ProductClient.class);
        var service = new CartPricingService(products);
        assertThatThrownBy(() -> service.subtotal(Map.of(2L,0))).isInstanceOf(IllegalArgumentException.class);
        when(products.find(2L)).thenReturn(Map.of("estado","ACTIVO","precio",999990));
        assertThatThrownBy(() -> service.subtotal(Map.of(2L,1))).isInstanceOf(IllegalArgumentException.class);
    }
}