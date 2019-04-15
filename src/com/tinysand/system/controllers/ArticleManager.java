package com.tinysand.system.controllers;

import com.alibaba.fastjson.JSONObject;
import com.tinysand.system.access.implement.AccessRunner;
import com.tinysand.system.access.implement.handlers.BeanHandler;
import com.tinysand.system.access.implement.wrapper.Trimmer;
import com.tinysand.system.models.Article;
import com.tinysand.system.services.CommonService;
import com.tinysand.system.util.StringUtils;
import com.tinysand.system.util.Toolkit;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@WebServlet(name = "articleManager", urlPatterns = "/article")
public class ArticleManager extends HttpServlet {

    private void fillData(final Object beanObject, final Map<String, String[]> dataMap)
    {
        try {
            PropertyDescriptor[] beanProperties = Introspector.getBeanInfo(
                    beanObject.getClass(), Object.class).getPropertyDescriptors();
            Set<String> collect = Arrays.stream(beanProperties).map
                    (FeatureDescriptor::getName)
                    .collect(Collectors.toSet());
            dataMap.forEach((key, value) -> {
                if (collect.contains(StringUtils.toCamel(key))) {
                    try {
                        Field declaredField = beanObject.getClass()
                                .getDeclaredField(StringUtils.toCamel(key));
                        declaredField.setAccessible(true);
                        declaredField.set(beanObject, value[0]);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
    }

    private void populate(final Object beanObject,
                          final Set<String> beanProperties,
                          final String propName, final Object[] propValue) {
        Objects.requireNonNull(beanObject);
//        PropertyDescriptor[] beanProperties;
        //            beanProperties = Introspector.getBeanInfo(
//                    Article.class, Object.class).getPropertyDescriptors();

//        Set<String> collect = Arrays.stream(beanProperties).map
//                (FeatureDescriptor::getName)
//                .collect(Collectors.toSet());

//        System.out.println(Arrays.toString(beanProperties.toArray()));
        if (beanProperties.contains(StringUtils.toCamel(propName)) &&
                !StringUtils.isEmpty(propValue[0])) {
            try {
                Field declaredField = beanObject.getClass().getDeclaredField
                        (StringUtils.toCamel(propName));
                if (Objects.nonNull(declaredField)) {
                    declaredField.setAccessible(true);
                    if (declaredField.getType().isArray())
                        declaredField.set(beanObject, propValue);
                    else
                        declaredField.set(beanObject, propValue[0]);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }


//            Arrays.stream(beanProperties).forEach(propertyDescriptor -> {
//                if (StringUtils.toCamel(propName).equalsIgnoreCase
//                        (propertyDescriptor.getName())) {
//                    Field declaredField;
//                    try {
//                        declaredField = article.getClass().getDeclaredField
//                                (StringUtils.toCamel(propName));
//                        if (Objects.nonNull(declaredField)) {
//                            declaredField.setAccessible(true);
//                            declaredField.set(article, value[0]);
//                        }
//                    } catch (NoSuchFieldException | IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
////                System.out.println(propertyDescriptor.getName());
//            });

//        try {
//            for (PropertyDescriptor propertyDescriptor : beanProperties) {
//                if (propertyDescriptor.getName().equals(StringUtils.toCamel(propName)))
//                {
//                    Field declaredField = article.getClass().getDeclaredField
//                            (StringUtils.toCamel(propName));
//                    if (Objects.nonNull(declaredField)) {
//                        declaredField.setAccessible(true);
//                        declaredField.set(article, value[0]);
//                    }
//                }
//            }
//        } catch (NoSuchFieldException | IntrospectionException |
//                IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }

    private void populateData(final Object javaBean, final String property,
                              final Object[] value) {
        Objects.requireNonNull(javaBean);
        try {
            Arrays.stream(getBeanProperties(javaBean)).forEach(prop -> {
                if (prop.getName().equals
                        (StringUtils.toCamel(property))) {
                    System.err.println("true => " + StringUtils.toCamel(property)
//                            + " => " + Arrays.toString(value));
                            + " => " + Arrays.toString(value));
                    try {
                        prop.getWriteMethod().invoke(javaBean, value);
                    } catch
                    (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
//        System.out.println("do post");
//        Article article = new Article();
        final Map<String, String[]> formDataMap = request.getParameterMap();
//        try {
//            PropertyDescriptor[] beanProperties = Introspector.getBeanInfo(
//                    Article.class, Object.class).getPropertyDescriptors();
//
//            Set<String> collect = Arrays.stream(beanProperties).map
//                    (FeatureDescriptor::getName)
//                    .collect(Collectors.toSet());
//
//            formDataMap.forEach((key, value) -> {
////            populateData(article, StringUtils.toCamel(key), value[0]);
//                try {
//                    populate(article, collect, key, value);
//                } catch (Exception e) {
//                    System.out.println("error");
//                    e.printStackTrace();
//                }
//            });
//        } catch (IntrospectionException e) {
//            e.printStackTrace();
//        }

//        System.err.println(article);
        CommonService commonService = new CommonService();
        commonService.addArticle(request, response, formDataMap);

//        final Map<String, String> queryParams = parseQueryString2Map
//                (request.getQueryString());
//        switch (queryParams.get("action")) {
//            case ACTION_ADD: addArticle
//                    (request, response, formDataMap); break;
//
//            case ACTION_EDIT:
//                System.out.println("edit");
////                editArticle(request, 2);
//                break;
//
//            case ACTION_UPDATE: updateArticle
//                    (request, response, formDataMap);break;
//
//            case ACTION_PUBLISH: break;
//
//            case ACTION_DELETE: break;
//
//            default: break;
//        }
    }

    private void editArticle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final int id) {
        Article article = queryDataBase(Article.class,
                ((accessRunner, articleClass) -> {
                    final String querySQL = String.format(
                            "select * from %s where id = %s",
                            StringUtils.toUnderline
                                    (articleClass.getSimpleName()), id);
                    try (Connection connection = Toolkit.obtainConnection()) {
                        return accessRunner.query(connection,
                                querySQL, new BeanHandler<>(Article.class),
                                new Object[0]);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }));
        request.getSession().setAttribute("article", article);
//        request.getRequestDispatcher("").forward(request, response);
//        System.out.println(article);
    }

    private <R> R queryDataBase(final Class<?> beanClass, final BiFunction
            <AccessRunner, Class<?>, R> operator) {
        return operator.apply(new AccessRunner() {
            @Override
            public ResultSet trimmedResult(final ResultSet resultSet) {
                return Trimmer.trimmedResult(resultSet);
            }
        }, beanClass);
    }

    private void updateArticle(final HttpServletRequest request,
                               final HttpServletResponse response,
                               final Map<String, String[]> formDataMap)
            throws IOException {
        executor(request, response, formDataMap, (article, dataEntry) -> {
            //最后更改时间、文章内容、文章标题、文章标签、
            // 文章字数、文章标题图片、文章内容描述
            commonPopulate(article, formDataMap);
            article.setLastModified(Timestamp.valueOf(LocalDateTime.now()));
            article.setPublisher(getPublisher(request));
            article.setArticleFolder(request.getSession().getId());
            article.setId((Integer) request.getSession()
                    .getAttribute(ARTICLE_ID));

        }, () -> MESSAGE_UPDATE);
    }

    private void commonPopulate(final Article article,
                                final Map<String, String[]> formData) {
        for (Map.Entry<String, String[]> dataEntry : formData.entrySet()) {
            switch (StringUtils.toCamel(dataEntry.getKey())) {
                case "articleContent":
                    final String articleContent = dataEntry.getValue()[0];
                    final String description = getArticleDescription
                            (articleContent).orElse(DEFAULT_DESC);

                    setWordCount(article, articleContent);
                    article.setArticleContent(articleContent);
                    article.setDescription(description); continue;

                case "essay":
                    article.setEssay(isEssay(dataEntry.getValue()[0])); continue;

//                default: populate(article, dataEntry.getKey(),
//                        dataEntry.getValue());
            }
        }
    }

    private void addArticle(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final Map<String, String[]> formDataMap)
            throws IOException {
        executor(request, response, formDataMap, (article, formData) -> {
            commonPopulate(article, formData);

            System.out.println(article);
            article.setPublisher(getPublisher(request));
            article.setArticleFolder(request.getSession().getId());
        }, () -> MESSAGE_ADD);
    }

    private void executor
            (final HttpServletRequest request,
             final HttpServletResponse response,
             final Map<String, String[]> formDataMap,
             final BiConsumer<Article, Map<String, String[]>> fillData,
             final Supplier<String> actionSupplier)
            throws IOException {
        Article article = (Article) Toolkit.getBeanInstance(Article.class);
        Objects.requireNonNull(formDataMap);
        fillData.accept(article, formDataMap);

        if (checkAllDataAndRecordError(request, article) &&
                operateDataBase(request, article, (accessRunner, javaBean) -> {
            try {
                try (Connection connection = Toolkit.obtainConnection()) {
                    operateData(accessRunner, connection, article,
                            actionSupplier.get());
                }
            } catch (SQLException e) {
                request.getSession().setAttribute
                        (ERROR_DETAIL, e.getMessage());
                e.printStackTrace();
            }
        })) {
            sendMessageWithJSON(response, jsonObject -> {
                jsonObject.put("message",
                        actionSupplier.get() + MESSAGE_SUCCESS);
                return jsonObject;
            });
        } else {
            final String errorDetails = (String) request.getSession()
                    .getAttribute(ERROR_DETAIL);
            sendMessageWithJSON(response, jsonObject -> {
                jsonObject.put("message", actionSupplier.get()
                        + MESSAGE_FAILED + errorDetails);
                return jsonObject;
            });
        }
    }

    private void operateData(final AccessRunner accessRunner,
                             final Connection connection,
                             final Article article, final String operateType)
            throws SQLException {
        switch (operateType) {
            case MESSAGE_ADD: accessRunner.insertWithBean
                    (connection, article); break;
            case MESSAGE_UPDATE:
            case MESSAGE_PUBLISH: accessRunner.updateWithBean
                    (connection, article); break;
            case MESSAGE_DELETE: accessRunner.deleteWithBean
                    (connection, article); break;
            default: break;
        }
    }

    private boolean checkAllDataAndRecordError
            (final HttpServletRequest request, final Article article) {
        try {
            ensureAllDataIsLegal(article);
        } catch (NullPointerException e) {
            request.getSession().setAttribute(ERROR_DETAIL, e.getMessage());
            System.err.println("error");
            return false;
        }
        return true;
    }

    private String getPublisher(final HttpServletRequest request) {
        final String publisher = (String) request.getSession()
                .getAttribute(PUBLISHER);
        return !StringUtils.isEmpty(publisher) ? publisher : DEFAULT_PUBLISHER;
    }

    private boolean isEssay(final String essay) {
        return !StringUtils.isEmpty(essay) &&
                "true".equalsIgnoreCase(essay);
    }

    private void setWordCount(final Article article,
                              final String articleContent) {
        if (Objects.nonNull(articleContent) &&
                !StringUtils.isEmpty(articleContent)) {
            article.setWordCounter(getWordCount(articleContent)
                    .orElse(DEFAULT_WORD_COUNT));
        }
    }

    @SuppressWarnings("unchecked")
    private Optional<Integer> getWordCount(final String content) {
        return wordProcessor(content, reader ->
                Optional.of((long) reader.lines()
                        .filter(line -> !line.trim().isEmpty())
                        .filter(line -> !line.startsWith("#"))
                        .map(String::trim)
                        .map(String::length)
                        .reduce(0, (num1, num2) -> num1 + num2))
        );
    }

    @SuppressWarnings("unchecked")
    private Optional<String> getArticleDescription(final String content) {
        return wordProcessor(content, reader ->
            reader.lines().filter(line -> !line.trim().isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .findFirst()
        );
    }

    private Optional wordProcessor
            (final String content,
             final Function<BufferedReader, Optional> processor) {
        BufferedReader reader = new BufferedReader(new InputStreamReader
                (new ByteArrayInputStream(content.getBytes
                        (StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        return processor.apply(reader);
    }

    private boolean operateDataBase(final HttpServletRequest request,
                                    final Object javaBean, final BiConsumer
            <AccessRunner, Object> operator) {
        try {
            operator.accept(new AccessRunner(), javaBean);
        } catch (NullPointerException e) {
            request.getSession().setAttribute
                    (ERROR_DETAIL, e.getMessage());
            return false;
        }
        return true;
    }

    private void ensureAllDataIsLegal(final Object javaBean)
            throws NullPointerException {
        final Class<?> beanClass = javaBean.getClass();
        try {
            for (PropertyDescriptor property : getBeanProperties(javaBean)) {
                Field declaredField = beanClass.getDeclaredField
                        (property.getName());

                NotNull notNull = declaredField.getAnnotation(NotNull.class);
                if (Objects.nonNull(notNull)) {
                    declaredField.setAccessible(true);
                    System.out.println(declaredField.getName());
                    final Object value = declaredField.get(javaBean);
                    if (!Objects.nonNull(value) || StringUtils.isEmpty(value))
                       throw new NullPointerException(
                               String.format("请把%s填写完整！", declaredField.getName()));
                }
            }
        } catch (IntrospectionException | NoSuchFieldException |
                IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void sendMessageWithJSON(final HttpServletResponse response,
                                     final UnaryOperator<JSONObject> handler)
            throws IOException {
        JSONObject jsonObject = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(handler.apply(jsonObject));
        writer.flush();
        writer.close();
    }

    private Map<String, String> parseQueryString2Map(final String queryString) {
        Objects.requireNonNull(queryString);
        Map<String, String> paramMap = new HashMap<>();
        if (!queryString.trim().isEmpty()) {
            Arrays.stream(queryString.split("&")).forEach(entry -> {
                String[] splitStrings = entry.split("=");
                if (splitStrings.length % 2 == 0)
                    paramMap.put(splitStrings[0], splitStrings[1]);
            });
        }
        return paramMap;
    }

    private PropertyDescriptor[] getBeanProperties(final Object javaBean)
            throws IntrospectionException {
        return Introspector.getBeanInfo
                (javaBean.getClass(), Object.class).getPropertyDescriptors();
    }

    private static final int DEFAULT_WORD_COUNT = 0;
    private static final String DEFAULT_DESC = "暂无描述";
    private static final String DEFAULT_PUBLISHER = "未知发布者";
    private static final String PUBLISHER = "username";
    private static final String ERROR_DETAIL = "detail";
    private static final String MESSAGE_ADD = "文章添加";
    private static final String MESSAGE_UPDATE = "文章更新";
    private static final String MESSAGE_DELETE = "文章删除";
    private static final String MESSAGE_PUBLISH = "文章发布";
    private static final String MESSAGE_SUCCESS = "成功！";
    private static final String MESSAGE_FAILED = "失败！";
    private static final String UTF_8 = "UTF-8";
    private static final String ACTION_PUBLISH = "publish";
    private static final String ACTION_ADD = "add";
    private static final String ACTION_EDIT = "edit";
    private static final String ACTION_UPDATE = "update";
    private static final String ACTION_DELETE = "delete";
    private static final String ARTICLE_ID = "article_id";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

}
