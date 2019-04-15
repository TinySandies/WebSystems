package com.tinysand.system.controllers;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

//@WebServlet(urlPatterns = "/testServlet")
public class TestServlet implements Servlet {
    @Override
    public void init(ServletConfig servletConfig) {
        System.err.println("init");
    }

    @Override
    public ServletConfig getServletConfig() {
        System.err.println("servletConfig");

        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) {
        System.err.println("service");

    }

    @Override
    public String getServletInfo() {
        System.err.println("servletInfo");

        return null;
    }

    @Override
    public void destroy() {
        System.err.println("destroy");

    }
}
