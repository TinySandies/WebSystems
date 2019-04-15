//package com.tinysand.system.controllers;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Set;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Locale;
//import javax.imageio.IIOImage;
//import javax.imageio.ImageIO;
//import javax.imageio.ImageWriteParam;
//import javax.imageio.ImageWriter;
//import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
//
//@WebServlet(name = "imageAccessor", urlPatterns = {
//        "/image", "/file"
//})
//public class ImageAccessor extends HttpServlet {
//    @Override
//    public void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws IOException {
//        //要预览的图片的路径，如果有目录则也包含了目录信息
//        final String imageUri = request.getParameter("uri");
//        //图片资源路径
//        Path imagePath = Paths.get(getServletContext()
//                .getRealPath(IMAGE_PATH + imageUri));
//
//        System.err.println(request.getRequestURI().contains(IMAGE_TYPE));
//        System.err.println(request.getRequestURI().contains(FILE_TYPE));
//
//        //所请求的是图片并且资源存在
//        if (isSupportedImageType(imageUri) && Files.exists(imagePath)) {
//            OutputStream responseStream = response.getOutputStream();
//            ImageWriter imageWriter = ImageIO
//                    .getImageWritersByFormatName("jpg").next();
//            imageWriter.setOutput(ImageIO.createImageOutputStream
//                    (responseStream));
//            ImageWriteParam writeParam = getDefaultWriteParam(ALPHA);
//            imageWriter.write(imageWriter.getDefaultStreamMetadata
//                            (writeParam),
//                    new IIOImage(ImageIO.read(imagePath.toFile()),
//                            null, null),
//                    writeParam);
//            responseStream.flush();
//            responseStream.close();
//        } else if (Files.exists(imagePath)) {
//            Path filePath = Paths.get(getServletContext().getRealPath
//                    (FILE_PATH + imageUri));
//            System.out.println("else branch");
////            Files.readAllBytes();
//        }
//
////        request.getRequestURI().contains()
//    }
//
//    private boolean isSupportedImageType(final String fileName) {
//        final String extension = fileName
//                .substring(fileName.lastIndexOf(".") + 1);
//        Set<String> imageExtension = new HashSet<>() {
//            {
//                addAll(Arrays.asList(IMAGE_EXTENSION));
//            }
//        };
//        return imageExtension.contains(extension);
//    }
//
//    @SuppressWarnings("all")
//    private ImageWriteParam getDefaultWriteParam(final float quality) {
//        ImageWriteParam writeParam = new JPEGImageWriteParam
//                (Locale.getDefault());
//        writeParam.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
//        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//        writeParam.setCompressionQuality(quality);
//        return writeParam;
//    }
//
//    private static final float ALPHA = 0.8F;
//    private static final String IMAGE_TYPE = "image";
//    private static final String FILE_TYPE = "file";
//    private static final String IMAGE_PATH = "WEB-INF/upload/images/";
//    private static final String FILE_PATH = "WEB-INF/upload/files/";
//    private static final String[] IMAGE_EXTENSION = new String[] {
//            "png", "jpeg", "jpg", "webp", "gif", "bmp"
//    };
//}
