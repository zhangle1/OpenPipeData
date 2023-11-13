package org.pipeData.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.pipeData.core.entity.User;
import org.pipeData.repository.IUserRepository;
import org.pipeData.security.base.PasswordToken;
import org.pipeData.security.util.JwtUtils;
import org.pipeData.service.BaseService;
import org.pipeData.service.IUserService;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class UserServiceImpl extends BaseService implements IUserService {


    IUserRepository userRepository;

    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String login(PasswordToken passwordToken) {
        try{
            securityManager.login(passwordToken);
        }catch(Exception e){
            log.error("Login error ({} {})", passwordToken.getSubject(), passwordToken.getPassword());
        }
        User user = userRepository.selectByNameOrEmail(passwordToken.getSubject());

        return JwtUtils.toJwtString(JwtUtils.createJwtToken(user));
    }
}
