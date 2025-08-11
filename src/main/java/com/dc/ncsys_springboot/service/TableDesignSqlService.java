package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.TableDesignSqlDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;

/**
 * <p>
 * 表设计SQL 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-07 19:37
 */
public interface TableDesignSqlService extends IService<TableDesignSqlDo> {

    /**
     * 获取最后表设计SQL
     *
     * @param tableId 表ID
     * @return ResVo
     */
    ResVo<TableDesignSqlDo> getLastTableDesignSql(String tableId);

    PageResVo<TableDesignSqlDo> pageQuery(PageQueryVo<TableDesignSqlDo> pageQueryVo);
}
