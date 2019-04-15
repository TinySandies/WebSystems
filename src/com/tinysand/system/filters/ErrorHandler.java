//package com.tinysand.system.filters;
//
//import com.tinysand.system.errors.ArticleException;
//import com.tinysand.system.util.Public;
//import com.tinysand.system.util.Toolkit;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//public class ErrorHandler extends HttpFilter {
//    @Override
//    public void doFilter(HttpServletRequest request,
//                         HttpServletResponse response,
//                         FilterChain filterChain)
//            throws IOException, ServletException {
//        try {
//            filterChain.doFilter(request, response);
//        } catch (Exception error) {
//            Throwable cause = error;
//            while (Toolkit.notNull(cause.getCause())) {
//                cause = cause.getCause();
//            }
//            String message = error.getMessage();
//            request.setAttribute("errorMessage",
//                    String.format("ERROR => %s", Toolkit
//                            .notNull(message) ? message :
//                            error.getClass().getName()));
//            request.setAttribute("error", cause);
//            if (cause instanceof ArticleException) {
//                request.getRequestDispatcher
//                        (request.getServletPath()
//                        + Public.LOGIN_PATH)
//                        .forward(request, response);
//            }
//        }
//    }
//}
