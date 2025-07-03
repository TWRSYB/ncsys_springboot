package com.dc.ncsys_springboot.service.impl;

import com.dc.ncsys_springboot.daoVo.TableDesignColumnDo;
import com.dc.ncsys_springboot.mapper.TableDesignColumnMapper;
import com.dc.ncsys_springboot.service.TableDesignColumnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.vo.ResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dc.ncsys_springboot.vo.SimpleTableDesign;

import java.util.*;

/**
 * <p>
 * 数据库设计表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-02 19:53
 */
@Service
public class TableDesignColumnServiceImpl extends ServiceImpl<TableDesignColumnMapper, TableDesignColumnDo> implements TableDesignColumnService {

    @Autowired
    private TableDesignColumnMapper tableDesignColumnMapper;


    @Override
    public ResVo getOption(String tableName, String columnName) {
        SimpleTableDesign column = tableDesignColumnMapper.getColumn(tableName, columnName);
        if (column == null) {
            return ResVo.fail("未找到对应表的对应列");
        }
        String columnComment = column.getColumnComment();
        if (columnComment.contains(":")) {
            String optionStr = columnComment.split(":")[1];
            String[] options = optionStr.split(",");
            if (optionStr.contains("-")) {
                Map<String, String> optionMap = new HashMap<>();
                for (String s : options) {
                    optionMap.put(s.split("-")[0], s.split("-")[1]);
                }
                return ResVo.success("获取列枚举成功", optionMap);
            } else {
                List<String> optionList = new ArrayList<>(Arrays.asList(options));
                return ResVo.success("获取列枚举成功", optionList);
            }
        } else {
            return ResVo.fail("该列不支持选项");
        }
    }
}
