package com.samourai.xmanager.server.beans;

import com.samourai.wallet.api.backend.beans.WalletResponse;
import com.samourai.wallet.util.FormatsUtilGeneric;
import com.samourai.wallet.util.XPubUtil;
import com.samourai.xmanager.server.config.XManagerServerConfig;
import com.samourai.xmanager.server.exceptions.NotifiableException;
import com.samourai.xmanager.server.services.BackendService;
import com.samourai.xmanager.server.services.MetricService;
import com.samourai.xmanager.server.utils.Utils;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class ManagedService {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final FormatsUtilGeneric formatUtil = FormatsUtilGeneric.getInstance();
  private static final XPubUtil xPubUtil = XPubUtil.getInstance();

  private XManagerServerConfig serverConfig;
  private BackendService backendService;
  private MetricService metricService;
  private Utils utils;

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
  private long lastResponseTime;

  public ManagedService(
      XManagerServerConfig serverConfig,
      BackendService backendService,
      MetricService metricService,
      Utils utils,
      String id,
      XManagerServerConfig.ServiceConfig serviceConfig) {
    this.serverConfig = serverConfig;
    this.backendService = backendService;
    this.metricService = metricService;
    this.utils = utils;

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
    this.lastResponseTime = 0;
  }

  public void validate() throws Exception {
    if (!formatUtil.isValidXpub(xpub)) {
      throw new NotifiableException(id + ".xpub is invalid: " + xpub);
    }
    for (int i = 0; i < addresses.size(); i++) {
      String address = addresses.get(i);
      if (!formatUtil.isValidBitcoinAddress(address, serverConfig.getNetworkParameters())) {
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
    NetworkParameters params = serverConfig.getNetworkParameters();
    String address;
    if (bech32) {
      address = xPubUtil.getAddressBech32(xpub, i, Utils.CHAIN_RECEIVE, params);
    } else {
      address = xPubUtil.getAddressSegwit(xpub, i, Utils.CHAIN_RECEIVE, params);
    }
    return address;
  }

  private AddressIndex getCachedResponseOrNull() {
    long now = System.currentTimeMillis();
    if (lastResponse != null && (now - lastResponseTime) < serverConfig.getCacheDuration()) {
      // use cached response
      return lastResponse;
    }
    return null;
  }

  public AddressIndex getAddressNextOrDefault() {
    // use cached response ?
    AddressIndex cachedResponse = getCachedResponseOrNull();
    if (cachedResponse != null) {
      return cachedResponse;
    }

    // fetch new response
    return fetchAddressNextOrDefault();
  }

  private synchronized AddressIndex fetchAddressNextOrDefault() {
    // use cached response ? double check for synchronized concurrency
    AddressIndex cachedResponse = getCachedResponseOrNull();
    if (cachedResponse != null) {
      return cachedResponse;
    }

    // measure latency
    return fetchAddressNextOrDefaultNoCache();
  }

  private synchronized AddressIndex fetchAddressNextOrDefaultNoCache() {
    long since = System.currentTimeMillis();
    // fetch from backend
    try {
      AddressIndex addressIndex = fetchAddressNextOrThrow();
      if (StringUtils.isEmpty(addressIndex.getAddress())) {
        throw new Exception("lastResponse.address is empty!");
      }
      long now = System.currentTimeMillis();
      lastResponse = addressIndex;
      lastResponseTime = now;
      successes++;
      lastSuccess = now;
      metricService.onHitSuccess(this);
    } catch (Exception e) {
      // fallback
      if (lastResponse != null) {
        // use lastResponse
        log.error("[" + id + "] backend not available, fallback to lastResponse", e);
      } else {
        // use default address
        log.error("[" + id + "] backend not available, fallback to default address", e);
        lastResponse = getAddressDefault();
      }
      long now = System.currentTimeMillis();
      lastResponseTime = now; // update cache time
      errors++;
      lastError = now;
      metricService.onHitFail(this);
    }
    // metrics
    long elapsed = System.currentTimeMillis() - since;
    metricService.hitTimer(this).record(elapsed, TimeUnit.MILLISECONDS);

    return lastResponse;
  }

  private AddressIndex fetchAddressNextOrThrow() throws Exception {
    // use timeout
    return utils.runOrTimeout(
        () -> {
          WalletResponse response = backendService.fetchWallet(xpub);
          WalletResponse.Address addressResponse = response.addresses[0];
          String address = computeAddress(addressResponse.account_index);
          return new AddressIndex(address, addressResponse.account_index);
        },
        serverConfig.getRequestTimeout());
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
