package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.CompanyDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.vo.ResVo;

import java.util.List;

/**
 * <p>
 * 企业表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-31 17:32
 */
public interface CompanyService extends IService<CompanyDo> {

    ResVo<List<CompanyDo>> getCompanyLike(CompanyDo companyDo);
}
