package com.matiasnicoletti.apiservicecatalog.repository.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.matiasnicoletti.apiservicecatalog.model.Api;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class ApiMapper implements RowMapper<Api> {

  private static final Logger logger = LoggerFactory.getLogger(ApiMapper.class);

  public static final String COLUMN_NAME = "name";
  public static final String COLUMN_SWAGGER_URL = "swagger_url";
  public static final String COLUMN_PORT = "port";
  public static final String COLUMNS_VERSION = "version";
  public static final String COLUMN_DATE_UPDATED = "dateUpdated";
  public static final String COLUMN_SWAGGER = "swagger";

  private ObjectMapper mapper = new ObjectMapper();

  @Override
  public Api mapRow(ResultSet rs, int index) throws SQLException {
    Api api = new Api(rs.getString(COLUMN_NAME), rs.getString(COLUMN_SWAGGER_URL),
        rs.getInt(COLUMN_PORT), rs.getString(COLUMNS_VERSION), rs.getTimestamp(COLUMN_DATE_UPDATED));

    PGobject pg = (PGobject) rs.getObject(COLUMN_SWAGGER);
    if (Objects.nonNull(pg)) {
      try {
        api.setSwagger(mapper.readValue(pg.getValue(), JsonNode.class));
      } catch (IOException ioe) {
        logger.warn("action=\"mapRow\" message=\"{}\"","Json invalido", ioe);
        api.setSwagger(JsonNodeFactory.instance.objectNode());
      }
    }
    return api;
  }
}
