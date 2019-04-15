//package com.tinysand.system.filters;
//
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.annotation.WebInitParam;
//import javax.servlet.http.HttpFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//@WebFilter(filterName = "AccessFilter", urlPatterns = {"/*"},
//        initParams = {
//        @WebInitParam(name = "authorization_required",
//                value = "comment")
//})
//public class AccessFilter extends HttpFilter {
//    private static final String LIMITED_ACCESS = "authorization_required";
//    private String[] limitedParams;
//    @Override
//    public void init(FilterConfig config) {
//        final String originParam = this.getInitParameter(LIMITED_ACCESS);
//        limitedParams = originParam.split("#");
//    }
//
//    @Override
//    public void doFilter(HttpServletRequest request,
//                         HttpServletResponse response,
//                         FilterChain filterChain) {
//        String requestURI = request.getRequestURI();
//        for (String limitedParam : limitedParams)
//            if (requestURI.contains(limitedParam)) {
//                request.getSession().getAttribute("");
//            }
//
//    }
//}
