package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.daoVo.CompanyDo;
import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.CompanyMapper;
import com.dc.ncsys_springboot.mapper.PersonMapper;
import com.dc.ncsys_springboot.service.CompanyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.vo.AddressVo;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>
 * 企业表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-31 17:32
 */
@Slf4j
@Transactional
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, CompanyDo> implements CompanyService {

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private PersonMapper personMapper;

    @Override
    public ResVo<List<CompanyDo>> getCompanyLike(CompanyDo companyDo) {
        if (ObjectUtils.isEmpty(companyDo)) {
            return ResVo.fail("参数异常");
        }
        if (ObjectUtils.isEmpty(companyDo.getCompanyName()) && ObjectUtils.isEmpty(companyDo.getCompanyPhoneNum())) {
            return ResVo.fail("查询条件不能为空");
        }

        List<CompanyDo> companyDos = companyMapper.getCompanyLike(companyDo);

        // 遍历企业列表，查询地址列表和人员列表
        for (CompanyDo company : companyDos) {
            // 查询企业地址列表
            List<AddressVo> addressList = companyMapper.getCompanyAddressList(company.getCompanyId());
            company.setAddressList(addressList);
            // 查询企业人员列表
            List<PersonDo> personList = companyMapper.getCompanyPersonList(company.getCompanyId());
            company.setPersonList(personList);
        }


        return ResVo.success("企业模糊查询成功", companyDos);
    }

    @Override
    public ResVo<Object> updateCompanyNameByCompanyPhoneNum(CompanyDo companyDo) {
        // 校验入参
        if (ObjectUtils.isEmpty(companyDo) || ObjectUtils.isEmpty(companyDo.getCompanyPhoneNum()) || ObjectUtils.isEmpty(companyDo.getCompanyName())) {
            return ResVo.fail("参数异常");
        }

        // 查询企业信息
        CompanyDo company = companyMapper.getByCompanyPhoneNum(companyDo.getCompanyPhoneNum());
        if (ObjectUtils.isEmpty(company)) {
            return ResVo.fail("企业不存在");
        }

        // 更新企业名称
        company.setCompanyName(companyDo.getCompanyName());
        int updateResult = companyMapper.updateById(company);
        if (updateResult == 1) {
            return ResVo.success("更新企业名称成功");
        }

        throw new BusinessException("更新企业名称数量不是1");
    }

    @Override
    public ResVo<Object> updateDockPersonNameByPhoneNum(CompanyDo companyDo) {
        // 校验入参
        if (ObjectUtils.isEmpty(companyDo) || ObjectUtils.isEmpty(companyDo.getCompanyPhoneNum()) || ObjectUtils.isEmpty(companyDo.getCompanyName()) || ObjectUtils.isEmpty(companyDo.getDockPersonName()) || ObjectUtils.isEmpty(companyDo.getDockPhoneNum())) {
            return ResVo.fail("参数异常");
        }

        // 查询企业信息
        CompanyDo company = companyMapper.getByCompanyPhoneNum(companyDo.getCompanyPhoneNum());
        if (ObjectUtils.isEmpty(company)) {
            return ResVo.fail("企业不存在");
        }

        // 查询对接人信息
        PersonDo dockPerson = personMapper.getByPhoneNum(companyDo.getDockPhoneNum());
        if (ObjectUtils.isEmpty(dockPerson)) {
            return ResVo.fail("对接人不存在");
        }

        // 更新对接人名称
        dockPerson.setPersonName(companyDo.getDockPersonName());
        int updateResult = personMapper.updateById(dockPerson);
        if (updateResult == 1) {
            return ResVo.success("更新对接人人员名称成功");
        }

        throw new BusinessException("更新对接人人员名称数量不是1");
    }
}
