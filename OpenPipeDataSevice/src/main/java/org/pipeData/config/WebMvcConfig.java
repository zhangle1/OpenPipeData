package org.pipeData.config;

import org.apache.commons.lang3.StringUtils;
import org.pipeData.server.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Value("${openPipe.server.path-prefix}")
    private String pathPrefix;

    private final LoginInterceptor loginInterceptor;

    public WebMvcConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns(getPathPrefix() + "/**");


    }


    public String getPathPrefix(){

        return StringUtils.removeEnd(pathPrefix, "/");

    }


}
