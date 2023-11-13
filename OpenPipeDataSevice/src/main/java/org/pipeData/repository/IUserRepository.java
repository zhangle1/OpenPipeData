package org.pipeData.repository;

import org.pipeData.core.entity.User;
import org.pipeData.core.mybatis.base.BaseCrudRepository;

public interface IUserRepository  extends BaseCrudRepository<User> {

    User selectByNameOrEmail(String subject);

}
