package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.daoVo.WorkerPayClearDo;
import com.dc.ncsys_springboot.mapper.WorkerPayClearMapper;
import com.dc.ncsys_springboot.service.WorkerPayClearService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 工人工钱结算表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-28 08:57
 */
@Service
public class WorkerPayClearServiceImpl extends ServiceImpl<WorkerPayClearMapper, WorkerPayClearDo> implements WorkerPayClearService {

    @Autowired
    private WorkerPayClearMapper workerPayClearMapper;

    @Override
    public PageResVo<WorkerPayClearDo> pageQuery(PageQueryVo<WorkerPayClearDo> pageQueryVo) {
        PageHelper.startPage(pageQueryVo.getPageNum(), pageQueryVo.getPageSize());
        Page<WorkerPayClearDo> page = workerPayClearMapper.pageQuery(pageQueryVo.getParams());
        return PageResVo.success(page);
    }
}
