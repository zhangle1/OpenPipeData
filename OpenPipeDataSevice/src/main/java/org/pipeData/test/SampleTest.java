package org.pipeData.test;


import com.baomidou.mybatisplus.core.toolkit.Assert;
import org.junit.jupiter.api.Test;
import org.pipeData.core.entity.User;
import org.pipeData.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SampleTest {

    @Autowired
    private IUserRepository service;

    @Test
    public void testSelect() {
//        List<User> userList = service.list();
        User user = service.selectByNameOrEmail("867997639@qq.com");
        Assert.isTrue(user!=null, "");

        User user2 = service.selectByNameOrEmail("867997633339@qq.com");
        Assert.isTrue(user2==null, "");


    }

}
