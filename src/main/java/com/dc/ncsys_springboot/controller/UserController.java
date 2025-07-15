package com.dc.ncsys_springboot.controller;


import com.dc.ncsys_springboot.daoVo.UserDo;
import com.dc.ncsys_springboot.service.UserService;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResVo<UserDo> login(@RequestBody UserDo userDo) {
        log.info("用户登录接口: 入参{}", userDo);
        return userService.login(userDo);

    }


    @GetMapping("/getUserInfo")
    public ResVo getUserInfo() {
        log.info("CONT入参为空");
        return userService.getUserInfo();
    }

    @GetMapping("/refreshToken")
    public ResVo<UserDo> refreshToken() {
        log.info("CONT入参为空");
        return userService.refreshToken();
    }

    @GetMapping("/logout")
    public ResVo logout() {
        log.info("CONT入参为空");
        return userService.logout();
    }

    @GetMapping("/getSubAccountList")
    public ResVo<List<UserDo>> getSubAccountList(@RequestParam("userId") String userId) {
        log.info("CONT入参: {}", userId);
        return userService.getSubAccountList(userId);
    }

    @PostMapping("/updateAvatar")
    public ResVo updateAvatar(@RequestBody UserDo userDo) {
        log.info("CONT入参: {}", userDo);
        return userService.updateAvatar(userDo);
    }

    @PostMapping("/changePassword")
    public ResVo changePassword(@RequestBody Map<String, String> map) {
        log.info("CONT入参: {}", map);
        return userService.changePassword(map);
    }

    @PostMapping("/changeAccount")
    public ResVo<UserDo> changeAccount(@RequestBody UserDo userDo) {
        log.info("CONT入参: {}", userDo);
        return userService.changeAccount(userDo);
    }

}

