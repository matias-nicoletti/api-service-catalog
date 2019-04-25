package com.matiasnicoletti.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalDate;
import java.time.LocalTime;


@Configuration
public class SwaggerConfig {

  @Value("${project.groupId}")
  private String groupId;
  @Value("${project.version}")
  private String version;
  @Value("${project.name}")
  private String name;
  @Value("${project.description}")
  private String description;

  /**
   * Swagger docket configuration.
   */
  @Bean
  public Docket swagger() {
    return new Docket(DocumentationType.SWAGGER_2).directModelSubstitute(LocalDate.class, java.sql.Date.class)
        .directModelSubstitute(LocalTime.class, String.class).apiInfo(apiInfo()).select()
        .apis(RequestHandlerSelectors.basePackage(groupId)).build();
  }


  private ApiInfo apiInfo() {
    return new ApiInfoBuilder().title(name)
        .description(description).version(version).build();
  }
}
