package org.pipeData.server.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pipeData.core.base.annotations.SkipLogin;
import org.pipeData.core.base.exception.Exceptions;
import org.pipeData.security.exception.AuthException;
import org.pipeData.security.manager.OpenPipeSecurityManager;
import org.springdoc.webmvc.api.MultipleOpenApiWebMvcResource;
import org.springdoc.webmvc.ui.SwaggerConfigResource;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    private final OpenPipeSecurityManager securityManager;
    public LoginInterceptor(OpenPipeSecurityManager securityManager) {
        this.securityManager = securityManager;
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
        Object controller = handlerMethod.getBean();

        // 判断特定的 Controller 类，根据需要进行修改
        if (controller instanceof SwaggerConfigResource || controller instanceof MultipleOpenApiWebMvcResource) {
            // 特定 Controller，放行请求
            return true;
        }

        Exceptions.tr(AuthException.class, "login.not-login");
        return false;
    }
}
