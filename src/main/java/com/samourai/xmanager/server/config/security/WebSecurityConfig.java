package com.samourai.xmanager.server.config.security;

import com.samourai.javaserver.config.ServerServicesConfig;
import com.samourai.xmanager.protocol.XManagerEndpoint;
import com.samourai.xmanager.server.controllers.rest.SystemController;
import com.samourai.xmanager.server.controllers.web.*;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private static final String[] WEB_ACTUATOR_ENDPOINTS = new String[] {"/actuator/prometheus"};

  private static final String[] WEB_ADMIN_ENDPOINTS =
      new String[] {
        StatusWebController.ENDPOINT,
        ConfigWebController.ENDPOINT,
        SystemWebController.ENDPOINT,
        MetricsWebController.ENDPOINT_APP,
        MetricsWebController.ENDPOINT_SYSTEM
      };

  private static final String[] REST_ENDPOINTS =
      new String[] {
        XManagerEndpoint.REST_ADDRESS,
        XManagerEndpoint.REST_ADDRESS_INDEX,
        XManagerEndpoint.REST_VERIFY_ADDRESS_INDEX,
        SystemController.ENDPOINT_HEALTH,
      };

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // disable csrf for our endpoints
    http.csrf()
        .ignoringAntMatchers(ArrayUtils.addAll(REST_ENDPOINTS))
        .and()
        .authorizeRequests()

        // public statics
        .antMatchers(ServerServicesConfig.STATICS)
        .permitAll()

        // public login form
        .antMatchers(LoginWebController.ENDPOINT)
        .permitAll()
        .antMatchers(LoginWebController.PROCESS_ENDPOINT)
        .permitAll()

        // public actuator
        .antMatchers(WEB_ACTUATOR_ENDPOINTS)
        .permitAll()

        // public REST endpoints
        .antMatchers(REST_ENDPOINTS)
        .permitAll()

        // restrict admin
        .antMatchers(WEB_ADMIN_ENDPOINTS)
        .hasAnyAuthority(WhirlpoolPrivilege.ALL.toString())

        // reject others
        .anyRequest()
        .denyAll()
        .and()

        // custom login form
        .formLogin()
        .loginProcessingUrl(LoginWebController.PROCESS_ENDPOINT)
        .loginPage(LoginWebController.ENDPOINT)
        .defaultSuccessUrl(StatusWebController.ENDPOINT, true);
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider(
      XManagerUserDetailsService userDetailsService) {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(encoder());
    return authProvider;
  }

  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder(11);
  }
}
