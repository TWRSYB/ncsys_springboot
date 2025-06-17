package com.dc.ncsys_springboot.daoVo;

import com.dc.ncsys_springboot.util.JsonUtils;
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


    // 列设计列表
    private List<TableDesignColumnDo> list_tableDesignColumn;

    // 最后Sql
    public TableDesignSqlDo last_tableDesignSql;

    // 唯一约束列表
    public List<TableDesignUniqueKeyDo> list_uniqueKey;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "=" +JsonUtils.toJson(this);
    }

}
