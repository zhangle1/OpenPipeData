package org.pipeData.core.data.provider.jdbc.adapters;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pipeData.core.base.exception.Exceptions;
import org.pipeData.core.common.BeanUtils;
import org.pipeData.core.data.provider.Column;
import org.pipeData.core.data.provider.DataProviderSource;
import org.pipeData.core.data.provider.ForeignKey;
import org.pipeData.core.data.provider.JdbcDataProvider;
import org.pipeData.core.data.provider.jdbc.DataTypeUtils;
import org.pipeData.core.data.provider.jdbc.JdbcDriverInfo;
import org.pipeData.core.data.provider.jdbc.JdbcProperties;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Slf4j
@Setter
@Getter
public class JdbcDataProviderAdapter implements Cloneable {

    protected DataSource dataSource;

    protected JdbcProperties jdbcProperties;

    protected JdbcDriverInfo driverInfo;


    protected static final String PKTABLE_CAT = "PKTABLE_CAT";

    protected static final String PKTABLE_NAME = "PKTABLE_NAME";

    protected static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";

    protected static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";


    public final void init(JdbcProperties jdbcProperties, JdbcDriverInfo driverInfo) {
        try {
            this.jdbcProperties = jdbcProperties;
            this.driverInfo = driverInfo;
            this.dataSource = JdbcDataProvider.getDataSourceFactory().createDataSource(jdbcProperties);

        } catch (Exception e) {
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

    public Set<String> readAllTables(String database) throws SQLException {
        try (Connection conn = getConn()) {
            Set<String> tables = new HashSet<>();
            DatabaseMetaData metadata = conn.getMetaData();
            String catalog = null;
            String schema = null;
            if (isReadFromCatalog(conn)) {
                catalog = database;
                schema = conn.getSchema();
            } else {
                schema = database;
            }
            try (ResultSet rs = metadata.getTables(catalog, schema, "%", new String[]{"TABLE", "VIEW"})) {
                while (rs.next()) {
                    String tableName = rs.getString(3);
                    tables.add(tableName);
                }
            }
            return tables;
        }
    }

    public Set<Column> readTableColumn(DataProviderSource source) throws SQLException {
        try (Connection conn = getConn()) {
            Set<Column> columnSet = new HashSet<>();
            DatabaseMetaData metadata = conn.getMetaData();
            Map<String, List<ForeignKey>> importedKeys = getImportedKeys(metadata, source.getDatabase(), source.getTable());
            try (ResultSet columns = metadata.getColumns(source.getDatabase(), null, source.getTable(), null)) {
                while (columns.next()) {
                    Column column = readTableColumn(columns);
                    column.setForeignKeys(importedKeys.get(column.columnKey()));
                    columnSet.add(column);
                }
            }
            return columnSet;
        }
    }
    
    private Column readTableColumn(ResultSet columnMetadata) throws SQLException {
        Column column = new Column();
        column.setName(columnMetadata.getString(4));
        column.setType(DataTypeUtils.jdbcType2DataType(columnMetadata.getInt(5)));
        return column;

    }



    /**
     * 获取表的外键关系
     */
    protected Map<String, List<ForeignKey>> getImportedKeys(DatabaseMetaData metadata, String database, String table) throws SQLException {
        HashMap<String, List<ForeignKey>> keyMap = new HashMap<>();
        try (ResultSet importedKeys = metadata.getImportedKeys(database, null, table)) {
            while (importedKeys.next()) {
                ForeignKey foreignKey = new ForeignKey();
                foreignKey.setDatabase(importedKeys.getString(PKTABLE_CAT));
                foreignKey.setTable(importedKeys.getString(PKTABLE_NAME));
                foreignKey.setColumn(importedKeys.getString(PKCOLUMN_NAME));
                keyMap.computeIfAbsent(importedKeys.getString(FKCOLUMN_NAME), key -> new ArrayList<>()).add(foreignKey);
            }
        } catch (SQLFeatureNotSupportedException e) {
            log.warn(e.getMessage());
        }
        return keyMap;
    }
}


