package com.samourai.xmanager.server.services;

import com.samourai.wallet.api.backend.BackendApi;
import com.samourai.wallet.api.backend.BackendServer;
import com.samourai.xmanager.server.config.XManagerServerConfig;
import java8.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class BackendService extends BackendApi {

  public BackendService(
      JavaHttpClientService httpClientService, XManagerServerConfig serverConfig) {
    super(
        httpClientService,
        serverConfig.isTestnet()
            ? BackendServer.TESTNET.getBackendUrlClear()
            : BackendServer.MAINNET.getBackendUrlClear(),
        Optional.empty());
  }
}
