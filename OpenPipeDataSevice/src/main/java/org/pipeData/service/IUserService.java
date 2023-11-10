package org.pipeData.service;

import org.pipeData.core.entity.User;
import org.pipeData.core.mappers.UserMapper;
import org.pipeData.core.mybatis.base.BaseCrudService;
import org.pipeData.core.mybatis.base.BaseCrudServiceImpl;
import org.pipeData.security.base.PasswordToken;

public interface IUserService extends BaseCrudService<User> {
    User selectByNameOrEmail(String subject);

    String login(PasswordToken passwordToken);
}
