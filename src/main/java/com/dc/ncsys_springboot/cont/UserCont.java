package com.dc.ncsys_springboot.cont;

import com.dc.ncsys_springboot.vo.ResVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserCont {

    @PostMapping("/login")
    public ResVo<String> login() {
        return ResVo.success("登录成功", "登录成功的token");
    }
}
