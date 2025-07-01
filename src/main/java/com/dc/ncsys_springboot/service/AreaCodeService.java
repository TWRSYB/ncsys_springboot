package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.AreaCodeDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.vo.ResVo;

/**
 * <p>
 * 地区码值表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-30 15:37
 */
public interface AreaCodeService extends IService<AreaCodeDo> {

    ResVo readJsonToDb();
}
