package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.CornXinSellDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.daoVo.MixedCornXinSellDo;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;

/**
 * <p>
 * 玉米芯出售表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-06 15:08
 */
public interface CornXinSellService extends IService<CornXinSellDo> {

    PageResVo<CornXinSellDo> pageQuery(PageQueryVo<CornXinSellDo> pageQueryVo);

    ResVo<Object> saveTrade(MixedCornXinSellDo mixedCornXinSellDo);

    ResVo<MixedCornXinSellDo> getTradeDetail(CornXinSellDo cornXinSellDo);

    ResVo<Object> sellComplete(MixedCornXinSellDo mixedCornXinSellDo);

    ResVo<Object> settleTrade(MixedCornXinSellDo mixedCornXinSellDo);
}
