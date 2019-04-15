package com.tinysand.system.access;

import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("all") public interface BaseAccessor {
    int insertWithBean(Connection connection, Object javaBean)
            throws SQLException;

    int updateWithBean(Connection connection, Object javaBean)
            throws SQLException;

    int deleteWithBean(Connection connection, Object javaBean)
            throws SQLException;

    int[] batchInsertWithBean(Connection connection,
                              Object[] javaBeans)
            throws SQLException;

    int[] batchUpdateWithBean(Connection connection,
                              Object[] javaBeans)
            throws SQLException;

    int[] batchDeletionWithBean(Connection connection,
                                Object[] javaBeans)
            throws SQLException;

    int executeWithParam(Connection connection, String sql,
                         Object[] params) throws SQLException;

    int[] executeBatchWithParam(Connection connection,
                                String sql, Object[][] params)
            throws SQLException;

    <R> R query(Connection connection, Class<?> beanClass,
                ResultHandler<R> resultHandler)
            throws SQLException;

    <R> R query(Connection connection, String sql,
                ResultHandler<R> resultHandler,
                Object[] params) throws SQLException;
}
