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
 * 表设计之唯一约束
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-13 17:12
 */
@Data
@Accessors(chain = true)
@TableName("s_table_design_unique_key")
public class TableDesignUniqueKeyDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表ID
     */
    @TableId("table_id")
    private String tableId;

    /**
     * 约束名称
     */
    @TableField("unique_key_name")
    private String uniqueKeyName;

    /**
     * 约束字段
     */
    private String uniqueKeyColumn;

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
     * 表名
     */
    @TableField(exist = false)
    private String tableName;

    /**
     * 约束字段数组
     */
    @TableField(exist = false)
    private List<String> uniqueKeyColumnArray;

    public TableDesignUniqueKeyDo setUniqueKeyColumn(String uniqueKeyColumn) throws JsonProcessingException {
        this.uniqueKeyColumn = uniqueKeyColumn;
        if (!ObjectUtils.isEmpty(uniqueKeyColumn)) {
            this.uniqueKeyColumnArray = new ObjectMapper().readValue(uniqueKeyColumn, new TypeReference<List<String>>() {});
        }
        return this;
    }
}
