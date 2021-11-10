package com.samourai.xmanager.server.controllers.web.beans;

import com.samourai.javaserver.web.models.DashboardTemplateModel;
import com.samourai.xmanager.server.config.XManagerServerConfig;

public class XManagerDashboardTemplateModel extends DashboardTemplateModel {
  public XManagerDashboardTemplateModel(XManagerServerConfig serverConfig, String currentPage) {
    super(serverConfig.getName(), serverConfig.getName(), currentPage);
  }
}
