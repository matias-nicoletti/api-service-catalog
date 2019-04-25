package com.matiasnicoletti.apiservicecatalog.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.matiasnicoletti.apiservicecatalog.model.Api;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;

import com.matiasnicoletti.ServiceNames;
import com.matiasnicoletti.apiservicecatalog.repository.ApiRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Facade for Api Service Catalog.
 */
@Component
public class ApiServiceCatalogFacade {

  private static final Logger logger = LoggerFactory.getLogger(ApiServiceCatalogFacade.class);

  public static final int FIRST_INSTANCE = 0;

  @Autowired
  private SwaggerInvoker swaggerInvoker;
  @Autowired
  private EurekaClient eurekaClient;
  @Autowired
  private ApiRepository apiRepository;

  /**
   * Actualiza los swagger.json y los guarda en la bd.
   */
  public void updateSwaggerServices() {

    Applications applications = eurekaClient.getApplications();
    List<Application> registeredApplications = applications.getRegisteredApplications();

    registeredApplications.forEach(this::processSwaggerByApplication);

    logger.debug("Se actualizaron: {} servicios", apiRepository.count());
  }

  private void processSwaggerByApplication(Application app) {
    List<InstanceInfo> appInstances = app.getInstances();
    if (!appInstances.isEmpty()) {
      InstanceInfo instanceInfo = appInstances.get(FIRST_INSTANCE);
      ResponseEntity<JsonNode> exchange = swaggerInvoker.retrieveSwaggerJson(instanceInfo);
      proccesSwaggerResponse(app.getName(), instanceInfo, exchange);
    }
  }

  private void proccesSwaggerResponse(String appName, InstanceInfo instanceInfo,
                                      ResponseEntity<JsonNode> exchange) {
    if (exchange.getStatusCode().is2xxSuccessful()) {
      apiRepository.deleteByName(appName);
      HttpHeaders headers = exchange.getHeaders();
      headers.get(ServiceNames.PROJECT_VERSION_NAME);
      String swaggerUrlHtml = buildSwaggerUrlHtml(instanceInfo);
      Api api = new Api(appName, swaggerUrlHtml, instanceInfo.getPort(), getVersion(headers),exchange.getBody());
      apiRepository.save(api);
    }
  }

  public void cleanApis(List<String> names) {
    apiRepository.deleteApiByNameNotIn(names);
  }

  private String getVersion(HttpHeaders headers) {
    String version = "UNKNOWN";
    if (headers.containsKey(ServiceNames.PROJECT_VERSION_NAME)) {
      String projectVersion = headers.getFirst(ServiceNames.PROJECT_VERSION_NAME);
      String[] split = projectVersion.split(":");
      if (split.length > 1) {
        version = split[1];
      }
    }
    return version;
  }

  /**
   * Construye la url de swagger.
   *
   * @param instanceInfo instancia del servicio.
   * @return url del swagger ui.
   */
  private String buildSwaggerUrlHtml(InstanceInfo instanceInfo) {

    return UriComponentsBuilder
        .fromHttpUrl(ServiceNames.DEFAULT_PROTOCOL + instanceInfo.getIPAddr())
        .port(instanceInfo.getPort())
        .path(ServiceNames.URL_SWAGGER_UI)
        .build().toString();
  }

  public void updateSwaggerByService(String name) {
    Application application = eurekaClient.getApplication(name);
    processSwaggerByApplication(application);
  }

  /**
   * Actualiza el swagger.json de forma asincrona.
   * @param appName nombre de la applicacion.
   * @param instanceInfo instancia del microservicio.
   */
  @Async("threadPoolSwaggerExecutor")
  public void asyncUpdateSwaggerServices(String appName, InstanceInfo instanceInfo) {
    logger.debug("app: {}, status: {}", appName, instanceInfo.getStatus());
    ResponseEntity<JsonNode> responseEntity = swaggerInvoker.retrieveSwaggerJson(instanceInfo);
    proccesSwaggerResponse(appName, instanceInfo, responseEntity);
  }
}
