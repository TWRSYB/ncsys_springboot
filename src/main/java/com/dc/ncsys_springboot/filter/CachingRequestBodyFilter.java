package com.dc.ncsys_springboot.filter;

import com.dc.ncsys_springboot.interceptor.CachedBodyHttpServletRequest;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

//@Component // 移除 @Component 注解，因为我们现在通过 FilterRegistrationBean 手动注册
@Slf4j
public class CachingRequestBodyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        long start = System.currentTimeMillis();
        // ... 执行过滤 ...
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        // 只处理需要记录请求体的类型（排除文件上传等）
        if (shouldCacheBody(httpServletRequest)) {
            CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpServletRequest);
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }

        long duration = System.currentTimeMillis() - start;
        if (duration > 100) { // 超过100ms记录警告
            log.warn("Caching request body took {}ms for {}", duration, ((HttpServletRequest) request).getRequestURI());
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