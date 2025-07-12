package com.dc.ncsys_springboot.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;



/**
 * <p>
 * 人员主表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-14 15:14
 */
@Mapper
public interface PersonMapper extends BaseMapper<PersonDo> {

    PersonDo getByPhoneNum(String phoneNum);

    PersonDo getByPhoneNumOrName(String phoneNumOrName);

    PersonDo selectByPhoneNum(String phoneNum);
}
