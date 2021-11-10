package com.samourai.xmanager.server.controllers.rest;

import com.samourai.xmanager.protocol.XManagerEndpoint;
import com.samourai.xmanager.protocol.rest.*;
import com.samourai.xmanager.server.beans.AddressIndex;
import com.samourai.xmanager.server.beans.ManagedService;
import com.samourai.xmanager.server.exceptions.NotifiableException;
import com.samourai.xmanager.server.services.MetricService;
import com.samourai.xmanager.server.services.XManagerService;
import java.lang.invoke.MethodHandles;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ManagedServiceController extends AbstractRestController {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private XManagerService xManagerService;
  private MetricService metricService;

  @Autowired
  public ManagedServiceController(XManagerService xManagerService, MetricService metricService) {
    this.xManagerService = xManagerService;
    this.metricService = metricService;
  }

  @RequestMapping(value = XManagerEndpoint.REST_ADDRESS, method = RequestMethod.POST)
  public AddressResponse address(@Valid @RequestBody AddressRequest payload) throws Exception {
    ManagedService managedService = xManagerService.getManagedService(payload.id);
    try {
      AddressIndex nextAddressIndex = managedService.fetchAddressNextOrDefault();
      AddressResponse response = new AddressResponse(nextAddressIndex.getAddress());
      metricService.onHitSuccess(managedService);
      return response;
    } catch (Exception e) {
      metricService.onHitFail(managedService);
      throw e;
    }
  }

  @RequestMapping(value = XManagerEndpoint.REST_ADDRESS_INDEX, method = RequestMethod.POST)
  public AddressIndexResponse addressIndex(@Valid @RequestBody AddressIndexRequest payload)
      throws Exception {
    ManagedService managedService = xManagerService.getManagedService(payload.id);
    checkAllowIndex(managedService);
    try {
      AddressIndex nextAddressIndex = managedService.fetchAddressNextOrDefault();
      AddressIndexResponse response =
          new AddressIndexResponse(nextAddressIndex.getAddress(), nextAddressIndex.getIndex());
      metricService.onHitSuccess(managedService);
      return response;
    } catch (Exception e) {
      metricService.onHitFail(managedService);
      throw e;
    }
  }

  @RequestMapping(value = XManagerEndpoint.REST_VERIFY_ADDRESS_INDEX, method = RequestMethod.POST)
  public VerifyAddressIndexResponse verifyAddressIndex(
      @Valid @RequestBody VerifyAddressIndexRequest payload) throws Exception {
    ManagedService managedService = xManagerService.getManagedService(payload.id);
    checkAllowIndex(managedService);
    try {
      String address = managedService.computeAddress(payload.index);
      boolean valid = address.equals(payload.address);
      VerifyAddressIndexResponse response = new VerifyAddressIndexResponse(valid);
      metricService.onHitSuccess(managedService);
      return response;
    } catch (Exception e) {
      metricService.onHitFail(managedService);
      throw e;
    }
  }

  private void checkAllowIndex(ManagedService managedService) throws Exception {
    if (!managedService.isAllowIndex()) {
      throw new NotifiableException("addressIndex not allowed for this service");
    }
  }
}
