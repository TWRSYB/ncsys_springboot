package com.dc.ncsys_springboot.cont;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloCont {

    @RequestMapping("/hello")
    public String hello() {
        return "hello world~";
    }
}
