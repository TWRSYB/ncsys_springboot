package com.dc.ncsys_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dc.ncsys_springboot.daoVo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 登录用户 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-05-27 11:55
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
