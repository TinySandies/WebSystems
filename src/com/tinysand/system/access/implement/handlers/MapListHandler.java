package com.tinysand.system.access.implement.handlers;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.tinysand.system.access.ResultHandler;

public class MapListHandler implements ResultHandler
        <List<Map<String, String>>> {
    private final RowHandler rowHandler = new RowHandler();

    @Override
    public List<Map<String, String>> handle
            (ResultSet resultSet) throws SQLException {
        return rowHandler.toMapList(resultSet);
    }
}
