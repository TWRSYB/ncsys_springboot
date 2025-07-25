package com.dc.ncsys_springboot.interceptor;

import com.dc.ncsys_springboot.daoVo.UserDo;
import com.dc.ncsys_springboot.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

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
            "/ncsys/user/logout",     // 登录接口
//            "/ncsys/static/*",      // 静态资源, 可以不放行
            "/ncsys/assets/*",      // 静态资源, 必须放行
            "/ncsys/index.html",      // 主页, 必须放行
            "/ncsys/"      // 主页, 必须放行

    ));

    // session中存在user对象且与请求头携带的令牌匹配则通过, 否则拒绝
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 输出请求的 URL 和方法
        log.info("拦截器处理请求: {} {}", request.getMethod(), request.getRequestURI());

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
            // 检查请求路径是否在放行列表中
            if (excludedPath.endsWith("*")) {
                // 处理通配符路径
                String prefix = excludedPath.substring(0, excludedPath.length() - 1);
                if (request.getRequestURI().startsWith(prefix)) {
                    log.info("放行请求: {}", request.getRequestURI());
                    return true; // 直接放行
                }
            } else if (request.getRequestURI().equals(excludedPath)) {
                log.info("放行请求: {}", request.getRequestURI());
                return true; // 直接放行
            }
        }


        // 登录状态认证
        boolean authResult = false;

        // 获取Session，不自动创建新Session
        HttpSession session = request.getSession(false);


        UserDo loginUserDo = session != null ? (UserDo) session.getAttribute("loginUser") : null;
        log.info("当前session中的loginUser: {}", loginUserDo);

        // 检查用户是否登录（假设用户信息存储在"user"属性中）
        if (loginUserDo == null) {
            log.warn("登录状态验证拒绝: session中不存在loginUser, {}", request.getRequestURI());
        } else {
            // 继续执行
            try {
                Map<String, Object> token = jwtUtil.parseToken(request.getHeader("Authorization"));
                log.info("解析token成功: {}", token);
                if (!loginUserDo.getLoginCode().equals(token.get("loginCode"))) {
                    log.error("登录状态验证拒绝: 警告, 请求携带的令牌与session用户不一致");
                } else {
                    log.info("登录状态验证通过: token验证通过, 放行至控制器");
                    authResult = true;
                }
            } catch (Exception e) {
                log.error("登录状态验证拒绝: token验证异常", e);
            }
        }

        // 登录认证失败响应401
        if (!authResult) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return true;

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, @Nullable Exception ex) {
        // 输出请求的 URL 和方法
        log.info("拦截器处理响应{}: {} {}", response.getStatus(), request.getMethod(), request.getRequestURI());
        try {
            if (response instanceof CachedBodyHttpServletResponse wrappedResponse) {
                // 输出响应体
                log.info("响应体: {}", wrappedResponse.getContentAsString());
            }
        } catch (Exception e) {
            log.error("Error logging response: {}", e.getMessage());
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
