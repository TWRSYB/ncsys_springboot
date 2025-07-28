package com.dc.ncsys_springboot.mapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.WorkerPayClearDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;


/**
 * <p>
 * 工人工钱结算表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-28 08:57
 */
@Mapper
public interface WorkerPayClearMapper extends BaseMapper<WorkerPayClearDo> {

    Page<WorkerPayClearDo> pageQuery(Map<String, String> params);
}
