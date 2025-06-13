package com.dc.ncsys_springboot.controller;


import com.dc.ncsys_springboot.daoVo.User;
import com.dc.ncsys_springboot.service.UserService;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 登录用户 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-05-26
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResVo login(@RequestBody User user) {
        log.info("用户登录接口: 入参{}", user);
        return userService.login(user);

    }


    @GetMapping("/getUserInfo")
    public ResVo getUserInfo() {
        log.info("CONT入参为空");
        return userService.getUserInfo();
    }


}

