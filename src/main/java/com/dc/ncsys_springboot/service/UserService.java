package com.dc.ncsys_springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.daoVo.User;
import com.dc.ncsys_springboot.vo.ResVo;

/**
 * <p>
 * 登录用户 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-05-27 11:55
 */
public interface UserService extends IService<User> {

    ResVo login(User user);

    ResVo getUserList();

    ResVo getUserInfo();

    ResVo refreshToken();

    ResVo logout();
}
