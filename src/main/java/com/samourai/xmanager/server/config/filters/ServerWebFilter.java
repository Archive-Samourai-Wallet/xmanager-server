package com.samourai.xmanager.server.config.filters;

import com.samourai.xmanager.protocol.XManagerEndpoint;
import com.samourai.xmanager.protocol.XManagerProtocol;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

@WebFilter(XManagerEndpoint.REST_PREFIX + "*")
public class ServerWebFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    httpServletResponse.setHeader(
        XManagerProtocol.HEADER_PROTOCOL_VERSION, XManagerProtocol.PROTOCOL_VERSION);
    chain.doFilter(request, response);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}
}
