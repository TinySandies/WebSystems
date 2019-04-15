//package com.tinysand.system.services.implement;
//
//import com.alibaba.fastjson.JSONObject;
//import com.tinysand.system.access.implement.AccessRunner;
//import com.tinysand.system.access.implement.handlers.BeanHandler;
//import com.tinysand.system.access.implement.wrapper.Trimmer;
//import com.tinysand.system.controllers.NotNull;
//import com.tinysand.system.models.Article;
//import com.tinysand.system.util.StringUtils;
//import com.tinysand.system.util.Toolkit;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.beans.IntrospectionException;
//import java.beans.Introspector;
//import java.beans.PropertyDescriptor;
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.nio.charset.StandardCharsets;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.function.BiConsumer;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//import java.util.function.Supplier;
//import java.util.function.UnaryOperator;
//
//public class ArticleService {
//    public void editArticle(final HttpServletRequest request,
//                             final HttpServletResponse response,
//                             final int id) throws ServletException, IOException {
//        Article article = queryDataBase(Article.class,
//                ((accessRunner, articleClass) -> {
//                    final String querySQL = String.format(
//                            "select * from %s where id = %s",
//                            StringUtils.toUnderline
//                                    (articleClass.getSimpleName()), id);
//                    try (Connection connection = Toolkit.obtainConnection()) {
//                        return accessRunner.query(connection,
//                                querySQL, new BeanHandler<>(Article.class),
//                                new Object[0]);
//
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                }));
//        request.getSession().setAttribute("article", article);
//        request.getRequestDispatcher("/WEB-INF/views/editor.jsp")
//                .forward(request, response);
//    }
//
//    private <R> R queryDataBase(final Class<?> beanClass, final BiFunction
//            <AccessRunner, Class<?>, R> operator) {
//        return operator.apply(new AccessRunner() {
//            @Override
//            public ResultSet trimmedResult(final ResultSet resultSet) {
//                return Trimmer.trimmedResult(resultSet);
//            }
//        }, beanClass);
//    }
//
//    public void updateArticle(final HttpServletRequest request,
//                               final HttpServletResponse response,
//                               final Map<String, String[]> formDataMap)
//            throws IOException {
//        executor(request, response, formDataMap, (article, dataEntry) -> {
//            //最后更改时间、文章内容、文章标题、文章标签、
//            // 文章字数、文章标题图片、文章内容描述
//            commonPopulate(article, formDataMap);
//            article.setLastModified(Timestamp.valueOf(LocalDateTime.now()));
//            article.setPublisher(getPublisher(request));
//            article.setArticleFolder(request.getSession().getId());
//            article.setId((Integer) request.getSession()
//                    .getAttribute(ARTICLE_ID));
//
//        }, () -> MESSAGE_UPDATE);
//    }
//
//    private void commonPopulate(final Article article,
//                                final Map<String, String[]> formData) {
//        for (Map.Entry<String, String[]> dataEntry : formData.entrySet()) {
//            switch (StringUtils.toCamel(dataEntry.getKey())) {
//                case "articleContent":
//                    final String articleContent = dataEntry.getValue()[0];
//                    final String description = getArticleDescription
//                            (articleContent).orElse(DEFAULT_DESC);
//
//                    setWordCount(article, articleContent);
//                    article.setArticleContent(articleContent);
//                    article.setDescription(description); break;
//
//                case "essay":
//                    article.setEssay(isEssay(dataEntry.getValue()[0])); break;
//
//                default: populateData(article, dataEntry.getKey(),
//                        dataEntry.getValue());
//            }
//        }
//    }
//
//    public void addArticle(final HttpServletRequest request,
//                            final HttpServletResponse response,
//                            final Map<String, String[]> formDataMap)
//            throws IOException {
//        executor(request, response, formDataMap, (article, formData) -> {
//            commonPopulate(article, formData);
//
//            article.setPublisher(getPublisher(request));
//            article.setArticleFolder(request.getSession().getId());
//        }, () -> MESSAGE_ADD);
//    }
//
//    private void executor
//            (final HttpServletRequest request,
//             final HttpServletResponse response,
//             final Map<String, String[]> formDataMap,
//             final BiConsumer<Article, Map<String, String[]>> fillData,
//             final Supplier<String> actionSupplier)
//            throws IOException {
//        Article article = (Article) Toolkit.getBeanInstance(Article.class);
//        Objects.requireNonNull(formDataMap);
//        fillData.accept(article, formDataMap);
//
//        if (checkAllDataAndRecordError(request, article) &&
//                operateDataBase(request, article, (accessRunner, javaBean) -> {
//                    try {
//                        try (Connection connection = Toolkit.obtainConnection()) {
//                            operateData(accessRunner, connection, article,
//                                    actionSupplier.get());
//                        }
//                    } catch (SQLException e) {
//                        request.getSession().setAttribute
//                                (ERROR_DETAIL, e.getMessage());
//                        e.printStackTrace();
//                    }
//                })) {
//            sendMessageWithJSON(response, jsonObject -> {
//                jsonObject.put("message",
//                        actionSupplier.get() + MESSAGE_SUCCESS);
//                return jsonObject;
//            });
//        } else {
//            final String errorDetails = (String) request.getSession()
//                    .getAttribute(ERROR_DETAIL);
//            sendMessageWithJSON(response, jsonObject -> {
//                jsonObject.put("message", actionSupplier.get()
//                        + MESSAGE_FAILED + errorDetails);
//                return jsonObject;
//            });
//        }
//    }
//
//    private void operateData(final AccessRunner accessRunner,
//                             final Connection connection,
//                             final Article article, final String operateType)
//            throws SQLException {
//        switch (operateType) {
//            case MESSAGE_ADD: accessRunner.insertWithBean
//                    (connection, article); break;
//            case MESSAGE_UPDATE:
//            case MESSAGE_PUBLISH: accessRunner.updateWithBean
//                    (connection, article); break;
//            case MESSAGE_DELETE: accessRunner.deleteWithBean
//                    (connection, article); break;
//            default: break;
//        }
//    }
//
//    private boolean checkAllDataAndRecordError
//            (final HttpServletRequest request, final Article article) {
//        try {
//            ensureAllDataIsLegal(article);
//        } catch (NullPointerException e) {
//            request.getSession().setAttribute(ERROR_DETAIL, e.getMessage());
//            return false;
//        }
//        return true;
//    }
//
//    private String getPublisher(final HttpServletRequest request) {
//        final String publisher = (String) request.getSession()
//                .getAttribute(PUBLISHER);
//        return !StringUtils.isEmpty(publisher) ? publisher : DEFAULT_PUBLISHER;
//    }
//
//    private boolean isEssay(final String essay) {
//        return !StringUtils.isEmpty(essay) &&
//                "true".equalsIgnoreCase(essay);
//    }
//
//    private void setWordCount(final Article article,
//                              final String articleContent) {
//        if (Objects.nonNull(articleContent) &&
//                !StringUtils.isEmpty(articleContent)) {
//            article.setWordCounter(getWordCount(articleContent)
//                    .orElse(DEFAULT_WORD_COUNT));
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    private Optional<Integer> getWordCount(final String content) {
//        return wordProcessor(content, reader ->
//                Optional.of((long) reader.lines()
//                        .filter(line -> !line.trim().isEmpty())
//                        .filter(line -> !line.startsWith("#"))
//                        .map(String::trim)
//                        .map(String::length)
//                        .reduce(0, (num1, num2) -> num1 + num2))
//        );
//    }
//
//    @SuppressWarnings("unchecked")
//    private Optional<String> getArticleDescription(final String content) {
//        return wordProcessor(content, reader ->
//                reader.lines().filter(line -> !line.trim().isEmpty())
//                        .filter(line -> !line.startsWith("#"))
//                        .findFirst()
//        );
//    }
//
//    private Optional wordProcessor
//            (final String content,
//             final Function<BufferedReader, Optional> processor) {
//        BufferedReader reader = new BufferedReader(new InputStreamReader
//                (new ByteArrayInputStream(content.getBytes
//                        (StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
//        return processor.apply(reader);
//    }
//
//    private boolean operateDataBase(final HttpServletRequest request,
//                                    final Object javaBean, final BiConsumer
//            <AccessRunner, Object> operator) {
//        try {
//            operator.accept(new AccessRunner(), javaBean);
//        } catch (NullPointerException e) {
//            request.getSession().setAttribute
//                    (ERROR_DETAIL, e.getMessage());
//            return false;
//        }
//        return true;
//    }
//
//    private void ensureAllDataIsLegal(final Object javaBean)
//            throws NullPointerException {
//        final Class<?> beanClass = javaBean.getClass();
//        try {
//            for (PropertyDescriptor property : getBeanProperties(javaBean)) {
//                Field declaredField = beanClass.getDeclaredField
//                        (property.getName());
//
//                NotNull notNull = declaredField.getAnnotation(NotNull.class);
//                if (Objects.nonNull(notNull)) {
//                    declaredField.setAccessible(true);
//                    final Object value = declaredField.get(javaBean);
//                    if (!Objects.nonNull(value) || StringUtils.isEmpty(value))
//                        throw new NullPointerException(
//                                String.format("请把%s填写完整！", declaredField.getName()));
//                }
//            }
//        } catch (IntrospectionException | NoSuchFieldException |
//                IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void sendMessageWithJSON(final HttpServletResponse response,
//                                     final UnaryOperator<JSONObject> handler)
//            throws IOException {
//        JSONObject jsonObject = new JSONObject();
//        response.setContentType("application/json;charset=UTF-8");
//        PrintWriter writer = response.getWriter();
//        writer.print(handler.apply(jsonObject));
//        writer.flush();
//        writer.close();
//    }
//
//    private PropertyDescriptor[] getBeanProperties(final Object javaBean)
//            throws IntrospectionException {
//        return Introspector.getBeanInfo
//                (javaBean.getClass(), Object.class).getPropertyDescriptors();
//    }
//
//    private void populateData(final Object javaBean, final String property,
//                              final Object[] value) {
//        Objects.requireNonNull(javaBean);
//        try {
//            for (PropertyDescriptor prop :
//                    getBeanProperties(javaBean)) {
//                if (prop.getName().equals
//                        (StringUtils.toCamel(property))) {
//                    prop.getWriteMethod().invoke(javaBean, value);
//                }
//            }
//        } catch (IntrospectionException | IllegalAccessException |
//                InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static final int DEFAULT_WORD_COUNT = 0;
//    private static final String DEFAULT_DESC = "暂无描述";
//    private static final String DEFAULT_PUBLISHER = "未知发布者";
//    private static final String PUBLISHER = "username";
//    private static final String ERROR_DETAIL = "detail";
//    private static final String MESSAGE_ADD = "文章添加";
//    private static final String MESSAGE_UPDATE = "文章更新";
//    private static final String MESSAGE_DELETE = "文章删除";
//    private static final String MESSAGE_PUBLISH = "文章发布";
//    private static final String MESSAGE_SUCCESS = "成功！";
//    private static final String MESSAGE_FAILED = "失败！";
//    private static final String UTF_8 = "UTF-8";
//    private static final String ARTICLE_ID = "article_id";
//    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
//
//}
