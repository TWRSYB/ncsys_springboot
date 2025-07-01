package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.service.AreaCodeService;
import com.dc.ncsys_springboot.vo.ResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 地区码值表 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-30 15:37
 */
@RestController
@RequestMapping("/areaCode")
public class AreaCodeController {

    @Autowired
    private AreaCodeService areaCodeService;

    @GetMapping("/readJsonToDb")
    public ResVo readJsonToDb(){
        return areaCodeService.readJsonToDb();
    }


}
