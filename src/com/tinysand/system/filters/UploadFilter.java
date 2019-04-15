//package com.tinysand.system.filters;
//
//import com.tinysand.system.util.Toolkit;
//import org.apache.commons.fileupload.disk.DiskFileItemFactory;
//import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
//
//import javax.servlet.http.HttpFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequestWrapper;
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
//public class UploadFilter extends HttpFilter {
//
//    class UploadWrapper extends HttpServletRequestWrapper {
//
//        private final Map<String, Object> uploadFile = new HashMap<>();
//        private final String MULTIPART_HEADER = "Content-type";
//        private final String BASE_DIRECTORY = "/WEB-INF/";
//        private final String CHARACTER_SET = "UTF-8";
//        private final int UPLOAD_THRESHOLD = 1024 * 1024;
//        private boolean isUploadFile;
//
//        UploadWrapper(HttpServletRequest request) {
//            super(request);
//            isUploadFile = Toolkit.notNull
//                    (request.getHeader(MULTIPART_HEADER))
//                    && request.getHeader(MULTIPART_HEADER)
//                    .startsWith("multipart/form-data");
//            if (isUploadFile) {
//                DiskFileItemFactory itemFactory = new DiskFileItemFactory();
//                itemFactory.setRepository(new File
//                        ((request.getContextPath() + BASE_DIRECTORY)));
//                //display the upload directory;
//                System.out.println
//                        ((request.getContextPath() + BASE_DIRECTORY));
//                itemFactory.setDefaultCharset(CHARACTER_SET);
//                itemFactory.setSizeThreshold(UPLOAD_THRESHOLD);
//                itemFactory.setFileCleaningTracker
//                        (FileCleanerCleanup.getFileCleaningTracker
//                                (this.getServletContext()));
//
//            }
//        }
//    }
//}
