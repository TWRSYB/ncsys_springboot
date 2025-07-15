package com.dc.ncsys_springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.daoVo.UserDo;
import com.dc.ncsys_springboot.vo.ResVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 登录用户 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-05-27 11:55
 */
public interface UserService extends IService<UserDo> {

    ResVo<UserDo> login(UserDo userDo);

    ResVo getUserList();

    ResVo getUserInfo();

    ResVo<UserDo> refreshToken();

    ResVo logout();

    ResVo addUser(UserDo userDo);

    ResVo<List<UserDo>> getSubAccountList(String userId);

    ResVo updateAvatar(UserDo userDo);

    ResVo changePassword(Map<String, String> map);

    ResVo<UserDo> changeAccount(UserDo userDo);
}
