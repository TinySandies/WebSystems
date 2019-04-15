//package com.tinysand.system.controllers.ajax;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.text.NumberFormat;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//import java.util.UUID;
//import java.util.function.UnaryOperator;
//
//import com.alibaba.fastjson.JSONObject;
//import com.tinysand.system.util.Toolkit;
//import org.apache.commons.fileupload.FileItem;
//import org.apache.commons.fileupload.disk.DiskFileItemFactory;
//import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
//import org.apache.commons.fileupload.servlet.ServletFileUpload;
//
//@WebServlet(name = "uploader", urlPatterns = "/testUpload")
//public class Test extends HttpServlet {
//    @Override
//    public void doPost(final HttpServletRequest request,
//                       final HttpServletResponse response) {
//        ServletFileUpload fileUploader = new ServletFileUpload();
//        fileUploader.setFileSizeMax(FILE_SIZE_LIMIT);
//        fileUploader.setSizeMax(TOTAL_FILE_SIZE);
//        fileUploader.setHeaderEncoding(UTF_8);
//        fileUploader.setFileItemFactory(buildItemFactory());
//        fileUploader.setProgressListener((uploadedSize, totalSize, item) ->
//            request.getSession().setAttribute("upload_status",
//                            NumberFormat.getPercentInstance()
//                                    .format((double) uploadedSize / totalSize))
//        );
//
//        try {
//            if (ServletFileUpload.isMultipartContent(request)) {
//                for (FileItem fileItem : fileUploader.parseRequest(request)) {
//                    if (!fileItem.isFormField()) {
//                        final String fileName = fileItem.getName();
//                        if (!Toolkit.notNull(fileName) ||
//                                Toolkit.isEmpty(fileName)) {
//                            writeUploadMessage(response, fileName);
//                            return;
//                        }
//
//                        final String randomFileName = getRandomFileName(fileName);
//                        uploadFile(fileItem, randomFileName);
//                        writeUploadMessage(response, randomFileName);
//                        request.getSession().setAttribute
//                                ("uploaded_file_name", randomFileName);
//
//                        System.err.println("HANDLE UPLOAD ACTION");
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
////    private void uploadFailed(final HttpServletResponse response)
////            throws IOException {
////        response(response, (jsonObject -> {
////            jsonObject.put("success", 0);
////            jsonObject.put("message", "Failed");
////            jsonObject.put("url", "null");
////            return jsonObject;
////        }));
////    }
//
//    private void response(final HttpServletResponse response,
//                          final UnaryOperator<JSONObject> handler)
//            throws IOException {
//        response.setContentType("application/json;charset=UTF-8");
//        PrintWriter writer = response.getWriter();
//        JSONObject jsonObject = new JSONObject();
//        writer.print(handler.apply(jsonObject));
//    }
//
////    private void uploadSucceed(final HttpServletResponse response,
////                               final String fileName) throws IOException {
////        response(response, (jsonObject -> {
////            jsonObject.put("success", 1);
////            jsonObject.put("message", "Success");
////            jsonObject.put("url", "/preview?uri=" + fileName);
////            return jsonObject;
////        }));
////    }
//
//    private void writeUploadMessage(final HttpServletResponse response,
//                                    final String fileName)
//            throws IOException {
//        response(response, jsonObject -> {
//            jsonObject.put("success", Objects.nonNull(fileName) ?
//                    1 : 0);
//            jsonObject.put("message", Objects.nonNull(fileName) ?
//                    "Success" : "failed");
//            jsonObject.put("url", Objects.nonNull(fileName) ?
//                    "/preview?uri=" + fileName : "null");
//            return jsonObject;
//        });
//    }
//
//    private void uploadFile(final FileItem fileItem, final String fileName)
//            throws Exception {
//        Path savedPath = Paths.get(getServletContext()
//                .getRealPath(IMAGE_PATH));
//        if (!Files.exists(savedPath))
//            Files.createDirectories(savedPath);
//
//        fileItem.write(Paths.get(getServletContext()
//                .getRealPath(IMAGE_PATH) + fileName).toFile());
//        fileItem.delete();
//    }
//
//    private void deleteFile(final String fileName) throws IOException {
//        Path filePath = Paths.get(getServletContext()
//                .getRealPath(IMAGE_PATH) + fileName);
//        if (Files.exists(filePath))
//            Files.delete(filePath);
//    }
//
//    private Map<String, String> parseQuery2Map(final String queryString) {
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
//
//    @Override
//    public void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws IOException {
//
//        Map<String, String> queryMap = parseQuery2Map(request.getQueryString());
//        if (!queryMap.isEmpty()) {
//            String action = queryMap.get("action");
//            if (Objects.nonNull(action)) {
//                if (action.equals("upload_status")) {
//                    response.getWriter().print(request.getSession()
//                            .getAttribute("upload_status"));
//
//                    System.err.println("action execute upload status: " + action);
//                } else if (action.equals("get_url")) {
//                    writeUploadMessage(response, (String) request.getSession()
//                            .getAttribute("uploaded_file_name"));
//                    System.err.println("action execute url: " + action +
//                            "file Name: " + request.getSession()
//                            .getAttribute("uploaded_file_name"));
//
//                }
//            }
//        }
//        System.err.println("doGet调用");
//    }
//
//    private String getRandomFileName(final String fileName) {
//        return UUID.randomUUID().toString() + fileName.substring
//                (fileName.lastIndexOf("."));
//    }
//
//    private DiskFileItemFactory buildItemFactory() {
//        DiskFileItemFactory itemFactory = new DiskFileItemFactory
//                (THRESHOLD_SIZE, Paths.get(getServletContext()
//                        .getRealPath(UPLOAD_TEMP_PATH)).toFile());
//
//        itemFactory.setDefaultCharset(UTF_8);
//        itemFactory.setFileCleaningTracker(FileCleanerCleanup
//                .getFileCleaningTracker(getServletContext()));
//        return itemFactory;
//    }
//
//    private static final String IMAGE_PATH = "WEB-INF/uploadImage/images/";
//    private static final String UPLOAD_TEMP_PATH = "WEB-INF/temp/";
//    private static final String UTF_8 = "UTF-8";
//    private static final int THRESHOLD_SIZE = 1024 * 1024 * 12;
//    private static final int FILE_SIZE_LIMIT = 1024 * 1024 * 6;
//    private static final int TOTAL_FILE_SIZE = 1024 * 1024 * 64;
//}
//
