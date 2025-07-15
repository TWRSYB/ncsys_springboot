package com.dc.ncsys_springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.constants.ComConst;
import com.dc.ncsys_springboot.daoVo.UserDo;
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
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TableDesignColumnService tableDesignColumnService;

    @Override
    public ResVo<UserDo> login(UserDo userDo) {
        LambdaQueryWrapper<UserDo> lambdaWrapper = new LambdaQueryWrapper<>();
        lambdaWrapper.eq(UserDo::getLoginCode, userDo.getLoginCode()); // 直接引用实体类的字段方法

        // 查询一条记录（可设置是否抛出异常）
        UserDo queryUserDo = userMapper.selectOne(lambdaWrapper); // 第二个参数：是否允许多条结果时抛出异常


        log.info("用户登录->查询登录用户结果: {}", queryUserDo);

        if (ObjectUtils.isEmpty(queryUserDo)) {
            log.warn("登录账号未查询到用户: {}", userDo.getLoginCode());
            return ResVo.fail("账号或密码错误!");
        }

        if (!queryUserDo.getLoginPassword().equals(userDo.getLoginPassword())) {
            log.warn("上送密码与用户密码不一致: {}", userDo.getLoginCode());
            return ResVo.fail("账号或密码错误!");
        }

        if (!queryUserDo.getDataStatus().equals("1")) {
            log.warn("用户状态不可用: {}", queryUserDo.getDataStatus());
            return ResVo.fail("当前用户不可用, 请联系管理员!");
        }

        // 将登录用户的信息放到session中
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", queryUserDo);

        Map<String, Object> claims = new HashMap<>();
        claims.put("loginCode", queryUserDo.getLoginCode());

        UserDo resUserDo = new UserDo();
        resUserDo.setUserId(queryUserDo.getUserId());
        resUserDo.setLoginCode(queryUserDo.getLoginCode());
        resUserDo.setUserName(queryUserDo.getUserName());
        resUserDo.setRoleCode(queryUserDo.getRoleCode());
        resUserDo.setAvatar(queryUserDo.getAvatar());
        resUserDo.setToken(jwtUtil.genToken(claims));


        return ResVo.success("登录成功", resUserDo);
    }

    @Override
    public ResVo getUserList() {
        Map<String, String> map = userMapper.getTableDesign();
        System.out.println("map = " + map);
        List<UserDo> userDos = userMapper.selectList(new QueryWrapper<>());
        return ResVo.success("查询用户列表成功", userDos);
    }

    @Override
    public ResVo getUserInfo() {
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        LambdaQueryWrapper<UserDo> lambdaWrapper = new LambdaQueryWrapper<>();
        lambdaWrapper.eq(UserDo::getLoginCode, sessionUserDo.getLoginCode()); // 直接引用实体类的字段方法

        // 查询一条记录（可设置是否抛出异常）
        UserDo queryUserDo = userMapper.selectOne(lambdaWrapper); // 第二个参数：是否允许多条结果时抛出异常
        UserDo userDo = new UserDo();
        userDo.setUserId(queryUserDo.getUserId());
        userDo.setLoginCode(queryUserDo.getLoginCode());
        userDo.setUserName(queryUserDo.getUserName());
        userDo.setRoleCode(queryUserDo.getRoleCode());
        userDo.setAvatar(queryUserDo.getAvatar());

        sessionUserDo = queryUserDo;

        return ResVo.success("获取用户信息成功", userDo);
    }

    @Override
    public ResVo<UserDo> refreshToken() {
        // 将登录用户的信息放到session中
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        HttpSession session = request.getSession();
        UserDo loginUserDo = (UserDo) session.getAttribute("loginUser");

        // 查询登录用户信息
        LambdaQueryWrapper<UserDo> lambdaWrapper = new LambdaQueryWrapper<>();
        lambdaWrapper.eq(UserDo::getLoginCode, loginUserDo.getLoginCode()); // 直接引用实体类的字段方法
        UserDo queryUserDo = userMapper.selectOne(lambdaWrapper);
        if (ObjectUtils.isEmpty(queryUserDo)) {
            throw new BusinessException("刷新token失败", "用户不存在");
        }

        loginUserDo = queryUserDo;

        Map<String, Object> claims = new HashMap<>();
        claims.put("loginCode", loginUserDo.getLoginCode());

        UserDo resUserDo = new UserDo();
        resUserDo.setUserId(loginUserDo.getUserId());
        resUserDo.setLoginCode(loginUserDo.getLoginCode());
        resUserDo.setUserName(loginUserDo.getUserName());
        resUserDo.setRoleCode(loginUserDo.getRoleCode());
        resUserDo.setAvatar(loginUserDo.getAvatar());
        resUserDo.setToken(jwtUtil.genToken(claims));

        return ResVo.success("刷新token成功", resUserDo);
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
    public ResVo addUser(UserDo userDo) {

        UserDo sessionUserDo = SessionUtils.getSessionUser();

        // 入参校验
        checkUser(userDo);

        // 检查用户是否已存在
        UserDo existingUserDo = userMapper.selectOne(new LambdaQueryWrapper<UserDo>().eq(UserDo::getLoginCode, userDo.getLoginCode()));
        if (existingUserDo != null) {
            return ResVo.fail("用户已存在");
        }

        userDo.setUserId(userDo.getPhoneNum() + DateTimeUtil.getMinuteKey());
        userDo.setDataStatus("1");
        userDo.setCreateUser(sessionUserDo.getLoginCode());
        userDo.setUpdateUser(sessionUserDo.getLoginCode());

        // 插入用户信息
        int rows = userMapper.insert(userDo);
        if (rows > 0) {
            return ResVo.success("添加用户成功");
        } else {
            return ResVo.fail("添加用户失败");
        }

    }

    @Override
    public ResVo<List<UserDo>> getSubAccountList(String userId) {
        // 获取当前登录用户的信息
        UserDo sessionUserDo = SessionUtils.getSessionUser();

        // 匹配userId
        if (!sessionUserDo.getUserId().equals(userId)) {
            throw new BusinessException("查询子账号失败", "用户ID不匹配");
        }

        // 如果用户不是系统管理员或管理员则直接返回空数据
        if (!sessionUserDo.getRoleCode().equals(ComConst.ROLE_SYS_ADMIN) && !sessionUserDo.getRoleCode().equals(ComConst.ROLE_MANAGER)) {
            return ResVo.success("查询子账号列表成功", new ArrayList<>());
        }

        // 查询子账号信息
        List<UserDo> subAccountList = userMapper.getSubAccountList(userId);

        return ResVo.success("查询子账号列表成功", subAccountList);
    }

    @Override
    public ResVo<Object> updateAvatar(UserDo userDo) {
        // 获取当前登录用户的信息
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        // 验证被更新的用户是否为当前登录用户或者当前用户的子账号
        if (!sessionUserDo.getUserId().equals(userDo.getUserId())) {
            // 如果不是当前登录用户，则验证是否为当前登录用户的子账号
            List<UserDo> subAccount = getSubAccountList(sessionUserDo.getUserId()).getData();
            boolean isSubAccount = subAccount.stream().anyMatch(sub -> sub.getUserId().equals(userDo.getUserId()));
            if (!isSubAccount) {
                throw new BusinessException("更新头像失败", "用户ID不匹配");
            }
        }
        // 更新用户头像
        userDo.setUpdateUser(sessionUserDo.getLoginCode());
        int rows = userMapper.updateAvatar(userDo);
        if (rows != 1) {
            throw new BusinessException("更新头像失败", "更新数量不是1");
        }
        return ResVo.success("更新头像成功");
    }

    @Override
    public ResVo changePassword(Map<String, String> map) {
        // 获取当前登录用户的信息
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        // 入参校验
        if (map == null || map.isEmpty()) {
            throw new BusinessException("修改密码失败", "入参为空");
        }
        if (map.get("userId") == null || map.get("userId").isEmpty()) {
            throw new BusinessException("修改密码失败", "用户ID不能为空");
        }
        if (map.get("oldPwd") == null || map.get("oldPwd").isEmpty()) {
            throw new BusinessException("修改密码失败", "原密码不能为空");
        }
        if (map.get("newPwd") == null || map.get("newPwd").isEmpty()) {
            throw new BusinessException("修改密码失败", "新密码不能为空");
        }
        if (map.get("phoneNumber") == null || map.get("phoneNumber").isEmpty()) {
            throw new BusinessException("修改密码失败", "手机号码不能为空");
        }
        // 检查新密码是否符合要求
        if (!map.get("newPwd").matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*_-])[0-9a-zA-Z!@#$%^&*_-]{8,16}$")) {
            throw new BusinessException("修改密码失败", "新密码格式不正确");
        }
        // 确认密码不能为空
        if (map.get("confirmPwd") == null || map.get("confirmPwd").isEmpty()) {
            throw new BusinessException("修改密码失败", "确认密码不能为空");
        }
        // 确认密码与新密码不一致
        if (!map.get("newPwd").equals(map.get("confirmPwd"))) {
            throw new BusinessException("修改密码失败", "新密码与确认密码不一致");
        }

        // 如果当前登录人是操作者则返回失败
        if (ComConst.ROLE_OPERATOR.equals(sessionUserDo.getRoleCode())) {
            throw new BusinessException("修改密码失败", "当前登录人是操作者, 无法修改密码");
        }

        // 验证被更新的用户是否为当前登录用户或者当前用户的子账号
        if (!sessionUserDo.getUserId().equals(map.get("userId"))) {
            // 如果不是当前登录用户，则验证是否为当前登录用户的子账号
            List<UserDo> subAccount = getSubAccountList(sessionUserDo.getUserId()).getData();
            boolean isSubAccount = subAccount.stream().anyMatch(sub -> sub.getUserId().equals(map.get("userId")));
            if (!isSubAccount) {
                throw new BusinessException("修改密码失败", "用户ID不匹配");
            }
        }

        // 检查原密码是否正确
        UserDo existingUserDo = userMapper.selectOne(new LambdaQueryWrapper<UserDo>().eq(UserDo::getUserId, map.get("userId")));
        if (existingUserDo == null) {
            throw new BusinessException("修改密码失败", "用户不存在");
        }
        if (!existingUserDo.getLoginPassword().equals(map.get("oldPwd"))) {
            throw new BusinessException("修改密码失败", "原密码错误");
        }
        // 匹配手机号码
        if (!existingUserDo.getPhoneNum().equals(map.get("phoneNumber"))) {
            throw new BusinessException("修改密码失败", "手机号码不匹配");
        }

        // 更新用户密码
        existingUserDo.setLoginPassword(map.get("newPwd"));
        existingUserDo.setUpdateUser(sessionUserDo.getLoginCode());
        int rows = userMapper.updateById(existingUserDo);
        if (rows != 1) {
            throw new BusinessException("修改密码失败", "更新数量不是1");
        }
        return ResVo.success("修改密码成功");
    }

    @Override
    public ResVo<UserDo> changeAccount(UserDo userDo) {
        // 获取当前登录用户的信息
        UserDo sessionUserDo = SessionUtils.getSessionUser();
        // 验证要切换的用户是否为当前用户的子账号
        List<UserDo> subAccount = getSubAccountList(sessionUserDo.getUserId()).getData();
        UserDo targetUser = subAccount.stream().filter(sub -> sub.getUserId().equals(userDo.getUserId())).findFirst().orElse(null);
        if (targetUser == null) {
            throw new BusinessException("切换账号失败", "用户ID未匹配到子账号");
        }
        // 匹配登录码
        if (!targetUser.getLoginCode().equals(userDo.getLoginCode())) {
            throw new BusinessException("切换账号失败", "登录码不匹配");
        }
        // 切换账号
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", targetUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put("loginCode", targetUser.getLoginCode());

        UserDo resUserDo = new UserDo();
        resUserDo.setUserId(targetUser.getUserId());
        resUserDo.setLoginCode(targetUser.getLoginCode());
        resUserDo.setUserName(targetUser.getUserName());
        resUserDo.setRoleCode(targetUser.getRoleCode());
        resUserDo.setAvatar(targetUser.getAvatar());
        resUserDo.setToken(jwtUtil.genToken(claims));

        return ResVo.success("切换成功", resUserDo);

    }

    /**
     * 入参校验
     *
     * @param userDo 用户信息
     */
    private void checkUser(UserDo userDo) {
        if (userDo == null) {
            throw new BusinessException("用户信息不能为空");
        }

        String loginCode = userDo.getLoginCode();
        if (loginCode == null || loginCode.isEmpty()) {
            throw new BusinessException("登录账号不能为空");
        }

        // 登录账号由 8~10位 数字和字母组成, 且必需同时包含数字和字母
        if (!loginCode.matches("^(?=.*[0-9])(?=.*[a-zA-Z])[0-9a-zA-Z]{8,10}$")) {
            throw new BusinessException("登录账号格式不正确");
        }

        String loginPassword = userDo.getLoginPassword();
        if (loginPassword == null || loginPassword.isEmpty()) {
            throw new BusinessException("登录密码不能为空");
        }

        // 登录密码由 8~16位 数字/字母/特殊符号组成, 且必需同时包含数字/小写字母/大写字母和特殊符号
        if (!loginPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_-])[0-9a-zA-Z!@#$%^&*()_-]{8,16}$")) {
            throw new BusinessException("登录密码格式不正确");
        }

        String userName = userDo.getUserName();
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

        String roleCode = userDo.getRoleCode();
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
