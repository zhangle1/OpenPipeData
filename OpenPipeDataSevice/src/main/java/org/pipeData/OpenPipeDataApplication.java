package org.pipeData;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@SpringBootApplication(scanBasePackages = {"org.pipeData"})
public class OpenPipeDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenPipeDataApplication.class, args);
    }

}
