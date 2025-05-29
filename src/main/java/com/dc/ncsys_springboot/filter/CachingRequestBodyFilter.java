package com.dc.ncsys_springboot.filter;

import com.dc.ncsys_springboot.interceptor.CachedBodyHttpServletRequest;
import com.dc.ncsys_springboot.interceptor.CachedBodyHttpServletResponse;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // 只处理文本类型的请求和响应
        if (isTextBasedContent(httpRequest.getContentType())) {
            // 包装请求
            CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpRequest);
            // 包装响应
            CachedBodyHttpServletResponse wrappedResponse = new CachedBodyHttpServletResponse(httpResponse);

            chain.doFilter(wrappedRequest, wrappedResponse);
            // 将包装后的响应设置回原始响应
            response = wrappedResponse;

        } else {
            chain.doFilter(request, response);
        }

        long duration = System.currentTimeMillis() - start;
        if (duration > 100) { // 超过100ms记录警告
            log.warn("Caching request body took {}ms for {}", duration, ((HttpServletRequest) request).getRequestURI());
        }
    }

    private boolean isTextBasedContent(String contentType) {
        return contentType != null &&
                (contentType.startsWith("application/json") ||
                        contentType.startsWith("application/xml") ||
                        contentType.startsWith("text/") ||
                        contentType.startsWith("application/x-www-form-urlencoded"));
    }
}