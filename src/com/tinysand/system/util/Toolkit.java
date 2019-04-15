package com.tinysand.system.util;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.tinysand.system.access.schemas.TableSchema;

public final class Toolkit {
    public static DataSource obtainDataSource() throws Exception {
        Properties properties = new Properties();
        FileInputStream configureFile = new FileInputStream
//                ("src/database.properties");
                ("F:\\IntellijIdeaProjects\\Java Web\\" +
                        "WebSystem\\src\\database.properties");
        properties.load(configureFile);
        return DruidDataSourceFactory.createDataSource(properties);
    }

    public static void closeQuietly(final Statement statement) {
        try {
            if (Objects.nonNull(statement))
                statement.close();
        } catch (SQLException e) {// e.printStackTrace();
        }
    }

    public static void closeQuietly(final Connection connection) {
        try {
            if (Objects.nonNull(connection))
                connection.close();
        } catch (SQLException e) {//  e.printStackTrace();
        }
    }

    public static void closeQuietly(final ResultSet resultSet) {
        try {
            if (Objects.nonNull(resultSet))
                resultSet.close();
        } catch (SQLException e) {// e.printStackTrace();
        }
    }

    public static void closeQuietly(final Connection connection,
                                    final Statement statement,
                                    final ResultSet resultSet) {
        closeQuietly(resultSet);
        closeQuietly(statement);
        closeQuietly(connection);
    }

    public static void nullObjectChecker(final Object... objects) {
        for (Object object : objects)
            Objects.requireNonNull(object);
    }

    public static ResultSet createResultSetProxy
            (final InvocationHandler handler) {
        return (ResultSet) Proxy.newProxyInstance
                (handler.getClass().getClassLoader(),
                        new Class<?>[]{ResultSet.class}, handler);
    }

    public static boolean isEmpty(String text) {
        return Objects.nonNull(text) && text.length() == 0;
    }

    public static boolean isEmpty(Object[] array) {
        return Objects.nonNull(array) && array.length == 0;
    }

    private static List<String> splitToList(String text, String regex) {
        List<String> list = new ArrayList<>();
        Matcher matcher = Pattern.compile(regex).matcher(text);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    public static Connection obtainConnection() {
        try {
            return obtainDataSource().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getBeanInstance(final Class<?> beanClass) {
        Object beanObject = null;
        try {
            beanObject = beanClass.getDeclaredConstructor()
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return beanObject;
    }

    public static boolean isBooleanType(final Class<?> propType) {
        return propType.equals(Boolean.TYPE) ||
                propType.equals(Boolean.class);
    }

//    private Map<String, String> parseQueryString2Map
//            (final String queryString) {
//        Objects.requireNonNull(queryString);
//        String[] paramEntryArray = queryString.split("&");
//        Map<String, String> parameterMap = new HashMap<>();
//        for (String paramEntry : paramEntryArray) {
//            String[] entryArray = paramEntry.split("=");
//            if (entryArray.length % 2 == 0)
//                parameterMap.put(entryArray[0], entryArray[1]);
//        }
//        return parameterMap;
//    }

    public static <T> Optional<T> errorHandler(Supplier<T> supplier) {
        try {
            T product = supplier.get();
            return Optional.ofNullable(product);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Map<String, Integer> wordCounter
            (String text, List<String> wordList) {
        Map<String, Integer> counter = new HashMap<>();
        splitToList(text, "\\b\\w+\\b").stream().map
                (String::toLowerCase).filter
                (word -> !wordList.contains(word))
                .forEach(word ->
                    counter.put(word, counter
                            .getOrDefault(word, 0) + 1)
                );
        return counter;
    }

    public static String obtainPlaceholder(List<?> list) {
        if (list.size() == 0) return "";
        StringBuilder placeholder = new StringBuilder();
        for (int index = 0; index < list.size(); index++)
            placeholder.append("?, ");
        return placeholder.substring(0, placeholder.length() - 2);
    }

    public static List<TableSchema> obtainTableSchema() {

        return null;
    }
    public static String textFilter(List<String> list) {
        return list.stream().filter(Objects::nonNull)
                .filter(word -> word.length() != 1)
                .map(word -> word.substring(0, 1)
                        .toUpperCase()
                        .concat(word.substring(1)))
                .collect(Collectors.joining(", "));
    }

    public static final boolean TRUE = true;
    public static final boolean FALSE = false;
}
