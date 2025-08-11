package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.MixedTableDesign;
import com.dc.ncsys_springboot.daoVo.TableDesignColumnDo;
import com.dc.ncsys_springboot.daoVo.TableDesignDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.daoVo.TableDesignUniqueKeyDo;
import com.dc.ncsys_springboot.vo.Field;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;

import java.util.List;

/**
 * <p>
 * 数据库设计表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-02 11:08
 */
public interface TableDesignService extends IService<TableDesignDo> {

    PageResVo<TableDesignDo> pageQuery(PageQueryVo<TableDesignDo> pageQueryVo);

    ResVo<List<Field>> getTableDesign(String tableName);

    ResVo<Object> saveTableDesign(MixedTableDesign mixedTableDesign);

    ResVo<MixedTableDesign> getTableDesignDetail(String tableName);

    ResVo<Object> createTableAndEntity(MixedTableDesign mixedTableDesign);

    ResVo<TableDesignColumnDo> addColumn(TableDesignColumnDo tableDesignColumnDo);

    ResVo<Object> deleteTableDesign(TableDesignDo tableDesignDo);

    ResVo<TableDesignUniqueKeyDo> addUniqueKey(TableDesignUniqueKeyDo tableDesignUniqueKeyDo);

    ResVo<Object> deleteUniqueKey(TableDesignUniqueKeyDo tableDesignUniqueKeyDo);

    ResVo<MixedTableDesign> generateTableDesign(String tableName);

    ResVo<TableDesignColumnDo> changeColumn(TableDesignColumnDo tableDesignColumnDo);
}
