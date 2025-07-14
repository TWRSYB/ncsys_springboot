package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.MixedTableDesign;
import com.dc.ncsys_springboot.daoVo.TableDesignColumnDo;
import com.dc.ncsys_springboot.daoVo.TableDesignDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.daoVo.TableDesignUniqueKeyDo;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;

/**
 * <p>
 * 数据库设计表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-02 11:08
 */
public interface TableDesignService extends IService<TableDesignDo> {

    PageResVo<TableDesignDo> getTableDesignList(PageQueryVo<TableDesignDo> pageQueryVo);

    ResVo getTableDesign(String tableName);

    ResVo saveTableDesign(MixedTableDesign mixedTableDesign);

    ResVo getTableDesignDetail(String tableName);

    ResVo createTableAndEntity(MixedTableDesign mixedTableDesign);

    ResVo addColumn(TableDesignColumnDo tableDesignColumnDo);

    ResVo deleteTableDesign(TableDesignDo tableDesignDo);

    ResVo addUniqueKey(TableDesignUniqueKeyDo tableDesignUniqueKeyDo);

    ResVo deleteUniqueKey(TableDesignUniqueKeyDo tableDesignUniqueKeyDo);

    ResVo generateTableDesign(String tableName);

    ResVo changeColumn(TableDesignColumnDo tableDesignColumnDo);
}
