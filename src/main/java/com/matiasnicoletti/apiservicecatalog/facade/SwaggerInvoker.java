package com.matiasnicoletti.apiservicecatalog.facade;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.matiasnicoletti.apiservicecatalog.model.ApiInfo;
import com.netflix.appinfo.InstanceInfo;

import com.matiasnicoletti.ServiceNames;
import io.swagger.models.Contact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Repository
public class SwaggerInvoker {

  private static final Logger logger = LoggerFactory.getLogger(SwaggerInvoker.class);

  @Autowired
  private RestTemplate restTemplate;

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String ownerFieldFormat = "%s. Pod: %s";

  static {
    mapper.setSerializationInclusion(Include.NON_NULL);
  }

  /**
   * Retorna el json de swagger
   *
   * @param instanceInfo
   *          info de la instancia a consultar.
   * @return JsonNode.
   */
  public ResponseEntity<JsonNode> retrieveSwaggerJson(InstanceInfo instanceInfo) {
    try {

      ResponseEntity<JsonNode> toReturn = restTemplate.exchange(
          buildUrlFromInstanceInfo(instanceInfo, ServiceNames.URL_SWAGGER_API), HttpMethod.GET, null, JsonNode.class);

      if (toReturn.getBody().has("info")) {
        ResponseEntity<ApiInfo> apiInfo = restTemplate.exchange(
            buildUrlFromInstanceInfo(instanceInfo, ServiceNames.URL_SERVICE_INFO), HttpMethod.GET, null, ApiInfo.class);

        if (apiInfo.getBody() != null && apiInfo.getBody().allNotNull()) {
          Contact contact = new Contact();
          contact.setName(String.format(ownerFieldFormat, apiInfo.getBody().getOwner(), apiInfo.getBody().getPod()));

          ((ObjectNode) toReturn.getBody().get("info")).replace("contact",
              mapper.convertValue(contact, JsonNode.class));
        }

      }

      return toReturn;

    } catch (HttpClientErrorException | HttpServerErrorException rce) {
      logger.warn("action=\"retrieveSwaggerJson\" message=\"Error al recuperar swagger.json {}\" errorCode=\"{}\"",
          rce.getMessage(), rce.getStatusCode(), rce);
      ObjectNode objectNode = JsonNodeFactory.instance.objectNode();

      return new ResponseEntity<>(objectNode, rce.getResponseHeaders(), rce.getStatusCode());
    } catch (Exception exception) {
      logger.warn("action=\"retrieveSwaggerJson\" message=\"Error inesperado\" errorCode=\"{}\"",
          HttpStatus.INTERNAL_SERVER_ERROR, exception);
      ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
      return new ResponseEntity<>(objectNode, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Construye la url de swagger.
   *
   * @param instanceInfo
   *          instancia del servicio.
   * @return url del json swagger.
   */
  private String buildUrlFromInstanceInfo(InstanceInfo instanceInfo, String uri) {
    return UriComponentsBuilder.fromPath(uri).host(instanceInfo.getVIPAddress()).port(instanceInfo.getPort()).build()
        .toString();
  }
}
