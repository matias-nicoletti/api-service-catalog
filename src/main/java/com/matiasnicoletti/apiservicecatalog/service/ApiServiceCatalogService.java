package com.matiasnicoletti.apiservicecatalog.service;

import com.matiasnicoletti.apiservicecatalog.model.Api;

import java.util.List;

/**
 * Service of Service Catalog.
 */
public interface ApiServiceCatalogService {

  void updateSwaggerServices();

  List<Api> getPaths(String status);

  List<Api> getApis();

  String getSwaggerByName(String serviceName);

  Api getApisByName(String serviceName);

  Api getPathsByService(String name);

  void updateSwaggerByService(String name);

  void asyncUpdateSwaggerServices();

  List<Api> searchEndpointsByText(String searchText);
}
