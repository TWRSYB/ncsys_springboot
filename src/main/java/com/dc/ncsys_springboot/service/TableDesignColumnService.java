package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.TableDesignColumnDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.vo.ResVo;

/**
 * <p>
 * 数据库设计表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-02 19:53
 */
public interface TableDesignColumnService extends IService<TableDesignColumnDo> {

    ResVo getOption(String tableName, String columnName);
}
