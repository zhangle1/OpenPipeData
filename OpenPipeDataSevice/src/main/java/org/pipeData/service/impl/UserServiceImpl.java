package org.pipeData.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.pipeData.core.entity.User;
import org.pipeData.core.mappers.UserMapper;
import org.pipeData.core.mybatis.base.BaseCrudServiceImpl;
import org.pipeData.security.base.PasswordToken;
import org.pipeData.service.IUserService;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends BaseCrudServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User selectByNameOrEmail(String subject) {
        User user = baseMapper
                .selectOne
                        (new QueryWrapper<User>()
                                .eq("username", subject)
                                .or()
                                .eq("email", subject));

        return user;
    }

    @Override
    public String login(PasswordToken passwordToken) {
        return null;
    }
}
