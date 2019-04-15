package com.tinysand.system.access.implement.handlers;


import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.beans.PropertyDescriptor;

import com.tinysand.system.util.StringUtils;
import com.tinysand.system.access.ResultHandler;

class RowHandler {
    <R> List<R> toBeanList(final ResultSet resultSet,
                           final Class<R> beanClass) throws SQLException {
        Objects.requireNonNull(resultSet);
        Objects.requireNonNull(beanClass);
        List<R> beanList = new ArrayList<>();
        while (resultSet.next()) {
            try {
                R bean = beanClass.getConstructor().newInstance();
                for (PropertyDescriptor property : ResultHandler
                        .getProperties(beanClass)) {
                    Object value = resultSet.getObject(StringUtils
                            .toUnderline(property.getName()));

                    property.getWriteMethod().invoke(bean, value);
                }
                beanList.add(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return beanList;
    }

    List<Map<String, String>> toMapList(final ResultSet resultSet)
            throws SQLException {
        Objects.requireNonNull(resultSet);
        List<Map<String, String>> resultList = new ArrayList<>();
        while (resultSet.next()) {
            ResultSetMetaData resultMeta = resultSet.getMetaData();
            Map<String, String> dataMap = new HashMap<>();
            for (int index = 1; index <= resultMeta
                    .getColumnCount(); index++) {
                Object value = resultSet.getObject(index);
                dataMap.put(getColumnName(resultMeta, index),
                        String.valueOf(value));
            }

            resultList.add(dataMap);
        }
        return resultList;
    }

    Map<String, String> toMap(final ResultSet resultSet)
            throws SQLException {
        Objects.requireNonNull(resultSet);
        Map<String, String> dataMap = null;
        if (resultSet.next()) {
            ResultSetMetaData resultMeta = resultSet.getMetaData();
            dataMap = new HashMap<>();
            for (int index = 1; index <= resultMeta.getColumnCount();
                 index++) {
                Object value = resultSet.getObject(index);
                dataMap.put(getColumnName(resultMeta, index),
                        String.valueOf(value));
            }
        }
        return dataMap;
    }

    <R> R toBean(final ResultSet resultSet, final Class<R> beanClass)
            throws SQLException {
        Objects.requireNonNull(resultSet);
        Objects.requireNonNull(beanClass);
        R bean = null;
        if (resultSet.next()) {
            try {
                bean = beanClass.getConstructor().newInstance();
                for (PropertyDescriptor property : ResultHandler
                        .getProperties(beanClass)) {
                    Object value = resultSet.getObject(StringUtils
                            .toUnderline(property.getName()));
                    property.getWriteMethod().invoke(bean, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bean;
    }

    private String getColumnName(final ResultSetMetaData resultMeta,
                                 final int index) throws SQLException {
        String columnName = resultMeta.getColumnLabel(index);
        if (!Objects.nonNull(columnName)
                || columnName.trim().length() == 0) {
            columnName = resultMeta.getColumnName(index);
        }
        return columnName;
    }
}
