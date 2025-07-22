package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.WorkerAttendanceDo;
import com.dc.ncsys_springboot.service.WorkerAttendanceService;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工人出工记录表 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-20 15:06
 */
@Slf4j
@RestController
@RequestMapping("/workerAttendance")
public class WorkerAttendanceController {

    @Autowired
    private WorkerAttendanceService workerAttendanceService;

    @GetMapping("/getWorkerAndAttendanceList")
    public ResVo<List<Map<String, Object>>> getWorkerAndAttendanceList(String ym) {
        log.info("CONT入参: {}", ym);
        return workerAttendanceService.getWorkerAndAttendanceList(ym);
    }

    @PostMapping("/addWorkerAttendance")
    public ResVo<Object> addWorkerAttendance(@RequestBody WorkerAttendanceDo workerAttendanceDo) {
        log.info("CONT入参: {}", workerAttendanceDo);
        return workerAttendanceService.addWorkerAttendance(workerAttendanceDo);
    }

}
