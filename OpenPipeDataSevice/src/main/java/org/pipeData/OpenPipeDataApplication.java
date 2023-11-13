package org.pipeData;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;

@Configuration
@EnableWebMvc
@SpringBootApplication(scanBasePackages = {"org.pipeData"})
@MapperScan("org.pipeData.repository.mapper")
public class OpenPipeDataApplication implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${server.port}")
    private int serverPort;

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(OpenPipeDataApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String activeProfile = environment.getProperty("spring.profiles.active");
        if(activeProfile.equals("dev")){
            openBrowser();
        }

    }
    private void openBrowser() {
        String url = "http://localhost:" + serverPort+"/swagger-ui/index.html";
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                // Windows
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                // Mac OS
                Runtime.getRuntime().exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux") || os.contains("bsd")) {
                // Linux or Unix
                Runtime.getRuntime().exec("xdg-open " + url);
            }
        } catch (IOException e) {
            // 处理异常
        }
    }

}
