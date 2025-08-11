package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.daoVo.TableDesignSqlDo;
import com.dc.ncsys_springboot.mapper.TableDesignSqlMapper;
import com.dc.ncsys_springboot.service.TableDesignSqlService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 表设计SQL 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-07 19:37
 */
@Service
public class TableDesignSqlServiceImpl extends ServiceImpl<TableDesignSqlMapper, TableDesignSqlDo> implements TableDesignSqlService {

    @Autowired
    private TableDesignSqlMapper tableDesignSqlMapper;

    @Override
    public ResVo<TableDesignSqlDo> getLastTableDesignSql(String tableId) {
        TableDesignSqlDo lastTableDesignSql = tableDesignSqlMapper.selectLast(tableId);
        if (lastTableDesignSql != null) {
            return ResVo.success("获取最后SQL成功", lastTableDesignSql);
        }
        return ResVo.fail("获取最后SQL失败");
    }

    @Override
    public PageResVo<TableDesignSqlDo> pageQuery(PageQueryVo<TableDesignSqlDo> pageQueryVo) {
        PageHelper.startPage(pageQueryVo.getPageNum(), pageQueryVo.getPageSize());
        Page<TableDesignSqlDo> page = tableDesignSqlMapper.pageQuery(pageQueryVo.getParams());
        return PageResVo.success(page);
    }
}
