package com.samourai.xmanager.server.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samourai.wallet.api.backend.IBackendClient;
import com.samourai.wallet.api.backend.beans.HttpException;
import com.samourai.xmanager.protocol.XManagerProtocol;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JavaHttpClientService implements IBackendClient {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String USER_AGENT = "xmanager-server " + XManagerProtocol.PROTOCOL_VERSION;

  private ObjectMapper objectMapper;

  public JavaHttpClientService() {
    this.objectMapper = new ObjectMapper();
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  @Override
  public <T> T getJson(String urlStr, Class<T> responseType, Map<String, String> headers)
      throws HttpException {
    try {
      HttpEntity request = new HttpEntity(computeHeaders(headers));

      // execute
      ResponseEntity<T> response =
          newRestTemplate().exchange(urlStr, HttpMethod.GET, request, responseType);
      checkSuccess(response);
      return response.getBody();
    } catch (Exception e) {
      if (!(e instanceof HttpException)) {
        e = new HttpException(e, null);
      }
      throw (HttpException) e;
    }
  }

  @Override
  public <T> T postUrlEncoded(
      String urlStr, Class<T> responseType, Map<String, String> headers, Map<String, String> body)
      throws HttpException {
    try {
      HttpHeaders allHeaders = computeHeaders(headers);
      allHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      HttpEntity<Map<String, String>> request =
          new HttpEntity<Map<String, String>>(body, allHeaders);

      // execute
      ResponseEntity<T> response =
          newRestTemplate().exchange(urlStr, HttpMethod.POST, request, responseType);
      checkSuccess(response);
      return response.getBody();
    } catch (Exception e) {
      if (!(e instanceof HttpException)) {
        e = new HttpException(e, null);
      }
      throw (HttpException) e;
    }
  }

  private HttpHeaders computeHeaders(Map<String, String> headersMap) {
    final HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.USER_AGENT, USER_AGENT);
    for (Map.Entry<String, String> headerEntry : headersMap.entrySet()) {
      headers.set(headerEntry.getKey(), headerEntry.getValue());
    }
    return headers;
  }

  private void checkSuccess(ResponseEntity response) throws HttpException {
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new HttpException(
          new Exception("Request failed: statusCode=" + response.getStatusCodeValue()), null);
    }
  }

  private RestTemplate newRestTemplate() {
    RestTemplate r = new RestTemplate();

    // prevent no suitable HttpMessageConverter found for ... content type
    // [application/octet-stream]
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
        new MappingJackson2HttpMessageConverter();
    mappingJackson2HttpMessageConverter.setSupportedMediaTypes(
        Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
    r.getMessageConverters().add(mappingJackson2HttpMessageConverter);
    return r;
  }
}
