package com.dc.ncsys_springboot.mapper;

import com.dc.ncsys_springboot.daoVo.CornCobPurchaseWeighRecordDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


/**
 * <p>
 * 玉米棒收购过磅记录 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-10 21:22
 */
@Mapper
public interface CornCobPurchaseWeighRecordMapper extends BaseMapper<CornCobPurchaseWeighRecordDo> {

    int deleteByTradeSerno(String tradeSerno);
}
