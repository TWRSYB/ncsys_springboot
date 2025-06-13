package com.dc.ncsys_springboot.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.TableDesignUniqueKeyDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;



/**
 * <p>
 * 表设计之唯一约束 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-13 17:12
 */
@Mapper
public interface TableDesignUniqueKeyMapper extends BaseMapper<TableDesignUniqueKeyDo> {

}
