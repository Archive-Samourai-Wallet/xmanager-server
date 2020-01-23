package com.samourai.xmanager.server.integration;

import com.samourai.javaserver.utils.ServerUtils;
import com.samourai.xmanager.server.config.XManagerServerConfig;
import com.samourai.xmanager.server.utils.Utils;
import java.lang.invoke.MethodHandles;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(ServerUtils.PROFILE_TEST)
public abstract class AbstractIntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @LocalServerPort protected int port;

  @Rule public ExpectedException thrown = ExpectedException.none();

  @Autowired protected XManagerServerConfig serverConfig;

  @Before
  public void setUp() throws Exception {
    // enable debug
    Utils.setLoggerDebug();

    serverConfig.validate();
  }

  @AfterAll
  public void tearDown() {}
}
