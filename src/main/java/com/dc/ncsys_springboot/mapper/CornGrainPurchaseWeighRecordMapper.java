package com.dc.ncsys_springboot.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.dc.ncsys_springboot.daoVo.CornGrainPurchaseWeighRecordDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;



/**
 * <p>
 * 玉米粒收购过磅记录 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-13 17:00
 */
@Mapper
public interface CornGrainPurchaseWeighRecordMapper extends BaseMapper<CornGrainPurchaseWeighRecordDo> {

    int deleteByTradeSerno(String tradeSerno);
}
