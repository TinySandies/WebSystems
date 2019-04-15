//package com.tinysand.system.filters;
//
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.annotation.WebInitParam;
//import javax.servlet.http.HttpFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Objects;
//
//@WebFilter(filterName = "protectedFilter", urlPatterns
//        = {"/WEB-INF/images/*"}, initParams = {
//        @WebInitParam(name = "errorResources",
//                value = "WEB-INF/images/error/error.png"),
//                @WebInitParam(name = "enable", value = "true")
//})
//public class ProtectedFilter extends HttpFilter {
//    private static String errorResources;
//    private static String enable;
//    @Override
//    public void init(FilterConfig filterConfig) {
//        errorResources = filterConfig.getInitParameter
//                ("errorResources");
//        enable = filterConfig.getInitParameter("enable");
//    }
//    @Override
//    public void doFilter(HttpServletRequest request,
//                         HttpServletResponse response,
//                         FilterChain filterChain)
//            throws IOException, ServletException {
//        final String refererURL = request.getHeader
//                ("referer");
//        if (Objects.nonNull(enable) && enable.trim()
//                .equalsIgnoreCase("true"))
//            if (!Objects.nonNull(refererURL) || refererURL.contains
//                    (request.getServerName()))
//                response.sendRedirect(request.getServletPath()
//                        + "/" + errorResources);
//            else
//                filterChain.doFilter(request, response);
//    }
//}
