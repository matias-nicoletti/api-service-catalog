package com.matiasnicoletti.apiservicecatalog.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.SerializationException;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class JsonSwaggerUserType implements UserType {

  private static final Logger logger = LoggerFactory.getLogger(JsonSwaggerUserType.class);

  private ObjectMapper mapper = new ObjectMapper();

  @Override
  public int[] sqlTypes() {
    return new int[]{Types.JAVA_OBJECT};
  }

  @Override
  public Class returnedClass() {
    return JsonNode.class;
  }

  @Override
  public boolean equals(Object object, Object object2) {
    return Objects.equals(object, object2);
  }

  @Override
  public int hashCode(Object object) {
    return Objects.hashCode(object);
  }

  @Override
  public Object nullSafeGet(ResultSet resultSet, String[] names,
                            SessionImplementor sessionImplementor, Object object)
      throws SQLException {
    PGobject pg = (PGobject) resultSet.getObject(names[0]);
    if (Objects.nonNull(pg)) {
      try {
        return mapper.readValue(pg.getValue(), JsonNode.class);
      } catch (IOException ioe) {
        logger.warn("action=\"nullSafeGet\" message=\"{}\"", ioe.getMessage(), ioe);
        return JsonNodeFactory.instance.objectNode();
      }
    }
    return JsonNodeFactory.instance.objectNode();
  }

  @Override
  public void nullSafeSet(PreparedStatement preparedStatement, Object object, int index,
                          SessionImplementor sessionImplementor) throws SQLException {
    if (Objects.isNull(object)) {
      preparedStatement.setNull(index, Types.OTHER);
    }
    preparedStatement.setObject(index, object, Types.OTHER);
  }

  @Override
  public Object deepCopy(Object originalValue) {
    if (originalValue == null) {
      return null;
    }
    if (!(originalValue instanceof JsonNode)) {
      return null;
    }
    return ((JsonNode) originalValue).deepCopy();
  }

  @Override
  public boolean isMutable() {
    return true;
  }

  @Override
  public Serializable disassemble(Object object) {
    Object copy = deepCopy(object);

    if (copy instanceof Serializable) {
      return (Serializable) copy;
    }
    throw new SerializationException(
        String.format("Cannot serialize '%s', %s is not Serializable.", object, object.getClass()), null);
  }

  @Override
  public Object assemble(Serializable serializable, Object object) {
    return deepCopy(serializable);
  }

  @Override
  public Object replace(Object original, Object target, Object owner) {
    return deepCopy(original);
  }
}
