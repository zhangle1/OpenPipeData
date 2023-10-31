package org.pipeData.service;




import org.pipeData.base.dto.ResponseData;
import org.pipeData.core.data.provider.Column;
import org.pipeData.core.data.provider.DataProviderSource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface DataProviderService {


    Object testConnection(DataProviderSource source) throws Exception;


    Set<String> readAllDatabases(DataProviderSource config) throws SQLException;

    Set<String> readTables(DataProviderSource config) throws SQLException;


    Set<Column> readTableColumns(DataProviderSource config) throws SQLException;
}
