package com.banquito.Documentacion.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {
  private static final DateTimeFormatter ISO_NO_TZ = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return b -> {
      JavaTimeModule jtm = new JavaTimeModule();
      jtm.addSerializer(java.time.LocalDateTime.class, new LocalDateTimeSerializer(ISO_NO_TZ));
      jtm.addDeserializer(java.time.LocalDateTime.class, new LocalDateTimeDeserializer(ISO_NO_TZ));
      b.modules(jtm);
      b.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    };
  }
}
