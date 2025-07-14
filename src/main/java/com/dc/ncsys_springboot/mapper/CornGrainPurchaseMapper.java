package com.dc.ncsys_springboot.mapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.CornGrainPurchaseDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;


/**
 * <p>
 * 玉米粒收购表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-13 16:55
 */
@Mapper
public interface CornGrainPurchaseMapper extends BaseMapper<CornGrainPurchaseDo> {

    Page<CornGrainPurchaseDo> getList(Map<String, String> params);
}
