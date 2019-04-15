package com.tinysand.system.filters;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

@WebFilter(filterName = "compressor", urlPatterns = {"/*"}, initParams = {
        @WebInitParam(name = "enabled", value = "true")
})
public class Compressor extends HttpFilter {
    @Override
    public void doFilter(HttpServletRequest request,
                         HttpServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {
        final String acceptEncoding = request.getHeader(ACCEPT_ENCODE);
        if (enabled && acceptEncoding.toLowerCase().contains(GZIP_ENCODING)) {
            ResponseCompressor responseCompressor = new
                    ResponseCompressor(response);
            filterChain.doFilter(request, responseCompressor);

            responseCompressor.handleResponse();
        } else {
            doFilter(request, response, filterChain);
        }
    }

    private class ResponseCompressor extends HttpServletResponseWrapper {
        ResponseCompressor
                (final HttpServletResponse response) {
            super(response);
            this.response = response;
        }

        void handleResponse() throws IOException {
            if (Objects.nonNull(compressionExporter))
                compressionExporter.close();

            if (Objects.nonNull(printWriter))
                printWriter.close();
        }

        @Override
        public void flushBuffer() throws IOException {
            compressionExporter.flush();
        }

        @Override
        public void setContentLength(int contentLength) {

        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return compressionExporter = (Objects.nonNull(compressionExporter)) ?
                    compressionExporter :
                    new CompressionOutputStream(response);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            printWriter = (Objects.nonNull(printWriter)) ?
                    printWriter : new PrintWriter(new OutputStreamWriter
                    (new CompressionOutputStream(response),
                            StandardCharsets.UTF_8));
            return printWriter;
        }

        private CompressionOutputStream compressionExporter;
        private PrintWriter printWriter;
        private HttpServletResponse response;
    }

    private class CompressionOutputStream extends ServletOutputStream {
        CompressionOutputStream(final HttpServletResponse response)
                throws IOException {
            super();
            this.response = response;
            byteArrayOutputStream = new ByteArrayOutputStream();
            gzipOutputStream = new GZIPOutputStream
                    (byteArrayOutputStream);
        }

        @Override
        public void close() throws IOException {
            gzipOutputStream.finish();
            byte[] content = byteArrayOutputStream.toByteArray();
            response.addHeader(CONTENT_ENCODING, GZIP_ENCODING);
            response.addHeader(CONTENT_LENGTH, Integer.toString
                    (content.length));
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(content);
            outputStream.close();
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }

        @Override
        public void write(int data) throws IOException {
            gzipOutputStream.write(data);
        }

        @Override
        public void write(byte[] data) throws IOException {
            gzipOutputStream.write(data);
        }

        @Override
        public void write(byte[] data, int offset, int length)
                throws IOException {
            gzipOutputStream.write(data, offset, length);
        }

        private ByteArrayOutputStream byteArrayOutputStream;
        private HttpServletResponse response;
        private GZIPOutputStream gzipOutputStream;

        private static final String GZIP_ENCODING = "gzip";
        private static final String CONTENT_ENCODING = "Content-Encoding";
        private static final String CONTENT_LENGTH = "Content-Length";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        enabled = Objects.nonNull(filterConfig.getInitParameter
                (ENABLED_FLAG)) && filterConfig.getInitParameter
                (ENABLED_FLAG).equalsIgnoreCase(TRUE);
    }

    private boolean enabled;
    private static final String ENABLED_FLAG = "enabled";
    private static final String TRUE = "true";
    private static final String ACCEPT_ENCODE = "Accept-Encoding";
    private static final String GZIP_ENCODING = "gzip";
}
