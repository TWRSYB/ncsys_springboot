package com.dc.ncsys_springboot.mapper;

import com.dc.ncsys_springboot.daoVo.CornCobPurchaseDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;



/**
 * <p>
 * 玉米棒收购表 Mapper 接口
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-02 14:45
 */
@Mapper
public interface CornCobPurchaseMapper extends BaseMapper<CornCobPurchaseDo> {

    Page<CornCobPurchaseDo> getList(CornCobPurchaseDo cornCobPurchaseDo);
}
