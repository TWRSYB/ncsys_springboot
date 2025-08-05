package com.dc.ncsys_springboot.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.CornXinPurchaseWeighRecordDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;



/**
 * <p>
 * 玉米芯收购过磅记录 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-05 08:59
 */
@Mapper
public interface CornXinPurchaseWeighRecordMapper extends BaseMapper<CornXinPurchaseWeighRecordDo> {

    int deleteByTradeSerno(String serno);
}
