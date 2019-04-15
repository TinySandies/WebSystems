package com.tinysand.system;

import com.tinysand.system.controllers.NotNull;
import com.tinysand.system.models.Article;
import com.tinysand.system.services.BeanType;
import com.tinysand.system.util.StringUtils;
import com.tinysand.system.util.Toolkit;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BaseTest {

    //    @Test
//    public void testAccessor() throws Exception {
//
//        baseAccessor.execute("insert into user(id, username, password)" +
//                " values (?, ?, ?)", new Object[]{4, "sand", "sand6373"});
//    }
//
//    @Test
//    public void testBeanHandler() throws SQLException {
//        User user = baseAccessor.query("select * from user",
//                new BeanHandler<>(User.class));
//
//        System.out.println(user);
//    }
//
//    @Test
//    public void testBeanListHandler() throws SQLException {
//        List<User> users = baseAccessor.query("select * from user",
//                new BeanListHandler<>(User.class));
//
//        for (User user : users)
//            System.out.println(user);
//    }
//
//
//    @Test
//    public void testMapListHandler() throws SQLException {
//        List<Map<String, String>> dataList = baseAccessor.query
//                ("select * from user",
//                        new MapListHandler());
//
//        for (Map<String, String> dataMap : dataList) {
//            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
//                System.out.print(String.format("[ %s = %s ]",
//                        entry.getKey(), entry.getValue()));
//            }
//            System.out.println();
//        }
//    }
//
//    @Test
//    public void testMapHandler() throws SQLException {
//        Map<String, String> dataMap = baseAccessor.query
//                ("select * from user where id = ?",
//                        new MapHandler(), new Object[]{1});
//
//        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
//            System.out.print(String.format("[ %s = %s ]",
//                    entry.getKey(), entry.getValue()));
//        }
//        System.out.println();
//    }

    public static String generateRandomPath(String fileName) {
        int hashcode = fileName.hashCode();
        int d1 = hashcode & 0xf;
        int d2 = (hashcode >> 4) & 0xf;
        return "/" + d1 + "/" + d2;
    }

    private static final String[] IMAGE_EXTENSION = new String[] {
            "png", "jpeg", "jpg", "webp", "gif", "bmp"
    };

//    @SuppressWarnings("unchecked")
    private boolean isImageType(final String fileName) {
        final String extension = fileName
                .substring(fileName.lastIndexOf(".") + 1);
        Set<String> imageExtension = new HashSet<>() {
            {
                addAll(Arrays.asList(IMAGE_EXTENSION));
            }
        };
        return imageExtension.contains(extension);
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
//                    System.out.println(declaredField.getName());
                    final Object value = declaredField.get(javaBean);
                    if (!Objects.nonNull(value) || StringUtils.isEmpty(value))
                        throw new NullPointerException(
                                String.format("请把%s填写完整！", declaredField.getName()));
//                    System.out.println(declaredField.getName());
                }
            }
        } catch (IntrospectionException | NoSuchFieldException |
                IllegalAccessException e) {
            System.out.println("error");
//            e.printStackTrace();
        }
    }

    private boolean checkAllDataAndRecordError(final Article article) {
        try {
            ensureAllDataIsLegal(article);
        } catch (NullPointerException e) {
//            request.getSession().setAttribute(ERROR_DETAIL, e.getMessage());
            System.err.println("error" + e.getMessage());
            return false;
        }
        return true;
    }

    private static final String ERROR_DETAIL = "detail";


    private PropertyDescriptor[] getBeanProperties(final Object javaBean)
            throws IntrospectionException {
        return Introspector.getBeanInfo
                (javaBean.getClass(), Object.class).getPropertyDescriptors();
    }

    @Test
    public void commonTest() {
//        throw new NullPointerException("hello");

        Object article = new Article();

//        PropertyDescriptor[] beanProperties = Introspector.getBeanInfo(
//                Article.class, Object.class).getPropertyDescriptors();
//
//        Arrays.stream(beanProperties).forEach(propertyDescriptor -> {
//            System.out.println(propertyDescriptor.getName());
//        });
//        System.out.println(Arrays.toString(beanProperties));



//        System.out.println(article.getClass().getSimpleName());
//        Article article = new Article(null, "hello", null, "folder", null, null);

//        checkAllDataAndRecordError(article);
//        this.<Article>beanGenerator(null);


//        System.out.println(instance(Article.class));


//        ServiceLoader<Matcher> serviceLoader = ServiceLoader.load(Matcher.class);
//        System.out.println(serviceLoader);
//        for (Matcher matcher : serviceLoader) {
//            System.out.println("service");
//            matcher.apply(null);
//        }


//        try (Connection connection = Toolkit.obtainConnection()) {
//            AccessRunner accessRunner = new AccessRunner();
//            Article query = accessRunner.query(connection, Article.class,
//                    new BeanHandler<>(Article.class));
//            System.out.println(query);
//        }
//        final String lastModifiedTime = LocalDateTime.now().format
//                (DateTimeFormatter.ofPattern
//                        (DATE_TIME_PATTERN, Locale.CHINA));
//
//        System.out.println(lastModifiedTime);
//        Timestamp timestamp = Timestamp.valueOf();
//        timestamp.toLocalDateTime()
//        System.out.println(StringUtils.toCamel(null));
//        Object article = new Article();
//        check(article);

//        Connection connection1;
//
//        try (Connection connection = Toolkit.obtainConnection()) {
//            System.out.println(connection);
//            connection1 = connection;
//        }
//        System.out.println(connection1);


//        System.out.println("hi你好".length());

//        System.out.println(new Object[0].length == 0);
//        System.out.println(isImageType("22.png"));
//        System.out.println(isImageType("22.gif"));
//        System.out.println(isImageType("22.webp"));
//        System.out.println(isImageType("22.jpeg"));
//        System.out.println(isImageType("22.jpg"));
//        System.err.println(isImageType("22.png"));
//        System.err.println(isImageType("22.jpg"));
//
//        System.out.println(generateRandomPath("33.png"));
//
//        String[] format = new String[] {
//                "png", "jpg", "gif", "webp"
//        };
//
//        Arrays.sort(format);
//        System.err.println(Arrays.binarySearch(format, "webp"));
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("key", "KEY");
//        jsonObject.put("name", "TINY");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(jsonObject);
//        System.out.println(jsonArray);

//        Article article = new Article("关于WEB-INF", "内容");
//        AccessRunner runner = new AccessRunner();
//        runner.insertWithBean(Toolkit.obtainConnection(), article);

        //            if (Objects.nonNull(action))
//                if (action.equals("upload_status")) {
//                    response.getWriter().print(request.getSession()
//                            .getAttribute(UPLOAD_STATUS));
//
//                } else if (action.equals("get_url")) {
//                    if (Objects.nonNull(queryParams.get("delete")) &&
//                            !queryParams.get("delete").isEmpty()) {
//                        System.out.println(queryParams.get("delete"));
//                        deleteFile(queryParams.get("delete"));
//                    }
//
//                    response(response, jsonObject -> {
//                        jsonObject.put("url", obtainFileUri(request));
//                        return jsonObject;
//                    });
//                } else if (action.equals("message")) {
//                    if (Objects.nonNull(request.getSession()
//                            .getAttribute(ERROR_MESSAGE))) {
//                        System.out.println("get the message" + request
//                                .getSession().getAttribute(ERROR_MESSAGE));
//
//                        response(response, jsonObject -> {
//                            jsonObject.put("error_message", request.getSession()
//                                    .getAttribute(ERROR_MESSAGE));
//                            return jsonObject;
//                        });
//                    }
//                }

    }
}
