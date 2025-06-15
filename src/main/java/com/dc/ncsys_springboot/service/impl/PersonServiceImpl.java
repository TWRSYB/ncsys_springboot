package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.dc.ncsys_springboot.mapper.PersonMapper;
import com.dc.ncsys_springboot.service.PersonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.vo.ResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 人员主表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-14 15:14
 */
@Service
public class PersonServiceImpl extends ServiceImpl<PersonMapper, PersonDo> implements PersonService {

    @Autowired
    private PersonMapper personMapper;
    

    @Override
    public ResVo getPersonList(PersonDo personDo) {
        List<PersonDo> personDos = personMapper.selectList(null);
        return ResVo.success("查询人员列表成功", personDos);
    }
}
