//package com.tinysand.system.services;
//
//import com.alibaba.fastjson.JSONObject;
//import com.tinysand.system.access.implement.AccessRunner;
//import com.tinysand.system.controllers.NotNull;
//import com.tinysand.system.models.Article;
//import com.tinysand.system.util.StringUtils;
//import com.tinysand.system.util.Toolkit;
//
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
//import java.sql.SQLException;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.function.BiConsumer;
//import java.util.function.Function;
//import java.util.function.Supplier;
//import java.util.function.UnaryOperator;
//
//@SuppressWarnings("all")
//public class AbstractService implements SystemService{
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
//    private void commonPopulate(final Article article,
//                                final Map<String, String[]> formData) {
//        for (Map.Entry<String, String[]> dataEntry : formData.entrySet()) {
//            System.err.println(dataEntry.getValue()[0]);
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
//    @Override
//    public void add(
//            HttpServletRequest request, HttpServletResponse response,
//            Map<String, String[]> formData) throws IOException {
//        Article article = (Article) Toolkit.getBeanInstance(Article.class);
//        commonPopulate(article, formData);
//        System.err.println(article);
//
//        article.setPublisher(getPublisher(request));
//        article.setArticleFolder(request.getSession().getId());
//        DMLExecutor(request, response, article, () ->
//                checkAllDataAndRecordError(request, article),
//                () -> OPERATION_TYPE_ADD);
//    }
//
//    private static PropertyDescriptor[] getBeanProperties(final Object javaBean)
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
//    @Override
//    public void update(
//            HttpServletRequest request, HttpServletResponse response,
//            Map<String, String[]> formData) {
//
//    }
//
//    @Override
//    public void delete(
//            HttpServletRequest request, HttpServletResponse response,
//            int id) {
//
//    }
//
//    @Override
//    public <R> R query(
//            HttpServletRequest request, HttpServletResponse response,
//            int id) {
//        return null;
//    }
//
//    private void InsertionAndUpdate() {
//
//    }
//
//    private static void dataPersistence(final AccessRunner accessRunner,
//                                        final Connection connection,
//                                        final Object beanObject,
//                                        final String operateType)
//            throws SQLException {
//        switch (operateType) {
//            case OPERATION_TYPE_ADD: accessRunner.insertWithBean
//                    (connection, beanObject); break;
//            case OPERATION_TYPE_UPDATE:
//            case OPERATION_TYPE_PUBLISH: accessRunner.updateWithBean
//                    (connection, beanObject); break;
//            case OPERATION_TYPE_DELETE: accessRunner.deleteWithBean
//                    (connection, beanObject); break;
//            default: break;
//        }
//    }
//
//    private static boolean operateDataBase(final HttpServletRequest request,
//                                           final Object javaBean, final BiConsumer
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
//    private void DMLExecutor(final HttpServletRequest request,
//                             final HttpServletResponse response,
//                             final Object beanObject,
//                             final Supplier<Boolean> checkData,
//                             final Supplier<String> operationType)
//            throws IOException {
//        if (checkData.get() && operateDataBase(request, beanObject,
//                (accessRunner, o) -> {
//            try (Connection connection = Toolkit.obtainConnection()) {
//                dataPersistence(accessRunner, connection, o, operationType.get());
//            } catch (SQLException e) {
//                request.getSession().setAttribute
//                        (ERROR_DETAIL, e.getMessage());
//                e.printStackTrace();
//            }
//        })) {
//            sendMessageWithJSON(response, jsonObject -> {
//                jsonObject.put("message",
//                        operationType.get() + MESSAGE_SUCCESS);
//                return jsonObject;
//            });
//        } else {
//            final String errorDetails = (String) request.getSession()
//                    .getAttribute(ERROR_DETAIL);
//            sendMessageWithJSON(response, jsonObject -> {
//                jsonObject.put("message", operationType.get()
//                        + MESSAGE_FAILED + errorDetails);
//                return jsonObject;
//            });
//        }
//    }
//
//    private static void sendMessageWithJSON(final HttpServletResponse response,
//                                            final UnaryOperator<JSONObject> handler)
//            throws IOException {
//        JSONObject jsonObject = new JSONObject();
//        response.setContentType("application/json;charset=UTF-8");
//        PrintWriter writer = response.getWriter();
//        writer.print(handler.apply(jsonObject));
//        writer.flush();
//        writer.close();
//    }
//
//    private static boolean checkAllDataAndRecordError
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
//    private static void ensureAllDataIsLegal(final Object javaBean)
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
//    private enum OperationType {
//        OPERATION_TYPE_ADD("添加"), OPERATION_TYPE_UPDATE("更新"),
//        OPERATION_TYPE_DELETE("删除"), OPERATION_TYPE_PUBLISH("发布");
//
//        private String operationType;
//        OperationType(final String operationType) {
//            this.operationType = operationType;
//        }
//    }
//
//    private static final int DEFAULT_WORD_COUNT = 0;
//    private static final String DEFAULT_DESC = "暂无描述";
//    private static final String DEFAULT_PUBLISHER = "未知发布者";
//    private static final String PUBLISHER = "username";
//    private static final String OPERATION_TYPE_ADD = "文章添加";
//    private static final String OPERATION_TYPE_UPDATE = "文章更新";
//    private static final String OPERATION_TYPE_DELETE = "文章删除";
//    private static final String OPERATION_TYPE_PUBLISH = "文章发布";
//    private static final String MESSAGE_SUCCESS = "成功！";
//    private static final String MESSAGE_FAILED = "失败！";
//    private static final String ERROR_DETAIL = "error_detail";
//
//}
