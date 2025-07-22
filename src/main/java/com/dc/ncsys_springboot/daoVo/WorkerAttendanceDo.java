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
 * 工人出工记录表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-20 15:06
 */
@Data
@Accessors(chain = true)
@TableName("t_worker_attendance")
public class WorkerAttendanceDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 出工ID
     */
    @TableId("attendance_id")
    private String attendanceId;

    /**
     * 工人ID
     */
    private String workerId;

    /**
     * 出工日期
     */
    private String date;

    /**
     * 上午出工与否:Y-是,N-否
     */
    private String morningYn;

    /**
     * 上午工钱
     */
    private BigDecimal morningPay;

    /**
     * 下午出工预购:Y-是,N-否
     */
    private String afternoonYn;

    /**
     * 下午工钱
     */
    private BigDecimal afternoonPay;

    /**
     * 当日工钱
     */
    private BigDecimal dayPay;

    /**
     * 是否结算:Y-是,N-否
     */
    private String clearYn;

    /**
     * 结算日期
     */
    private String clearDate;

    /**
     * 结算ID
     */
    private String clearId;

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
