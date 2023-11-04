package org.pipeData.server.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.pipeData.core.common.Application;
import org.springframework.web.servlet.HandlerInterceptor;

public class BasicValidRequestInterceptor implements HandlerInterceptor {

    private String apiPrePath = null;

    private String contextPath = null;

    private static final String resourcePath = "/resources";

    private static final String staticPath = "/static";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!isValidRequest(request)) {
            request.getRequestDispatcher("/").forward(request, response);
            return false;
        }
        return true;
    }

    private boolean isValidRequest(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        contextPath = getContextPath();
        if (requestURI.startsWith(contextPath)) {
            requestURI = StringUtils.removeStart(requestURI, contextPath);
            requestURI = StringUtils.prependIfMissing(requestURI, "/");
        }
        return requestURI.startsWith(getApiPrePath())
                || requestURI.equals("/")
                || requestURI.equals("/index.html")
                || requestURI.equals("/favicon.ico")
                || requestURI.equals("/manifest.json")
                || requestURI.equals("/editor.worker.js")
                || requestURI.startsWith(resourcePath)
                || requestURI.startsWith("/swagger")
                || requestURI.startsWith("/swagger-ui")
                || requestURI.startsWith("/swagger-ui/swagger-initializer.js")
                || requestURI.startsWith("/webjars")
                || requestURI.startsWith("/custom-chart-plugins")
                || requestURI.startsWith("/antd")
                || requestURI.startsWith("/v2/")
                || requestURI.startsWith("/v3/api-docs/swagger-config")
                || requestURI.startsWith("/share")
                || requestURI.startsWith(staticPath);

    }

    private String getApiPrePath() {
        if (apiPrePath == null) {
            apiPrePath = Application.getProperty("openPipe.server.path-prefix");
        }
        return apiPrePath;
    }


    private String getContextPath(){
        if(contextPath==null){
            contextPath = Application.getServerPrefix();
        }
        return contextPath;
    }
}
