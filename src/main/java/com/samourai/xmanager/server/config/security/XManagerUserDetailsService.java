package com.samourai.xmanager.server.config.security;

import com.samourai.javaserver.config.security.ServerUserDetailsService;
import com.samourai.xmanager.server.persistence.repositories.UserRepository;
import com.samourai.xmanager.server.persistence.to.UserTO;
import org.springframework.stereotype.Service;

@Service
public class XManagerUserDetailsService extends ServerUserDetailsService<UserTO, UserRepository> {

  public XManagerUserDetailsService(UserRepository userRepository) {
    super(userRepository);
  }
}
