package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.NationalAdministrativeDivisionInfoDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.vo.ResVo;

/**
 * <p>
 * 全国行政区划信息 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-01 16:13
 */
public interface NationalAdministrativeDivisionInfoService extends IService<NationalAdministrativeDivisionInfoDo> {

    ResVo readJsonToDb();
}
