package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.CompanyDo;
import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.dc.ncsys_springboot.service.CompanyService;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 企业表 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-31 17:32
 */
@Slf4j
@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/getCompanyLike")
    public ResVo<List<CompanyDo>> getCompanyLike(@RequestBody CompanyDo companyDo) {
        log.info("CONT入参: {}", companyDo);
        return companyService.getCompanyLike(companyDo);
    }


}
