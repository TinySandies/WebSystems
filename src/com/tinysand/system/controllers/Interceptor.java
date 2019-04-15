package com.tinysand.system.controllers;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebServlet(name = "interceptor", urlPatterns = "/test")
public class Interceptor extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.err.println("doPost调用");
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        final String prefixPath = "WEB-INF/upload/33.png";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", prefixPath);
        response.getWriter().print(jsonObject.toJSONString());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        System.err.println("doPost调用");
    }
}
