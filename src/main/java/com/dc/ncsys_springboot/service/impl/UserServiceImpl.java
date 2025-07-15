package com.dc.ncsys_springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.constants.ComConst;
import com.dc.ncsys_springboot.daoVo.User;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.UserMapper;
import com.dc.ncsys_springboot.service.TableDesignColumnService;
import com.dc.ncsys_springboot.service.UserService;
import com.dc.ncsys_springboot.util.DateTimeUtil;
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

import java.util.ArrayList;
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

    @Autowired
    private TableDesignColumnService tableDesignColumnService;

    @Override
    public ResVo login(User user) {
        LambdaQueryWrapper<User> lambdaWrapper = new LambdaQueryWrapper<>();
        lambdaWrapper.eq(User::getLoginCode, user.getLoginCode()); // 直接引用实体类的字段方法

        // 查询一条记录（可设置是否抛出异常）
        User queryUser = userMapper.selectOne(lambdaWrapper); // 第二个参数：是否允许多条结果时抛出异常


        log.info("用户登录->查询登录用户结果: {}", queryUser);

        if (ObjectUtils.isEmpty(queryUser)) {
            log.warn("登录账号未查询到用户: {}", user.getLoginCode());
            return ResVo.fail("账号或密码错误!");
        }

        if (!queryUser.getLoginPassword().equals(user.getLoginPassword())) {
            log.warn("上送密码与用户密码不一致: {}", user.getLoginCode());
            return ResVo.fail("账号或密码错误!");
        }

        if (!queryUser.getDataStatus().equals("1")) {
            log.warn("用户状态不可用: {}", queryUser.getDataStatus());
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

    @Override
    public ResVo addUser(User user) {

        User sessionUser = SessionUtils.getSessionUser();

        // 入参校验
        checkUser(user);

        // 检查用户是否已存在
        User existingUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getLoginCode, user.getLoginCode()));
        if (existingUser != null) {
            return ResVo.fail("用户已存在");
        }

        user.setUserId(user.getPhoneNum() + DateTimeUtil.getMinuteKey());
        user.setDataStatus("1");
        user.setCreateUser(sessionUser.getLoginCode());
        user.setUpdateUser(sessionUser.getLoginCode());

        // 插入用户信息
        int rows = userMapper.insert(user);
        if (rows > 0) {
            return ResVo.success("添加用户成功");
        } else {
            return ResVo.fail("添加用户失败");
        }

    }

    @Override
    public ResVo<List<User>> getSubAccountList(String userId) {
        // 获取当前登录用户的信息
        User sessionUser = SessionUtils.getSessionUser();

        // 匹配userId
        if (!sessionUser.getUserId().equals(userId)) {
            throw new BusinessException("查询子账号失败", "用户ID不匹配");
        }

        // 如果用户不是系统管理员或管理员则直接返回空数据
        if (!sessionUser.getRoleCode().equals(ComConst.ROLE_SYS_ADMIN) && !sessionUser.getRoleCode().equals(ComConst.ROLE_MANAGER)) {
            return ResVo.success("查询子账号列表成功", new ArrayList<>());
        }

        // 查询子账号信息
        List<User> subAccountList = userMapper.getSubAccountList(userId);

        return ResVo.success("查询子账号列表成功", subAccountList);
    }

    /**
     * 入参校验
     * @param user 用户信息
     */
    private void checkUser(User user) {
        if (user == null) {
            throw new BusinessException("用户信息不能为空");
        }

        String loginCode = user.getLoginCode();
        if (loginCode == null || loginCode.isEmpty()) {
            throw new BusinessException("登录账号不能为空");
        }

        // 登录账号由 8~10位 数字和字母组成, 且必需同时包含数字和字母
        if (!loginCode.matches("^(?=.*[0-9])(?=.*[a-zA-Z])[0-9a-zA-Z]{8,10}$")) {
            throw new BusinessException("登录账号格式不正确");
        }

        String loginPassword = user.getLoginPassword();
        if (loginPassword == null || loginPassword.isEmpty()) {
            throw new BusinessException("登录密码不能为空");
        }

        // 登录密码由 8~16位 数字/字母/特殊符号组成, 且必需同时包含数字/小写字母/大写字母和特殊符号
        if (!loginPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_-])[0-9a-zA-Z!@#$%^&*()_-]{8,16}$")) {
            throw new BusinessException("登录密码格式不正确");
        }

        String userName = user.getUserName();
        if (userName == null || userName.isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }

        // 用户名长度2~10位
        if (userName.length() < 2 || userName.length() > 10) {
            throw new BusinessException("用户名长度不正确");
        }

        // 用户名不能包含空格
        if (userName.contains(" ")) {
            throw new BusinessException("用户名不能包含空格");
        }

        String roleCode = user.getRoleCode();
        if (roleCode == null || roleCode.isEmpty()) {
            throw new BusinessException("角色编码不能为空");
        }

        // 必须指定的角色编码
        ResVo option = tableDesignColumnService.getOption("m_user", "role_code");
        @SuppressWarnings("unchecked")
        Map<String, String> roleCodeMap = (Map<String, String>) option.getData();
        if (!roleCodeMap.containsKey(roleCode)) {
            throw new BusinessException("角色编码不正确");
        }


    }


}
