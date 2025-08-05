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
 * 玉米芯收购过磅记录
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-05 08:59
 */
@Data
@Accessors(chain = true)
@TableName("ts_corn_xin_purchase_weigh_record")
public class CornXinPurchaseWeighRecordDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 过磅ID
     */
    @TableId("weigh_id")
    private String weighId;

    /**
     * 交易ID
     */
    private String tradeSerno;

    /**
     * 交易日期
     */
    private String tradeDate;

    /**
     * 承运方:我方,客方
     */
    private String carrier;

    /**
     * 车牌号
     */
    private String verhicleNum;

    /**
     * 毛重
     */
    private BigDecimal grossWeight;

    /**
     * 皮重
     */
    private BigDecimal tareWeight;

    /**
     * 净重
     */
    private BigDecimal netWeight;

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
