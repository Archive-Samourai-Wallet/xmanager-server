package com.samourai.xmanager.server.controllers.rest;

import com.samourai.xmanager.protocol.XManagerEndpoint;
import com.samourai.xmanager.server.controllers.rest.beans.HealthResponse;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController extends AbstractRestController {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  public static final String ENDPOINT_HEALTH = XManagerEndpoint.REST_PREFIX + "system/health";

  @Autowired
  public SystemController() {}

  @RequestMapping(value = ENDPOINT_HEALTH, method = RequestMethod.GET)
  public HealthResponse health() {
    return HealthResponse.ok();
  }
}
