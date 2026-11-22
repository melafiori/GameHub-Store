package com.gamehub.promotion.services;
import com.gamehub.promotion.models.Promotion;
import java.util.LinkedHashMap;
import java.util.Map;
public final class PromotionValidation {
 private PromotionValidation() {}
 public static Map<String,String> errors(Promotion p) {
  Map<String,String> e=new LinkedHashMap<>();
  if(p.getCode()==null || p.getCode().isBlank() || p.getCode().trim().length()>100) e.put("code","Ingrese un codigo de hasta 100 caracteres");
  if(!"PORCENTAJE".equalsIgnoreCase(p.getTipo())) e.put("tipo","Solo se admite descuento porcentual");
  if(p.getValor()==null || !Double.isFinite(p.getValor()) || p.getValor()<=0 || p.getValor()>100) e.put("valor","El porcentaje debe ser mayor que 0 y hasta 100");
  if(p.getFechaInicio()==null) e.put("fechaInicio","Ingrese la fecha de inicio");
  if(p.getFechaFin()==null) e.put("fechaFin","Ingrese la fecha de termino");
  else if(p.getFechaInicio()!=null && p.getFechaFin().isBefore(p.getFechaInicio())) e.put("fechaFin","La fecha de termino no puede ser anterior al inicio");
  if(p.getUsosMaximos()==null || p.getUsosMaximos()<1) e.put("usosMaximos","Ingrese al menos un uso");
  if(p.getMontoMinimo()==null || !Double.isFinite(p.getMontoMinimo()) || p.getMontoMinimo()<0) e.put("montoMinimo","El monto minimo no puede ser negativo");
  if(p.getMaximoDescuento()!=null && (!Double.isFinite(p.getMaximoDescuento()) || p.getMaximoDescuento()<0)) e.put("maximoDescuento","El tope no puede ser negativo");
  return e;
 }
}
