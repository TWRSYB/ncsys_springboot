package com.dc.ncsys_springboot.filter;

import com.dc.ncsys_springboot.interceptor.CachedBodyHttpServletRequest;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CachingRequestBodyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        // 只处理需要记录请求体的类型（排除文件上传等）
        if (shouldCacheBody(httpServletRequest)) {
            CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpServletRequest);
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean shouldCacheBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null &&
                (contentType.startsWith("application/json") ||
                        contentType.startsWith("application/xml") ||
                        contentType.startsWith("text/"));
    }
}