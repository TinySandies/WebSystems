package com.tinysand.system.services.implement;

import com.tinysand.system.access.implement.handlers.BeanHandler;
import com.tinysand.system.access.implement.AccessRunner;
import com.tinysand.system.access.implement.wrapper.Trimmer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.tinysand.system.access.BaseAccessor;
import com.tinysand.system.access.schemas.Field;
import com.tinysand.system.models.User;
import com.tinysand.system.services.ISystemEntrance;
import com.tinysand.system.util.StringUtils;
import com.tinysand.system.util.Toolkit;

public class SystemEntrance implements ISystemEntrance {
    private static final String QUERY_SPECIFIED_USER =
            "SELECT * FROM %s WHERE %s";
    private final BaseAccessor baseAccessor;

    public SystemEntrance() {
        baseAccessor = new AccessRunner() {
            @Override
            public ResultSet trimmedResult(ResultSet resultSet) {
                return Trimmer.trimmedResult(resultSet);
            }
        };
    }

    @Override
    public boolean registration(User user) throws SQLException {
        Objects.requireNonNull(user);
        return baseAccessor.insertWithBean
                (Toolkit.obtainConnection(), user) > 0;
    }

    @Override
    public boolean systemSignIn(User user) throws SQLException {
        Objects.requireNonNull(user);
        final String tableName = StringUtils.toUnderline(user.getClass()
                        .getSimpleName());
        List<Field> fields = ((AccessRunner) baseAccessor)
                .obtainValuableFields(user);

        final String condition = fields.stream()
                .map(Field::getFieldName)
                .collect(Collectors.joining(" = ? AND "))
                .concat(" = ?");

        User userBean  = baseAccessor.query(Toolkit.obtainConnection(),
                String.format(QUERY_SPECIFIED_USER, tableName,
                        condition), new BeanHandler<>(User.class),
                fields.stream()
                        .map(Field::getFieldValue).toArray());

        return Objects.nonNull(userBean);
    }

}
