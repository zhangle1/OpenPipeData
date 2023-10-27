package org.pipeData.core.data.provider.jdbc.adapters;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pipeData.core.base.exception.Exceptions;
import org.pipeData.core.common.BeanUtils;
import org.pipeData.core.data.provider.JdbcDataProvider;
import org.pipeData.core.data.provider.jdbc.JdbcDriverInfo;
import org.pipeData.core.data.provider.jdbc.JdbcProperties;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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


    public Set<String> readAllDatabases() throws SQLException {
        Set<String> databases = new HashSet<>();
        try (Connection conn = getConn()) {
            DatabaseMetaData metaData = conn.getMetaData();
            boolean isCatalog = isReadFromCatalog(conn);
            ResultSet rs = null;
            if (isCatalog) {
                rs = metaData.getCatalogs();
            } else {
                rs = metaData.getSchemas();
                log.info("Database 'catalogs' is empty, get databases with 'schemas'");
            }

//            String currDatabase = readCurrDatabase(conn, isCatalog);
//            if (StringUtils.isNotBlank(currDatabase)) {
//                return Collections.singleton(currDatabase);
//            }

            while (rs.next()) {
                String database = rs.getString(1);
                databases.add(database);
            }
            return databases;
        }
    }

    protected boolean isReadFromCatalog(Connection conn) throws SQLException {
        return conn.getMetaData().getCatalogs().next();
    }

    protected String readCurrDatabase(Connection conn, boolean isCatalog) throws SQLException {
        return isCatalog ? conn.getCatalog() : conn.getSchema();
    }


    protected Connection getConn() throws SQLException {
        return dataSource.getConnection();
    }
}


