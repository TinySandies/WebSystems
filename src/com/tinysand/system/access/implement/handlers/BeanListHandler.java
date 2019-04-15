package com.tinysand.system.access.implement.handlers;

import com.tinysand.system.access.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BeanListHandler<R> implements ResultHandler<List<R>> {
    private static final RowHandler rowHandler = new RowHandler();

    private final Class<R> expectedType;

    public BeanListHandler(Class<R> expectedType) {
        this.expectedType = expectedType;
    }

    @Override
    public List<R> handle(ResultSet resultSet) throws SQLException {
        return rowHandler.toBeanList(resultSet, expectedType);
    }
}
