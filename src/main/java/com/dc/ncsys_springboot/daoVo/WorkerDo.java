package com.dc.ncsys_springboot.daoVo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dc.ncsys_springboot.util.JsonUtils;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工人表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-19 11:57
 */
@Data
@TableName("m_worker")
@Accessors(chain = true)
public class WorkerDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工人ID
     */
    @TableId("worker_id")
    private String workerId;

    /**
     * 人员ID
     */
    private String personId;

    /**
     * 日工资
     */
    private BigDecimal dailyPay;

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
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private String personName;
    @TableField(exist = false)
    private String phoneNum;
    @TableField(exist = false)
    private String idNum;
    @TableField(exist = false)
    private String alias;
    @TableField(exist = false)
    private String sex;
    @TableField(exist = false)
    private String address;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "=" + JsonUtils.toJson(this);
    }

}
