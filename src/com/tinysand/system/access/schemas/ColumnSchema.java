package com.tinysand.system.access.schemas;

public class ColumnSchema {
    public ColumnSchema(Field column,
                        boolean isPrimary,
                        boolean isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
        this.column = column;
        this.isPrimary = isPrimary;
    }

    public ColumnSchema(Field column,
                        boolean isPrimary) {
        this(column, isPrimary, false);
    }

    private boolean isAutoIncrement;

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement
            (boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    private Field column;
    private boolean isPrimary;

    public Field getColumn() {
        return column;
    }

    public void setColumn(Field column) {
        this.column = column;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

}
