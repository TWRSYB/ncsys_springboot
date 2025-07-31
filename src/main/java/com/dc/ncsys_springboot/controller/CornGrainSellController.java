package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.CornGrainSellDo;
import com.dc.ncsys_springboot.service.CornGrainSellService;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 玉米粒出售表 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-31 15:04
 */
@Slf4j
@RestController
@RequestMapping("/cornGrainSell")
public class CornGrainSellController {

    @Autowired
    private CornGrainSellService cornGrainSellService;

    @PostMapping("/pageQuery")
    public PageResVo<CornGrainSellDo> pageQuery(@RequestBody PageQueryVo<CornGrainSellDo> pageQueryVo) {
        log.info("CONT 入参: {}", pageQueryVo);
        return cornGrainSellService.pageQuery(pageQueryVo);
    }

}
