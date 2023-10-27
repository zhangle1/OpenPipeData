package org.pipeData.core.data.provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pipeData.core.base.AutoCloseBean;

import java.io.IOException;
import java.io.InputStream;

public abstract class DataProvider extends AutoCloseBean {
    static ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public DataProviderInfo getBaseInfo() throws IOException {
        DataProviderConfigTemplate template = getConfigTemplate();
        DataProviderInfo dataProviderInfo = new DataProviderInfo();
        dataProviderInfo.setName(template.getName());
        dataProviderInfo.setType(template.getType());
        return dataProviderInfo;
    }

    /**
     * 读取DataProvider的配置模板，配置模板的信息是创建这个DataProvider实例时所需的信息。
     * <p>
     * Read the configuration template of the DataProvider. The configuration template information is needed to create the instance of the DataProvider.
     * This template is configured by the DataProvider according to the information it needs and stored in JSON format.The default save path is classpath:/data-provider.json
     *
     * @return 配置模板
     * @throws IOException 配置文件不存在或格式错误时抛出异常
     */
    public DataProviderConfigTemplate getConfigTemplate() throws IOException {
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(getConfigFile())) {
            return MAPPER.readValue(resourceAsStream, DataProviderConfigTemplate.class);
        }
    }

    /**
     * 返回DataProvider的type，type的值由实现者定义。
     * 这个type值作为DataProvider的唯一标识，必须是全局唯一的。
     * <p>
     * Returns the dataProvider's type.The value of type is defined by the implementation.
     * This type, as the unique identifier of the DataProvider, must be globally unique.
     *
     * @return dataProvider's type
     */
    public String getType() throws IOException {
        return getBaseInfo().getType();
    }


    public abstract String getConfigFile();


    @Override
    public int timeoutMillis() {
        return Integer.MAX_VALUE;
    }
    /**
     * 测试DataProvider的连接。连接成功返回true，否则返回false。
     * <p>
     * Test the connection to the data source.Returns true on success of the connection or false on failure.
     *
     * @param source 一个Json字符串
     * @return Returns true on success of the connection or false on failure.
     */
    public abstract Object test(DataProviderSource source) throws Exception;




}
