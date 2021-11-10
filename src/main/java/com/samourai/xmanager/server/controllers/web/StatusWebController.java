package com.samourai.xmanager.server.controllers.web;

import com.samourai.wallet.api.explorer.ExplorerApi;
import com.samourai.xmanager.protocol.XManagerProtocol;
import com.samourai.xmanager.server.beans.AddressIndex;
import com.samourai.xmanager.server.config.XManagerServerConfig;
import com.samourai.xmanager.server.controllers.web.beans.ManagedServiceTemplateModel;
import com.samourai.xmanager.server.controllers.web.beans.XManagerDashboardTemplateModel;
import com.samourai.xmanager.server.services.XManagerService;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class StatusWebController {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  public static final String ENDPOINT = "/status/status";

  private XManagerServerConfig serverConfig;
  private XManagerService xManagerService;
  private ExplorerApi explorerApi;

  @Autowired
  public StatusWebController(
      XManagerServerConfig serverConfig, XManagerService xManagerService, ExplorerApi explorerApi) {
    this.serverConfig = serverConfig;
    this.xManagerService = xManagerService;
    this.explorerApi = explorerApi;
  }

  @RequestMapping(value = ENDPOINT, method = RequestMethod.GET)
  public String status(Model model) throws Exception {
    new XManagerDashboardTemplateModel(serverConfig, "status").apply(model);
    model.addAttribute("protocolVersion", XManagerProtocol.PROTOCOL_VERSION);
    List<ManagedServiceTemplateModel> services =
        xManagerService
            .getManagedServices()
            .stream()
            .map(
                service -> {
                  AddressIndex lastResponse = service.getLastResponse();
                  String lastAddress = lastResponse != null ? lastResponse.getAddress() : null;
                  String urlExplorer =
                      lastAddress != null ? explorerApi.getUrlAddress(lastAddress) : null;
                  return new ManagedServiceTemplateModel(service, urlExplorer);
                })
            .collect(Collectors.toList());
    model.addAttribute("managedServices", services);
    model.addAttribute("explorerApi", explorerApi);
    return "status";
  }
}
