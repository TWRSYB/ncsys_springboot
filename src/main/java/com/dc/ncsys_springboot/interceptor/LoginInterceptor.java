package com.dc.ncsys_springboot.interceptor;

import com.dc.ncsys_springboot.daoVo.User;
import com.dc.ncsys_springboot.util.JwtUtil;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    // 定义需要放行的请求路径集合
    private static final Set<String> EXCLUDE_PATHS = new HashSet<>(Arrays.asList(
            "/ncsys/test/loginPage",          // DeepSeek建议登录的友好Url, 可以进行拓展处理, 但仍需要放行login.html
            "/ncsys/login.html",          // 登录页面
            "/ncsys/user/login",     // 登录接口
            "/ncsys/static/**"      // 静态资源
    ));

    // session中存在user对象且与请求头携带的令牌匹配则通过, 否则拒绝
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 输出请求的 URL 和方法
        log.info("拦截器收到请求: {} {}", request.getMethod(), request.getRequestURI());

        // 输出 URL 参数
        Map<String, String> queryParams = getQueryParams(request);
        if (!queryParams.isEmpty()) {
            log.info("URL 参数: {}", queryParams);
        }

        // 输出请求体
        if (request instanceof CachedBodyHttpServletRequest) {
            // 读取缓存的请求体
            byte[] cachedBody = ((CachedBodyHttpServletRequest) request).getCachedBody();
            String bodyString = new String(cachedBody, request.getCharacterEncoding());
            log.info("请求体: {}", bodyString);
        }


        // 检查请求路径是否需要放行
        if (isExcludedPath(request.getRequestURI())) {
            log.info("放行请求: {}", request.getRequestURI());
            return true; // 直接放行
        }


        // 获取Session，不自动创建新Session
        HttpSession session = request.getSession(false);

        // 检查用户是否登录（假设用户信息存储在"user"属性中）
        if (session == null || session.getAttribute("loginUser") == null) {
            log.warn("session中不存在loginUser, 跳转登录页面");
            // 处理AJAX请求
            if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
                log.warn("AJAX请求, 响应401要求前端自己跳转登录页面");
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("""
                        {"code": 401, "message": "未登录，请先登录！"}
                        """);
            } else {
                // 普通请求重定向到登录页，并携带原始请求URL作为参数
                log.warn("普通请求重定向到登录页，并携带原始请求URL作为参数");
                String redirectUrl = request.getContextPath() + "/login.html?redirect=" + URLEncoder.encode(getOriginalRequestUrl(request), "UTF-8");
                response.sendRedirect(redirectUrl);
            }
            return false; // 中断请求
        }
        // 继续执行
        try {
            Map<String, Object> token = jwtUtil.parseToken(request.getHeader("Authorization"));
            log.info("解析token成功: {}", token);
            if (!((User) session.getAttribute("loginUser")).getLoginCode().equals(token.get("loginCode"))) {
                log.error("警告, 遭受攻击请求携带的令牌与session用户不一致");
                return true;
            }
            log.info("token验证通过, 放行至控制器");
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            log.error("token验证异常", e);
            return false;
        }
    }


    // 获取原始请求URL（含参数）
    private String getOriginalRequestUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return request.getRequestURI() + (queryString != null ? "?" + queryString : "");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 输出响应状态码
        log.info("响应状态码: {}", response.getStatus());
        // 确保响应是 ContentCachingResponseWrapper 类型
        if (response instanceof ContentCachingResponseWrapper responseWrapper) {

            // 输出响应状态码
            log.info("响应状态码: {}", responseWrapper.getStatus());

            // 输出响应体内容
            byte[] responseBody = responseWrapper.getContentAsByteArray();
            if (responseBody.length > 0) {
                log.info("响应体内容: {}", new String(responseBody, response.getCharacterEncoding()));
            }
        } else {
            // 输出响应状态码
            log.info("响应状态码: {}", response.getStatus());
        }
    }

    // 获取 URL 查询参数
    private Map<String, String> getQueryParams(HttpServletRequest request) {
        Map<String, String> queryParams = new LinkedHashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues != null && paramValues.length > 0) {
                queryParams.put(paramName, paramValues[0]);
            }
        }
        return queryParams;
    }

    // 获取请求体内容
    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = request.getReader()) {
            char[] charBuffer = new char[128];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        }
        return stringBuilder.toString();
    }

    private void logRequest(HttpServletRequest request) throws IOException {

        // 输出请求的 URL 和方法
        log.info("拦截器收到请求: {} {}", request.getMethod(), request.getRequestURI());

        // 输出 URL 参数
        Map<String, String> queryParams = getQueryParams(request);
        if (!queryParams.isEmpty()) {
            log.info("URL 参数: {}", queryParams);
        }
//        StringBuilder logMessage = new StringBuilder();
//        logMessage.append("\n=== REQUEST START ===\n");
//        logMessage.append("URL: ").append(request.getRequestURL()).append("\n");
//        logMessage.append("Method: ").append(request.getMethod()).append("\n");
//
//        // 记录请求头
//        logMessage.append("Headers:\n");
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            logMessage.append("  ").append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
//        }
//
//        // 记录请求参数
//        logMessage.append("Parameters:\n");
//        request.getParameterMap().forEach((key, values) ->
//                logMessage.append("  ").append(key).append("=").append(String.join(",", values)).append("\n"));
//
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = request.getReader()) {
            char[] charBuffer = new char[128];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        }
        System.out.println("stringBuilder.toString() = " + stringBuilder.toString());

//
//        logMessage.append("=== REQUEST END ===");
//        log.info(logMessage.toString());
    }


    // 检查请求路径是否在放行列表中
    private boolean isExcludedPath(String requestURI) {
        for (String excludedPath : EXCLUDE_PATHS) {
            if (requestURI.startsWith(excludedPath)) {
                return true;
            }
        }
        return false;
    }

}
