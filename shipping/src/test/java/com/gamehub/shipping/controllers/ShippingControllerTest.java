package com.gamehub.shipping.controllers;
import com.gamehub.shipping.clients.OrderClient;
import com.gamehub.shipping.models.Shipping;
import com.gamehub.shipping.repositories.ShippingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ShippingControllerTest {
    @Mock ShippingRepository repo;
    @Mock OrderClient orders;
    @Mock Jwt jwt;
    private ShippingController controller(){return new ShippingController(repo,orders,"0123456789abcdef0123456789abcdef");}
    @Test void unpaidCannotBeShipped(){
        when(orders.start(eq(2L),anyString())).thenReturn(new OrderClient.Snapshot(2L,1L,"PENDIENTE_DE_PAGO",new OrderClient.Address("region","comuna","calle")));
        assertThat(controller().create(2L).getStatusCode().value()).isEqualTo(409);
        verify(repo,never()).saveAndFlush(any());
    }
    @Test void repeatedCreationReturnsExistingShipment(){
        Shipping saved=new Shipping();saved.setOrderId(1L);saved.setTracking("GH-existing");
        when(orders.start(eq(1L),anyString())).thenReturn(new OrderClient.Snapshot(1L,1L,"PAGADA",new OrderClient.Address("region","comuna","calle")));
        when(repo.findFirstByOrderId(1L)).thenReturn(Optional.of(saved));
        assertThat(controller().create(1L).getBody().getTracking()).isEqualTo("GH-existing");
        verify(repo,never()).saveAndFlush(any());
    }
    @Test void anotherCustomerCannotReadShipment(){
        Shipping saved=new Shipping();saved.setUserId(1L);
        when(repo.findFirstByOrderId(1L)).thenReturn(Optional.of(saved));
        when(jwt.getClaimAsString("role")).thenReturn("CLIENTE");when(jwt.getSubject()).thenReturn("2");
        assertThatThrownBy(() -> controller().byOrder(1L,jwt)).isInstanceOf(ResponseStatusException.class);
    }
    @Test void deliveryDateIsRecordedOnce() {
        Shipping saved = new Shipping(); saved.setEstado("EN_CAMINO");
        when(repo.findById(1L)).thenReturn(Optional.of(saved));
        when(repo.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        var first=controller().change(1L,new ShippingController.StatusRequest("ENTREGADO"));
        var date=first.getFechaEntrega(); assertThat(date).isNotNull();
        var repeated=controller().change(1L,new ShippingController.StatusRequest("ENTREGADO"));
        assertThat(repeated.getFechaEntrega()).isEqualTo(date);
    }
}
