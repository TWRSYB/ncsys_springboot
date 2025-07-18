package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.constants.ComConst;
import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.dc.ncsys_springboot.daoVo.UserDo;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.PersonMapper;
import com.dc.ncsys_springboot.service.PersonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.util.DateTimeUtil;
import com.dc.ncsys_springboot.vo.AddressVo;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import com.dc.ncsys_springboot.util.SessionUtils;

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
@Transactional
@Slf4j
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
            List<AddressVo> addressList = personMapper.getPersonAddressList(person.getPersonId());
            person.setAddressList(addressList);
        }
        return ResVo.success("查询人员列表成功", personDos);
    }

    @Override
    public PageResVo<PersonDo> pageQuery(PageQueryVo<PersonDo> pageQueryVo) {
        PageHelper.startPage(pageQueryVo.getPageNum(), pageQueryVo.getPageSize());
        Page<PersonDo> page = personMapper.pageQuery(pageQueryVo.getParams());
        return PageResVo.success(page);
    }

    @Override
    public ResVo savePerson(PersonDo personDo) {

        // 获取当前登录用户信息
        UserDo sessionUser = SessionUtils.getSessionUser();

        validatePerson(personDo);

        // 校验手机号是否已存在
        PersonDo person = personMapper.selectByPhoneNum(personDo.getPhoneNum());
        if (!ObjectUtils.isEmpty(person)) {
            return ResVo.fail("手机号已存在");
        }

        // 校验身份证号是否已存在
        if (!ObjectUtils.isEmpty(personDo.getIdNum())) {
            PersonDo personId = personMapper.selectByIdNum(personDo.getIdNum());
            if (!ObjectUtils.isEmpty(personId)) {
                return ResVo.fail("身份证号已存在");
            }
        }

        personDo.setPersonId("People_" + personDo.getPhoneNum() + "_" + DateTimeUtil.getMinuteKey());
        personDo.setCreateUser(sessionUser.getLoginCode());
        personDo.setUpdateUser(sessionUser.getLoginCode());
        personDo.setDataStatus(ComConst.DATASTATUS_EFFECTIVE);
        int result = personMapper.insert(personDo);
        if (result > 0) {
            return ResVo.success("保存人员信息成功");
        } else {
            return ResVo.fail("保存人员信息失败");
        }

    }

    private static void validatePerson(PersonDo personDo) {
        // 校验入参
        if (ObjectUtils.isEmpty(personDo)) {
            throw new BusinessException("参数异常", "请求参数为空");
        }
        // 姓名不能为空
        if (ObjectUtils.isEmpty(personDo.getPersonName())) {
            throw new BusinessException("姓名不能为空");
        }
        // 姓名长度2-10
        if (personDo.getPersonName().length() < 2 || personDo.getPersonName().length() > 10) {
            throw new BusinessException("姓名由2-10个汉字组成");
        }
        // 姓名只能是汉字
        if (!personDo.getPersonName().matches("^[\u4e00-\u9fa5]+$")) {
            throw new BusinessException("姓名由2-10个汉字组成");
        }
        // 手机号不能为空
        if (ObjectUtils.isEmpty(personDo.getPhoneNum())) {
            throw new BusinessException("手机号不能为空");
        }
        // 手机号正则匹配
        if (!personDo.getPhoneNum().matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式不正确");
        }
    }

    @Override
    public ResVo<List<AddressVo>> getPersonAddressList(PersonDo personDo) {
        if (ObjectUtils.isEmpty(personDo)) {
            return ResVo.fail("参数异常");
        }
        if (ObjectUtils.isEmpty(personDo.getPersonId())) {
            return ResVo.fail("人员ID不能为空");
        }
        List<AddressVo> addressList = personMapper.getPersonAddressList(personDo.getPersonId());
        return ResVo.success("查询人员地址列表成功", addressList);
    }

    @Override
    public ResVo updatePerson(PersonDo personDo) {

        // 获取当前登录用户信息
        UserDo sessionUser = SessionUtils.getSessionUser();
        validatePerson(personDo);

        // 查询人员信息
        PersonDo person = personMapper.selectById(personDo.getPersonId());
        if (ObjectUtils.isEmpty(person)) {
            return ResVo.fail("人员不存在");
        }

        // 检查人员状态
        if (!person.getDataStatus().equals(ComConst.DATASTATUS_EFFECTIVE)) {
            return ResVo.fail("人员状态异常");
        }

        // 更新人员信息
        personDo.setUpdateUser(sessionUser.getLoginCode());
        int updateResult = personMapper.updateById(personDo);
        if (updateResult == 1) {
            return ResVo.success("更新人员信息成功");
        } else {
            throw new BusinessException("更新人员信息失败");
        }

    }


}
