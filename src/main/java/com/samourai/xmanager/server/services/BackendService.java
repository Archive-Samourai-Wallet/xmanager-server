package com.samourai.xmanager.server.services;

import com.samourai.http.client.HttpUsage;
import com.samourai.http.client.JavaHttpClient;
import com.samourai.wallet.api.backend.BackendApi;
import com.samourai.wallet.api.backend.BackendServer;
import com.samourai.whirlpool.cli.utils.CliUtils;
import com.samourai.xmanager.protocol.XManagerProtocol;
import com.samourai.xmanager.server.config.XManagerServerConfig;
import java8.util.Optional;
import org.eclipse.jetty.client.HttpClient;
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
        Optional.empty());
  }

  private static JavaHttpClient computeHttpClient(long requestTimeout) {
    HttpClient httpClient = CliUtils.computeHttpClient(null, USER_AGENT);
    return new JavaHttpClient(HttpUsage.BACKEND, httpClient, requestTimeout);
  }
}
