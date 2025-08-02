package com.dc.ncsys_springboot.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.CompanyPersonDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;



/**
 * <p>
 * 企业人员表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-02 15:40
 */
@Mapper
public interface CompanyPersonMapper extends BaseMapper<CompanyPersonDo> {

    CompanyPersonDo getByCompanyIdAndPersonId(String companyId, String personId);
}
