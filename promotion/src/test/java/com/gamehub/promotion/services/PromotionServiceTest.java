package com.gamehub.promotion.services;

import com.gamehub.promotion.exceptions.PromotionException;
import com.gamehub.promotion.models.Promotion;
import com.gamehub.promotion.repositories.PromotionRepository;
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
public class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private PromotionServiceImpl promotionService;
    private Promotion promotionPrueba;
    private List<Promotion> promotionList = new ArrayList<>();

    @BeforeEach
    public void setUp() {

        this.promotionPrueba = new Promotion();
        this.promotionPrueba.setPromotionId(1L);
        this.promotionPrueba.setCode("DESC10");
        this.promotionPrueba.setTipo("PORCENTAJE");
        this.promotionPrueba.setValor(10.0);
        this.promotionPrueba.setMontoMinimo(10000.0);
        this.promotionPrueba.setUsosMaximos(100);
        this.promotionPrueba.setUsosActuales(0);
        this.promotionPrueba.setEstado("ACTIVA");
        this.promotionPrueba.setFechaInicio(LocalDateTime.now());
        this.promotionPrueba.setFechaFin(LocalDateTime.now().plusDays(30));

        promotionList.add(promotionPrueba);

        Faker faker = new Faker(Locale.of("es", "CL"));

        for (int i = 0; i < 50; i++) {
            Promotion promotion = new Promotion();
            promotion.setPromotionId((long) (i + 2));
            promotion.setCode(faker.code().ean13());
            promotion.setTipo("PORCENTAJE");
            promotion.setValor(15.0);
            promotion.setMontoMinimo(5000.0);
            promotion.setUsosMaximos(50);
            promotion.setUsosActuales(0);
            promotion.setEstado("ACTIVA");
            promotion.setFechaInicio(LocalDateTime.now());
            promotion.setFechaFin(LocalDateTime.now().plusDays(10));

            promotionList.add(promotion);
        }
    }

    @Test
    @DisplayName("Debe buscar una promoción por id")
    public void shouldFindPromotionById() {
        Long id = 1L;

        when(promotionRepository.findById(id)).thenReturn(Optional.of(promotionPrueba));
        Promotion result = promotionService.findById(id);
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("DESC10");
        verify(promotionRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar una promoción inexistente")
    public void shouldNotFindPromotionById() {
        Long id = 999L;

        when(promotionRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> promotionService.findById(id))
                .isInstanceOf(PromotionException.class)
                .hasMessage("Promoción no encontrada");
        verify(promotionRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe guardar una promoción")
    public void shouldSavePromotion() {
        when(promotionRepository.findByCode(promotionPrueba.getCode())).thenReturn(Optional.empty());
        when(promotionRepository.save(any(Promotion.class))).thenReturn(promotionPrueba);
        Promotion result = promotionService.save(promotionPrueba);
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("DESC10");
        verify(promotionRepository, times(1)).findByCode(promotionPrueba.getCode());
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el código ya existe")
    public void shouldNotSavePromotionWhenCodeExists() {

        when(promotionRepository.findByCode(promotionPrueba.getCode())).thenReturn(Optional.of(promotionPrueba));
        assertThatThrownBy(() -> promotionService.save(promotionPrueba)).isInstanceOf(PromotionException.class).hasMessage("El código ya existe");
        verify(promotionRepository, never()).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Debe actualizar una promoción existente")
    public void shouldUpdatePromotion() {

        Long id = 1L;

        Promotion cambios = new Promotion();
        cambios.setTipo("MONTO");
        cambios.setValor(5000.0);
        cambios.setMontoMinimo(20000.0);
        cambios.setUsosMaximos(200);
        cambios.setEstado("INACTIVA");
        cambios.setFechaInicio(LocalDateTime.now());
        cambios.setFechaFin(LocalDateTime.now().plusDays(60));

        when(promotionRepository.findById(id)).thenReturn(Optional.of(promotionPrueba));

        when(promotionRepository.save(any(Promotion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Promotion result = promotionService.updateById(id, cambios);
        assertThat(result.getTipo()).isEqualTo("MONTO");
        assertThat(result.getEstado()).isEqualTo("INACTIVA");
        verify(promotionRepository, times(1)).findById(id);
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar una promoción inexistente")
    public void shouldNotUpdatePromotionWhenNotExists() {
        Long id = 999L;

        when(promotionRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> promotionService.updateById(id, promotionPrueba)).isInstanceOf(PromotionException.class).hasMessage("Promoción no encontrada");
        verify(promotionRepository, never()).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Debe eliminar lógicamente una promoción")
    public void shouldDeletePromotion() {

        Long id = 1L;

        when(promotionRepository.findById(id)).thenReturn(Optional.of(promotionPrueba));
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        promotionService.deleteById(id);
        verify(promotionRepository, times(1)).findById(id);
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Debe buscar una promoción por código")
    public void shouldFindPromotionByCode() {

        String code = "DESC10";
        when(promotionRepository.findByCode(code)).thenReturn(Optional.of(promotionPrueba));
        Promotion result = promotionService.findByCode(code);
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(code);
        verify(promotionRepository, times(1)).findByCode(code);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar un código inexistente")
    public void shouldNotFindPromotionByCode() {

        String code = "NOEXISTE";
        when(promotionRepository.findByCode(code)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> promotionService.findByCode(code)).isInstanceOf(PromotionException.class).hasMessage("Código no encontrado");
        verify(promotionRepository, times(1)).findByCode(code);
    }
}