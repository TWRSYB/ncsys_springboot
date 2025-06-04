package com.dc.ncsys_springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dc.ncsys_springboot.daoVo.MixedTableDesign;
import com.dc.ncsys_springboot.daoVo.TableDesign;
import com.dc.ncsys_springboot.mapper.TableDesignMapper;
import com.dc.ncsys_springboot.service.TableDesignService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.util.FieldUtil;
import com.dc.ncsys_springboot.vo.Field;
import com.dc.ncsys_springboot.vo.ResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dc.ncsys_springboot.vo.SimpleTableDesign;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 数据库设计表 服务实现类
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-02 11:08
 */
@Service
public class TableDesignServiceImpl extends ServiceImpl<TableDesignMapper, TableDesign> implements TableDesignService {

    @Autowired
    private  TableDesignMapper tableDesignMapper;

    @Override
    public ResVo getTableDesignList() {
        List<TableDesign> tableDesigns = tableDesignMapper.selectList(new QueryWrapper<>());
        return ResVo.success("获取表设计成功", tableDesigns);
    }

    @Override
    public ResVo getTableDesign(String tableName) {

        List<Field> Fields = new ArrayList<>();
        List<SimpleTableDesign> simpleTableDesigns = tableDesignMapper.getTableDesign(tableName);
        // 遍历字段
        for (SimpleTableDesign simpleTableDesign : simpleTableDesigns) {
            int ordinalPosition = simpleTableDesign.getOrdinalPosition();
            String columnName = FieldUtil.underLineToCamel(simpleTableDesign.getColumnName());
            String columnComment = simpleTableDesign.getColumnComment();
            String type = "";   //"text" input; "lv" 页面铺数字 [0-未收录,1-xx,2-xx,3-xx,4-xx,5-xx];  "type" selection [未收录, xx, xx, xx, xx]; "YMD" dataPicker;
            ArrayList<String> types = new ArrayList<>();
            HashMap<String, String> lvs = new HashMap<>();
            String columnDefault = simpleTableDesign.getColumnDefault();

            String[] split = columnComment.split(":");
            columnComment = split[0];
            if (columnName.endsWith("Ymd")) {
                type = "ymd";
            } else if (columnName.startsWith("ms")) {
                type = "textarea";
            } else if (columnName.endsWith("Time")) {
                type = "null";
            } else if (split.length > 1 && split[1].contains(",")) {
                String[] split1 = split[1].split(",");
                if (split[1].contains("-")) {
                    type = "lv";
                    for (String s : split1) {
                        String[] split2 = s.split("-");
                        lvs.put(split2[0].trim(), split2[1].trim());
                    }
                } else {
                    type = "type";
                    for (String s : split1) {
                        types.add(s.trim());
                    }
                }

            } else {
                type = "text";
            }
            Field field = new Field(ordinalPosition, columnName, columnComment, type, lvs, types, columnDefault);
            Fields.add(field);
        }
        return ResVo.success("获取表设计成功", Fields);
    }

    @Override
    public ResVo saveTableDesign(MixedTableDesign mixedTableDesign) {
        return null;
    }
}
