package com.matiasnicoletti.apiservicecatalog.repository;

import org.hibernate.dialect.PostgreSQL9Dialect;

import java.sql.Types;

public class CustomPostgresSqlDialect extends PostgreSQL9Dialect{

  public CustomPostgresSqlDialect() {
    this.registerColumnType(Types.JAVA_OBJECT,"json");
  }
}
