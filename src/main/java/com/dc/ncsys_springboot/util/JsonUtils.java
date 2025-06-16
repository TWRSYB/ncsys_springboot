package com.dc.ncsys_springboot.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.text.SimpleDateFormat;

public class JsonUtils {
    // 静态共享的ObjectMapper实例（线程安全）
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 配置ObjectMapper（按需调整）
        objectMapper.findAndRegisterModules(); // 自动注册JDK8等模块
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 可添加其他全局配置（如忽略未知属性）
        // objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }
}