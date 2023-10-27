package org.pipeData.core.data.provider;


import lombok.extern.slf4j.Slf4j;
import org.pipeData.base.dto.ResponseData;
import org.pipeData.core.base.exception.Exceptions;
import org.pipeData.core.data.provider.optimize.DataProviderExecuteOptimizer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ProviderManager extends DataProviderExecuteOptimizer implements DataProviderManager {

    private static final Map<String, DataProvider> cachedDataProviders = new ConcurrentHashMap<>();

    public Map<String, DataProvider> getDataProviders() {
        if (cachedDataProviders.isEmpty()) {
            synchronized (ProviderManager.class) {
                if (cachedDataProviders.isEmpty()) {
                    ServiceLoader<DataProvider> load = ServiceLoader.load(DataProvider.class);
                    for (DataProvider dataProvider : load) {
                        try {
                            cachedDataProviders.put(dataProvider.getType(), dataProvider);
                        } catch (IOException e) {
                            log.error("", e);
                        }
                    }
                }
            }
        }
        return cachedDataProviders;
    }


    @Override
    public Object testConnection(DataProviderSource source) throws Exception {
        return getDataProviderService(source.getType()).test(source);
    }

    @Override
    public Set<String> readAllDatabases(DataProviderSource source) throws SQLException {
        return getDataProviderService(source.getType()).readAllDatabases(source);
    }


    private DataProvider getDataProviderService(String type) {
        DataProvider dataProvider = getDataProviders().get(type);
        if (dataProvider == null) {
            Exceptions.msg("No data provider type " + type);
        }
        return dataProvider;
    }


}
