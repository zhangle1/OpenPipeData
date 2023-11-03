package org.pipeData.server.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pipeData.core.base.annotations.SkipLogin;
import org.pipeData.core.base.exception.Exceptions;
import org.pipeData.security.exception.AuthException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    public LoginInterceptor() {


    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }


        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (handlerMethod.getMethodAnnotation(SkipLogin.class) != null) {
            return true;
        }

        Exceptions.tr(AuthException.class, "login.not-login");

        return false;
    }
}
