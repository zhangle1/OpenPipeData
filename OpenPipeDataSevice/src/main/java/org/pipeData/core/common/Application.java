package org.pipeData.core.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;



@Component
@Slf4j
public class Application  implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Application.context = applicationContext;
    }


    public static String getFileBasePath() {
        String path = getProperty("pipeData.env.file-path");
        if (path.startsWith(".")) {
            path = path.replace(".", userDir());
        }
        return StringUtils.appendIfMissing(path, "/");
    }

    public static String getProperty(String key) {
        return context.getEnvironment().getProperty(key);
    }

    public static String userDir() {
        return StringUtils.removeEnd(System.getProperty("user.dir"), "/");
    }
}
