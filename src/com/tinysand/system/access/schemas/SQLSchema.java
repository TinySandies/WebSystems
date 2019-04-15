package com.tinysand.system.access.schemas;

import java.util.List;

public class SQLSchema {
    private List<Field> fields;
    private String SQL;

    public List<Field> getFields() {
        return fields;
    }

    public void setFields
            (List<Field> fields) {
        this.fields = fields;
    }

    public String getSQL() {
        return SQL;
    }

    public void setSQL(String SQL) {
        this.SQL = SQL;
    }

    public SQLSchema(String sql,
                     List<Field> fields) {
        this.SQL = sql;
        this.fields = fields;
    }
}
