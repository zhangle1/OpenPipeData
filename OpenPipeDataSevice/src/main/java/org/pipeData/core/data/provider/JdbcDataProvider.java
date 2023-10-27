package org.pipeData.core.data.provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pipeData.core.base.exception.Exceptions;
import org.pipeData.core.common.FileUtils;
import org.pipeData.core.data.provider.base.DataProviderException;
import org.pipeData.core.data.provider.jdbc.DataSourceFactory;
import org.pipeData.core.data.provider.jdbc.DataSourceFactoryDruidImpl;
import org.pipeData.core.data.provider.jdbc.JdbcDriverInfo;
import org.pipeData.core.data.provider.jdbc.JdbcProperties;
import org.pipeData.core.data.provider.jdbc.adapters.JdbcDataProviderAdapter;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@Slf4j
public class JdbcDataProvider  extends  DataProvider{

    public static final String DEFAULT_ADAPTER = "org.pipeData.core.data.provider.jdbc.adapters.JdbcDataProviderAdapter";
    private static final String JDBC_DRIVER_BUILD_IN = "/jdbc-driver.yml";

    private static final String JDBC_DRIVER_EXT = "config/jdbc-driver-ext.yml";

    public static final String DB_TYPE = "dbType";

    public static final String USER = "user";

    public static final String PASSWORD = "password";

    public static final String URL = "url";

    public static final String DRIVER_CLASS = "driverClass";

    public static final String ENABLE_SPECIAL_SQL = "enableSpecialSQL";


    /**
     * 获取连接时最大等待时间（毫秒）
     */
    public static final Integer DEFAULT_MAX_WAIT = 5000;

    private final Map<String, JdbcDataProviderAdapter> cachedProviders = new ConcurrentSkipListMap<>();


    @Override
    public String getConfigFile() {
        return "jdbc-data-provider.json";
    }

    @Override
    public Object test(DataProviderSource source) throws Exception {

        JdbcProperties jdbcProperties = conv2JdbcProperties(source);

        return ProviderFactory.createDataProvider(jdbcProperties, false).test(jdbcProperties);
    }

    @Override
    public Set<String> readAllDatabases(DataProviderSource source) throws SQLException {
        JdbcDataProviderAdapter adapter = matchProviderAdapter(source);
        return adapter.readAllDatabases();
    }


    private JdbcDataProviderAdapter matchProviderAdapter(DataProviderSource source) {
        JdbcDataProviderAdapter adapter;
        adapter = cachedProviders.get(source.getSourceId());
        if (adapter != null) {
            return adapter;
        }
        adapter = ProviderFactory.createDataProvider(conv2JdbcProperties(source), true);
        cachedProviders.put(source.getSourceId(), adapter);
        return adapter;
    }


    private JdbcProperties conv2JdbcProperties(DataProviderSource config) {
        JdbcProperties jdbcProperties = new JdbcProperties();
        jdbcProperties.setDbType(config.getProperties().get(DB_TYPE).toString().toUpperCase());
        jdbcProperties.setUrl(config.getProperties().get(URL).toString());
        Object user = config.getProperties().get(USER);
        if (user != null && StringUtils.isNotBlank(user.toString())) {
            jdbcProperties.setUser(user.toString());
        }
        Object password = config.getProperties().get(PASSWORD);
        if (password != null && StringUtils.isNotBlank(password.toString())) {
            jdbcProperties.setPassword(password.toString());
        }
        String driverClass = config.getProperties().getOrDefault(DRIVER_CLASS, "").toString();
        jdbcProperties.setDriverClass(StringUtils.isBlank(driverClass) ?
                ProviderFactory.getJdbcDriverInfo(jdbcProperties.getDbType()).getDriverClass() :
                driverClass);

        Object enableSpecialSQL = config.getProperties().get(ENABLE_SPECIAL_SQL);

        if (enableSpecialSQL != null && "true".equals(enableSpecialSQL.toString())) {
            jdbcProperties.setEnableSpecialSql(true);
        }

        Object properties = config.getProperties().get("properties");
        if (properties != null) {
            if (properties instanceof Map) {
                Properties prop = new Properties();
                prop.putAll((Map) properties);
                jdbcProperties.setProperties(prop);
            }
        }
        return jdbcProperties;
    }


    @Override
    public void close() throws IOException {

    }


    public static DataSourceFactory<? extends DataSource> getDataSourceFactory() {
        return new DataSourceFactoryDruidImpl();
    }



    public static class ProviderFactory{


        private static final Map<String, JdbcDriverInfo> jdbcDriverInfoMap = new ConcurrentSkipListMap<>();

        public static JdbcDataProviderAdapter createDataProvider(JdbcProperties prop, boolean init) {
            List<JdbcDriverInfo> jdbcDriverInfos = loadDriverInfoFromResource();

            List<JdbcDriverInfo> driverInfos = jdbcDriverInfos.stream().filter(item -> prop.getDbType().equals(item.getDbType()))
                    .collect(Collectors.toList());

            if (driverInfos.size() == 0) {
                Exceptions.tr(DataProviderException.class, "message.provider.jdbc.dbtype", prop.getDbType());
            }
            if (driverInfos.size() > 1) {
                Exceptions.msg("Duplicated dbType " + prop.getDbType());
            }
            JdbcDriverInfo driverInfo = driverInfos.get(0);

            if (StringUtils.isNotBlank(prop.getDriverClass())) {
                driverInfo.setDriverClass(prop.getDriverClass());
            }
            JdbcDataProviderAdapter adapter = null;
            try {
                if (StringUtils.isNotBlank(driverInfo.getAdapterClass())) {
                    try {
                        Class<?> aClass = Class.forName(driverInfo.getAdapterClass());
                        adapter = (JdbcDataProviderAdapter) aClass.newInstance();
                    } catch (Exception e) {
                        log.error("Jdbc adapter class (" + driverInfo.getAdapterClass() + ") load error.use default adapter");
                    }
                }
                if (adapter == null) {
                    adapter = (JdbcDataProviderAdapter) Class.forName(DEFAULT_ADAPTER).newInstance();
                }
            } catch (Exception e) {
                log.error("Jdbc adapter class load error ", e);
            }
            if (adapter == null) {
                Exceptions.tr(DataProviderException.class, "message.provider.jdbc.create.error", prop.getDbType());
            }
            if (init) {
                adapter.init(prop, driverInfo);
            }
            return adapter;
        }

        private static JdbcDriverInfo getJdbcDriverInfo(String dbType) {
            if (jdbcDriverInfoMap.isEmpty()) {
                for (JdbcDriverInfo jdbcDriverInfo : loadDriverInfoFromResource()) {
                    jdbcDriverInfoMap.put(jdbcDriverInfo.getDbType(), jdbcDriverInfo);
                }
            }
            return jdbcDriverInfoMap.get(dbType);
        }


        private static List<JdbcDriverInfo> loadDriverInfoFromResource() {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            //Build in database types
            Map<String, Map<String, String>> buildIn = loadYml(JDBC_DRIVER_BUILD_IN);
            // user ext database types
            Map<String, Map<String, String>> extDrivers = loadYml(new File(FileUtils.concatPath(System.getProperty("user.dir"), JDBC_DRIVER_EXT)));
            if (!CollectionUtils.isEmpty(extDrivers)) {
                for (String key : extDrivers.keySet()) {
                    Map<String, String> driver = buildIn.get(key);
                    if (driver == null) {
                        buildIn.put(key, extDrivers.get(key));
                    } else {
                        driver.putAll(extDrivers.get(key));
                    }
                }
            }

            return buildIn.entrySet().stream().map(entry -> {
                try {
                    JdbcDriverInfo jdbcDriverInfo = objectMapper.convertValue(entry.getValue(), JdbcDriverInfo.class);
                    if (StringUtils.isBlank(jdbcDriverInfo.getAdapterClass())) {
                        jdbcDriverInfo.setAdapterClass(DEFAULT_ADAPTER);
                    }
                    // default to quote all identifiers ,  for support special column names and most databases
                    if (jdbcDriverInfo.getQuoteIdentifiers() == null) {
                        jdbcDriverInfo.setQuoteIdentifiers(true);
                    }
                    jdbcDriverInfo.setDbType(jdbcDriverInfo.getDbType().toUpperCase());
                    return jdbcDriverInfo;
                } catch (Exception e) {
                    log.error("DbType " + entry.getKey() + " driver read Exception", e);
                }
                return null;
            }).filter(Objects::nonNull).sorted(Comparator.comparing(JdbcDriverInfo::getDbType)).collect(Collectors.toList());
        }

        private static Map<String, Map<String, String>> loadYml(String file) {
            try (InputStream inputStream = ProviderFactory.class.getResourceAsStream(file)) {
                Yaml yaml = new Yaml();
                return yaml.loadAs(inputStream, HashMap.class);
            } catch (Exception e) {
                Exceptions.e(e);
            }
            return null;
        }

        private static Map<String, Map<String, String>> loadYml(File file) {
            try (InputStream inputStream = new FileInputStream(file)) {
                Yaml yaml = new Yaml();
                return yaml.loadAs(inputStream, HashMap.class);
            } catch (Exception e) {
                Exceptions.e(e);
            }
            return null;
        }
    }
}
