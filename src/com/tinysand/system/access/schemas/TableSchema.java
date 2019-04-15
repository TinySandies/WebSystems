package com.tinysand.system.access.schemas;

import java.util.List;

public class TableSchema {
    private List<ColumnSchema> columnSchemas;
    private String tableName;

    public List<ColumnSchema> getColumnSchemas() {
        return columnSchemas;
    }

    public void setColumnSchemas
            (List<ColumnSchema> columnSchemas) {
        this.columnSchemas = columnSchemas;
    }

    public String getTableName() {
        return tableName;
    }

    public TableSchema(String tableName,
                       List<ColumnSchema> columnSchemas) {
        this.columnSchemas = columnSchemas;
        this.tableName = tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
