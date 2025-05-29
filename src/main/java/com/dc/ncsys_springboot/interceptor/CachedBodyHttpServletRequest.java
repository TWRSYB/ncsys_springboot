package com.dc.ncsys_springboot.interceptor;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] cachedBody;
    private final String bodyString;

    // 添加公共方法获取缓存体
    public byte[] getCachedBody() {
        return this.cachedBody;
    }

    public String getBodyString() {
        return bodyString;
    }

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        // long contentLength = request.getContentLengthLong();
        this.cachedBody = request.getInputStream().readAllBytes(); // 缓存请求体
        String bodyStr = new String(cachedBody, Optional.ofNullable(request.getCharacterEncoding())
                .orElse(StandardCharsets.UTF_8.name()));
        if (bodyStr.length() > 4096) { // 过大则不转了
            this.bodyString = "请求体长度大于4096, 此处截取展示" + bodyStr.substring(0, 4096);
        } else {
            this.bodyString = bodyStr;
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }

    static class CachedBodyServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream buffer;

        public CachedBodyServletInputStream(byte[] body) {
            this.buffer = new ByteArrayInputStream(body);
        }

        @Override
        public int read() {
            return buffer.read();
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}