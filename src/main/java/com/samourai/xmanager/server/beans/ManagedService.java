package com.samourai.xmanager.server.beans;

import com.samourai.wallet.api.backend.beans.MultiAddrResponse;
import com.samourai.wallet.util.FormatsUtilGeneric;
import com.samourai.xmanager.server.config.XManagerServerConfig;
import com.samourai.xmanager.server.exceptions.NotifiableException;
import com.samourai.xmanager.server.services.BackendService;
import com.samourai.xmanager.server.utils.Utils;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class ManagedService {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final FormatsUtilGeneric formatUtils = FormatsUtilGeneric.getInstance();
  private static final Utils utils = Utils.getInstance();

  private NetworkParameters params;
  private BackendService backendService;

  private String id;
  private String xpub;
  private List<String> addresses;

  private int successes;
  private Long lastSuccess;
  private int errors;
  private Long lastError;
  private Integer lastIndex;
  private String lastAddress;

  public ManagedService(
      XManagerServerConfig serverConfig,
      BackendService backendService,
      String id,
      XManagerServerConfig.ServiceConfig serviceConfig) {
    this.params = serverConfig.getNetworkParameters();
    this.backendService = backendService;

    this.id = id;
    this.xpub = serviceConfig.getXpub();
    this.addresses = serviceConfig.getAddresses();

    this.successes = 0;
    this.lastSuccess = null;
    this.errors = 0;
    this.lastError = null;
    this.lastIndex = null;
    this.lastAddress = null;
  }

  public void validate() throws Exception {
    if (!formatUtils.isValidXpub(xpub)) {
      throw new NotifiableException(id + ".xpub is invalid: " + xpub);
    }
    for (int i = 0; i < addresses.size(); i++) {
      String address = addresses.get(i);
      if (!formatUtils.isValidBech32(address)) {
        throw new NotifiableException(id + ".address[" + i + "] is invalid: " + address);
      }

      // verify against xpub
      String computedAddress = computeAddress(i);
      if (!address.equals(computedAddress)) {
        throw new NotifiableException(
            id
                + ".address["
                + i
                + "] doesn't match xpub: configured="
                + address
                + ", computed="
                + computedAddress);
      }
    }
  }

  public String computeAddress(int i) {
    String address = utils.computeXpubAddressBech32(i, xpub, Utils.CHAIN_RECEIVE, params);
    return address;
  }

  public synchronized String fetchNextAddress() {
    // fetch from backend
    try {
      MultiAddrResponse.Address address = backendService.fetchAddress(xpub);
      lastIndex = address.account_index;
      lastAddress = computeAddress(address.account_index);
    } catch (Exception e) {
      log.error("", e);
    }
    long now = System.currentTimeMillis();

    if (StringUtils.isEmpty(lastAddress)) {
      // fallback
      log.error("[" + id + "] backend not available, fallback to default address");
      lastIndex = 0;
      lastAddress = addresses.get(0);
      errors++;
      lastError = now;
    } else {
      successes++;
      lastSuccess = now;
    }
    return lastAddress;
  }

  public String getId() {
    return id;
  }

  public int getSuccesses() {
    return successes;
  }

  public Long getLastSuccess() {
    return lastSuccess;
  }

  public int getErrors() {
    return errors;
  }

  public Long getLastError() {
    return lastError;
  }

  public List<String> getAddresses() {
    return addresses;
  }

  public String getLastAddress() {
    return lastAddress;
  }

  public Integer getLastIndex() {
    return lastIndex;
  }
}
