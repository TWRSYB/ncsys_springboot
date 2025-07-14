package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.CornGrainPurchaseDo;
import com.dc.ncsys_springboot.daoVo.MixedCornGrainPurchaseDo;
import com.dc.ncsys_springboot.service.CornGrainPurchaseService;
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
 * 玉米粒收购表 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-13 16:55
 */
@Slf4j
@RestController
@RequestMapping("/cornGrainPurchase")
public class CornGrainPurchaseController {

    @Autowired
    private CornGrainPurchaseService cornGrainPurchaseService;

    @PostMapping("/getList")
    public PageResVo<CornGrainPurchaseDo> getList(@RequestBody PageQueryVo<CornGrainPurchaseDo> pageQueryVo) {
        log.info("CONT入参: {}", pageQueryVo);
        return cornGrainPurchaseService.getList(pageQueryVo);
    }

    @PostMapping("/saveTrade")
    public ResVo<Object> saveTrade(@RequestBody MixedCornGrainPurchaseDo mixedCornGrainPurchaseDo) {
        log.info("CONT入参: {}", mixedCornGrainPurchaseDo);
        return cornGrainPurchaseService.saveTrade(mixedCornGrainPurchaseDo);
    }

    @PostMapping("/getTradeDetail")
    public ResVo<MixedCornGrainPurchaseDo> getTradeDetail(@RequestBody CornGrainPurchaseDo cornGrainPurchaseDo) {
        log.info("CONT入参: {}", cornGrainPurchaseDo);
        return cornGrainPurchaseService.getTradeDetail(cornGrainPurchaseDo);
    }

    @PostMapping("/purchaseComplete")
    public ResVo<Object> purchaseComplete(@RequestBody MixedCornGrainPurchaseDo mixedCornGrainPurchaseDo) {
        log.info("CONT入参: {}", mixedCornGrainPurchaseDo);
        return cornGrainPurchaseService.purchaseComplete(mixedCornGrainPurchaseDo);
    }

    @PostMapping("/settleTrade")
    public ResVo<Object> settleTrade(@RequestBody MixedCornGrainPurchaseDo mixedCornGrainPurchaseDo) {
        log.info("CONT入参: {}", mixedCornGrainPurchaseDo);
        return cornGrainPurchaseService.settleTrade(mixedCornGrainPurchaseDo);
    }
}
