package com.dc.ncsys_springboot.interceptor;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    // 添加公共方法获取缓存体
    public byte[] getCachedBody() {
        return this.cachedBody;
    }

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        this.cachedBody = request.getInputStream().readAllBytes(); // 缓存请求体
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