package com.dc.ncsys_springboot.mapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.CornXinPurchaseDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;


/**
 * <p>
 * 玉米芯收购表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-05 08:55
 */
@Mapper
public interface CornXinPurchaseMapper extends BaseMapper<CornXinPurchaseDo> {

    Page<CornXinPurchaseDo> pageQuery(Map<String, String> params);
}
