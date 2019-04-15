package com.tinysand.system.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface SystemService {
    void add(final HttpServletRequest request,
             final HttpServletResponse response,
             final Map<String, String[]> formData) throws IOException;

    void update(final HttpServletRequest request,
                final HttpServletResponse response,
                final Map<String, String[]> formData);

    void delete(final HttpServletRequest request,
                final HttpServletResponse response,
                final int id);

    <R> R query(final HttpServletRequest request,
                final HttpServletResponse response,
                final int id);

    static void queryData() {

    }
}
