package com.dc.ncsys_springboot.daoVo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 数据库设计表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-02 11:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class MixedTableDesign extends TableDesignDo implements Serializable {

    // 表名前缀
    private String pre_tableName;

    // 表名后段
    private String sub_tableName;


    private List<TableDesignColumnDo> list_tableDesignColumn;
//    public TableDesignSql last_tableDesignSql;
}
