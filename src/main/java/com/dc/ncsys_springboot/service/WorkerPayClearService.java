package com.dc.ncsys_springboot.service;

import com.dc.ncsys_springboot.daoVo.MixedWorkerPayClearVo;
import com.dc.ncsys_springboot.daoVo.WorkerPayClearDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;

/**
 * <p>
 * 工人工钱结算表 服务类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-28 08:57
 */
public interface WorkerPayClearService extends IService<WorkerPayClearDo> {

    PageResVo<WorkerPayClearDo> pageQuery(PageQueryVo<WorkerPayClearDo> pageQueryVo);

    ResVo<Object> clear(MixedWorkerPayClearVo mixedWorkerPayClearVo);
}
