package com.samourai.xmanager.server.config;

import com.samourai.javaserver.config.ServerConfig;
import com.samourai.javaserver.utils.ServerUtils;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "server")
@Configuration
public class XManagerServerConfig extends ServerConfig {
  private static final ServerUtils serverUtils = ServerUtils.getInstance();

  private boolean testnet;
  private String metricsUrlApp;
  private String metricsUrlSystem;
  private long requestTimeout;
  private long cacheDuration;
  private NetworkParameters networkParameters;
  private Map<String, ServiceConfig> services;

  public boolean isTestnet() {
    return testnet;
  }

  public void setTestnet(boolean testnet) {
    this.testnet = testnet;
    NetworkParameters networkParameters = testnet ? TestNet3Params.get() : MainNetParams.get();
    this.networkParameters = networkParameters;
  }

  public String getMetricsUrlApp() {
    return metricsUrlApp;
  }

  public void setMetricsUrlApp(String metricsUrlApp) {
    this.metricsUrlApp = metricsUrlApp;
  }

  public String getMetricsUrlSystem() {
    return metricsUrlSystem;
  }

  public void setMetricsUrlSystem(String metricsUrlSystem) {
    this.metricsUrlSystem = metricsUrlSystem;
  }

  public long getRequestTimeout() {
    return requestTimeout;
  }

  public void setRequestTimeout(long requestTimeout) {
    this.requestTimeout = requestTimeout;
  }

  public long getCacheDuration() {
    return cacheDuration;
  }

  public void setCacheDuration(long cacheDuration) {
    this.cacheDuration = cacheDuration;
  }

  public NetworkParameters getNetworkParameters() {
    return networkParameters;
  }

  public Map<String, ServiceConfig> getServices() {
    return services;
  }

  public void setServices(Map<String, ServiceConfig> services) {
    this.services = services;
  }

  public static class ServiceConfig {
    @NotEmpty private String xpub;
    @NotEmpty private List<String> addresses;
    private boolean allowIndex;

    public void validate() throws Exception {
      if (StringUtils.isEmpty(xpub)) {
        throw new Exception("xpub is empty");
      }
      if (addresses.isEmpty()) {
        throw new Exception("addresses is empty");
      }
    }

    public String getXpub() {
      return xpub;
    }

    public void setXpub(String xpub) {
      this.xpub = xpub;
    }

    public List<String> getAddresses() {
      return addresses;
    }

    public void setAddresses(List<String> addresses) {
      this.addresses = addresses;
    }

    public boolean isAllowIndex() {
      return allowIndex;
    }

    public void setAllowIndex(boolean allowIndex) {
      this.allowIndex = allowIndex;
    }

    @Override
    public String toString() {
      String feesXpub = serverUtils.obfuscateString(xpub, 3);
      return "xpub="
          + feesXpub
          + " ; allowIndex="
          + allowIndex
          + ", addresses="
          + String.join(", ", addresses);
    }
  }

  @Override
  public void validate() throws Exception {
    super.validate();
    for (ServiceConfig serviceConfig : services.values()) {
      serviceConfig.validate();
    }
  }

  @Override
  public Map<String, String> getConfigInfo() {
    Map<String, String> configInfo = super.getConfigInfo();

    for (Map.Entry<String, ServiceConfig> entry : services.entrySet()) {
      configInfo.put("services[" + entry.getKey() + "]", entry.getValue().toString());
    }
    return configInfo;
  }
}
