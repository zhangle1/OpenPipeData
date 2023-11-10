package org.pipeData.test;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import org.junit.jupiter.api.Test;
import org.pipeData.core.entity.User;
import org.pipeData.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SampleTest {

    @Autowired
    private IUserService service;

    @Test
    public void testSelect() {
//        List<User> userList = service.list();
        User user = service.selectByNameOrEmail("86799762139@qq.com");
        Assert.isTrue(user!=null, "");

        User user2 = service.selectByNameOrEmail("867997633339@qq.com");
        Assert.isTrue(user2==null, "");


    }

}
