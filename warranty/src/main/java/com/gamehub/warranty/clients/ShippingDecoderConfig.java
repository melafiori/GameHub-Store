package com.gamehub.warranty.clients;
import feign.codec.Decoder;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.json.JsonMapper;
public class ShippingDecoderConfig {
 @Bean Decoder shippingDecoder() {
  var mapper=JsonMapper.builder().findAndAddModules().build();
  return (response,type) -> {
   if(response.status()==204 || response.body()==null) return null;
   try(var input=response.body().asInputStream()) { return mapper.readValue(input,mapper.constructType(type)); }
  };
 }
}
