package com.samourai.xmanager.server.controllers.web;

import com.samourai.javaserver.web.controllers.AbstractErrorWebController;
import com.samourai.javaserver.web.models.ErrorTemplateModel;
import com.samourai.xmanager.server.config.XManagerServerConfig;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ErrorController extends AbstractErrorWebController {
  private static final String ENDPOINT = "/error";

  private XManagerServerConfig serverConfig;

  @Autowired
  public ErrorController(XManagerServerConfig serverConfig) {
    this.serverConfig = serverConfig;
  }

  @RequestMapping(value = ENDPOINT)
  public ModelAndView errorHtml(WebRequest webRequest, HttpServletResponse response, Model model) {
    return super.errorHtml(
        webRequest, response, model, new ErrorTemplateModel(serverConfig.getName()));
  }
}
