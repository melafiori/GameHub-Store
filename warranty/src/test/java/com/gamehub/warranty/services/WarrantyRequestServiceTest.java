package com.gamehub.warranty.services;
import com.gamehub.warranty.clients.OrderClient;
import com.gamehub.warranty.clients.ShippingClient;
import com.gamehub.warranty.repositories.WarrantyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
class WarrantyRequestServiceTest {
 private final OrderClient orders=mock(OrderClient.class); private final ShippingClient shippings=mock(ShippingClient.class); private final WarrantyRepository repo=mock(WarrantyRepository.class);
 private final WarrantyRequestService service=new WarrantyRequestService(orders,shippings,repo,180);
 private final Jwt jwt=Jwt.withTokenValue("token").header("alg","HS256").subject("5").claim("role","CLIENTE").build();
 @Test void inclusiveDeadline() { var day=LocalDate.of(2026,1,1); assertThat(WarrantyRequestService.withinPeriod(day,day.plusDays(180),180)).isTrue(); assertThat(WarrantyRequestService.withinPeriod(day,day.plusDays(181),180)).isFalse(); assertThat(WarrantyRequestService.withinPeriod(day,day.minusDays(1),180)).isFalse(); }
 @Test void requiresPaidOwnedProductReasonAndDelivery() {
  assertThatThrownBy(() -> service.create(jwt,1L,2L," ")).isInstanceOf(ResponseStatusException.class).hasMessageContaining("400");
  when(orders.myOrders("Bearer token")).thenReturn(List.of(new OrderClient.Order(1L,"PAGADA",List.of(new OrderClient.Item(2L)))));
  assertThatThrownBy(() -> service.create(jwt,9L,2L,"falla")).isInstanceOf(ResponseStatusException.class).hasMessageContaining("404");
  assertThatThrownBy(() -> service.create(jwt,1L,3L,"falla")).isInstanceOf(ResponseStatusException.class).hasMessageContaining("403");
  when(shippings.byOrder(1L,"Bearer token")).thenReturn(new ShippingClient.Shipment(1L,"EN_CAMINO",null),new ShippingClient.Shipment(1L,"ENTREGADO",LocalDateTime.now().minusDays(181)),new ShippingClient.Shipment(1L,"ENTREGADO",LocalDateTime.now()));
  assertThatThrownBy(() -> service.create(jwt,1L,2L,"falla")).isInstanceOf(ResponseStatusException.class).hasMessageContaining("409");
  assertThatThrownBy(() -> service.create(jwt,1L,2L,"falla")).isInstanceOf(ResponseStatusException.class).hasMessageContaining("409");
  when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
  var result=service.create(jwt,1L,2L," falla "); assertThat(result.getUserId()).isEqualTo(5L); assertThat(result.getEstado()).isEqualTo("PENDIENTE"); assertThat(result.getMotivo()).isEqualTo("falla");
 }
 @Test void eligibilityRequiresOwnedPaidDeliveredOrderWithinPeriod() {
  when(orders.myOrders("Bearer token")).thenReturn(List.of(new OrderClient.Order(1L,"PAGADA",List.of(new OrderClient.Item(2L)))));
  assertThatThrownBy(() -> service.eligibility(jwt,9L)).isInstanceOf(ResponseStatusException.class).hasMessageContaining("404");
  when(shippings.byOrder(1L,"Bearer token")).thenReturn(null,new ShippingClient.Shipment(1L,"ENTREGADO",LocalDateTime.now().minusDays(181)),new ShippingClient.Shipment(1L,"ENTREGADO",LocalDateTime.now()));
  assertThat(service.eligibility(jwt,1L).elegible()).isFalse();
  assertThat(service.eligibility(jwt,1L).motivo()).contains("plazo");
  assertThat(service.eligibility(jwt,1L).elegible()).isTrue();
 }

}
