package com.matiasnicoletti.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JsonConfig {

  @Value("${swagger.client.readTimeout:2000}")
  private int readTimeout;
  @Value("${swagger.client.connectTimeout:2000}")
  private int connectTimeout;
  
  /**
   * Returns jsonObjectMapper configuration.
   */
  @Bean
  public Jackson2ObjectMapperBuilder objectMapperBuilder() {
    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
    builder.serializationInclusion(JsonInclude.Include.NON_NULL);
    builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
        DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE,
        SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
    return builder;
  }

  /**
   * Configura el rest template con el object mapper customizado.
   * @return RestTemplateCustomizer.
   */
  @Bean
  public RestTemplateCustomizer restTemplateCustomizerTimeOut() {
    return restTemplate -> {

      SimpleClientHttpRequestFactory requestFactory =
          (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
      requestFactory.setReadTimeout(readTimeout);
      requestFactory.setConnectTimeout(connectTimeout);

    };
  }

  @Bean
  public Module javaTimeModule() {
    return new JavaTimeModule();
  }

}
