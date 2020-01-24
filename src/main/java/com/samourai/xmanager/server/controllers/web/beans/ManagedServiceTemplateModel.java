package com.samourai.xmanager.server.controllers.web.beans;

import com.samourai.xmanager.server.beans.AddressIndex;
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
    AddressIndex addressIndex = service.getLastResponse();
    return addressIndex != null ? addressIndex.getAddress() : null;
  }

  public Integer getLastIndex() {
    AddressIndex addressIndex = service.getLastResponse();
    return addressIndex != null ? addressIndex.getIndex() : null;
  }

  public String getLastAddressExplorer() {
    return lastAddressExplorer;
  }

  public boolean isUp() {
    if (getLastError() != null && (getLastSuccess() == null || getLastError() > getLastSuccess())) {
      return false;
    }
    return true;
  }
}
