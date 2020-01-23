package com.samourai.xmanager.server;

import com.samourai.javaserver.config.ServerConfig;
import com.samourai.javaserver.run.ServerApplication;
import com.samourai.javaserver.utils.ServerUtils;
import com.samourai.xmanager.server.config.XManagerServerConfig;
import com.samourai.xmanager.server.services.XManagerService;
import com.samourai.xmanager.server.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan(value = "com.samourai.xmanager.config.filters")
public class Application extends ServerApplication {
  private static final Logger log = LoggerFactory.getLogger(Application.class);

  @Autowired private ServerUtils serverUtils;

  @Autowired private XManagerServerConfig serverConfig;

  @Autowired private XManagerService xManagerService;

  @Autowired private Utils utils;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void runServer() throws Exception {
    // server starting...
    xManagerService.validate();
  }

  @Override
  protected ServerConfig getServerConfig() {
    return serverConfig;
  }

  @Override
  protected void setLoggerDebug() {
    utils.setLoggerDebug();
  }
}
