package com.gamehub.warranty.services;

import com.gamehub.warranty.exceptions.WarrantyException;
import com.gamehub.warranty.models.Warranty;
import com.gamehub.warranty.repositories.WarrantyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WarrantyServiceTest {

    @Mock
    private WarrantyRepository warrantyRepository;

    @InjectMocks
    private WarrantyServiceImpl warrantyService;

    private Warranty warrantyPrueba;

    @BeforeEach
    public void setUp() {

        warrantyPrueba = new Warranty();
        warrantyPrueba.setWarrantyId(1L);
        warrantyPrueba.setUserId(1L);
        warrantyPrueba.setOrderId(1L);
        warrantyPrueba.setProductId(1L);
        warrantyPrueba.setMotivo("Producto defectuoso");
        warrantyPrueba.setEstado("PENDIENTE");
        warrantyPrueba.setFechaSolicitud(LocalDateTime.now());
        warrantyPrueba.setResolution("Cambio aprobado");
    }

    @Test
    @DisplayName("Debe guardar una garantía correctamente")
    public void shouldSaveWarranty() {

        when(warrantyRepository.save(any(Warranty.class))).thenReturn(warrantyPrueba);
        Warranty result = warrantyService.save(warrantyPrueba);
        assertThat(result).isNotNull();
        verify(warrantyRepository, times(1)).save(any(Warranty.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el motivo está vacío")
    public void shouldNotSaveWarrantyWithoutReason() {

        warrantyPrueba.setMotivo("");
        assertThatThrownBy(() ->
                warrantyService.save(warrantyPrueba))
                .isInstanceOf(WarrantyException.class)
                .hasMessage("Debe ingresar un motivo");

        verify(warrantyRepository, never()).save(any(Warranty.class));
    }

    @Test
    @DisplayName("Debe buscar garantía por ID")
    public void shouldFindWarrantyById() {
        Long id = 1L;

        when(warrantyRepository.findById(id)).thenReturn(Optional.of(warrantyPrueba));
        Warranty result = warrantyService.findById(id);
        assertThat(result).isNotNull();
        assertThat(result.getMotivo()).isEqualTo("Producto defectuoso");
        verify(warrantyRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción si la garantía no existe")
    public void shouldNotFindWarrantyById() {

        Long id = 999L;

        when(warrantyRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() ->
                warrantyService.findById(id))
                .isInstanceOf(WarrantyException.class)
                .hasMessage("Garantía no encontrada");

        verify(warrantyRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe actualizar una garantía")
    public void shouldUpdateWarranty() {
        Long id = 1L;

        Warranty cambios = new Warranty();
        cambios.setEstado("APROBADA");
        cambios.setResolution("Reemplazo autorizado");

        when(warrantyRepository.findById(id)).thenReturn(Optional.of(warrantyPrueba));
        when(warrantyRepository.save(any(Warranty.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Warranty result = warrantyService.updateById(id, cambios);
        assertThat(result.getEstado()).isEqualTo("APROBADA");
        assertThat(result.getResolution()).isEqualTo("Reemplazo autorizado");
        verify(warrantyRepository, times(1))
                .save(any(Warranty.class));
    }

    @Test
    @DisplayName("Debe cerrar una garantía correctamente")
    public void shouldCloseWarranty() {
        Long id = 1L;

        when(warrantyRepository.findById(id)).thenReturn(Optional.of(warrantyPrueba));
        when(warrantyRepository.save(any(Warranty.class))).thenReturn(warrantyPrueba);
        warrantyService.deleteById(id);
        verify(warrantyRepository, times(1)).save(any(Warranty.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al cerrar una garantía sin resolución")
    public void shouldNotCloseWarrantyWithoutResolution() {
        Long id = 1L;

        warrantyPrueba.setResolution("");
        when(warrantyRepository.findById(id)).thenReturn(Optional.of(warrantyPrueba));
        assertThatThrownBy(() ->
                warrantyService.deleteById(id))
                .isInstanceOf(WarrantyException.class)
                .hasMessage("No se puede cerrar una garantía sin resolución");
        verify(warrantyRepository, never()).save(any(Warranty.class));
    }
}