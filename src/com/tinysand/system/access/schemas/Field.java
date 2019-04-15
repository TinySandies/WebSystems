package com.tinysand.system.access.schemas;

public class Field {
    private String fieldName;
    private Object fieldValue;

    public Field(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    @SuppressWarnings("all")
    @Override
    public boolean equals(Object field) {
        String fieldName = ((Field) field)
                .getFieldName();
        return this.fieldName.equals(fieldName);
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }
}
