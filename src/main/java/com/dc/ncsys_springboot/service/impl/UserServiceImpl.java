package com.dc.ncsys_springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.daoVo.User;
import com.dc.ncsys_springboot.mapper.UserMapper;
import com.dc.ncsys_springboot.service.UserService;
import com.dc.ncsys_springboot.util.JwtUtil;
import com.dc.ncsys_springboot.util.SessionUtils;
import com.dc.ncsys_springboot.vo.ResVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 登录用户 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-05-27 11:55
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public ResVo login(User user) {
        LambdaQueryWrapper<User> lambdaWrapper = new LambdaQueryWrapper<>();
        lambdaWrapper.eq(User::getLoginCode, user.getLoginCode()); // 直接引用实体类的字段方法

        // 查询一条记录（可设置是否抛出异常）
        User queryUser = userMapper.selectOne(lambdaWrapper); // 第二个参数：是否允许多条结果时抛出异常


        log.info("用户登录->查询登录用户结果: {}", user);

        if (ObjectUtils.isEmpty(queryUser)) {
            log.warn("登录账号未查询到用户: {}", user.getLoginCode());
            return ResVo.fail("账号或密码错误!");
        }

        if (!queryUser.getLoginPassword().equals(user.getLoginPassword())) {
            log.warn("上送密码与用户密码不一致: {}", user.getLoginCode());
            return ResVo.fail("账号或密码错误!");
        }

        if (!queryUser.getDataStatus().equals("1")) {
            log.warn("用户状态不可用: {}", user.getDataStatus());
            return ResVo.fail("当前用户不可用, 请联系管理员!");
        }

        // 将登录用户的信息放到session中
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", queryUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put("loginCode", queryUser.getLoginCode());


        return ResVo.success("登录成功", jwtUtil.genToken(claims));
    }

    @Override
    public ResVo getUserList() {
        Map<String, String> map = userMapper.getTableDesign();
        System.out.println("map = " + map);
        List<User> users = userMapper.selectList(new QueryWrapper<>());
        return ResVo.success("查询用户列表成功", users);
    }

    @Override
    public ResVo getUserInfo() {
        User sessionUser = SessionUtils.getSessionUser();
        User user = new User();
        user.setUserId(sessionUser.getUserId());
        user.setLoginCode(sessionUser.getLoginCode());
        user.setUserName(sessionUser.getUserName());
        user.setRoleCode(sessionUser.getRoleCode());
        return ResVo.success("获取用户信息成功", user);
    }

    @Override
    public ResVo refreshToken() {
        // 将登录用户的信息放到session中
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        HttpSession session = request.getSession();
        User loginUser = (User)session.getAttribute("loginUser");

        Map<String, Object> claims = new HashMap<>();
        claims.put("loginCode", loginUser.getLoginCode());

        return ResVo.success("刷新token成功", jwtUtil.genToken(claims));
    }

    @Override
    public ResVo logout() {
        // 将登录用户的信息放到session中
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        HttpSession session = request.getSession();
        session.removeAttribute("loginUser");
        return ResVo.success("退出登录成功");
    }
}
