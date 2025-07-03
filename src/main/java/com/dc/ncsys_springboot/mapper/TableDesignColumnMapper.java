package com.dc.ncsys_springboot.mapper;

import com.dc.ncsys_springboot.daoVo.TableDesignColumnDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dc.ncsys_springboot.vo.SimpleTableDesign;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 表设计之字段设计表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-07 17:12
 */
@Mapper
public interface TableDesignColumnMapper extends BaseMapper<TableDesignColumnDo> {


    List<TableDesignColumnDo> getByTableId(@Param("tableId") String tableId);

    int deleteByTableId(@Param("tableId") String tableId);

    List<TableDesignColumnDo> selectByTableId(@Param("tableId") String tableId);

    int insertNext(TableDesignColumnDo tableDesignColumnDo);

    TableDesignColumnDo selectByTableIdAndColumnName(TableDesignColumnDo tableDesignColumnDo);

    TableDesignColumnDo selectByTableIdAndFieldIndex(TableDesignColumnDo tableDesignColumnDo);

    int updateByTableIdAndFieldIndex(TableDesignColumnDo tableDesignColumnDo);

    SimpleTableDesign getColumn(@Param("tableName") String tableName, @Param("columnName") String columnName);
}
