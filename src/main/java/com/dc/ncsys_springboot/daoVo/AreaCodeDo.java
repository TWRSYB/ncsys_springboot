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
 * 省市区码值表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-30 17:02
 */
@Data
@Accessors(chain = true)
@TableName("s_area_code")
public class AreaCodeDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地区级别:1-省级,2-市级,3-县区级
     */
    @TableId("area_lv")
    private String areaLv;

    /**
     * 地区代码
     */
    @TableField("area_code")
    private String areaCode;

    /**
     * 地区名称
     */
    private String areaName;

    /**
     * 上级代码
     */
    private String upCode;

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
