package com.dc.ncsys_springboot.mapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.CornXinSellDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;


/**
 * <p>
 * 玉米芯出售表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-06 15:08
 */
@Mapper
public interface CornXinSellMapper extends BaseMapper<CornXinSellDo> {

    Page<CornXinSellDo> pageQuery(Map<String, String> params);
}
