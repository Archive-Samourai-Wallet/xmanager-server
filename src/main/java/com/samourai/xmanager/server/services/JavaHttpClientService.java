package com.samourai.xmanager.server.services;

import com.samourai.http.client.JavaHttpClient;
import com.samourai.whirlpool.cli.utils.CliUtils;
import com.samourai.xmanager.protocol.XManagerProtocol;
import com.samourai.xmanager.server.config.XManagerServerConfig;
import org.eclipse.jetty.client.HttpClient;
import org.springframework.stereotype.Service;

@Service
public class JavaHttpClientService extends JavaHttpClient {
  private static final String USER_AGENT = "xmanager-server " + XManagerProtocol.PROTOCOL_VERSION;

  public JavaHttpClientService(XManagerServerConfig config) {
    super(config.getRequestTimeout());
  }

  protected HttpClient computeHttpClient(boolean isRegisterOutput) throws Exception {
    HttpClient httpClient = CliUtils.computeHttpClient(null, USER_AGENT);
    return httpClient;
  }
}
