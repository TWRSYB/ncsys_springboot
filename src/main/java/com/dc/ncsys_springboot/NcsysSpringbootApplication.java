package com.dc.ncsys_springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//启动类
@SpringBootApplication
@MapperScan("com.dc.ncsys_springboot.mapper") // 确保包路径正确
public class NcsysSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(NcsysSpringbootApplication.class, args);
    }

}
