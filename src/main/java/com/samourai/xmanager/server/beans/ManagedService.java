package com.samourai.xmanager.server.beans;

import com.samourai.wallet.api.backend.beans.MultiAddrResponse;
import com.samourai.wallet.util.FormatsUtilGeneric;
import com.samourai.wallet.util.XPubUtil;
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
  private static final FormatsUtilGeneric formatUtil = FormatsUtilGeneric.getInstance();
  private static final XPubUtil xPubUtil = XPubUtil.getInstance();

  private NetworkParameters params;
  private BackendService backendService;

  private String id;
  private String xpub;
  private List<String> addresses;
  private boolean allowIndex;
  private boolean bech32;

  private int successes;
  private Long lastSuccess;
  private int errors;
  private Long lastError;
  private AddressIndex lastResponse;

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
    this.allowIndex = serviceConfig.isAllowIndex();
    this.bech32 = formatUtil.isValidBech32(addresses.get(0));

    this.successes = 0;
    this.lastSuccess = null;
    this.errors = 0;
    this.lastError = null;
    this.lastResponse = null;
  }

  public void validate() throws Exception {
    if (!formatUtil.isValidXpub(xpub)) {
      throw new NotifiableException(id + ".xpub is invalid: " + xpub);
    }
    for (int i = 0; i < addresses.size(); i++) {
      String address = addresses.get(i);
      if (!formatUtil.isValidBitcoinAddress(address, params)) {
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
    String address;
    if (bech32) {
      address = xPubUtil.getAddressBech32(xpub, i, Utils.CHAIN_RECEIVE, params);
    } else {
      address = xPubUtil.getAddressSegwit(xpub, i, Utils.CHAIN_RECEIVE, params);
    }
    return address;
  }

  public synchronized AddressIndex fetchAddressNextOrDefault() {
    long now = System.currentTimeMillis();
    // fetch from backend
    try {
      lastResponse = fetchAddressNext();
      if (StringUtils.isEmpty(lastResponse.getAddress())) {
        throw new Exception("lastResponse.address is empty!");
      }
      successes++;
      lastSuccess = now;
    } catch (Exception e) {
      // fallback
      log.error("[" + id + "] backend not available, fallback to default address", e);
      lastResponse = getAddressDefault();
      errors++;
      lastError = now;
    }
    return lastResponse;
  }

  private AddressIndex fetchAddressNext() throws Exception {
    MultiAddrResponse.Address response = backendService.fetchAddress(xpub);
    String address = computeAddress(response.account_index);
    return new AddressIndex(address, response.account_index);
  }

  private AddressIndex getAddressDefault() {
    String address = addresses.get(0);
    return new AddressIndex(address, 0);
  }

  public String getId() {
    return id;
  }

  public boolean isBech32() {
    return bech32;
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

  public AddressIndex getLastResponse() {
    return lastResponse;
  }

  public boolean isAllowIndex() {
    return allowIndex;
  }
}
