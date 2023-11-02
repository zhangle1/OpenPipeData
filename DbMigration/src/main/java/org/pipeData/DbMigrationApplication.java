
package org.pipeData;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@SpringBootApplication(scanBasePackages = {"org.pipeData"})
public class DbMigrationApplication {

    @Autowired
    private DataSource dataSource;

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:/db/changelog/master.xml");
        liquibase.setContexts("development");
        liquibase.setShouldRun(true);
        return liquibase;
    }

    public static void main(String[] args) {
        SpringApplication.run(DbMigrationApplication.class, args);
    }
}