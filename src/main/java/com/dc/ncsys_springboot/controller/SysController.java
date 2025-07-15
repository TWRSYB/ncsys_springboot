package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.UserDo;
import com.dc.ncsys_springboot.service.UserService;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/sys")
public class SysController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUserList")
    public ResVo getUserList() {
        log.info("CONT入参为空");
        return userService.getUserList();
    }

    @PostMapping("/addUser")
    public ResVo addUser(@RequestBody UserDo userDo) {
        log.info("CONT入参:{}", userDo);
        return userService.addUser(userDo);
    }
}
