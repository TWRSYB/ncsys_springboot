package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.daoVo.CompanyDo;
import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.dc.ncsys_springboot.mapper.CompanyMapper;
import com.dc.ncsys_springboot.service.CompanyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.vo.AddressVo;
import com.dc.ncsys_springboot.vo.ResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, CompanyDo> implements CompanyService {

    @Autowired
    private CompanyMapper companyMapper;

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
}
