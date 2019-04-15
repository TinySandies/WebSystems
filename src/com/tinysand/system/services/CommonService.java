package com.tinysand.system.services;

import com.alibaba.fastjson.JSONObject;
import com.tinysand.system.access.implement.AccessRunner;
import com.tinysand.system.controllers.NotNull;
import com.tinysand.system.models.Article;
import com.tinysand.system.util.StringUtils;
import com.tinysand.system.util.Toolkit;

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
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class CommonService {
    private boolean checkAllDataAndRecordError(
            final HttpServletRequest request, final Object beanObject) {
        final Class<?> beanClass = beanObject.getClass();
        try {
            for (PropertyDescriptor property : getBeanProperties(beanObject))
            {
                Field field = beanClass.getDeclaredField(property.getName());
                if (Objects.nonNull(field.getAnnotation(NotNull.class)))
                {
                    field.setAccessible(true);
                    final Object value = field.get(beanObject);
                    if (!Objects.nonNull(value) || StringUtils.isEmpty(value))
                        throw new NullPointerException(String.format
                                (ERROR_MESSAGE, field.getName()));
                }
            }
        } catch (NullPointerException | IntrospectionException |
                NoSuchFieldException | IllegalAccessException e) {
            request.getSession().setAttribute("detail", e.getMessage());
            return false;
        }
        return true;
    }

    public void addArticle(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final Map<String, String[]> formDataMap)
            throws IOException {
        final Article article = (Article) Toolkit.getBeanInstance(Article.class);
        commonPopulate(article, formDataMap);
        article.setPublisher(getPublisher(request));
        article.setArticleFolder(request.getSession().getId());
        DMLExecutor(request, response, article, () -> MESSAGE_ADD);
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

    private void commonPopulate(final Article article,
                                final Map<String, String[]> formDataMap) {
        try {
            PropertyDescriptor[] beanProperties = Introspector.getBeanInfo(
                    Article.class, Object.class).getPropertyDescriptors();

            Set<String> propertySet = Arrays.stream(beanProperties).map
                    (FeatureDescriptor::getName)
                    .collect(Collectors.toSet());

            for (Map.Entry<String, String[]> entry : formDataMap.entrySet()) {
                String key = entry.getKey();
                String[] value = entry.getValue();
                try {
                    switch (StringUtils.toCamel(key)) {
                        case "articleContent":
                            final String articleContent = value[0];
                            final String description = getArticleDescription
                                    (articleContent).orElse(DEFAULT_DESC);

                            setWordCount(article, articleContent);
                            article.setArticleContent(articleContent);
                            article.setDescription(description);
                            continue;

                        case "essay":
                            article.setEssay(isEssay(value[0]));
                            continue;

                        default:
                            populateData(article, propertySet, key, value);
                    }
                    populateData(article, propertySet, key, value);
                } catch (Exception e) {
                    System.out.println("error");
                    e.printStackTrace();
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
    }

    private void DMLExecutor(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object beanObject,
                             final Supplier<String> operationType)
    {
        if (checkAllDataAndRecordError(request, beanObject) &&
                dataPersistence(new AccessRunner(), beanObject))
        {
            writeMessageWithJSON(response, jsonObject -> {
                jsonObject.put("message",
                        operationType.get() + MESSAGE_SUCCESS);
                return jsonObject;
            });
        } else {
            writeMessageWithJSON(response, jsonObject -> {
                jsonObject.put("message",
                        operationType.get() + MESSAGE_FAILED +
                                request.getSession().getAttribute(ERROR_DETAIL));
                return jsonObject;
            });
        }
    }

    private boolean dataPersistence(final AccessRunner accessRunner,
                                    final Object beanObject) {
        try (Connection connection = Toolkit.obtainConnection()) {
            accessRunner.insertWithBean(connection, beanObject);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void writeMessageWithJSON(final HttpServletResponse response,
                                      final UnaryOperator<JSONObject> handler)
    {
        JSONObject jsonObject = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.print(handler.apply(jsonObject));
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateData(final Object beanObject,
                              final Set<String> beanProperties,
                              final String propName, final Object[] propValue)
    {
        Objects.requireNonNull(beanObject);
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
    }

    private Optional<Integer> getWordCount(final String text)
    {
        return wordProcessor(text, reader -> Optional.of(
                (int) reader.lines()
                        .filter(lineText -> !lineText.trim().isEmpty())
                        .filter(lineText -> !lineText.startsWith("#"))
                        .map(String::trim).map(String::length)
                        .reduce(0, (len1, len2) -> len1 + len2)));
    }

    private Optional<String> getArticleDescription(final String content)
    {
        return wordProcessor(content, reader -> reader.lines()
                .filter(lineText -> !lineText.trim().isEmpty())
                .filter(lineText -> !lineText.startsWith("#"))
                .findFirst());
    }

    private Optional wordProcessor(final String text, final Function
            <BufferedReader, Optional> textProcessor)
    {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(
                        text.getBytes(StandardCharsets.UTF_8)),
                        StandardCharsets.UTF_8));
        return textProcessor.apply(bufferedReader);
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
    private static final String ERROR_MESSAGE = "请把%s填写完整！";
    private static final String ERROR_DETAIL = "detail";
    private static final String MESSAGE_ADD = "文章添加";
    private static final String MESSAGE_UPDATE = "文章更新";
    private static final String MESSAGE_DELETE = "文章删除";
    private static final String MESSAGE_PUBLISH = "文章发布";
    private static final String MESSAGE_SUCCESS = "成功！";
    private static final String MESSAGE_FAILED = "失败！";
}
