package com.gamehub.shipping.services;

import com.gamehub.shipping.exceptions.ShippingException;
import com.gamehub.shipping.models.Shipping;
import com.gamehub.shipping.repositories.ShippingRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShippingServiceTest {

    @Mock
    private ShippingRepository shippingRepository;

    @InjectMocks
    private ShippingServiceImpl shippingService;

    private Shipping shippingPrueba;
    private List<Shipping> shippingList = new ArrayList<>();

    @BeforeEach
    public void setUp() {

        this.shippingPrueba = new Shipping();
        this.shippingPrueba.setShippingId(1L);
        this.shippingPrueba.setOrderId(100L);
        this.shippingPrueba.setUserId(200L);
        this.shippingPrueba.setDireccion("Av. Siempre Viva 123");
        this.shippingPrueba.setTransportista("Chilexpress");
        this.shippingPrueba.setTracking("TRACK123");
        this.shippingPrueba.setEstado("PENDIENTE");
        this.shippingPrueba.setFechaEnvio(LocalDateTime.now());

        shippingList.add(shippingPrueba);

        Faker faker = new Faker(Locale.of("es", "CL"));

        for (int i = 0; i < 20; i++) {
            Shipping shipping = new Shipping();
            shipping.setShippingId((long) (i + 2));
            shipping.setOrderId((long) (1000 + i));
            shipping.setUserId((long) (2000 + i));
            shipping.setDireccion(faker.address().fullAddress());
            shipping.setTransportista("Bluexpress");
            shipping.setTracking("TRACK-" + i);
            shipping.setEstado("PENDIENTE");
            shipping.setFechaEnvio(LocalDateTime.now());

            shippingList.add(shipping);
        }
    }

    @Test
    @DisplayName("Debe buscar un despacho por id")
    public void shouldFindShippingById() {
        Long id = 1L;

        when(shippingRepository.findById(id)).thenReturn(Optional.of(shippingPrueba));
        Shipping result = shippingService.findById(id);
        assertThat(result).isNotNull();
        assertThat(result.getTracking()).isEqualTo("TRACK123");
        verify(shippingRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar un despacho inexistente")
    public void shouldNotFindShippingById() {
        Long id = 999L;

        when(shippingRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> shippingService.findById(id))
                .isInstanceOf(ShippingException.class)
                .hasMessage("Despacho no encontrado");
        verify(shippingRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe guardar un despacho")
    public void shouldSaveShipping() {

        when(shippingRepository.findByTracking("TRACK123")).thenReturn(Optional.empty());
        when(shippingRepository.save(any(Shipping.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Shipping result = shippingService.save(shippingPrueba);
        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo("PENDIENTE");
        verify(shippingRepository, times(1)).findByTracking("TRACK123");
        verify(shippingRepository, times(1)).save(any(Shipping.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el tracking ya existe")
    public void shouldNotSaveShippingWhenTrackingExists() {

        when(shippingRepository.findByTracking("TRACK123")).thenReturn(Optional.of(shippingPrueba));
        assertThatThrownBy(() -> shippingService.save(shippingPrueba)).isInstanceOf(ShippingException.class).hasMessage("El tracking ya existe");
        verify(shippingRepository, times(1)).findByTracking("TRACK123");
        verify(shippingRepository, never()).save(any(Shipping.class));
    }

    @Test
    @DisplayName("Debe actualizar un despacho")
    public void shouldUpdateShipping() {
        Long id = 1L;

        Shipping cambios = new Shipping();
        cambios.setEstado("EN_TRANSITO");
        cambios.setTransportista("Correos Chile");
        cambios.setTracking("TRACK999");

        when(shippingRepository.findById(id)).thenReturn(Optional.of(shippingPrueba));
        when(shippingRepository.findByTracking("TRACK999")).thenReturn(Optional.empty());
        when(shippingRepository.save(any(Shipping.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Shipping result = shippingService.updateById(id, cambios);

        assertThat(result.getEstado()).isEqualTo("EN_TRANSITO");
        assertThat(result.getTransportista()).isEqualTo("Correos Chile");
        assertThat(result.getTracking()).isEqualTo("TRACK999");

        verify(shippingRepository, times(1)).findById(id);
        verify(shippingRepository, times(1)).save(any(Shipping.class));
    }

    @Test
    @DisplayName("Debe eliminar lógicamente un despacho")
    public void shouldDeleteShipping() {
        Long id = 1L;

        when(shippingRepository.findById(id)).thenReturn(Optional.of(shippingPrueba));
        when(shippingRepository.save(any(Shipping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        shippingService.deleteById(id);
        verify(shippingRepository, times(1)).findById(id);
        verify(shippingRepository, times(1)).save(any(Shipping.class));
    }
}