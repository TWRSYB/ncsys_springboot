package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.CornGrainSellDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.daoVo.MixedCornGrainSellDo;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;

/**
 * <p>
 * 玉米粒出售表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-31 15:04
 */
public interface CornGrainSellService extends IService<CornGrainSellDo> {

    PageResVo<CornGrainSellDo> pageQuery(PageQueryVo<CornGrainSellDo> pageQueryVo);

    ResVo<Object> saveTrade(MixedCornGrainSellDo mixedCornGrainSellDo);
}
