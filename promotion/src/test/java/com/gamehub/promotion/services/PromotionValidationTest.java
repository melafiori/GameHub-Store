package com.gamehub.promotion.services;
import com.gamehub.promotion.models.Promotion;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;
class PromotionValidationTest {
 @Test void rejectsEndBeforeStartAndInvalidValues() {
  Promotion p=new Promotion();p.setCode("TEST");p.setTipo("PORCENTAJE");p.setValor(10.0);p.setMontoMinimo(0.0);p.setUsosMaximos(2);p.setFechaInicio(LocalDateTime.of(2026,1,2,0,0));p.setFechaFin(LocalDateTime.of(2026,1,1,0,0));
  assertThat(PromotionValidation.errors(p)).containsKey("fechaFin");p.setFechaFin(p.getFechaInicio());assertThat(PromotionValidation.errors(p)).isEmpty();
  p.setValor(101.0);p.setUsosMaximos(0);assertThat(PromotionValidation.errors(p)).containsKeys("valor","usosMaximos");
 }
}
