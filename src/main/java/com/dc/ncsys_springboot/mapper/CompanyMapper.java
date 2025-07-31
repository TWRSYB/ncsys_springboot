package com.dc.ncsys_springboot.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.CompanyDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;



/**
 * <p>
 * 企业表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-31 17:32
 */
@Mapper
public interface CompanyMapper extends BaseMapper<CompanyDo> {

}
