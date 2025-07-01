package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.service.NationalAdministrativeDivisionInfoService;
import com.dc.ncsys_springboot.vo.ResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 全国行政区划信息 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-01 16:13
 */
@RestController
@RequestMapping("/nationalAdministrativeDivisionInfo")
public class NationalAdministrativeDivisionInfoController {

    @Autowired
    private NationalAdministrativeDivisionInfoService nationalAdministrativeDivisionInfoService;

    @GetMapping("/readJsonToDb")
    public ResVo readJsonToDb(){
        return nationalAdministrativeDivisionInfoService.readJsonToDb();
    }

}
