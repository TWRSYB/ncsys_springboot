package com.dc.ncsys_springboot.interceptor;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream content = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter writer;

    public CachedBodyHttpServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            outputStream = new CachedServletOutputStream(super.getOutputStream(), content);
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(getOutputStream(), getCharacterEncoding()));
        }
        return writer;
    }

    public byte[] getContentAsBytes() {
        return content.toByteArray();
    }

    public String getContentAsString() {
        return content.toString();
    }

    private static class CachedServletOutputStream extends ServletOutputStream {

        private final ServletOutputStream original;
        private final ByteArrayOutputStream cache;

        public CachedServletOutputStream(ServletOutputStream original, ByteArrayOutputStream cache) {
            this.original = original;
            this.cache = cache;
        }

        @Override
        public void write(int b) throws IOException {
            cache.write(b);
            original.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            cache.write(b);
            original.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            cache.write(b, off, len);
            original.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            cache.flush();
            original.flush();
        }

        @Override
        public void close() throws IOException {
            cache.close();
            original.close();
        }

        @Override
        public boolean isReady() {
            return original.isReady();
        }

        @Override
        public void setWriteListener(WriteListener listener) {
            original.setWriteListener(listener);
        }
    }
}