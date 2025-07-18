package com.dc.ncsys_springboot.daoVo;

import com.dc.ncsys_springboot.util.JsonUtils;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dc.ncsys_springboot.vo.AddressVo;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 人员主表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-18 11:52
 */
@Data
@TableName("m_person")
@Accessors(chain = true)
public class PersonDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 人员ID
     */
    @TableId("person_id")
    private String personId;

    /**
     * 人员名称
     */
    private String personName;

    /**
     * 手机号码
     */
    private String phoneNum;

    /**
     * 身份证号
     */
    private String idNum;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区/县
     */
    private String area;

    /**
     * 详细地址
     */
    private String address;

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
     * 别名
     */
    private String alias;

    /**
     * 性别:男,女
     */
    private String sex;

    /**
     * 地址列表
     */
    @TableField(exist = false)
    private List<AddressVo> addressList;

    private String provinceName;
    private String cityName;
    private String areaName;

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "=" +JsonUtils.toJson(this);
	}

}
