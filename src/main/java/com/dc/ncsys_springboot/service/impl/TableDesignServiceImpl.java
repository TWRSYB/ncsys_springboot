package com.dc.ncsys_springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dc.ncsys_springboot.daoVo.MixedTableDesign;
import com.dc.ncsys_springboot.daoVo.TableDesignColumnDo;
import com.dc.ncsys_springboot.daoVo.TableDesignDo;
import com.dc.ncsys_springboot.daoVo.User;
import com.dc.ncsys_springboot.mapper.TableDesignColumnMapper;
import com.dc.ncsys_springboot.mapper.TableDesignMapper;
import com.dc.ncsys_springboot.service.TableDesignColumnService;
import com.dc.ncsys_springboot.service.TableDesignService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.util.DateTimeUtil;
import com.dc.ncsys_springboot.util.FieldUtil;
import com.dc.ncsys_springboot.util.SessionUtils;
import com.dc.ncsys_springboot.vo.Field;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dc.ncsys_springboot.vo.SimpleTableDesign;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;


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
@Slf4j
@Service
@Transactional
public class TableDesignServiceImpl extends ServiceImpl<TableDesignMapper, TableDesignDo> implements TableDesignService {

    @Autowired
    private TableDesignMapper tableDesignMapper;

    @Autowired
    private TableDesignColumnMapper tableDesignColumnMapper;

    @Autowired
    private TableDesignColumnService tableDesignColumnService;

    @Override
    public ResVo getTableDesignList() {
        List<TableDesignDo> tableDesigns = tableDesignMapper.selectList(new QueryWrapper<>());
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
        User sessionUser = SessionUtils.getSessionUser();
        String nowUserLoginCode = sessionUser.getLoginCode();
        if (ObjectUtils.isEmpty(mixedTableDesign.getTableId())) {
            log.info("SVC保存表设计: 当前入参没有tableId, 表名: {}", mixedTableDesign.getTableName());
            mixedTableDesign.setCreateUser(nowUserLoginCode);
            mixedTableDesign.setTableId(mixedTableDesign.getTableName() + "_" + DateTimeUtil.getMinuteKey());
        }

        mixedTableDesign.setUpdateUser(nowUserLoginCode);
        boolean insertOrUpdateTableDesign = tableDesignMapper.insertOrUpdate(mixedTableDesign);
        if (!insertOrUpdateTableDesign) {
            return ResVo.fail("保存表设计失败");
        }

        // 删除表设计列后再插入
        LambdaQueryWrapper<TableDesignColumnDo> tdcQueryWrapper = new LambdaQueryWrapper<>();
        tdcQueryWrapper.eq(TableDesignColumnDo::getTableId, mixedTableDesign.getTableId());
        int delete = tableDesignColumnMapper.delete(tdcQueryWrapper);
        log.info("SVC保存表设计: 重新插入列之前删除了{}个列", delete);

        List<TableDesignColumnDo> listTableDesignColumn = mixedTableDesign.getList_tableDesignColumn();
        int fieldIndex = 0;
        for (TableDesignColumnDo tableDesignColumn : listTableDesignColumn) {
            if (ObjectUtils.isEmpty(tableDesignColumn.getTableId())) {
                log.info("SVC保存表设计: 当前列没有tableId, 列名: {}", tableDesignColumn.getColumnName());
                tableDesignColumn.setCreateUser(nowUserLoginCode);
                tableDesignColumn.setTableId(mixedTableDesign.getTableId());
            }
            tableDesignColumn.setFieldIndex(++fieldIndex);
            tableDesignColumn.setUpdateUser(nowUserLoginCode);
            tableDesignColumnMapper.insert(tableDesignColumn);
            log.info("SVC保存表设计: 保存列完成, 列名: {}", tableDesignColumn.getColumnName());
        }

        return ResVo.success("保存表设计成功");
    }



    @Override
    public ResVo getTableDesignDetail(String tableName) {
        MixedTableDesign mixedTableDesign = new MixedTableDesign();
        LambdaQueryWrapper<TableDesignDo> tdQueryWrapper = new LambdaQueryWrapper<>();
        tdQueryWrapper.eq(TableDesignDo::getTableName, tableName);
        TableDesignDo tableDesignDo = tableDesignMapper.selectOne(tdQueryWrapper);
        if (ObjectUtils.isEmpty(tableDesignDo)) {
            return ResVo.fail("未能查询到当前表的详细设计信息");
        }
        BeanUtils.copyProperties(tableDesignDo, mixedTableDesign);

        List<TableDesignColumnDo> tableDesignColumnDos = tableDesignColumnMapper.getByTableId(mixedTableDesign.getTableId());
        mixedTableDesign.setList_tableDesignColumn(tableDesignColumnDos);

        return ResVo.success("获取表详细设计成功", mixedTableDesign);
    }
}
