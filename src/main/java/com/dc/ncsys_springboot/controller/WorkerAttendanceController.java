package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.WorkerAttendanceDo;
import com.dc.ncsys_springboot.daoVo.WorkerDo;
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

    /**
     * 获取指定月份的工人出工记录
     * @param ym 年月
     * @return 工人出工记录列表
     */
    @GetMapping("/getWorkerAndAttendanceList")
    public ResVo<List<Map<String, Object>>> getWorkerAndAttendanceList(String ym) {
        log.info("CONT入参: {}", ym);
        return workerAttendanceService.getWorkerAndAttendanceList(ym);
    }

    /**
     * 添加工人出工记录
     * @param workerAttendanceDo 工人出工记录
     * @return 操作结果
     */
    @PostMapping("/addWorkerAttendance")
    public ResVo<Object> addWorkerAttendance(@RequestBody WorkerAttendanceDo workerAttendanceDo) {
        log.info("CONT入参: {}", workerAttendanceDo);
        return workerAttendanceService.addWorkerAttendance(workerAttendanceDo);
    }

    /**
     * 更新工人出工记录
     * @param workerAttendanceDo 工人出工记录
     * @return 操作结果
     */
    @PostMapping("/updateWorkerAttendance")
    public ResVo<Object> updateWorkerAttendance(@RequestBody WorkerAttendanceDo workerAttendanceDo) {
        log.info("CONT入参: {}", workerAttendanceDo);
        return workerAttendanceService.updateWorkerAttendance(workerAttendanceDo);
    }

    /**
     * 获取工人所有出工记录
     * @param workerDo 工人
     * @return 工人出工记录列表
     */
    @PostMapping("/getWorkerAllAttendance")
    public ResVo<List<WorkerAttendanceDo>> getWorkerAllAttendance(@RequestBody WorkerDo workerDo) {
        log.info("CONT入参: {}", workerDo);
        return workerAttendanceService.getWorkerAllAttendance(workerDo);
    }

    /**
     * 获取工人所有出工记录，按年月分组
     * @param workerDo 工人
     * @return 工人出工记录列表
     */
    @PostMapping("/getWorkerAllAttendanceGroupByYm")
    public ResVo<List<Map<String, Object>>> getWorkerAllAttendanceGroupByYm(@RequestBody WorkerDo workerDo) {
        log.info("CONT入参: {}", workerDo);
        return workerAttendanceService.getWorkerAllAttendanceGroupByYm(workerDo);
    }

    /**
     * 获取工人所有出工记录，按年月分组
     * @param workerDo 工人
     * @return 工人出工记录Map
     */
    @PostMapping("/getWorkerAllAttendanceGroupByYm2")
    public ResVo<Map<String, Object>> getWorkerAllAttendanceGroupByYm2(@RequestBody WorkerDo workerDo) {
        log.info("CONT入参: {}", workerDo);
        return workerAttendanceService.getWorkerAllAttendanceGroupByYm2(workerDo);
    }

}
