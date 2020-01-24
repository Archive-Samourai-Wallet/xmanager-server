package com.samourai.xmanager.server.controllers.web.beans;

import com.samourai.xmanager.server.beans.ManagedService;
import java.util.List;

public class ManagedServiceTemplateModel {
  private ManagedService service;
  private String lastAddressExplorer;

  public ManagedServiceTemplateModel(ManagedService service, String lastAddressExplorer) {
    this.service = service;
    this.lastAddressExplorer = lastAddressExplorer;
  }

  public String getId() {
    return service.getId();
  }

  public int getSuccesses() {
    return service.getSuccesses();
  }

  public Long getLastSuccess() {
    return service.getLastSuccess();
  }

  public int getErrors() {
    return service.getErrors();
  }

  public Long getLastError() {
    return service.getLastError();
  }

  public List<String> getAddresses() {
    return service.getAddresses();
  }

  public String getLastAddress() {
    return service.getLastAddress();
  }

  public Integer getLastIndex() {
    return service.getLastIndex();
  }

  public String getLastAddressExplorer() {
    return lastAddressExplorer;
  }
}
