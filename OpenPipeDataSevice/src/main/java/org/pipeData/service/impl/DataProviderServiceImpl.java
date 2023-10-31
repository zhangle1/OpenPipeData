package org.pipeData.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.pipeData.base.dto.ResponseData;
import org.pipeData.core.data.provider.Column;
import org.pipeData.core.data.provider.DataProviderManager;
import org.pipeData.core.data.provider.DataProviderSource;
import org.pipeData.service.BaseService;
import org.pipeData.service.DataProviderService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class DataProviderServiceImpl extends BaseService implements DataProviderService {


    private final DataProviderManager dataProviderManager;

    public DataProviderServiceImpl(DataProviderManager dataProviderManager) {
        this.dataProviderManager = dataProviderManager;
    }

    @Override
    public Object testConnection(DataProviderSource source) throws Exception {
        Map<String, Object> properties = source.getProperties();
        if (!CollectionUtils.isEmpty(properties)) {
            for (String key : properties.keySet()) {
                Object val = properties.get(key);
                if (val instanceof String) {
                    properties.put(key, (val.toString()));
                }
            }
        }
        return dataProviderManager.testConnection(source);
    }

    @Override
    public Set<String> readAllDatabases(DataProviderSource config) throws SQLException {

        return dataProviderManager.readAllDatabases(config);
    }

    @Override
    public Set<String> readTables(DataProviderSource config) throws SQLException {
        return dataProviderManager.readTables(config,config.getDatabase());
    }

    @Override
    public Set<Column> readTableColumns(DataProviderSource config) throws SQLException {
        return dataProviderManager.readTableColumns(config);
    }
}
