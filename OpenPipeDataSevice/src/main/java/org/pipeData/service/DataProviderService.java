package org.pipeData.service;




import org.pipeData.core.data.provider.DataProviderSource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface DataProviderService {


    Object testConnection(DataProviderSource source) throws Exception;





}
