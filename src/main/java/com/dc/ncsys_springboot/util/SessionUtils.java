package com.dc.ncsys_springboot.util;

import com.dc.ncsys_springboot.daoVo.UserDo;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SessionUtils {

    public static UserDo getSessionUser() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpSession session = servletRequestAttributes.getRequest().getSession(false); // false表示不自动创建新Session
            return (UserDo)session.getAttribute("loginUser");
        }
        throw new IllegalStateException("当前线程无活跃的HTTP请求上下文");
    }
}