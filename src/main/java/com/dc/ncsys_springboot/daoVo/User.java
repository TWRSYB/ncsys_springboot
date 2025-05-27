package com.dc.ncsys_springboot.daoVo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 登录用户
 * </p>
 *
 * @author sysAdmin
 * @since 2025-05-27 11:55
 */
@Data
@Accessors(chain = true)
@TableName("m_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId("user_id")
    private String userId;

    /**
     * 登录码
     */
    private String loginCode;

    /**
     * 登录密码
     */
    private String loginPassword;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 手机号
     */
    private String phoneNum;

    /**
     * 角色:sysAdmin-系统管理员,manager-管理员,operator-操作员
     */
    private String roleCode;

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
    private LocalDateTime createTime;

    /**
     * 最后更新用户
     */
    private String updateUser;

    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;

    public static final String USER_ID = "user_id";

    public static final String LOGIN_CODE = "login_code";

    public static final String LOGIN_PASSWORD = "login_password";

    public static final String USER_NAME = "user_name";

    public static final String PHONE_NUM = "phone_num";

    public static final String ROLE_CODE = "role_code";

    public static final String DATA_STATUS = "data_status";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";
}
