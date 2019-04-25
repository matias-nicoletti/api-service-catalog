package com.matiasnicoletti.apiservicecatalog.service;


import com.matiasnicoletti.apiservicecatalog.facade.ApiServiceCatalogFacade;
import com.matiasnicoletti.apiservicecatalog.model.Api;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.matiasnicoletti.apiservicecatalog.repository.ApiDao;
import com.matiasnicoletti.apiservicecatalog.repository.ApiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ServiceCatalog implements ApiServiceCatalogService {

  private static final Logger logger = LoggerFactory.getLogger(ServiceCatalog.class);
  public static final String STATUS_DEPRECATED = "DEPRECATED";

  @Autowired
  private ApiServiceCatalogFacade serviceCatalogFacade;
  @Autowired
  private ApiRepository apiRepository;
  @Autowired
  private EurekaClient eurekaClient;
  @Autowired
  private ApiDao apiDao;


  @Override
  public void updateSwaggerServices() {
    serviceCatalogFacade.updateSwaggerServices();
  }

  @Override
  public List<Api> getPaths(String status) {
    if (Objects.nonNull(status) || STATUS_DEPRECATED.equalsIgnoreCase(status)) {
      return apiDao.getDeprecatedEndpoints();
    }
    return apiRepository.findByPaths();
  }

  @Override
  public List<Api> getApis() {
    return apiRepository.findAllApis();
  }

  @Override
  public String getSwaggerByName(String name) {
    Api one = apiRepository.findOne(name);
    if (Objects.nonNull(one)) {
      return one.getSwagger().toString();
    }

    return null;
  }

  @Override
  public Api getApisByName(String serviceName) {
    return apiRepository.findOneByName(serviceName);
  }

  @Override
  public Api getPathsByService(String name) {
    return apiRepository.findPathsById(name);
  }

  @Override
  public void updateSwaggerByService(String name) {
    serviceCatalogFacade.updateSwaggerByService(name);
  }

  @Override
  @Scheduled(cron = "${schedule.cron:0 0 0/3 * * *}")
  public void asyncUpdateSwaggerServices() {
    Applications applications = eurekaClient.getApplications();
    List<Application> registeredApplications = applications.getRegisteredApplications();
    serviceCatalogFacade.cleanApis(registeredApplications.stream()
        .map(Application::getName).collect(Collectors.toList()));
    logger.debug("registeredApplications size {}", registeredApplications.size());
    registeredApplications.forEach(app -> {
      List<InstanceInfo> instances = app.getInstances();
      if (!instances.isEmpty()) {
        InstanceInfo instanceInfo = instances.get(0);
        serviceCatalogFacade.asyncUpdateSwaggerServices(app.getName(), instanceInfo);
      }
    });
  }



  @Override
  public List<Api> searchEndpointsByText(String searchText) {
    return apiDao.searchEndpointsByText(searchText);
  }

}
