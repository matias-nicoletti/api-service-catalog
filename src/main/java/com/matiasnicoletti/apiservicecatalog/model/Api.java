package com.matiasnicoletti.apiservicecatalog.model;

import com.fasterxml.jackson.databind.JsonNode;

import com.matiasnicoletti.apiservicecatalog.repository.JsonSwaggerUserType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The type Api.
 */
@Entity
@Table(name = "apis")
@TypeDefs(@TypeDef(name = "JsonSwaggerUserType", typeClass = JsonSwaggerUserType.class))
public class Api {

  @Id
  private String name;
  @Column(name = "swagger_url")
  private String swaggerUrl;
  private int port;
  private String version;
  @Column(name = "swagger", nullable = false)
  @Type(type = "JsonSwaggerUserType")
  private JsonNode swagger;

  @Column(name = "date_updated", nullable = true)
  @Type(type = "timestamp")
  @UpdateTimestamp
  private Date dateUpdated;

  /**
   * Instantiates a new Api.
   */
  public Api() {
    // Default constructor.
  }

  /**
   * Instantiates a new Api.
   *
   * @param name the name
   * @param swaggerUrl the swagger url
   * @param port the port
   * @param version the version
   * @param dateUpdated the date updated
   */
  public Api(String name, String swaggerUrl, int port, String version, Date dateUpdated) {
    this.name = name;
    this.swaggerUrl = swaggerUrl;
    this.port = port;
    this.version = version;
    this.dateUpdated = dateUpdated;
  }

  /**
   * Instantiates a new Api.
   *
   * @param name the name
   * @param swaggerUrl the swagger url
   * @param port the port
   * @param version the version
   * @param swagger the swagger
   */
  public Api(String name, String swaggerUrl, int port, String version,
      JsonNode swagger) {
    this.name = name;
    this.swaggerUrl = swaggerUrl;
    this.port = port;
    this.version = version;
    this.swagger = swagger;
  }

  /**
   * Instantiates a new Api.
   *
   * @param name the name
   * @param swaggerUrl the swagger url
   * @param port the port
   * @param version the version
   * @param dateUpdated the date updated
   * @param swagger the swagger
   */
  public Api(String name, String swaggerUrl, int port, String version, Date dateUpdated,
      JsonNode swagger) {
    this.name = name;
    this.swaggerUrl = swaggerUrl;
    this.port = port;
    this.version = version;
    this.dateUpdated = dateUpdated;
    this.swagger = swagger;
  }

  /**
   * Getter for property 'name'.
   *
   * @return Value for property 'name'.
   */
  public String getName() {
    return name;
  }

  /**
   * Setter for property 'name'.
   *
   * @param name Value to set for property 'name'.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Getter for property 'swaggerUrl'.
   *
   * @return Value for property 'swaggerUrl'.
   */
  public String getSwaggerUrl() {
    return swaggerUrl;
  }

  /**
   * Setter for property 'swaggerUrl'.
   *
   * @param swaggerUrl Value to set for property 'swaggerUrl'.
   */
  public void setSwaggerUrl(String swaggerUrl) {
    this.swaggerUrl = swaggerUrl;
  }

  /**
   * Getter for property 'port'.
   *
   * @return Value for property 'port'.
   */
  public int getPort() {
    return port;
  }

  /**
   * Setter for property 'port'.
   *
   * @param port Value to set for property 'port'.
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * Getter for property 'version'.
   *
   * @return Value for property 'version'.
   */
  public String getVersion() {
    return version;
  }

  /**
   * Setter for property 'version'.
   *
   * @param version Value to set for property 'version'.
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Getter for property 'swagger'.
   *
   * @return Value for property 'swagger'.
   */
  public JsonNode getSwagger() {
    return swagger;
  }

  /**
   * Setter for property 'swagger'.
   *
   * @param swagger Value to set for property 'swagger'.
   */
  public void setSwagger(JsonNode swagger) {
    this.swagger = swagger;
  }

  /**
   * Getter for property 'dateUpdated'.
   *
   * @return Value for property 'dateUpdated'.
   */
  public Date getDateUpdated() {
    return dateUpdated;
  }

  /**
   * Setter for property 'dateUpdated'.
   *
   * @param dateUpdated Value to set for property 'dateUpdated'.
   */
  public void setDateUpdated(Date dateUpdated) {
    this.dateUpdated = dateUpdated;
  }
}
