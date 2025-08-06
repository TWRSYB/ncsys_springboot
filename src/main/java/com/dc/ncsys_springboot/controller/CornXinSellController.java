package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.CornXinSellDo;
import com.dc.ncsys_springboot.daoVo.MixedCornXinSellDo;
import com.dc.ncsys_springboot.service.CornXinSellService;
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
 * 玉米芯出售表 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-06 15:08
 */
@Slf4j
@RestController
@RequestMapping("/cornXinSell")
public class CornXinSellController {

    @Autowired
    private CornXinSellService cornXinSellService;

    @PostMapping("/pageQuery")
    public PageResVo<CornXinSellDo> pageQuery(@RequestBody PageQueryVo<CornXinSellDo> pageQueryVo) {
        log.info("CONT 入参: {}", pageQueryVo);
        return cornXinSellService.pageQuery(pageQueryVo);
    }

    @PostMapping("/saveTrade")
    public ResVo<Object> saveTrade(@RequestBody MixedCornXinSellDo mixedCornXinSellDo) {
        log.info("CONT入参: {}", mixedCornXinSellDo);
        return cornXinSellService.saveTrade(mixedCornXinSellDo);
    }

    @PostMapping("/getTradeDetail")
    public ResVo<MixedCornXinSellDo> getTradeDetail(@RequestBody CornXinSellDo cornXinSellDo) {
        log.info("CONT入参: {}", cornXinSellDo);
        return cornXinSellService.getTradeDetail(cornXinSellDo);
    }

    @PostMapping("/sellComplete")
    public ResVo<Object> sellComplete(@RequestBody MixedCornXinSellDo mixedCornXinSellDo) {
        log.info("CONT入参: {}", mixedCornXinSellDo);
        return cornXinSellService.sellComplete(mixedCornXinSellDo);
    }

    @PostMapping("/settleTrade")
    public ResVo<Object> settleTrade(@RequestBody MixedCornXinSellDo mixedCornXinSellDo) {
        log.info("CONT入参: {}", mixedCornXinSellDo);
        return cornXinSellService.settleTrade(mixedCornXinSellDo);
    }
}
