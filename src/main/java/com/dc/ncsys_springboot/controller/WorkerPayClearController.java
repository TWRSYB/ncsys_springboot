package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.MixedWorkerPayClearVo;
import com.dc.ncsys_springboot.daoVo.WorkerPayClearDo;
import com.dc.ncsys_springboot.service.WorkerPayClearService;
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
 * 工人工钱结算表 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-28 08:57
 */
@Slf4j
@RestController
@RequestMapping("/workerPayClear")
public class WorkerPayClearController {
    @Autowired
    private WorkerPayClearService workerPayClearService;

    @PostMapping("/pageQuery")
    public PageResVo<WorkerPayClearDo> pageQuery(@RequestBody PageQueryVo<WorkerPayClearDo> pageQueryVo) {
        log.info("CONT入参: {}", pageQueryVo);
        return workerPayClearService.pageQuery(pageQueryVo);
    }

    @PostMapping("/clear")
    public ResVo<Object> clear(@RequestBody MixedWorkerPayClearVo mixedWorkerPayClearVo) {
        log.info("CONT入参: {}", mixedWorkerPayClearVo);
        return workerPayClearService.clear(mixedWorkerPayClearVo);
    }
}
