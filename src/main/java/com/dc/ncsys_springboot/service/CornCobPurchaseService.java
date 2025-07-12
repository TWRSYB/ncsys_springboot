package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.CornCobPurchaseDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.daoVo.MixedCornCobPurchaseDo;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;

/**
 * <p>
 * 玉米棒收购表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-01 21:59
 */
public interface CornCobPurchaseService extends IService<CornCobPurchaseDo> {

    PageResVo<CornCobPurchaseDo> getList(PageQueryVo<CornCobPurchaseDo> queryVo);

    ResVo<Object> saveTrade(MixedCornCobPurchaseDo mixedCornCobPurchaseDo);

    ResVo<MixedCornCobPurchaseDo> getTradeDetail(CornCobPurchaseDo cornCobPurchaseDo);

    ResVo<Object> purchaseComplete(MixedCornCobPurchaseDo mixedCornCobPurchaseDo);

    ResVo<Object> settleTrade(MixedCornCobPurchaseDo mixedCornCobPurchaseDo);
}
