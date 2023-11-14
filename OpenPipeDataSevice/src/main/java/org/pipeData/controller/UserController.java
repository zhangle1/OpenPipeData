package org.pipeData.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.pipeData.base.dto.ResponseData;
import org.pipeData.core.base.annotations.SkipLogin;
import org.pipeData.core.base.consts.Const;
import org.pipeData.core.entity.UserBaseInfo;
import org.pipeData.security.base.PasswordToken;
import org.pipeData.server.base.params.UserLoginParam;
import org.pipeData.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
public class UserController  extends BaseController{

//    public ResponseData

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }


    @SkipLogin
    @Operation(description = "用户登录")
    @PostMapping(value = "/login")
    public ResponseData<UserBaseInfo> login(@RequestBody UserLoginParam loginParam,
                                            HttpServletResponse response) {
        PasswordToken passwordToken = new PasswordToken(loginParam.getUsername(),
                loginParam.getPassword(),
                System.currentTimeMillis());
        String token = userService.login(passwordToken);
        response.setHeader(Const.TOKEN, token);
        return ResponseData.success(new UserBaseInfo(securityManager.getCurrentUser()));


    }



}
