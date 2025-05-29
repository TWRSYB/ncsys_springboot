package com.dc.ncsys_springboot.interceptor;

import com.dc.ncsys_springboot.daoVo.User;
import com.dc.ncsys_springboot.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
            log.info("请求体: {}", ((CachedBodyHttpServletRequest) request).getBodyString());
        }

        // 检查请求路径是否在放行列表中
        for (String excludedPath : EXCLUDE_PATHS) {
            if (request.getRequestURI().startsWith(excludedPath)) {
                log.info("放行请求: {}", request.getRequestURI());
                return true; // 直接放行
            }
        }


        // 登录状态认证
        boolean authResult = false;

        // 获取Session，不自动创建新Session
        HttpSession session = request.getSession(false);

        // 检查用户是否登录（假设用户信息存储在"user"属性中）
        if (session == null || session.getAttribute("loginUser") == null) {
            log.warn("session中不存在loginUser, 跳转登录页面");
        } else {
            // 继续执行
            try {
                Map<String, Object> token = jwtUtil.parseToken(request.getHeader("Authorization"));
                log.info("解析token成功: {}", token);
                if (!((User) session.getAttribute("loginUser")).getLoginCode().equals(token.get("loginCode"))) {
                    log.error("警告, 请求携带的令牌与session用户不一致");
                }
                log.info("token验证通过, 放行至控制器");
                authResult = true;
            } catch (Exception e) {
                response.setStatus(401);
                log.error("token验证异常", e);
            }
        }

        if (!authResult) {
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
                String redirectUrl = request.getContextPath() + "/login.html?redirect=" + URLEncoder.encode(getOriginalRequestUrl(request), StandardCharsets.UTF_8);
                response.sendRedirect(redirectUrl);
            }
            return false;
        }

        return true;

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

    // 获取原始请求URL（含参数）
    private String getOriginalRequestUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return request.getRequestURI() + (queryString != null ? "?" + queryString : "");
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

}
