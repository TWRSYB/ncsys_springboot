package com.dc.ncsys_springboot.daoVo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * 表设计之字段设计表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-07 17:12
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
    private Integer fieldIndex;

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
     * 是否可空:Y-是,N-否
     */
    private String nullAbleYn;

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
     * 默认值
     */
    private String defaultValue;

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

    /**
     * 序号
     */
    @TableField(exist = false)
    private int ordinalPosition;

    /**
     * 必需输入长度
     */
    @TableField(exist = false)
    private boolean needLength;

    /**
     * 可以设置枚举
     */
    @TableField(exist = false)
    private boolean canEnum;

    /**
     * 枚举数组
     */
    @TableField(exist = false)
    private List<String> fieldEnumArray;


    /**
     * 表名
     */
    @TableField(exist = false)
    private String tableName;

    public TableDesignColumnDo setFieldEnum(final String fieldEnum) throws JsonProcessingException {
        this.fieldEnum = fieldEnum;
        if (!ObjectUtils.isEmpty(fieldEnum)) {
            this.fieldEnumArray = new ObjectMapper().readValue(fieldEnum, new TypeReference<List<String>>() {});
        }
        return this;
    }
}
