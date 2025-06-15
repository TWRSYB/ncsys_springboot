package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.dc.ncsys_springboot.service.PersonService;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 人员主表 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-14 15:14
 */
@Slf4j
@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping("/getPersonList")
    public ResVo getPersonList(@RequestBody PersonDo personDo) {
        log.info("CONT入参: {}", personDo);
        return personService.getPersonList(personDo);
    }
}
