package com.dc.ncsys_springboot.daoVo;

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
 * 工人工钱结算表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-28 08:57
 */
@Data
@Accessors(chain = true)
@TableName("t_worker_pay_clear")
public class WorkerPayClearDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结算ID
     */
    @TableId("clear_id")
    private String clearId;

    /**
     * 工人ID
     */
    private String workerId;

    /**
     * 结算日期
     */
    private String clearDate;

    /**
     * 涉及日期列表
     */
    private String involveDayList;

    /**
     * 涉及天数
     */
    private Double involveDays;

    /**
     * 金额
     */
    private BigDecimal involvePay;

    /**
     * 补价
     */
    private BigDecimal premium;

    /**
     * 最终金额
     */
    private BigDecimal clearingAmount;

    /**
     * 备注
     */
    private String remark;

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

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "=" +JsonUtils.toJson(this);
	}

}
