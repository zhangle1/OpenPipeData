package org.pipeData.core.data.provider;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class DataProviderSource {

    private String sourceId;

    private String type;

    private String name;

    private String database;

    private String table;

    private Map<String, Object> properties;

}
