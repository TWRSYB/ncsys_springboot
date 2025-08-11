package com.dc.ncsys_springboot.mapper;

import com.dc.ncsys_springboot.daoVo.TableDesignDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dc.ncsys_springboot.vo.SimpleTableDesign;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;


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

    @Update("${sql}")
    int createTable(@Param("sql") String sql);


    @Update("${sql}")
    int addColumn(@Param("sql") String sql);

    @MapKey("Table")
    Map<String, Map<String, String>> showCreateTable(@Param("tableName") String tableName);


    Boolean isTableExist(@Param("tableName")String tableName);

    @Update("${sql}")
    int addUniqueKey(@Param("sql") String sql);


    @Update("${sql}")
    int deleteUniqueKey(@Param("sql") String sql);

    Page<TableDesignDo> pageQuery(Map<String, String> params);

    @Update("${sql}")
    int changeColumn(@Param("sql") String sql);

}
