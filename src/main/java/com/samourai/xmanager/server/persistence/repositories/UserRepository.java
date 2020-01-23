package com.samourai.xmanager.server.persistence.repositories;

import com.samourai.javaserver.persistence.repositories.ServerUserRepository;
import com.samourai.xmanager.server.persistence.to.UserTO;

public interface UserRepository extends ServerUserRepository<UserTO> {}
