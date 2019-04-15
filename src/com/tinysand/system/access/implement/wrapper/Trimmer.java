package com.tinysand.system.access.implement.wrapper;

import com.tinysand.system.util.Toolkit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;

@SuppressWarnings({"unused"})
public class Trimmer implements InvocationHandler {
    private final ResultSet resultSet;
    private Trimmer(final ResultSet resultSet) {
        this.resultSet = resultSet;
    }


    @Override
    public Object invoke(Object proxy, Method method,
                         Object[] args) throws Throwable {

        Object value = method.invoke(this.resultSet, args);
        if (("getObject".equals(method.getName()) ||
                "getString".equals(method.getName())) &&
                value instanceof String)
            return ((String) value).trim();
        else
            return value;
    }

    public static ResultSet trimmedResult(ResultSet resultSet) {
        return Toolkit.createResultSetProxy
                (new Trimmer(resultSet));
    }
}
