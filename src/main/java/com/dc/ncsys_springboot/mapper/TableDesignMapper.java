package com.dc.ncsys_springboot.mapper;

import com.dc.ncsys_springboot.daoVo.TableDesignDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dc.ncsys_springboot.vo.SimpleTableDesign;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 数据库设计表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-02 11:08
 */
@Mapper
public interface TableDesignMapper extends BaseMapper<TableDesignDo> {

    List<SimpleTableDesign> getTableDesign(@Param("tableName") String tableName);
}
