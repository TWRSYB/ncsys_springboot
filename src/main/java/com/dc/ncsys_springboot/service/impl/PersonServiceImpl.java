package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.dc.ncsys_springboot.mapper.PersonMapper;
import com.dc.ncsys_springboot.service.PersonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.vo.ResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>
 * 人员主表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-14 15:14
 */
@Service
public class PersonServiceImpl extends ServiceImpl<PersonMapper, PersonDo> implements PersonService {

    @Autowired
    private PersonMapper personMapper;


    @Override
    public ResVo getPersonList(PersonDo personDo) {
        List<PersonDo> personDos = personMapper.selectList(null);
        return ResVo.success("查询人员列表成功", personDos);
    }

    @Override
    public ResVo<Object> updateNameByPhoneNum(PersonDo personDo) {
        // 校验入参
        if (ObjectUtils.isEmpty(personDo) || ObjectUtils.isEmpty(personDo.getPhoneNum()) || ObjectUtils.isEmpty(personDo.getPersonName())) {
            return ResVo.fail("参数异常");
        }

        // 查询人员信息
        PersonDo person = personMapper.selectByPhoneNum(personDo.getPhoneNum());
        if (ObjectUtils.isEmpty(person)) {
            return ResVo.fail("人员不存在");
        }

        // 更新人员名称
        person.setPersonName(personDo.getPersonName());
        int updateResult = personMapper.updateById(person);
        if (updateResult > 0) {
            return ResVo.success("更新人员名称成功");
        }

        return ResVo.fail("更新人员名称失败");
    }

    @Override
    public ResVo<List<PersonDo>> getPersonLike(PersonDo personDo) {
        if (ObjectUtils.isEmpty(personDo)) {
            return ResVo.fail("参数异常");
        }
        if (ObjectUtils.isEmpty(personDo.getPersonName()) && ObjectUtils.isEmpty(personDo.getPhoneNum())) {
            return ResVo.fail("查询条件不能为空");
        }

        List<PersonDo> personDos = personMapper.getPersonLike(personDo);

        // 查询人员地址列表
        for (PersonDo person : personDos) {
            // 查询人员地址列表
            List<String> addressList = personMapper.getPersonAddressList(person.getPersonId());
            person.setAddressList(addressList);
        }
        return ResVo.success("查询人员列表成功", personDos);
    }
}
