package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.CornGrainPurchaseDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.daoVo.MixedCornGrainPurchaseDo;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;

/**
 * <p>
 * 玉米粒收购表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-13 16:55
 */
public interface CornGrainPurchaseService extends IService<CornGrainPurchaseDo> {

    PageResVo<CornGrainPurchaseDo> getList(PageQueryVo<CornGrainPurchaseDo> pageQueryVo);

    ResVo<Object> saveTrade(MixedCornGrainPurchaseDo mixedCornGrainPurchaseDo);

    ResVo<MixedCornGrainPurchaseDo> getTradeDetail(CornGrainPurchaseDo cornGrainPurchaseDo);

    ResVo<Object> purchaseComplete(MixedCornGrainPurchaseDo mixedCornGrainPurchaseDo);

    ResVo<Object> settleTrade(MixedCornGrainPurchaseDo mixedCornGrainPurchaseDo);
}
