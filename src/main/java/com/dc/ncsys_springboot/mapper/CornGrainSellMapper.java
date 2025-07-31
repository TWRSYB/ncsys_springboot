package com.dc.ncsys_springboot.mapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.CornGrainSellDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;


/**
 * <p>
 * 玉米粒出售表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-31 15:04
 */
@Mapper
public interface CornGrainSellMapper extends BaseMapper<CornGrainSellDo> {

    Page<CornGrainSellDo> pageQuery(Map<String, String> params);
}
