package com.dc.ncsys_springboot.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.CompanyPeopleDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;



/**
 * <p>
 * 企业人员表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-31 17:40
 */
@Mapper
public interface CompanyPeopleMapper extends BaseMapper<CompanyPeopleDo> {

}
