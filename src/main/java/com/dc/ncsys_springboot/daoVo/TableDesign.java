package com.dc.ncsys_springboot.daoVo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 数据库设计表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-02 11:08
 */
@Data
@Accessors(chain = true)
@TableName("s_table_design")
public class TableDesign implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表名
     */
    @TableId("table_name")
    private String tableName;

    /**
     * 表注释
     */
    private String tableComment;

    /**
     * 表分类:s-系统表(system),l-日志表(log),m-主数据表(main),t-交易表(trade),ts-交易子表(tradesub)
     */
    private String tableType;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 最后更新用户
     */
    private String updateUser;

    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
