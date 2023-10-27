package org.pipeData.core.data.provider.jdbc;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Properties;

@Data
public class JdbcProperties {


    @NotBlank
    private String dbType;
    @NotBlank
    private String url;

    private String user;

    private String password;
    @NotBlank
    private String driverClass;

    private Properties properties;

    private boolean enableSpecialSql;

    @Override
    public String toString() {
        return "JdbcConnectionProperties{" +
                "dbType='" + dbType + '\'' +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
