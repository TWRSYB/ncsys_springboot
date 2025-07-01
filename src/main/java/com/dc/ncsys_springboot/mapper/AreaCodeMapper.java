package com.dc.ncsys_springboot.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.AreaCodeDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;



/**
 * <p>
 * 省市区码值表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-30 17:02
 */
@Mapper
public interface AreaCodeMapper extends BaseMapper<AreaCodeDo> {

}
