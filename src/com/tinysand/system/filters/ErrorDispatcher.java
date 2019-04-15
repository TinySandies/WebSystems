package com.tinysand.system.filters;

import com.tinysand.system.controllers.FileUploader;
import com.tinysand.system.errors.FileUploadError;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

//@WebFilter(filterName = "errorDispatcher", urlPatterns = {"/*"},
//        initParams = {
//        @WebInitParam(name = "enabled", value = "true")
//})
public class ErrorDispatcher extends HttpFilter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        enabled = (Objects.nonNull(getFilterConfig().getInitParameter(ENABLED)) &&
                !getFilterConfig().getInitParameter(ENABLED).equals("true"));
    }

    @Override
    public void doFilter(HttpServletRequest request,
                         HttpServletResponse response,
                         FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            Throwable rootCause = e;
            while (Objects.nonNull(rootCause.getCause()))
                rootCause = rootCause.getCause();

            if (rootCause instanceof FileUploadError) {
                System.out.println("Message: " + rootCause.getMessage());
                request.getSession().setAttribute(FileUploader.UPLOAD_MESSAGE,
                        rootCause.getMessage());
            }
        }
    }

    private static boolean enabled;
    private static final String ENABLED = "enabled";
}
