package com.samourai.xmanager.server.services;

import com.samourai.http.client.JettyHttpClient;
import com.samourai.wallet.api.backend.BackendApi;
import com.samourai.wallet.api.backend.BackendServer;
import com.samourai.xmanager.protocol.XManagerProtocol;
import com.samourai.xmanager.server.config.XManagerServerConfig;
import org.springframework.stereotype.Service;

@Service
public class BackendService extends BackendApi {
  private static final String USER_AGENT = "xmanager-server " + XManagerProtocol.PROTOCOL_VERSION;

  public BackendService(XManagerServerConfig serverConfig) {
    super(
        computeHttpClient(serverConfig.getRequestTimeout()),
        serverConfig.isTestnet()
            ? BackendServer.TESTNET.getBackendUrlClear()
            : BackendServer.MAINNET.getBackendUrlClear(),
        null);
  }

  private static JettyHttpClient computeHttpClient(long requestTimeout) {
    return new JettyHttpClient(requestTimeout, null, USER_AGENT);
  }
}
