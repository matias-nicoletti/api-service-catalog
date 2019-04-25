package com.matiasnicoletti.apiservicecatalog.controller;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.matiasnicoletti.apiservicecatalog.model.Api;
import com.matiasnicoletti.apiservicecatalog.service.ApiServiceCatalogService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import javax.annotation.Resource;

/**
 * The type Api service catalog controller.
 */
@RestController
@RequestMapping(value = "/service-catalog")
public class ApiServiceCatalogController {

  @Resource(name = "serviceCatalog")
  private ApiServiceCatalogService serviceCatalogService;

  /**
   * Gets all swagger urls.
   *
   * @return the all swagger urls
   */
  @ApiOperation(value = "Retorna la lista de apis registradas en eureka",
      tags = {"Api Service Catalog"})
  @ResponseBody
  @RequestMapping(value = "/services", method = RequestMethod.GET)
  public List<Api> getAllSwaggerUrls() {
    return serviceCatalogService.getApis();
  }

  /**
   * Gets service info by name.
   *
   * @param name the name
   * @return the service info by name
   */
  @ApiOperation(value = "Retorna la informacion basica de un api", tags = {"Api Service Catalog"})
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "Nombre del microservicio",
          dataType = "string", paramType = "path", required = true)})
  @ResponseBody
  @RequestMapping(value = "/services/{name}", method = RequestMethod.GET)
  public Api getServiceInfoByName(@PathVariable String name) {
    return serviceCatalogService.getApisByName(name);
  }

  /**
   * Async update swaggers.
   */
  @ApiOperation(value = "Actualiza de forma asincrona los swaggers de cada microservicio",
      tags = {"Api Service Catalog"})
  @ResponseBody
  @RequestMapping(value = "/services/swaggers", method = RequestMethod.PUT)
  public void asyncUpdateSwaggers() {
    serviceCatalogService.asyncUpdateSwaggerServices();
  }

  /**
   * Update swagger.
   */
  @ApiOperation(value = "Actualiza los swaggers de cada microservicio",
      tags = {"Api Service Catalog"})
  @ResponseBody
  @RequestMapping(value = "/services/swaggers", method = RequestMethod.POST)
  public void updateSwagger() {
    serviceCatalogService.updateSwaggerServices();
  }

  /**
   * Update swagger by service.
   *
   * @param name the name
   */
  @ApiOperation(value = "Actualiza el swagger de un microservicio",
      tags = {"Api Service Catalog"})
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "Nombre del microservicio",
          dataType = "string", paramType = "path", required = true)})
  @ResponseBody
  @RequestMapping(value = "/services/swaggers/{name}", method = RequestMethod.POST)
  public void updateSwaggerByService(@PathVariable String name) {
    serviceCatalogService.updateSwaggerByService(name);
  }

  /**
   * Retrieve swagger by service string.
   *
   * @param name the name
   * @return the string
   */
  @ApiOperation(value = "Retorna el swagger.json del api", tags = {"Api Service Catalog"})
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "Nombre del microservicio",
          dataType = "string", paramType = "path", required = true)})
  @ResponseBody
  @RequestMapping(value = "/services/swaggers/{name}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @JsonRawValue
  public String retrieveSwaggerByService(@PathVariable String name) {
    return serviceCatalogService.getSwaggerByName(name);
  }

  /**
   * Gets services endpoints.
   *
   * @param status the status
   * @return the services endpoints
   */
  @ApiOperation(value = "Retorna la lista de endpoints de todas las apis",
      tags = {"Api Service Catalog"})
  @ResponseBody
  @RequestMapping(value = "/services/endpoints", method = RequestMethod.GET)
  public List<Api> getServicesEndpoints(@RequestParam(required = false) String status) {
    return serviceCatalogService.getPaths(status);
  }

  /**
   * Gets service endpoints.
   *
   * @param name the name
   * @return the service endpoints
   */
  @ApiOperation(value = "Retorna la lista de endpoints de un api", tags = {"Api Service Catalog"})
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "Nombre del microservicio",
          dataType = "string", paramType = "path", required = true)})
  @ResponseBody
  @RequestMapping(value = "/services/endpoints/{name}", method = RequestMethod.GET)
  public Api getServiceEndpoints(@PathVariable String name) {
    return serviceCatalogService.getPathsByService(name);
  }


  /**
   * Gets deprecated endpoints.
   *
   * @param searchText the search text
   * @return the deprecated endpoints
   */
  @ApiOperation(value = "Retorna la lista de endpoints que hacen match con searchText",
      tags = {"Api Service Catalog"})
  @ApiImplicitParams({
      @ApiImplicitParam(name = "searchText", value = "Texto con el que se realizara la búsqueda",
          dataType = "string", paramType = "query", required = false)})
  @ResponseBody
  @RequestMapping(value = "/services/endpoints/search", method = RequestMethod.GET)
  public List<Api> getDeprecatedEndpoints(@RequestParam String searchText) {
    return serviceCatalogService.searchEndpointsByText(searchText);
  }

}
