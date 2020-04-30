package com.samourai.xmanager.server.config.security;

import com.samourai.javaserver.config.ServerServicesConfig;
import com.samourai.xmanager.protocol.XManagerEndpoint;
import com.samourai.xmanager.server.controllers.web.ConfigWebController;
import com.samourai.xmanager.server.controllers.web.LoginWebController;
import com.samourai.xmanager.server.controllers.web.StatusWebController;
import com.samourai.xmanager.server.controllers.web.SystemWebController;
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
  private static final String[] REST_ENDPOINTS =
      new String[] {
        XManagerEndpoint.REST_ADDRESS,
        XManagerEndpoint.REST_ADDRESS_INDEX,
        XManagerEndpoint.REST_VERIFY_ADDRESS_INDEX
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

        // public REST endpoints
        .antMatchers(REST_ENDPOINTS)
        .permitAll()

        // restrict admin
        .antMatchers(StatusWebController.ENDPOINT)
        .hasAnyAuthority(WhirlpoolPrivilege.STATUS.toString(), WhirlpoolPrivilege.ALL.toString())
        .antMatchers(ConfigWebController.ENDPOINT)
        .hasAnyAuthority(WhirlpoolPrivilege.CONFIG.toString(), WhirlpoolPrivilege.ALL.toString())
        .antMatchers(SystemWebController.ENDPOINT)
        .hasAnyAuthority(WhirlpoolPrivilege.SYSTEM.toString(), WhirlpoolPrivilege.ALL.toString())

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
