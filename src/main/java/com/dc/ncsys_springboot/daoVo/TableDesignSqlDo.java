package com.dc.ncsys_springboot.daoVo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 表设计SQL
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-07 19:37
 */
@Data
@Accessors(chain = true)
@TableName("s_table_design_sql")
public class TableDesignSqlDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表名
     */
    @TableId("table_id")
    private String tableId;

    /**
     * sql类型:TABLE_CREATE-表_新建,COLUMN_ADD-列_新增,COLUMN_DROP-列_删除,COLUMN_CHANGE-列_修改
     */
    private String sqlType;

    /**
     * 执行顺序
     */
    @TableField("execute_order")
    private Integer executeOrder;

    /**
     * 执行SQL
     */
    private String executeSql;

    /**
     * 最新建表SQL
     */
    private String lastCreateSql;

    /**
     * 数据状态:0-未生效,1-生效,2-禁用,9-废弃
     */
    private String dataStatus;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新用户
     */
    private String updateUser;

    /**
     * 最后更新时间
     */
    private Date updateTime;
}
