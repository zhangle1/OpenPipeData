package org.pipeData.core.data.provider.jdbc.adapters;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pipeData.core.base.exception.Exceptions;
import org.pipeData.core.common.BeanUtils;
import org.pipeData.core.data.provider.JdbcDataProvider;
import org.pipeData.core.data.provider.jdbc.JdbcDriverInfo;
import org.pipeData.core.data.provider.jdbc.JdbcProperties;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@Setter
@Getter
public class JdbcDataProviderAdapter  implements  Cloneable{

    protected DataSource dataSource;

    protected JdbcProperties jdbcProperties;

    protected JdbcDriverInfo driverInfo;

    public final void init(JdbcProperties jdbcProperties, JdbcDriverInfo driverInfo){
        try{
            this.jdbcProperties = jdbcProperties;
            this.driverInfo = driverInfo;
            this.dataSource = JdbcDataProvider.getDataSourceFactory().createDataSource(jdbcProperties);

        } catch (Exception e){
            log.error("data provider init error", e);
            Exceptions.e(e);

        }

    }

    public boolean test(JdbcProperties properties) {
        BeanUtils.validate(properties);
        try {
            Class.forName(properties.getDriverClass());
        } catch (ClassNotFoundException e) {
            String errMsg = "Driver class not found " + properties.getDriverClass();
            log.error(errMsg, e);
            Exceptions.e(e);
        }
        try {
            DriverManager.getConnection(properties.getUrl(), properties.getUser(), properties.getPassword());
        } catch (SQLException sqlException) {
            Exceptions.e(sqlException);
        }
        return true;
    }


}


