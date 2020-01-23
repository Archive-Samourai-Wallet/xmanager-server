package com.samourai.xmanager.server.services;

import com.samourai.xmanager.server.beans.ManagedService;
import com.samourai.xmanager.server.config.XManagerServerConfig;
import com.samourai.xmanager.server.exceptions.NotifiableException;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XManagerService {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private XManagerServerConfig serverConfig;
  private BackendService backendService;

  private Map<String, ManagedService> managedServices;

  @Autowired
  public XManagerService(XManagerServerConfig serverConfig, BackendService backendService) {
    this.serverConfig = serverConfig;
    this.backendService = backendService;
    this.init();
  }

  private void init() {
    this.managedServices = new LinkedHashMap<>();
    for (Map.Entry<String, XManagerServerConfig.ServiceConfig> serviceConfig :
        serverConfig.getServices().entrySet()) {
      String id = serviceConfig.getKey();
      ManagedService managedService =
          new ManagedService(serverConfig, backendService, id, serviceConfig.getValue());
      managedServices.put(id, managedService);
    }
  }

  public void validate() throws Exception {
    for (ManagedService managedService : managedServices.values()) {
      managedService.validate();
    }
  }

  public ManagedService getManagedService(String id) throws Exception {
    ManagedService managedService = managedServices.get(id);
    if (managedService == null) {
      throw new NotifiableException("Unknown service: " + id);
    }
    return managedService;
  }

  public Collection<ManagedService> getManagedServices() {
    return managedServices.values();
  }
}
