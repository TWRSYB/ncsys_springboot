package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.TableDesign;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.vo.ResVo;

/**
 * <p>
 * 数据库设计表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-02 11:08
 */
public interface TableDesignService extends IService<TableDesign> {

    ResVo getTableDesign();
}
