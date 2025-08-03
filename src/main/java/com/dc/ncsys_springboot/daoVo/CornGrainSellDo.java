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
 * 玉米粒出售表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-02 22:09
 */
@Data
@Accessors(chain = true)
@TableName("t_corn_grain_sell")
public class CornGrainSellDo implements Serializable {

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
     * 购买人类型:个人,企业
     */
    private String buyerType;

    /**
     * 购买人
     */
    private String buyerId;

    /**
     * 地址
     */
    private String address;

    /**
     * 霉菌率
     */
    private String qualityMouldRate;

    /**
     * 湿度
     */
    private String qualityHumidity;

    /**
     * 杂质:很多,较多,一般,较少,很少
     */
    private String impurity;

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
     * 结算方式:现结,延结
     */
    private String clearingForm;

    /**
     * 计划结算日期
     */
    private String planClearingDate;

    /**
     * 最终结算日期
     */
    private String actualClearingDate;

    /**
     * 补价(元)
     */
    private BigDecimal premium;

    /**
     * 最终结算金额
     */
    private BigDecimal clearingAmount;

    /**
     * 交易状态:出售中,待结算,已结算
     */
    private String tradeStatus;

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

    /**
     * 对接人ID
     */
    private String dockPersonId;

    /**
     * 计重方:我方,买方
     */
    private String weighSide;

    /**
     * 买方名称
     */
    @TableField(exist = false)
    private String buyerName;

    /**
     * 对接人名称
     */
    @TableField(exist = false)
    private String dockPersonName;

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "=" +JsonUtils.toJson(this);
	}

}
