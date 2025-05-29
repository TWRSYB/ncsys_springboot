package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/test")
public class HelloController {

    @Autowired
    private JwtUtil jwtUtil;


    @RequestMapping("/hello")
    public String hello() {
        return "hello world~";
    }

    @RequestMapping("/mustLogin")
    public String mustLogin(HttpServletRequest request) {

        HttpSession session = request.getSession();
        Object loginUser = session.getAttribute("loginUser");
        System.out.println("loginUser = " + loginUser);

        String userAgent = request.getHeader("User-Agent");
        System.out.println("userAgent = " + userAgent);

        return "hello world~";
    }


}
