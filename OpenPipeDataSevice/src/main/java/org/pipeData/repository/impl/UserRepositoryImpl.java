package org.pipeData.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.pipeData.core.entity.User;
import org.pipeData.core.mybatis.base.BaseCrudRepositoryImpl;
import org.pipeData.repository.IUserRepository;
import org.pipeData.repository.mapper.UserMapper;
import org.springframework.stereotype.Repository;


@Repository
public class UserRepositoryImpl extends BaseCrudRepositoryImpl<UserMapper, User> implements IUserRepository {


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
}
