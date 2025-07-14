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
 * 玉米棒收购表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-02 14:45
 */
@Data
@Accessors(chain = true)
@TableName("t_corn_cob_purchase")
public class CornCobPurchaseDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 交易流水号
     */
    @TableId("serno")
    private String serno;

    /**
     * 交易日期
     */
    private String tradeDate;

    /**
     * 出售人
     */
    private String sellerId;

    /**
     * 霉菌率
     */
    private String qualityMouldRate;

    /**
     * 湿度
     */
    private String qualityHumidity;

    /**
     * 交易状态:收购中,待结算,已结算
     */
    private String tradeStatus;

    /**
     * 是否脱粒:Y-是,N-否
     */
    private String threshingYn;

    /**
     * 结算方式:现结,延结
     */
    private String clearingForm;

    /**
     * 单价(元/斤)
     */
    private BigDecimal unitPrice;

    /**
     * 总重(kg)
     */
    private BigDecimal totalWeight;

    /**
     * 总价(元)
     */
    private BigDecimal totalPrice;

    /**
     * 补价(元)
     */
    private BigDecimal premium;

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

    /**
     * 最终结算日期
     */
    private String actualClearingDate;

    /**
     * 最终结算金额
     */
    private BigDecimal clearingAmount;

    /**
     * 计划结算日期
     */
    private String planClearingDate;

    /**
     * 地址
     */
    private String address;

    /**
     * 出售人名称
     */
    @TableField(exist = false)
    private String sellerName;

    @Override
	public String toString() {
		return this.getClass().getSimpleName() + "=" +JsonUtils.toJson(this);
	}

}
