package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.service.UserService;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sys")
public class SysController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUserList")
    public ResVo getUserList() {
        log.info("获取用户列表接口");
        return userService.getUserList();
    }
}
