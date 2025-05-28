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

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    // session中存在user对象且与请求头携带的令牌匹配则通过, 否则拒绝
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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
            if (!((User)session.getAttribute("loginUser")).getLoginCode().equals(token.get("loginCode"))){
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


}
