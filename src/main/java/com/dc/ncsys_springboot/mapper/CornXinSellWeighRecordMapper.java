package com.dc.ncsys_springboot.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.CornXinSellWeighRecordDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;



/**
 * <p>
 * 玉米芯出售过磅记录 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-08-06 15:13
 */
@Mapper
public interface CornXinSellWeighRecordMapper extends BaseMapper<CornXinSellWeighRecordDo> {

    int deleteByTradeSerno(String tradeSerno);
}
