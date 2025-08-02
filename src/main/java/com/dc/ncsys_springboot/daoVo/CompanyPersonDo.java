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
 * 企业人员表
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-02 15:40
 */
@Data
@Accessors(chain = true)
@TableName("m_company_person")
public class CompanyPersonDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 企业ID
     */
    @TableId("company_id")
    private String companyId;

    /**
     * 人员ID
     */
    @TableField("person_id")
    private String personId;

    /**
     * 职位
     */
    private String job;

    /**
     * 入职日期
     */
    private String entryDate;

    /**
     * 离职日期
     */
    private String departureDate;

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
