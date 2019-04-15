//package com.tinysand.system.filters;
//
//import com.tinysand.system.util.Toolkit;
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
//import java.io.UnsupportedEncodingException;
//
//@WebFilter(filterName = "encodingFilter", urlPatterns = {
//        "/*"
//}, initParams = {
//        @WebInitParam(name = "characterSet", value = "UTF-8")
//})
//public class EncodingFilter extends HttpFilter {
//    private static String characterSet;
//    private static String enable;
//
//    @Override
//    public void init(FilterConfig filterConfig) {
//        characterSet = this.getInitParameter("characterSet");
//        enable = this.getInitParameter("enable");
//    }
//
//    @Override
//    public void doFilter(HttpServletRequest request,
//                         HttpServletResponse response,
//                         FilterChain filterChain)
//            throws IOException, ServletException {
//        if (!Toolkit.isEmpty(enable) &&
//                !Toolkit.isEmpty(characterSet))
//            if (enable.trim().equalsIgnoreCase("true"))
//            {
//                try {
//                    request.setCharacterEncoding(characterSet);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                response.setCharacterEncoding(characterSet);
//            }
//            filterChain.doFilter(request, response);
//    }
//}
