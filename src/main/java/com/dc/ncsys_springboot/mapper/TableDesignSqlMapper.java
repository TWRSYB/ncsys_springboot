package com.dc.ncsys_springboot.mapper;

import com.dc.ncsys_springboot.daoVo.TableDesignSqlDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 表设计SQL Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-07 19:37
 */
@Mapper
public interface TableDesignSqlMapper extends BaseMapper<TableDesignSqlDo> {

    int insertNextRecord(TableDesignSqlDo tableDesignSqlDo);

    List<TableDesignSqlDo> selectByTableId(@Param("tableId") String tableId);
}
