package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.dc.ncsys_springboot.service.PersonService;
import com.dc.ncsys_springboot.vo.AddressVo;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
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

    @PostMapping("/pageQuery")
    public PageResVo<PersonDo> pageQuery(@RequestBody PageQueryVo<PersonDo> pageQueryVo) {
        log.info("CONT入参: {}", pageQueryVo);
        return personService.pageQuery(pageQueryVo);
    }

    @PostMapping("/getPersonList")
    public ResVo getPersonList(@RequestBody PersonDo personDo) {
        log.info("CONT入参: {}", personDo);
        return personService.getPersonList(personDo);
    }

    @PostMapping("/updateNameByPhoneNum")
    public ResVo<Object> updateNameByPhoneNum(@RequestBody PersonDo personDo) {
        log.info("CONT入参: {}", personDo);
        return personService.updateNameByPhoneNum(personDo);
    }

    @PostMapping("/getPersonLike")
    public ResVo<List<PersonDo>> getPersonLike(@RequestBody PersonDo personDo) {
        log.info("CONT入参: {}", personDo);
        return personService.getPersonLike(personDo);
    }

    @PostMapping("/savePerson")
    public ResVo savePerson(@RequestBody PersonDo personDo) {
        log.info("CONT入参: {}", personDo);
        return personService.savePerson(personDo);
    }

    @PostMapping("/getPersonAddressList")
    public ResVo<List<AddressVo>> getPersonAddressList(@RequestBody PersonDo personDo) {
        log.info("CONT入参: {}", personDo);
        return personService.getPersonAddressList(personDo);
    }

    @PostMapping("/updatePerson")
    public ResVo updatePerson(@RequestBody PersonDo personDo) {
        log.info("CONT入参: {}", personDo);
        return personService.updatePerson(personDo);
    }
}
