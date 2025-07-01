package com.dc.ncsys_springboot.daoVo;
import com.baomidou.mybatisplus.annotation.TableField;

import com.dc.ncsys_springboot.util.JsonUtils;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 全国行政区划信息
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-01 16:13
 */
@Data
@Accessors(chain = true)
@TableName("s_national_administrative_division_info")
public class NationalAdministrativeDivisionInfoDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 行政级别:1-省级,2-市级,3-区县级
     */
    @TableId("area_lv")
    private String areaLv;

    /**
     * 地区名称
     */
    @TableField("area_name")
    private String areaName;

    /**
     * 行政区划代码
     */
    @TableField("area_code")
    private String areaCode;

    /**
     * 上级行政区划代码
     */
    private String upCode;

    /**
     * 全路径
     */
    private String areaPath;

    /**
     * 地区全名
     */
    private String areaNameFull;

    /**
     * 驻地
     */
    private String zhudi;

    /**
     * 人口（万人）
     */
    private String renkou;

    /**
     * 面积（平方千米）
     */
    private String mianji;

    /**
     * 区号
     */
    private String quhao;

    /**
     * 邮编
     */
    private String youbian;

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
