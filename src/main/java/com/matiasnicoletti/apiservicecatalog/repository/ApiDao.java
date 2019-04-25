package com.matiasnicoletti.apiservicecatalog.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.matiasnicoletti.apiservicecatalog.model.Api;
import com.matiasnicoletti.apiservicecatalog.repository.mapper.ApiMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

/**
 * The type Api dao.
 */
@Repository
public class ApiDao extends NamedParameterJdbcTemplate {

  private static final String QUERY_GET_DEPRECATED_ENDPOINTS =
      "SELECT t1.name,t1.swagger_url,t1.port,t1.version,t1.date_updated dateUpdated,"
          + "json_build_object(t2.key,json_build_object(t3.key,t3.value)) swagger "
          + "FROM apis t1,json_each(swagger ->'paths') t2,json_each(t2.value) t3 "
          + "WHERE t3.value->'deprecated' is not null order by t1.name";

  private static final String QUERY_SEARCH_IN_PARAMETERS_AND_SUMMARY =
      "SELECT t1.name,t1.swagger_url,t1.port,t1.version,t1.date_updated dateUpdated,"
          + "json_build_object(t2.key,json_build_object(t3.key,t3.value)) swagger "
          + "FROM apis t1,json_each(swagger ->'paths') t2,json_each(t2.value) t3,"
          + "json_array_elements(t3.value->'parameters') item "
          + "WHERE (t3.value->>'summary' LIKE :searchText OR t3.value->>'description' LIKE :searchText "
          + "OR item->>'name' LIKE :searchText "
          + "OR item->>'description' LIKE :searchText) order by t1.name";

  /**
   * Instantiates a new Api dao.
   *
   * @param dataSource the data source
   */
  @Autowired
  public ApiDao(DataSource dataSource) {
    super(dataSource);
  }

  /**
   * Gets deprecated endpoints.
   *
   * @return the deprecated endpoints
   */
  public List<Api> getDeprecatedEndpoints() {

    List<Api> queryResult = this.query(QUERY_GET_DEPRECATED_ENDPOINTS, new ApiMapper());
    return buildApiList(queryResult);
  }

  /**
   * Search endpoints by text list.
   *
   * @param searchText the search text
   * @return the list
   */
  public List<Api> searchEndpointsByText(String searchText) {

    SqlParameterSource sqlParameterSource = new MapSqlParameterSource("searchText",
        "%" + searchText + "%");
    List<Api> queryResult = this
        .query(QUERY_SEARCH_IN_PARAMETERS_AND_SUMMARY, sqlParameterSource, new ApiMapper());
    return buildApiList(queryResult);
  }

  private List<Api> buildApiList(List<Api> apiCustoms) {
    List<Api> endpoints = new ArrayList<>();

    Map<String, Api> apps = new HashMap<>();
    ObjectNode paths = JsonNodeFactory.instance.objectNode();
    ObjectNode operationNode = JsonNodeFactory.instance.objectNode();

    for (Api apiCustom : apiCustoms) {

      JsonNode swagger = apiCustom.getSwagger();
      Iterator<Entry<String, JsonNode>> fields = swagger.fields();
      Map.Entry<String, JsonNode> pathNode = fields.next();

      ObjectNode operationNodeLocal = buildOperationNode(pathNode.getValue());
      if (apps.containsKey(apiCustom.getName())) {

        if (paths.has(pathNode.getKey())) {
          operationNode.setAll(operationNodeLocal);
        } else {
          operationNode = JsonNodeFactory.instance.objectNode();
          paths.set(pathNode.getKey(), operationNode.setAll(operationNodeLocal));
        }

      } else {
        paths = JsonNodeFactory.instance.objectNode();
        operationNode = JsonNodeFactory.instance.objectNode();
        paths.set(pathNode.getKey(), operationNode.setAll(operationNodeLocal));//se agrega el path

        Api api = new Api(apiCustom.getName(), apiCustom.getSwaggerUrl(), apiCustom.getPort(),
            apiCustom.getVersion(), apiCustom.getDateUpdated(), paths);
        endpoints.add(api);
        apps.put(apiCustom.getName(), api);
      }
    }
    return endpoints;
  }

  private ObjectNode buildOperationNode(JsonNode jsonNode) {
    ObjectNode operationNode = JsonNodeFactory.instance.objectNode();

    Iterator<Map.Entry<String, JsonNode>> methodIt = jsonNode.fields();
    Map.Entry<String, JsonNode> method = methodIt.next();
    operationNode.set(method.getKey(), method.getValue());

    return operationNode;
  }

}
