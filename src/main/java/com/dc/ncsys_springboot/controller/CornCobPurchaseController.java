package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.CornCobPurchaseDo;
import com.dc.ncsys_springboot.daoVo.MixedCornCobPurchaseDo;
import com.dc.ncsys_springboot.service.CornCobPurchaseService;
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
 * 玉米棒收购表 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-01 21:59
 */
@Slf4j
@RestController
@RequestMapping("/cornCobPurchase")
public class CornCobPurchaseController {

    @Autowired
    private CornCobPurchaseService cornCobPurchaseService;

    @PostMapping("/getList")
    public PageResVo<CornCobPurchaseDo> getList(@RequestBody PageQueryVo<CornCobPurchaseDo> queryVo) {
        log.info("CONT入参:{}", queryVo);
        return cornCobPurchaseService.getList(queryVo);
    }

    @PostMapping("/saveTrade")
    public ResVo saveTrade(@RequestBody MixedCornCobPurchaseDo mixedCornCobPurchaseDo) {
        log.info("CONT入参:{}", mixedCornCobPurchaseDo);
        return cornCobPurchaseService.saveTrade(mixedCornCobPurchaseDo);
    }

    @PostMapping("/getTradeDetail")
    public ResVo<MixedCornCobPurchaseDo> getTradeDetail(@RequestBody CornCobPurchaseDo cornCobPurchaseDo) {
        log.info("CONT入参:{}", cornCobPurchaseDo);
        return cornCobPurchaseService.getTradeDetail(cornCobPurchaseDo);
    }

    @PostMapping("/purchaseComplete")
    public ResVo<Object> purchaseComplete(@RequestBody MixedCornCobPurchaseDo mixedCornCobPurchaseDo) {
        log.info("CONT入参:{}", mixedCornCobPurchaseDo);
        return cornCobPurchaseService.purchaseComplete(mixedCornCobPurchaseDo);
    }


    @PostMapping("/settleTrade")
    public ResVo<Object> settleTrade(@RequestBody MixedCornCobPurchaseDo mixedCornCobPurchaseDo) {
        log.info("CONT入参:{}", mixedCornCobPurchaseDo);
        return cornCobPurchaseService.settleTrade(mixedCornCobPurchaseDo);
    }

}
