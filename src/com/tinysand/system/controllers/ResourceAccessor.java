package com.tinysand.system.controllers;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

@WebServlet(name = "resourceAccessor", urlPatterns = {
        "/image", "/file", "/base", "/safe"
})
public class ResourceAccessor extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding(UTF_8);
        response.setCharacterEncoding(UTF_8);
        //要预览的图片的路径，如果有目录则也包含了目录信息
        final String requestUri = request.getParameter("url");
        final String requestType = request.getRequestURI().replaceFirst
                ("/", NULL_TEXT);
        switch (requestType) {
            case IMAGE_TYPE : handleImageRequest
                    (response, requestUri); break;

            case FILE_TYPE : handleFileRequest
                    (response, requestUri); break;

            case BASE_TYPE: baseRequestHandler
                    (response, () -> requestUri); break;

            case SAFETY_TYPE: baseRequestHandler
                    (response, () -> SAFE_PATH + requestUri); break;

            default: break;
        }
    }

    private void handleImageRequest(final HttpServletResponse response,
                                    final String requestUri) throws IOException {
        handleRequest(response, () -> IMAGE_PATH + requestUri,
                (outputStream, path) -> {
            if (isSupportedImageType(requestUri)) {
                ImageWriter imageWriter = ImageIO
                        .getImageWritersByFormatName("jpg").next();
                try {
                    imageWriter.setOutput(ImageIO.createImageOutputStream
                            (outputStream));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageWriteParam writeParam = getDefaultWriteParam(ALPHA);
                try {
                    imageWriter.write(imageWriter.getDefaultStreamMetadata
                                    (writeParam),
                            new IIOImage(ImageIO.read(path.toFile()),
                                    null, null),
                            writeParam);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleRequest(final HttpServletResponse response,
                               final Supplier<String> requestUri,
                               final BiConsumer<OutputStream, Path> handler)
            throws IOException {
        Path requestPath = Paths.get(getServletContext().getRealPath
                (requestUri.get()));
        if (Files.exists(requestPath)) {
            OutputStream outputStream = response.getOutputStream();
            handler.accept(outputStream, requestPath);
            outputStream.flush();
            outputStream.close();
        }
    }

    private void handleFileRequest(final HttpServletResponse response,
                                   final String requestUri)
            throws IOException {
        handleRequest(response, () -> FILE_PATH + requestUri,
                (outputStream, path) -> {
            try {
                byte[] allBytes = Files.readAllBytes(path);
                outputStream.write(allBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void baseRequestHandler(final HttpServletResponse response,
                                    final Supplier<String> requestPath)
            throws IOException {
        handleRequest(response, requestPath,
                (outputStream, path) -> {
                    try {
                        byte[] allBytes = Files.readAllBytes(path);
                        outputStream.write(allBytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
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

    @SuppressWarnings("all")
    private ImageWriteParam getDefaultWriteParam(final float quality) {
        ImageWriteParam writeParam = new JPEGImageWriteParam
                (Locale.getDefault());
        writeParam.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(quality);
        return writeParam;
    }

    private static final String IMAGE_PATH = "WEB-INF/upload/images/";
    private static final String FILE_PATH = "WEB-INF/upload/files/";
    private static final String SAFE_PATH = "WEB-INF/";
    private static final float ALPHA = 0.8F;
    private static final String UTF_8 = "UTF-8";
    private static final String NULL_TEXT = "";
    private static final String SAFETY_TYPE = "safe";
    private static final String BASE_TYPE = "base";
    private static final String IMAGE_TYPE = "image";
    private static final String FILE_TYPE = "file";
    private static final String[] IMAGE_EXTENSION = new String[] {
            "png", "jpeg", "jpg", "webp", "gif", "bmp"
    };
}
