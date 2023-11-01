package org.pipeData.controller;


import io.swagger.v3.oas.annotations.Operation;
import org.pipeData.base.dto.ResponseData;
import org.pipeData.core.data.provider.Column;
import org.pipeData.core.data.provider.DataProviderSource;
import org.pipeData.service.DataProviderService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/data-provider")
public class DataProviderController extends BaseController {

    private final DataProviderService dataProviderService;


    public DataProviderController(DataProviderService dataProviderService) {
        this.dataProviderService = dataProviderService;
    }

    @Operation(description = "获得支持数据库类型列表")
    @GetMapping(value = "/providers")
    public ResponseData<List<String>> listSupportedDataProviders() {
        return ResponseData.success(List.of("成功了"));
    }

    @Operation(description = "测试连接")
    @PostMapping(value = "/test")
    public ResponseData<Object> testConnection(@RequestBody DataProviderSource config) throws Exception {
        return ResponseData.success(dataProviderService.testConnection(config));
    }

    @Operation(description = "数据库列表")
    @PostMapping(value = "/databases")
    public ResponseData<Set<String>> listDatabases(@RequestBody DataProviderSource config) throws SQLException {
//        checkBlank(sourceId, "sourceId");
        return ResponseData.success(dataProviderService.readAllDatabases(config));
    }

    @Operation(description = "获取所有表格")
    @PostMapping(value = "/tables")
    public ResponseData<Set<String>> listTables(@RequestBody DataProviderSource config) throws SQLException {
//        checkBlank(sourceId, "sourceId");
//        checkBlank(database, "database");
        return ResponseData.success(dataProviderService.readTables(config));
    }


    @Operation(description = "获取表格信息")
    @PostMapping(value = "/columns")
    public ResponseData<Set<Column>>getTableInfo(
            @RequestBody DataProviderSource config
    ) throws SQLException {
        return ResponseData.success(dataProviderService.readTableColumns(config));
    }

//    @Operation(description = "获取所有表格信息")
//    @GetMapping(value = "/{sourceId}/{database}/{table}/columns")
//    public ResponseData<Set<Column>> getTableInfo(@PathVariable String sourceId,
//                                                  @PathVariable String database,
//                                                  @PathVariable String table) throws SQLException {
//
//        return ResponseData.success(dataProviderService.readTableColumns(sourceId, database, table));
//    }


}
