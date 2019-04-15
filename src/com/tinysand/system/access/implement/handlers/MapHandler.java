package com.tinysand.system.access.implement.handlers;

import com.tinysand.system.access.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class MapHandler implements ResultHandler
        <Map<String, String>> {
    private static final RowHandler rowHandler = new RowHandler();

    @Override
    public Map<String, String> handle(ResultSet resultSet)
            throws SQLException {
        return rowHandler.toMap(resultSet);
    }
}
