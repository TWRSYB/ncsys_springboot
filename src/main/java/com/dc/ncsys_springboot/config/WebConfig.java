package com.dc.ncsys_springboot.config;

import com.dc.ncsys_springboot.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(   // 放行的页面
                        "/test/loginPage",          // 登录页面
                        "/login.html",          // 登录页面
                        "/user/login",     // 登录接口
                        "/static/**"      // 静态资源
                );
    }
}
