package com.tinysand.system.access;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultHandler<T> {
    T handle(ResultSet resultSet) throws SQLException;

    static PropertyDescriptor[] getProperties(Class type)
            throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo
                (type, Object.class);
        return beanInfo.getPropertyDescriptors();
    }
}
