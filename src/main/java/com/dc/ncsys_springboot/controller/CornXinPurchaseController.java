package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.CornXinPurchaseDo;
import com.dc.ncsys_springboot.daoVo.MixedCornXinPurchaseDo;
import com.dc.ncsys_springboot.service.CornXinPurchaseService;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 玉米芯收购表 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-05 08:55
 */
@Slf4j
@RestController
@RequestMapping("/cornXinPurchase")
public class CornXinPurchaseController {

    @Autowired
    private CornXinPurchaseService cornXinPurchaseService;

    @PostMapping("/pageQuery")
    public PageResVo<CornXinPurchaseDo> pageQuery(@RequestBody PageQueryVo<CornXinPurchaseDo> pageQueryVo) {
        log.info("CONT入参: {}", pageQueryVo);
        return cornXinPurchaseService.pageQuery(pageQueryVo);
    }


    @PostMapping("/saveTrade")
    public ResVo<Object> saveTrade(@RequestBody MixedCornXinPurchaseDo mixedCornXinPurchaseDo) {
        log.info("CONT入参: {}", mixedCornXinPurchaseDo);
        return cornXinPurchaseService.saveTrade(mixedCornXinPurchaseDo);
    }


    @PostMapping("/getTradeDetail")
    public ResVo<MixedCornXinPurchaseDo> getTradeDetail(@RequestBody CornXinPurchaseDo cornXinPurchaseDo) {
        log.info("CONT入参: {}", cornXinPurchaseDo);
        return cornXinPurchaseService.getTradeDetail(cornXinPurchaseDo);
    }

    @PostMapping("/purchaseComplete")
    public ResVo<Object> purchaseComplete(@RequestBody MixedCornXinPurchaseDo mixedCornXinPurchaseDo) {
        log.info("CONT入参: {}", mixedCornXinPurchaseDo);
        return cornXinPurchaseService.purchaseComplete(mixedCornXinPurchaseDo);
    }

    @PostMapping("/settleTrade")
    public ResVo<Object> settleTrade(@RequestBody MixedCornXinPurchaseDo mixedCornXinPurchaseDo) {
        log.info("CONT入参: {}", mixedCornXinPurchaseDo);
        return cornXinPurchaseService.settleTrade(mixedCornXinPurchaseDo);
    }

}
