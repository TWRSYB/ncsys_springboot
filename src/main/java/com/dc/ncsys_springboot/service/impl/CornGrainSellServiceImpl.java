package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.daoVo.CornGrainSellDo;
import com.dc.ncsys_springboot.mapper.CornGrainSellMapper;
import com.dc.ncsys_springboot.service.CornGrainSellService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 玉米粒出售表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-07-31 15:04
 */
@Service
public class CornGrainSellServiceImpl extends ServiceImpl<CornGrainSellMapper, CornGrainSellDo> implements CornGrainSellService {

    @Autowired
    private CornGrainSellMapper cornGrainSellMapper;

    @Override
    public PageResVo<CornGrainSellDo> pageQuery(PageQueryVo<CornGrainSellDo> pageQueryVo) {
        PageHelper.startPage(pageQueryVo.getPageNum(), pageQueryVo.getPageSize());
        Page<CornGrainSellDo> page = cornGrainSellMapper.pageQuery(pageQueryVo.getParams());
        return PageResVo.success(page);
    }
}
