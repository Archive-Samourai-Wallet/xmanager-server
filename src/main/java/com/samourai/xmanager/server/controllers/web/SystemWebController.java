package com.samourai.xmanager.server.controllers.web;

import com.samourai.javaserver.web.controllers.AbstractSystemWebController;
import com.samourai.javaserver.web.models.SystemTemplateModel;
import com.samourai.xmanager.server.config.XManagerServerConfig;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SystemWebController extends AbstractSystemWebController {
  public static final String ENDPOINT = "/status/system";

  private XManagerServerConfig serverConfig;

  @Autowired
  public SystemWebController(XManagerServerConfig serverConfig) {
    this.serverConfig = serverConfig;
  }

  @RequestMapping(value = ENDPOINT, method = RequestMethod.GET)
  public String system(Model model) {
    Map<String, String> metrics = new HashMap<>();
    return super.system(
        model, new SystemTemplateModel(serverConfig.getName(), serverConfig.getName(), metrics));
  }
}
