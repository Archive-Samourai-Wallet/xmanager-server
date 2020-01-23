package com.samourai.xmanager.server.config;

import com.samourai.javaserver.config.ServerServicesConfig;
import com.samourai.wallet.api.explorer.ExplorerApi;
import com.samourai.xmanager.protocol.XManagerProtocol;
import com.samourai.xmanager.server.utils.Utils;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class ServicesConfig extends ServerServicesConfig {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  protected XManagerServerConfig serverConfig;

  public ServicesConfig(XManagerServerConfig serverConfig) {
    super();
    this.serverConfig = serverConfig;
  }

  @Bean
  XManagerProtocol xManagerProtocol() {
    return XManagerProtocol.getInstance();
  }

  @Bean
  Utils utils() {
    return Utils.getInstance();
  }

  @Bean
  ExplorerApi explorerApi(XManagerServerConfig serverConfig) {
    return new ExplorerApi(serverConfig.isTestnet());
  }
}
