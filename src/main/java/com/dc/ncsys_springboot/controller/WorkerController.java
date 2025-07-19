package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.WorkerDo;
import com.dc.ncsys_springboot.service.WorkerService;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 工人表 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-19 11:43
 */
@Slf4j
@Transactional
@RestController
@RequestMapping("/worker")
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    @PostMapping("/pageQuery")
    public PageResVo<WorkerDo> pageQuery(@RequestBody PageQueryVo<WorkerDo> pageQueryVo) {
        log.info("CONT入参: {}", pageQueryVo);
        return workerService.pageQuery(pageQueryVo);
    }

    @PostMapping("/saveWorker")
    public ResVo<Object> saveWorker(@RequestBody WorkerDo workerDo) {
        log.info("CONT入参: {}", workerDo);
        return workerService.saveWorker(workerDo);
    }

    @PostMapping("/updateWorker")
    public ResVo<Object> updateWorker(@RequestBody WorkerDo workerDo) {
        log.info("CONT入参: {}", workerDo);
        return workerService.updateWorker(workerDo);
    }

}
