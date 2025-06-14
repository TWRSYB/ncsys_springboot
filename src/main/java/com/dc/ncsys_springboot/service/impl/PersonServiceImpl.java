package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.daoVo.PersonDo;
import com.dc.ncsys_springboot.mapper.PersonMapper;
import com.dc.ncsys_springboot.service.PersonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
