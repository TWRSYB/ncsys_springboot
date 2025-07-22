package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.WorkerAttendanceDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.vo.ResVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工人出工记录表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-20 15:06
 */
public interface WorkerAttendanceService extends IService<WorkerAttendanceDo> {

    ResVo<List<Map<String, Object>>> getWorkerAndAttendanceList(String ym);

    ResVo<Object> addWorkerAttendance(WorkerAttendanceDo workerAttendanceDo);
}
