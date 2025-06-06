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
 * 表设计之字段设计表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-06 16:56
 */
@Data
@Accessors(chain = true)
@TableName("s_table_design_column")
public class TableDesignColumnDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表ID
     */
    @TableId("table_id")
    private String tableId;

    /**
     * 序号
     */
    private String fieldIndex;

    /**
     * 列名
     */
    @TableField("column_name")
    private String columnName;

    /**
     * 列注释
     */
    private String columnComment;

    /**
     * 是否主键:Y-是,N-否
     */
    private String keyYn;

    /**
     * 字段类型:varchar,char,int,timestamp,TEXT,BLOB,JSON
     */
    private String fieldType;

    /**
     * 字段长度
     */
    private Integer fieldLength;

    /**
     * 字段枚举
     */
    private String fieldEnum;

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
