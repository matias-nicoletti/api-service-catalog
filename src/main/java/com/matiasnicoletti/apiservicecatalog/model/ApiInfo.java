package com.matiasnicoletti.apiservicecatalog.model;

/**
 * Represents the Api info, returned from any /info endpoint.
 */
public class ApiInfo {

  private String owner;
  private String pod;

  public String getOwner() {
    return owner;
  }
  
  public String getPod() {
    return pod;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }
  
  public void setPod(String pod) {
    this.pod = pod;
  }
  
  public boolean allNotNull() {
    return owner != null && pod != null;
  }
}
