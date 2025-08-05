package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.CornXinPurchaseDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.daoVo.MixedCornXinPurchaseDo;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;

/**
 * <p>
 * 玉米芯收购表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-05 08:55
 */
public interface CornXinPurchaseService extends IService<CornXinPurchaseDo> {

    PageResVo<CornXinPurchaseDo> pageQuery(PageQueryVo<CornXinPurchaseDo> pageQueryVo);

    ResVo<Object> saveTrade(MixedCornXinPurchaseDo mixedCornXinPurchaseDo);

    ResVo<Object> purchaseComplete(MixedCornXinPurchaseDo mixedCornXinPurchaseDo);

    ResVo<MixedCornXinPurchaseDo> getTradeDetail(CornXinPurchaseDo cornXinPurchaseDo);

    ResVo<Object> settleTrade(MixedCornXinPurchaseDo mixedCornXinPurchaseDo);
}
