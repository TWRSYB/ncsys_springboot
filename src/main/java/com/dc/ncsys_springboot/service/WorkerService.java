package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.WorkerDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;

import java.util.List;

/**
 * <p>
 * 工人表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-19 11:43
 */
public interface WorkerService extends IService<WorkerDo> {

    PageResVo<WorkerDo> pageQuery(PageQueryVo<WorkerDo> pageQueryVo);

    ResVo<Object> saveWorker(WorkerDo workerDo);


    ResVo<Object> updateWorker(WorkerDo workerDo);

    ResVo<List<WorkerDo>> getWorkerList();

    PageResVo<WorkerDo> pageQueryWageSettle(PageQueryVo<WorkerDo> pageQueryVo);
}
