package org.pipeData.service;

import org.pipeData.core.entity.User;
import org.pipeData.core.mybatis.base.BaseCrudRepository;
import org.pipeData.security.base.PasswordToken;

public interface IUserService {

    String login(PasswordToken passwordToken);
}
