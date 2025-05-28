package com.dc.ncsys_springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {
    @GetMapping("/loginPage")
    public String redirectToLogin() {
        return "redirect:/login.html"; // 重定向到静态资源路径
    }
}
