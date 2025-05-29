package com.dc.ncsys_springboot.config;

import com.dc.ncsys_springboot.filter.CachingRequestBodyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<CachingRequestBodyFilter> cachingRequestBodyFilterRegistration() {
        FilterRegistrationBean<CachingRequestBodyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CachingRequestBodyFilter());
        registrationBean.addUrlPatterns("/*");  // 应用到所有URL
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);  // 最高优先级 这是缓存请求体的关键，必须在其他组件读取请求体之前完成缓存
        return registrationBean;
    }
}