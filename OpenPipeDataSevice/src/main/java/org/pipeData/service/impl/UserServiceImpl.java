package org.pipeData.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.pipeData.security.base.PasswordToken;
import org.pipeData.service.BaseService;
import org.pipeData.service.IUserService;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


@Service
@Repository
@Slf4j
public class UserServiceImpl extends BaseService implements IUserService {




    @Override
    public String login(PasswordToken passwordToken) {
        try{
            securityManager.login(passwordToken);
        }catch(Exception e){
            log.error("Login error ({} {})", passwordToken.getSubject(), passwordToken.getPassword());
        }


        return null;
    }
}
