package com.tinysand.system.controllers;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.text.NumberFormat;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSONObject;
import com.tinysand.system.errors.FileUploadError;
import com.tinysand.system.util.Toolkit;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;


@WebServlet(name = "uploader", urlPatterns = "/upload")
public class FileUploader extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        ServletFileUpload fileUploader = createFileUpload
                (FILE_SIZE_LIMIT, TOTAL_FILE_SIZE,
                        UTF_8, buildItemFactory());
        fileUploader.setProgressListener((uploadedSize, totalSize, item) ->
                progressListener(request, uploadedSize, totalSize)
        );
        try {
            if (ServletFileUpload.isMultipartContent(request)) {
                for (FileItem fileItem : fileUploader.parseRequest(request)) {
                    if (!fileItem.isFormField()) {
                        String fileName = fileItem.getName();
                        if (!Objects.nonNull(fileName) ||
                                Toolkit.isEmpty(fileName)) {
                            writeUploadMessage(response, fileName);
                            return;
                        }
                        fileName = fileName.contains("\\") ? fileName
                                .substring(fileName.lastIndexOf("\\") + 1) :
                                fileName;

                        final String randomFileName = getRandomFileName(fileName);
                        writeUploadMessage(response,
                                uploadFile(request, fileItem, randomFileName));
                        request.getSession().setAttribute(FILE_URL,
                                request.getSession().getId() + "/"
                                        + randomFileName);
                    }
                }
            }
        } catch (FileUploadBase.SizeLimitExceededException e) {
            throw new FileUploadError(String.format
                    ("上传文件总大小不能超过%sMib",
                            TOTAL_FILE_SIZE / 1024 / 1024));
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            throw new FileUploadError(String.format
                    ("单个文件大小不能超过%sMib",
                            FILE_SIZE_LIMIT / 1024 / 1024));
        } catch (FileUploadBase.FileUploadIOException |
                FileUploadException e) {
            throw new FileUploadError("文件/图片上传失败");
        } catch (UnsupportedEncodingException e) {
            throw new FileUploadError("不支持该类型的文件上传");
        } catch (IOException e) {
            throw new FileUploadError("文件读取或写入异常");
        } catch (Exception e) {
            throw new FileUploadError("文件/图片上传时发生未知异常");
        }
    }

    @SuppressWarnings("all")
    private ServletFileUpload createFileUpload
            (final long fileSizeLimit, final long totalFileSize,
             final String charset, final FileItemFactory itemFactory) {
        ServletFileUpload fileUploader = new ServletFileUpload();
        fileUploader.setFileSizeMax(fileSizeLimit);
        fileUploader.setSizeMax(totalFileSize);
        fileUploader.setHeaderEncoding(charset);
        fileUploader.setFileItemFactory(itemFactory);
        return fileUploader;
    }

    private void progressListener(final HttpServletRequest request,
                                  final long uploadedSize,
                                  final long totalSize) {
        request.getSession().setAttribute
                (UPLOAD_STATUS, NumberFormat.getPercentInstance()
                        .format(((double) uploadedSize / totalSize)));
    }

    private void response(final HttpServletResponse response,
                          final UnaryOperator<JSONObject> handler)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        JSONObject jsonObject = new JSONObject();
        writer.print(handler.apply(jsonObject));
    }

    private void writeUploadMessage(final HttpServletResponse response,
                                    final String fileName)
            throws IOException {
        response(response, jsonObject -> {
            jsonObject.put("success", Objects.nonNull(fileName) ?
                    1 : 0);
            jsonObject.put("message", Objects.nonNull(fileName) ?
                    "Success" : "failed");
            jsonObject.put("url", Objects.nonNull(fileName) ?
                    "/image?url=" + fileName : "null");
            return jsonObject;
        });
    }

    private String uploadFile(final HttpServletRequest request,
                            final FileItem fileItem, final String fileUri)
            throws Exception {
        Path storagePath = Paths.get(createDirectoriesWithSessionId(request,
                fileUri) + "/" + fileUri);
        fileItem.write(storagePath.toFile());
        fileItem.delete();

        return String.format("%s/%s", request.getSession()
                .getId(), fileUri);
    }

    private void deleteFile(final String fileUri) throws IOException {
        Path filePath = Paths.get(getServletContext()
                .getRealPath((isSupportedImageType(fileUri) ?
                        IMAGE_PATH : FILE_PATH) + fileUri));
        if (Files.exists(filePath))
            Files.delete(filePath);
    }

    private Path createDirectoriesWithSessionId
            (final HttpServletRequest request, final String fileUri)
            throws IOException {
        final String sessionId = request.getSession().getId();
        Path storagePath = Paths.get(request.getServletContext()
                .getRealPath(isSupportedImageType(fileUri) ?
                        IMAGE_PATH : FILE_PATH) + sessionId + "/");
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }
        return storagePath;
    }

    private boolean isSupportedImageType(final String fileName) {
        final String extension = fileName
                .substring(fileName.lastIndexOf(".") + 1);
        Set<String> imageExtension = new HashSet<>() {
            {
                addAll(Arrays.asList(IMAGE_EXTENSION));
            }
        };
        return imageExtension.contains(extension);
    }

    private Map<String, String> parseQueryString2Map(final String queryString) {
        Objects.requireNonNull(queryString);
        String[] paramEntryArray = queryString.split("&");
        Map<String, String> parameterMap = new HashMap<>();
        for (String paramEntry : paramEntryArray) {
            String[] entryArray = paramEntry.split("=");
            if (entryArray.length % 2 == 0)
                parameterMap.put(entryArray[0], entryArray[1]);
        }
        return parameterMap;
    }

    private void handleRequest(final HttpServletRequest request,
                               final HttpServletResponse response,
                               final Map<String, String> queryParams,
                               final String action) throws IOException {
        switch (action) {
            case UPLOAD_STATUS:
                response(response, jsonObject -> {
                    jsonObject.put(UPLOAD_STATUS, request.getSession()
                            .getAttribute(UPLOAD_STATUS));
                    return jsonObject;
                }); break;

            case GET_URL:
                if (Objects.nonNull(queryParams.get(FILE_DELETION)))
                    deleteFile(queryParams.get(FILE_DELETION));

                response(response, jsonObject -> {
                    jsonObject.put(FILE_URL, obtainFileUri(request));
                    return jsonObject;
                }); break;

            case GET_MESSAGE:
                if (Objects.nonNull(request.getSession()
                        .getAttribute(UPLOAD_MESSAGE)))
                    response(response, jsonObject -> {
                        jsonObject.put(UPLOAD_MESSAGE, request.getSession()
                                .getAttribute(UPLOAD_MESSAGE));
                        request.getSession()
                                .removeAttribute(UPLOAD_MESSAGE);
                        return jsonObject;
                    }); break;
            default: break;
        }
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(UTF_8);
        Map<String, String> queryParams = parseQueryString2Map
                (request.getQueryString());
        if (!queryParams.isEmpty()) {
            String action = queryParams.get("action");
            if (Objects.nonNull(action))
                handleRequest(request, response, queryParams, action);
        }
    }

    private String obtainFileUri(final HttpServletRequest request) {
        HttpSession session = request.getSession();
        String fileUri = (String) session
                .getAttribute(FILE_URL);
        if (!Objects.nonNull(fileUri)) {
            try {
                Thread.sleep(200);
                fileUri = obtainFileUri(request);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        session.removeAttribute(FILE_URL);
        return fileUri;
    }

    private String getRandomFileName(final String fileName) {
        return UUID.randomUUID().toString() + "_" + fileName;
    }

    private DiskFileItemFactory buildItemFactory() {
        DiskFileItemFactory itemFactory = new DiskFileItemFactory
                (THRESHOLD_SIZE, Paths.get(getServletContext()
                        .getRealPath(UPLOAD_TEMP_PATH)).toFile());

        itemFactory.setDefaultCharset(UTF_8);
        itemFactory.setFileCleaningTracker(FileCleanerCleanup
                .getFileCleaningTracker(getServletContext()));
        return itemFactory;
    }

    private static final String[] IMAGE_EXTENSION = new String[] {
      "png", "jpeg", "jpg", "webp", "gif", "bmp"
    };

    public static final String UPLOAD_MESSAGE = "upload_message";
    private static final String UPLOAD_STATUS = "upload_status";
    private static final String GET_MESSAGE = "get_message";
    private static final String FILE_URL = "url";
    private static final String FILE_DELETION = "delete";
    private static final String FILE_PATH = "WEB-INF/upload/files/";
    private static final String IMAGE_PATH = "WEB-INF/upload/images/";
    private static final String GET_URL = "get_url";
    private static final String UPLOAD_TEMP_PATH = "WEB-INF/temp";
    private static final String UTF_8 = "UTF-8";
    private static final int THRESHOLD_SIZE = 1024 * 1024 * 16;
    private static final int FILE_SIZE_LIMIT = 1024 * 1024 * 3;
    private static final int TOTAL_FILE_SIZE = 1024 * 1024 * 128;
}

