package com.dc.ncsys_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dc.ncsys_springboot.daoVo.UserDo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 登录用户 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-05-27 11:55
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDo> {

    @MapKey("Table")
    Map<String, String> getTableDesign();

    List<UserDo> getSubAccountList(String userId);

    int updateAvatar(UserDo userDo);
}
