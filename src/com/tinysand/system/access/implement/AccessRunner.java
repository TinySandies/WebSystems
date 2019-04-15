package com.tinysand.system.access.implement;

import com.tinysand.system.access.BaseAccessor;
import com.tinysand.system.access.Batch;
import com.tinysand.system.access.ResultHandler;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.tinysand.system.access.schemas.Field;
import com.tinysand.system.access.schemas.ColumnSchema;
import com.tinysand.system.access.schemas.SQLSchema;
import com.tinysand.system.access.schemas.TableSchema;
import com.tinysand.system.util.StringUtils;
import com.tinysand.system.util.Toolkit;

import static com.tinysand.system.util.Toolkit.obtainConnection;

@SuppressWarnings("unused")
public class AccessRunner implements BaseAccessor {
    private static Map<String, TableSchema> tableSchemas;
    static {
        tableSchemas = cacheTableSchemas(obtainConnection());
    }

    /**
     * 缓存数据库中所有的表的表结构信息，需要手动关闭{@code connection}对象。
     * @param connection 数据库连接对象
     * @return TableSchema集合
     */
    private static Map<String, TableSchema> cacheTableSchemas
            (final Connection connection) {
        Map<String, TableSchema> tableSchemaMap = new HashMap<>();
        try {
            obtainTableNames(connection).forEach(tableName -> {
                try {
                    tableSchemaMap.put(tableName,
                            obtainTableSchema(connection, tableName));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableSchemaMap;
    }

    /**
     * 这是一个DML操作的模板方法，抽取了其他DML操作的公共部分，不同的行为通过
     * {@code processor}传入。
     *
     * @param connection 数据库连接对象
     * @param javaBean 普通Java Bean对象
     * @param processor SQLSchema生成器
     * @return 执行DML操作后影响的记录行数
     * @throws SQLException 执行DML操作时发生异常
     */
    private int executeWithBean(final Connection connection,
                                final Object javaBean,
                                final BiFunction<Connection, Object,
                                        SQLSchema> processor)
            throws SQLException {
        Toolkit.nullObjectChecker(connection, javaBean);
        final SQLSchema sqlSchema = processor.apply(connection, javaBean);

        /////////////////////////////////////////////////////////////////
        System.out.println(sqlSchema.getSQL());

        try (PreparedStatement statement = connection.prepareStatement
                (sqlSchema.getSQL())) {
            return populateStatement(statement, sqlSchema.getFields()
                    .stream().map(Field::getFieldValue).toArray())
                    .executeUpdate();
        }
    }

    /**
     * 这是一个批量DML操作的模板方法，抽取了其他DML方法公共部分，不同的行为通过
     * {@code processor}传入。
     *
     * @param connection 数据库连接对象
     * @param javaBeans 普通Java Bean对象
     * @param processor SQLSchema生成器
     * @return 批量执行DML语句后影响的记录行数
     * @throws SQLException 执行批量DML操作时发生异常
     */
    private int[] executeWithBatchBean
            (final Connection connection, final Object[] javaBeans,
             final BiFunction<Connection, Object, SQLSchema> processor)
            throws SQLException {
        Toolkit.nullObjectChecker(connection, javaBeans);
        connection.setAutoCommit(false);
        try (PreparedStatement statement = connection.prepareStatement
                (processor.apply(connection, javaBeans[0]).getSQL())) {
            for (Object javaBean : javaBeans) {
                final SQLSchema sqlSchema = processor.apply(connection, javaBean);
                List<Field> fields = sqlSchema.getFields();
                populateStatement(statement, fields.stream()
                        .map(Field::getFieldValue).toArray()).addBatch();
            }
            int[] effectedRows = statement.executeBatch();
            if (!connection.getAutoCommit()) connection.commit();
            return effectedRows;
        }
    }

    /**
     * 执行DML插入语句，从给定的非空Java Bean对象中自动解析构建符合SQL数据
     * 更新规范的语句并执行，注意数据表中要求不能为空的字段在对应的Java
     * Bean对象中的属性必须有值，否则将引发异常。需要在外部手动关闭{@code
     * connection}连接对象。
     *
     * @param connection 数据库连接对象
     * @param javaBean 普通Java Bean对象
     * @return SQLSchema对象
     * @throws SQLException 执行数据更新时发生异常
     */
    @Override
    public int insertWithBean(final Connection connection,
                              final Object javaBean) throws SQLException {
        return executeWithBean(connection, javaBean,
                this::parseBean2InsertionSQLSchema);
    }

    /**
     * 将一个Java Bean对象解析成一个{@code SQLSchema}对象，这个{@code
     * SQLSchema}对象包括一条带占位符的符合SQL数据插入规范的语句和含有
     * 字段名和字段值的{@code Field}对象列表，主调方法直接使用{@code
     * Field}对象列表填充SQL语句占位符。获取的字段名称仍是符合Java规范
     * 的驼峰式命名，注意在Java Bean对象中{@code Boolean}类型的属性不
     * 要使用is前缀命名（含数据表）。需要在外部手动关闭{@code connection}
     * 数据库连接对象。
     *
     * @param connection 数据库连接对象
     * @param javaBean 普通Java Bean对象
     * @return SQLSchema
     */
    private SQLSchema parseBean2InsertionSQLSchema(
            final Connection connection, final Object javaBean) {
        //从数据库中获取表结构
        Map<String, TableSchema> tableSchemas = obtainTableSchemas
                (connection);
        final Class<?> beanClass = javaBean.getClass();
        final String tableName = StringUtils.toUnderline
                (beanClass.getSimpleName());

        List<Field> fields = null;
        try {
            TableSchema tableSchema = tableSchemas.getOrDefault(tableName,
                    obtainTableSchema(connection, tableName));

            Set<Field> autoIncrementFields = tableSchema
                    .getColumnSchemas().stream()
                    .filter(ColumnSchema::isAutoIncrement)
                    .map(ColumnSchema::getColumn)
                    .collect(Collectors.toSet());

            fields = new ArrayList<>();
            for (PropertyDescriptor property :
                    obtainPropertyDescriptors(javaBean.getClass())) {

                Object value = property.getReadMethod().invoke(javaBean);
                if (!StringUtils.isEmpty(value)) {
                    fields.add(new Field(property.getName(), value));
                }
            }

            fields = fields.stream()
                    .filter(param -> {
                        for (Field autoIncrementField : autoIncrementFields)
                            if (!autoIncrementField.equals(param))
                                return true;
                        return false;
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String fieldList = Objects.requireNonNull(fields).stream()
                .map(Field::getFieldName)
                .map(StringUtils::toUnderline)
                .collect(Collectors.joining(", "));

        final String executableSQL = String
                .format("INSERT INTO %s (%S) VALUES (%S)",
                        tableName, fieldList,
                        Toolkit.obtainPlaceholder(fields));
        return new SQLSchema(executableSQL, fields);
    }

    /**
     * 将一个Java Bean对象解析成一个{@code SQLSchema}对象，这个{@code
     * SQLSchema}对象包括一条带占位符的符合SQL数据更新规范的语句和含有
     * 字段名和字段值的{@code Field}对象列表，主调方法直接使用{@code
     * Field}对象列表填充SQL语句占位符。获取的字段名称仍是符合Java规范
     * 的驼峰式命名，注意在Java Bean对象中{@code Boolean}类型的属性不
     * 要使用is前缀命名（含数据表）。需要在外部手动关闭{@code connection}
     * 数据库连接对象。
     *
     * @param connection 数据库连接对象
     * @param javaBean 普通Java Bean对象
     * @return SQLSchema
     */
    private SQLSchema parseBean2UpdateSQLSchema(final Connection connection,
                                                final Object javaBean) {
        //获取缓存的表结构数据
        Map<String, TableSchema> tableSchemas = obtainTableSchemas
                (connection);
        final String tableName = StringUtils.toUnderline
                (javaBean.getClass().getSimpleName());
        List<Object> parameters = null;
        String executableSQL = null;
        List<Field> fieldList = null;
        try {
            TableSchema tableSchema = tableSchemas.getOrDefault(tableName,
                    obtainTableSchema(connection, tableName));
            List<ColumnSchema> primaryKeys = tableSchema
                    .getColumnSchemas().stream()
                    .collect(Collectors.partitioningBy
                            (ColumnSchema::isPrimary)).get(true);
            final String[] conditions = new String[1];
            if (primaryKeys.size() > 1)
                primaryKeys.forEach(key -> conditions[0] = String
                        .join(" = ? AND ", new ArrayList<String>() {
                            {
                                add(key.getColumn().getFieldName());
                            }
                        }));
            else
                conditions[0] = primaryKeys.get(0).getColumn().getFieldName()
                        .concat(" = ?");

            final List<Field> valuableFields = obtainValuableFields(javaBean);
            List<Field> keyFields = new ArrayList<>() {
                {
                    primaryKeys.forEach(field -> add(field.getColumn()));
                }
            };

            final List<Field> fields = new ArrayList<>(valuableFields);
            valuableFields.stream().filter(keyFields::contains)
                    .forEach(field -> {
                        fields.remove(field);
                        fields.add((fields.size()), field);
                    });

            executableSQL = String.format("UPDATE %s SET %s WHERE %s",
                    tableName, fields.stream().filter(field ->
                            !keyFields.contains(field))
                            .map(Field::getFieldName)
                            .map(StringUtils::toUnderline)
                            .collect(Collectors.joining(" = ?, "))
                            .concat(" = ? "),
                    conditions[0]);
            fieldList = fields;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new SQLSchema(executableSQL, fieldList);
    }

    /**
     * 将一个Java Bean对象解析成一个{@code SQLSchema}对象，这个{@code
     * SQLSchema}对象包括一条带占位符的符合SQL数据删除规范的语句和含有
     * 字段名和字段值的{@code Field}对象列表，主调方法直接使用{@code
     * Field}对象列表填充SQL语句占位符。获取的字段名称仍是符合Java规范
     * 的驼峰式命名，注意在Java Bean对象中{@code Boolean}类型的属性不
     * 要使用is前缀命名（含数据表）。需要在外部手动关闭{@code connection}
     * 数据库连接对象。
     *
     * @param connection 数据库连接对象
     * @param javaBean 普通Java Bean对象
     * @return SQLSchema
     */
    private SQLSchema parseBean2DeletionSQLSchema(
            final Connection connection, final Object javaBean) {
        Map<String, TableSchema> tableSchemas = obtainTableSchemas
                (connection);
        final String tableName = StringUtils.toUnderline
                (javaBean.getClass().getSimpleName());
        TableSchema tableSchema = null;
        try {
            tableSchema = tableSchemas.getOrDefault(tableName,
                    obtainTableSchema(connection, tableName));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Field> valuableField = obtainValuableFields(javaBean);
        List<ColumnSchema> columnSchemas = Objects
                .requireNonNull(tableSchema).getColumnSchemas();

        List<Field> fieldList = columnSchemas.stream()
                .filter(ColumnSchema::isPrimary)
                .map(ColumnSchema::getColumn)
                .collect(Collectors.toList());

        List<Field> conditionList = valuableField.stream()
                .filter(fieldList::contains)
                .collect(Collectors.toList());

        final String returnedSQL = String.format
                ("DELETE FROM %s WHERE %s", tableName, conditionList.stream()
                                .map(Field::getFieldName)
                                .map(StringUtils::toUnderline)
                                .collect(Collectors.joining(" = ? AND "))
                                .concat(" = ? "));
        return new SQLSchema(returnedSQL, conditionList);
    }

    /**
     * 执行DML语句，从给定的非空Java Bean对象中自动解析构建符合SQL数据更新规
     * 范的语句并执行。需要在外部手动关闭{@code connection}连接对象。
     *
     * @param connection 数据库连接对象
     * @param javaBean 普通Java Bean对象
     * @return SQLSchema对象
     * @throws SQLException 执行数据更新时发生异常
     */
    @Override
    public int updateWithBean(final Connection connection,
                              final Object javaBean)
            throws SQLException {
        return executeWithBean(connection, javaBean,
                this::parseBean2UpdateSQLSchema);
    }

    /**
     * 执行DML语句，从给定的非空Java Bean对象中自动解析构建符合SQL数据删除
     * 规范的语句并执行。需要在外部手动关闭{@code connection}连接对象。
     *
     * @param connection 数据库连接对象
     * @param javaBean 普通Java Bean对象
     * @return SQLSchema对象
     * @throws SQLException 执行数据删除时发生异常
     */
    @Override
    public int deleteWithBean(final Connection connection,
                              final Object javaBean)
            throws SQLException {
        return executeWithBean(connection, javaBean,
                this::parseBean2DeletionSQLSchema);
    }

    /**
     * 根据{@code tableName}获取数据库中对应的数据表结构，{@code TableSchema}
     * 中有两个字段，一个存有表名，另一个则是表中的字段信息列表，使用另一个类
     * {@code ColumnSchema}存储，其中存有{@code Field}、是否是主键以及字段是
     * 否自增的标志位，{@code Field}中存有字段名与字段值。需要手动关闭{@code
     * connection}数据库连接对象。
     *
     * @param connection 数据库连接对象
     * @param tableName 数据表名
     * @return 存有数据表结构信息的TableSchema
     * @throws SQLException 查询数据表结构信息时发生异常
     */
    private static TableSchema obtainTableSchema(
            final Connection connection,
            final String tableName) throws SQLException {
        DatabaseMetaData databaseMeta = connection.getMetaData();
        ResultSet tables = databaseMeta.getColumns
                (connection.getCatalog(), ANY_MATCH,
                        tableName, ANY_MATCH);
        List<ColumnSchema> columnSchemas = new ArrayList<>() {
            {
                while (tables.next()) {
                    add(new ColumnSchema(
                            new Field(tables.getString(COLUMN_NAME), (null)),
                            FALSE, (tables.getString(IS_AUTO_INCREMENT))
                            .equalsIgnoreCase(YES) ? TRUE : FALSE)
                    );
                }
            }
        };

        ResultSet primaryKeys = databaseMeta.getPrimaryKeys
                (connection.getCatalog(), ANY_MATCH, tableName);
        Set<String> primarySet = new HashSet<>() {
            {
                while (primaryKeys.next())
                    add(primaryKeys.getString(COLUMN_NAME));
            }
        };

        for (ColumnSchema columnSchema : columnSchemas) {
            if (primarySet.contains
                    (columnSchema.getColumn().getFieldName()))
                columnSchema.setPrimary(true);
        }
        Toolkit.closeQuietly(tables);
        Toolkit.closeQuietly(primaryKeys);
        return new TableSchema(tableName, columnSchemas);
    }

    /**
     * 获取数据库中所有的数据表的表名，用于在初始化时根据表名获取所有的数据
     * 表结构并进行缓存，以供在需要时快速获取。需要在外部手动关闭{@code
     * connection}数据库连接。
     *
     * @param connection 数据库连接对象
     * @return 以流的形式返回数据库中的所有表名
     * @throws SQLException 在获取数据表名的时候发生异常
     */
    private static Stream<String> obtainTableNames(
            final Connection connection)
            throws SQLException {
        DatabaseMetaData databaseMeta = connection.getMetaData();
        try (ResultSet tables = databaseMeta.getTables(
                connection.getCatalog(), ANY_MATCH, ANY_MATCH,
                new String[]{"TABLE"})) {
            return new ArrayList<String>() {
                {
                    while (tables.next())
                        add(tables.getString(TABLE_NAME));
                }
            }.stream();
        }
    }

    /**
     * 获取给定的Java Bean中所有有值的属性，将属性名和属性值用{@code Field}
     * 封装并以列表的形式返回。{@code Field}中封装了{@code javaBean}的属性
     * 名和属性值。
     *
     * @param javaBean 普通的Java Bean对象
     * @return 值不为空的Fields
     */
    public List<Field> obtainValuableFields(final Object javaBean) {
        Objects.requireNonNull(javaBean);
        List<Field> fields = null;
        try {
            fields = Stream.of(obtainPropertyDescriptors
                    (javaBean.getClass())).filter(property -> {
                Object fieldValue = null;
                try {
                    fieldValue = property.getReadMethod().invoke(javaBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Objects.nonNull(fieldValue);
            }).map(property -> {
                Object fieldValue = null;
                try {
                    fieldValue = property.getReadMethod().invoke(javaBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new Field(property.getName(), fieldValue);
            }).collect(Collectors.toList());
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return fields;
    }

    /**
     * 获取所有数据库中存在的数据表的结构，使用{@code Map<java.util.String,
     * com.tinysand.system.access.schemas.TableSchema}将数据表名与数据表
     * 结构对应存储。由于使用了缓存机制，如果{@code tableSchemas}存在则返回，
     * 否则再次缓存，供下次使用，提高效率。需要在外部手动关闭{@code
     * connection}数据库连接。
     *
     * @return 所有数据表的表结构的Map集合
     */
    private static Map<String, TableSchema> obtainTableSchemas
    (final Connection connection) {
        return Objects.nonNull(tableSchemas) ? tableSchemas :
                (tableSchemas = cacheTableSchemas(connection));
    }

    /**
     * 获取Java Bean对象中所有的属性描述符。
     *
     * @param bean 普通Java Bean对象
     * @return Java Bean对象的属性描述符
     * @throws IntrospectionException 获取Java Bean对象属性描述时发生异常
     */
    private PropertyDescriptor[] obtainPropertyDescriptors(
            final Class<?> bean) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(bean, Object.class);
        return beanInfo.getPropertyDescriptors();
    }

    /**
     * 使用提供的{@code params}为{@code statement}语句填充参数并返回一个
     * 设置了参数的语句。
     *
     * @param statement PreparedStatement对象
     * @param params 填充参数
     * @return 设置好参数的PreparedStatement对象
     * @throws SQLException 填充SQL语句参数时发生异常
     */
    private PreparedStatement populateStatement(
            final PreparedStatement statement, final Object[] params)
            throws SQLException {
        ParameterMetaData parameterMeta = statement.getParameterMetaData();
        int parameterCount = parameterMeta.getParameterCount();
        if (Objects.nonNull(params) && !Toolkit.isEmpty(params))
            for (int index = 1; index <= parameterCount; index++)
                statement.setObject(index, params[index - 1]);
        return statement;
    }

    /**
     * 数据库操作异常包装器，对数据库的原始异常进行捕捉，并为其添加更多
     * 详尽的信息后再抛出。
     *
     * @param exception 捕捉到的SQL异常对象
     * @param sql 发生异常时执行的SQL语句
     * @param params 发生异常时的SQL语句参数
     * @throws SQLException 对捕捉到的异常封装后再次抛出
     */
    public static void errorWrapper(final SQLException exception,
                                    final String sql, final Object[][] params)
            throws SQLException {
        String errorMessage = exception.getMessage();
        String message = String.format
                ("Error => Query: %s : Parameters: %s [%s]",
                        Objects.nonNull(errorMessage) ? errorMessage : "",
                        Objects.nonNull(sql) ? sql : "",
                        Objects.nonNull(params) ?
                                Arrays.deepToString(params) : "[]");
        SQLException error = new SQLException(message);
        error.setNextException(exception);
        throw error;
    }

    /**
     * 批量更新数据库中与{@code javaBeans}对应的记录。使用一个普通Java
     * Bean对象数组构建SQL数据插入规范的语句批量执行，需要在外部手动
     * 关闭{@code connection}数据库连接。
     *
     * @param connection 数据库连接对象
     * @param javaBeans 普通Java Bean对象数组
     * @return 以一维数组的形式返回执行每条语句所影响的记录函数
     * @throws SQLException 执行批量数据插入时发生异常
     */
    @Override
    @Batch
    public int[] batchInsertWithBean(final Connection connection,
                                     final Object[] javaBeans)
            throws SQLException {
        Toolkit.nullObjectChecker(connection, javaBeans);
        return executeWithBatchBean(connection, javaBeans,
                this::parseBean2InsertionSQLSchema);
    }

    /**
     * 批量更新数据库中与{@code javaBeans}对应的记录。使用一个普通Java
     * Bean对象数组构建SQL数据更新规范的语句批量执行，需要在外部手动
     * 关闭{@code connection}数据库连接。
     *
     * @param connection 数据库连接对象
     * @param javaBeans 普通Java Bean对象数组
     * @return 以一维数组的形式返回执行每条语句所影响的记录函数
     * @throws SQLException 执行批量数据更新时发生异常
     */
    @Override
    @Batch
    public int[] batchUpdateWithBean(final Connection connection,
                                     final Object[] javaBeans)
            throws SQLException {
        Toolkit.nullObjectChecker(connection, javaBeans);
        return executeWithBatchBean(connection, javaBeans,
                this::parseBean2UpdateSQLSchema);
    }

    /**
     * 批量删除数据库中与{@code javaBeans}对应的记录。使用一个普通Java
     * Bean对象数组构建SQL数据删除规范的语句并批量执行，需要在外部手动
     * 关闭{@code connection}对象连接。
     *
     * @param connection 数据库连接对象
     * @param javaBeans 普通Java Bean对象数组
     * @return 以一维数组的形式返回执行每条语句所影响的记录函数
     * @throws SQLException 执行批量数据删除时发生异常
     */
    @Override
    @Batch
    public int[] batchDeletionWithBean(final Connection connection,
                                       final Object[] javaBeans)
            throws SQLException {
        Toolkit.nullObjectChecker(connection, javaBeans);
        return executeWithBatchBean(connection, javaBeans,
                this::parseBean2DeletionSQLSchema);
    }

    /**
     * 执行一条DML语句，可使用{@code params}填充SQL语句，当{@code params}
     * 为空时{@code sql}不能是一个带有参数占位符的模板语句，否则将引发异常。
     * 该方法需要在外部手动关闭{@code connection}对象。
     *
     * @param connection 数据库连接对象
     * @param sql DML语句
     * @param params DML语句填充参数数组
     * @return 执行DML语句后所影响的记录行数
     * @throws SQLException 执行DML语句时发生异常
     */
    @Override
    public int executeWithParam(final Connection connection,
                                final String sql,
                                final Object[] params) throws SQLException {
        try (PreparedStatement statement = connection
                .prepareStatement(sql)) {
            return populateStatement(statement, params)
                    .executeUpdate();
        }
    }

    /**
     * 该方法可批量执行DML语句。执行多条由二维参数数组{@code params}
     * 填充的{@code sql}语句，该方法自动管理事务。要使该方法有效请在
     * 数据库属性文件中的{@code url}配置后加上{@code
     * rewriteBatchedStatements=true}参数，在使用完该方法后需要手动
     * 关闭{@code connection}对象。注意该方法的二维参数数组不能为空。
     *
     * @param connection 数据库连接对象
     * @param sql 带参数占位符的DML模板语句
     * @param params 填充SQL语句的二维参数数组
     * @return 每条语句执行后所影响的记录行数
     * @throws SQLException 执行批量DML操作时发生异常
     */
    @Override
    @Batch
    public int[] executeBatchWithParam(final Connection connection,
                                       final String sql,
                                       final Object[][] params)
            throws SQLException {
        connection.setAutoCommit(false);
        List<Integer> effectedRows = new ArrayList<>();
        for (Object[] param : params) {
            try (PreparedStatement statement = connection
                    .prepareStatement(sql))
            {
                populateStatement(statement, param).addBatch();
                effectedRows.add(statement.executeBatch()[0]);
            }
        }
        if (!connection.getAutoCommit()) connection.commit();
        return effectedRows.stream().mapToInt(Integer::intValue)
                .toArray();
    }

    /**
     * 执行SQL查询，并将结果使用给定的结果集包装器包装并返回，用给定的
     * {@code params}参数数组填充带有占位符的{@code sql}模板语句。如果
     * 要执行一条简单的SQL语句，请用对{@code params}使用{@code null}，
     * 该方法需要在外部手动关闭{@code connection}连接。
     *
     * @param connection 数据库连接对象
     * @param sql SQL查询语句
     * @param resultHandler 结果集包装器
     * @param params SQL语句填充参数
     * @param <R> 期待的返回类型，由传入的{@code resultHandler}决定
     * @return 包装好的R类型结果集
     * @throws SQLException 执行数据库查询时发生异常
     */
    @Override
    public <R> R query(final Connection connection, final String sql,
                       final ResultHandler<R> resultHandler,
                       final Object[] params) throws SQLException {
        Toolkit.nullObjectChecker(connection, sql, resultHandler);
        try (PreparedStatement statement = connection
                .prepareStatement(sql);
             ResultSet resultSet = populateStatement(statement, params)
                     .executeQuery())
        {
            return resultHandler.handle(trimmedResult
                    (resultSet));
        }
    }

    /**
     * 执行DQL语句，根据给定的{@code beanClass}类型查询其所有的字段
     * 值并封装，按照{@code resultHandler}期待的类型包装后返回。该
     * 方法不支持部分字段的查询，如果不想查询所有的字段请使用{@code
     * AccessRunner#query(connection,sql,resultHandler,params)}查询。
     *
     * @param connection 数据库连接对象
     * @param beanClass 所要查询对象的类型
     * @param resultHandler 结果集包装器
     * @param <R> 期待的返回类型，由传入的{@code resultHandler}决定
     * @return 包装好的R类型结果集
     * @throws SQLException 执行数据查询时发生异常
     */
    @Override
    public <R> R query(final Connection connection,
                       final Class<?> beanClass,
                       final ResultHandler<R> resultHandler)
            throws SQLException {
        Toolkit.nullObjectChecker(connection, beanClass, resultHandler);
        final String executableSQL = String.format
                ("SELECT * FROM %s", StringUtils
                        .toUnderline(beanClass.getSimpleName()));
        try (PreparedStatement statement = connection
                .prepareStatement(executableSQL);
             ResultSet resultSet = statement.executeQuery()) {
            return resultHandler.handle(trimmedResult
                    (resultSet));
        }
    }

    public ResultSet trimmedResult(final ResultSet resultSet) {
        return resultSet;
    }

    private static final String COLUMN_NAME = "COLUMN_NAME";
    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String IS_AUTO_INCREMENT = "IS_AUTOINCREMENT";
    private static final String ANY_MATCH = "%";
    private static final String YES = "YES";
    public static final boolean FALSE = false;
    public static final boolean TRUE  = true;

}
