package com.dc.ncsys_springboot.daoVo;

import com.dc.ncsys_springboot.util.JsonUtils;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 企业表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-31 17:45
 */
@Data
@TableName("m_company")
@Accessors(chain = true)
public class CompanyDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 企业ID
     */
    @TableId("company_id")
    private String companyId;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 统一社会信用代码
     */
    private String creditCode;

    /**
     * 企业类型:民企,国企
     */
    private String companyType;

    /**
     * 成立日期
     */
    private String establishDate;

    /**
     * 注册地址
     */
    private String registeredAddress;

    /**
     * 经营地址
     */
    private String operatingAddress;

    /**
     * 企业规模:1~10,11~30,31~50,50以上
     */
    private String companySize;

    /**
     * 法人代表
     */
    private String legalPeopleId;

    /**
     * 注册资本
     */
    private String registeredCapital;

    /**
     * 经营范围:饲养,加工,仓库,混合,其他
     */
    private String businessScope;

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
     * 电话
     */
    private String companyPhoneNum;

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "=" +JsonUtils.toJson(this);
	}

}
