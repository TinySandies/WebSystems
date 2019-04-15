package com.tinysand.system.access.implement.handlers;

import com.tinysand.system.access.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BeanHandler<R> implements ResultHandler<R> {
    private static final RowHandler rowHandler = new RowHandler();

    @Override
    public R handle(ResultSet resultSet) throws SQLException {
        return rowHandler.toBean(resultSet, returnedType);
    }

    private final Class<R> returnedType;

    public BeanHandler(Class<R> returnedType) {
        this.returnedType = returnedType;
    }

}
