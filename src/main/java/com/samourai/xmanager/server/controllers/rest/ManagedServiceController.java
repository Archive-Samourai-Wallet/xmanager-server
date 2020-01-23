package com.samourai.xmanager.server.controllers.rest;

import com.samourai.xmanager.protocol.XManagerEndpoint;
import com.samourai.xmanager.protocol.rest.AddressRequest;
import com.samourai.xmanager.protocol.rest.AddressResponse;
import com.samourai.xmanager.server.beans.ManagedService;
import com.samourai.xmanager.server.services.XManagerService;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ManagedServiceController extends AbstractRestController {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private XManagerService xManagerService;

  @Autowired
  public ManagedServiceController(XManagerService xManagerService) {
    this.xManagerService = xManagerService;
  }

  @RequestMapping(value = XManagerEndpoint.REST_ADDRESS, method = RequestMethod.POST)
  public AddressResponse address(@RequestBody AddressRequest payload) throws Exception {
    ManagedService managedService = xManagerService.getManagedService(payload.id);
    String nextAddress = managedService.fetchNextAddress();
    AddressResponse response = new AddressResponse(nextAddress);
    return response;
  }
}
