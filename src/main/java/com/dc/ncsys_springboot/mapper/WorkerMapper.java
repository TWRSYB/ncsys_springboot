package com.dc.ncsys_springboot.mapper;

import com.dc.ncsys_springboot.daoVo.WorkerDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;


/**
 * <p>
 * 工人表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-19 11:57
 */
@Mapper
public interface WorkerMapper extends BaseMapper<WorkerDo> {

    Page<WorkerDo> pageQuery(Map<String, String> params);

    int updateByWorkerId(WorkerDo existingWorker);
}
