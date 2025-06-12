package com.dc.ncsys_springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dc.ncsys_springboot.daoVo.*;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.TableDesignColumnMapper;
import com.dc.ncsys_springboot.mapper.TableDesignMapper;
import com.dc.ncsys_springboot.mapper.TableDesignSqlMapper;
import com.dc.ncsys_springboot.service.TableDesignColumnService;
import com.dc.ncsys_springboot.service.TableDesignService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.util.DateTimeUtil;
import com.dc.ncsys_springboot.util.FieldUtil;
import com.dc.ncsys_springboot.util.SessionUtils;
import com.dc.ncsys_springboot.util.StrUtils;
import com.dc.ncsys_springboot.vo.Field;
import com.dc.ncsys_springboot.vo.ResVo;
import com.mybatis_plus_generator.CodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dc.ncsys_springboot.vo.SimpleTableDesign;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Pattern;

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
    private TableDesignSqlMapper tableDesignSqlMapper;

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
        if (!validateMixedTableDesign(mixedTableDesign)) return ResVo.fail("表设计校验失败");
        saveMixedTableDesign(mixedTableDesign);
        return ResVo.success("保存表设计成功");
    }

    private void saveMixedTableDesign(MixedTableDesign mixedTableDesign) {
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
            throw new BusinessException("保存表设计失败");
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


    /**
     * 1. 获取SessionUser
     * 2. 表设计验证(如果TableId为空则赋值)
     * 3. 查看表是否已经存在
     * 4. 拼接建表SQL
     * 5. 建表
     * 6. 记录表设计SQL
     * 7. 更新混合表数据状态并落库
     * 8. 生成实体类
     * 9. 文件处理
     */
    @Override
    public ResVo createTableAndEntity(MixedTableDesign mixedTableDesign) {

        // 1. 获取SessionUser
        log.info("↓↓↓ 1. 获取SessionUser ↓↓↓");
        User sessionUser = SessionUtils.getSessionUser();
        String tableName = mixedTableDesign.getTableName();
        log.info("↑↑↑ 1. 获取SessionUser ↑↑↑");


        // 2. 表设计校验
        log.info("↓↓↓ 2. 表设计校验 ↓↓↓");
        if (!validateMixedTableDesign(mixedTableDesign)) return ResVo.fail("表设计校验失败");
        log.info("↑↑↑ 2. 表设计校验 ↑↑↑");

        // 3. 查看表是否已经存在
        log.info("↓↓↓ 3. 查看表是否已经存在 ↓↓↓");
        Boolean isTableExist = tableDesignMapper.isTableExist(tableName);
        if (isTableExist) {
            log.warn("当前表名的表已经存在, {}", tableName);
            return ResVo.fail("当前表名的表已经存在");
        }
        log.info("↑↑↑ 3. 查看表是否已经存在 ↑↑↑");


        // 4. 拼接建表SQL
        log.info("↓↓↓ 4. 拼接建表SQL ↓↓↓");
        StringBuilder sqlBuilder = new StringBuilder("CREATE TABLE `");
        sqlBuilder.append(tableName).append("` (");
        List<TableDesignColumnDo> listTableDesignColumn = mixedTableDesign.getList_tableDesignColumn();
        List<String> keys = new ArrayList<>();
        for (TableDesignColumnDo tableDesignColumnDo : listTableDesignColumn) {

            if (tableDesignColumnDo.getKeyYn().equals("Y")) {
                keys.add(tableDesignColumnDo.getColumnName());
            }

            sqlBuilder.append(System.lineSeparator());

            sqlAppendColumn(sqlBuilder, tableDesignColumnDo);
            sqlBuilder.append(",");
        }
        sqlBuilder.append(System.lineSeparator()).append("\t`data_status`\tchar(1)\tNOT NULL\tDEFAULT '0'\tCOMMENT '数据状态:0-未生效,1-生效,2-禁用,9-废弃',");
        sqlBuilder.append(System.lineSeparator()).append("\t`create_user`\tvarchar(8)\tNOT NULL\tCOMMENT '创建用户',");
        sqlBuilder.append(System.lineSeparator()).append("\t`create_time`\ttimestamp\tNOT NULL\tDEFAULT CURRENT_TIMESTAMP\tCOMMENT '创建时间',");
        sqlBuilder.append(System.lineSeparator()).append("\t`update_user`\tvarchar(8)\tNOT NULL\tCOMMENT '最后更新用户',");
        sqlBuilder.append(System.lineSeparator()).append("\t`update_time`\ttimestamp\tNOT NULL\tDEFAULT CURRENT_TIMESTAMP\tON UPDATE CURRENT_TIMESTAMP\tCOMMENT '最后更新时间'");

        if (!ObjectUtils.isEmpty(keys)) {
            sqlBuilder.append(",").append(System.lineSeparator()).append("\tPRIMARY KEY (`").append(String.join("`, `", keys)).append("`)");
        }

        sqlBuilder.append(System.lineSeparator()).append(") ENGINE=InnoDB COMMENT='").append(mixedTableDesign.getTableComment()).append("'");

        String sql = getSql(sqlBuilder);

        log.info("生成了建表SQL: {}", sql);

        log.info("↑↑↑ 4. 拼接建表SQL ↑↑↑");


        // 5. 建表
        log.info("↓↓↓ 5. 建表 ↓↓↓");
        try {
            tableDesignMapper.createTable(sql);
            log.info("建表成功");
        } catch (Exception e) {
            throw new BusinessException("建表时出现异常", e);
        }
        log.info("↑↑↑ 5. 建表 ↑↑↑");


        // 6. 记录表设计SQL
        log.info("↓↓↓ 6. 记录表设计SQL ↓↓↓");
        try {
            Map<String, Map<String, String>> lastCreateSqlMap = tableDesignMapper.showCreateTable(tableName);
            String lastCreateSql = lastCreateSqlMap.get(tableName).get("Create Table");
            TableDesignSqlDo tableDesignSqlDo = new TableDesignSqlDo();
            tableDesignSqlDo.setTableId(mixedTableDesign.getTableId()).setSqlType("TABLE_CREATE").setExecuteOrder(1);
            tableDesignSqlDo.setExecuteSql(sql);
            tableDesignSqlDo.setLastCreateSql(lastCreateSql).setDataStatus("1").setCreateUser(sessionUser.getLoginCode()).setUpdateUser(sessionUser.getLoginCode());
            tableDesignSqlMapper.insert(tableDesignSqlDo);
            log.info("记录表设计SQL成功");
        } catch (Exception e) {
            throw new BusinessException("建表成功, 但记录表设计SQL异常", e);
        }
        log.info("↑↑↑ 6. 记录表设计SQL ↑↑↑");

        // 7. 更新混合表数据状态并落库
        log.info("↓↓↓ 7. 更新混合表数据状态并落库 ↓↓↓");
        mixedTableDesign.setDataStatus("1");
        for (TableDesignColumnDo tableDesignColumnDo : mixedTableDesign.getList_tableDesignColumn()) {
            tableDesignColumnDo.setDataStatus("1");
        }
        saveMixedTableDesign(mixedTableDesign);
        log.info("表设计保存成功: 表名: {}", tableName);
        log.info("↑↑↑ 7. 更新混合表数据状态并落库 ↑↑↑");

        // 8. 生成实体类
        log.info("↓↓↓ 8. 生成实体类 ↓↓↓");
        CodeGenerator.generator(tableName);
        log.info("↑↑↑ 8. 生成实体类 ↑↑↑");

        // 9. 文件处理
        log.info("↓↓↓ 9. 文件处理 ↓↓↓");
        // 9.1 为Mapper.java添加@Mapper注解
        log.info("↓↓↓ 9.1. 为Mapper添加@Mapper注解 ↓↓↓");
        String generateMapperDir = "src/main/java/com/dc/ncsys_springboot/mapper/";
        String mapperName = StrUtils.underLine2BigCamel(tableName.substring(tableName.indexOf("_") + 1)) + "Mapper.java";
        Path mapperPath = Path.of(generateMapperDir, mapperName);
        List<String> mapperLines;
        try {
            mapperLines = Files.readAllLines(mapperPath);
            boolean hasMapperImport = mapperLines.stream()
                    .anyMatch(line -> line.contains("import org.apache.ibatis.annotations.Mapper;"));
            boolean hasMapperAnnotation = mapperLines.stream()
                    .anyMatch(line -> line.contains("@Mapper"));

            if (hasMapperImport || hasMapperAnnotation) {
                log.info("Mapper已有@Mapper注解, 无需添加");
            } else {
                log.info("Mapper没有@Mapper注解, 执行添加");
                // 添加import语句
                int importInsertIndex = findImportInsertIndex(mapperLines);
                mapperLines.add(importInsertIndex, "import org.apache.ibatis.annotations.Mapper;");

                // 添加注解
                int classDeclLine = findClassDeclarationLine(mapperLines);
                if (classDeclLine != -1) {
                    mapperLines.add(classDeclLine, "@Mapper");
                }
                // 写回文件
                Files.write(mapperPath, mapperLines);
                log.info("为Mapper添加@Mapper注解成功");
            }

        } catch (IOException e) {
            log.warn("为Mapper文件添加@Mapper注解出现异常: ", e);
            throw new BusinessException("为Mapper文件添加@Mapper注解出现异常");
        }
        log.info("↑↑↑ 9.1. 为Mapper添加@Mapper注解 ↑↑↑");


        // 9.2 将Mapper.xml移动到resources目录下
        log.info("↓↓↓ 9.2. 将Mapper.xml移动到resources目录下 ↓↓↓");
        moveMapperXml(tableName);
        log.info("↑↑↑ 9.2. 将Mapper.xml移动到resources目录下 ↑↑↑");


        // 9.3 如果keys>1, 则把Do后面的key的@TableId替换为@TableField
        log.info("↓↓↓ 9.3. 如果keys>1, 则把Do后面的key的@TableId替换为@TableField ↓↓↓");
        if (keys.size() > 1) {
            log.info("当前表包含多个key, 需要替换后面的@TableId为@TableField");
            String generateDoDir = "src/main/java/com/dc/ncsys_springboot/daoVo/";
            String doName = StrUtils.underLine2BigCamel(tableName.substring(tableName.indexOf("_") + 1)) + "Do.java";
            Path doPath = Path.of(generateDoDir, doName);
            List<String> doLines;
            try {
                doLines = Files.readAllLines(doPath);
                boolean hasTableFieldImport = doLines.stream()
                        .anyMatch(line -> line.contains("import com.baomidou.mybatisplus.annotation.TableField;"));

                // 添加TableField import（如果需要）
                if (!hasTableFieldImport) {
                    int importInsertIndex = findImportInsertIndex(doLines);
                    doLines.add(importInsertIndex, "import com.baomidou.mybatisplus.annotation.TableField;");
                }

                for (int k = 1; k < keys.size(); k++) {
                    String fieldName = keys.get(k);
                    // 替换注解
                    for (int i = 10; i < doLines.size() - 2; i++) {
                        String line = doLines.get(i);
                        if (doLines.get(i + 1).contains(" " + fieldName + ";") && line.contains("@TableId")) {
                            doLines.set(i, line.replace("@TableId", "@TableField"));
                        }
                    }
                }
                Files.write(doPath, doLines);
                log.info("当前表包含多个key, 需要替换后面的@TableId为@TableField, 替换成功");
            } catch (IOException e) {
                log.error("当前表包含多个key, 需要替换后面的@TableId为@TableField, 替换出现异常: ", e);
                throw new BusinessException("当前表包含多个key, 需要替换后面的@TableId为@TableField, 替换出现异常");
            }

        }
        log.info("↑↑↑ 9.3. 如果keys>1, 则把Do后面的key的@TableId替换为@TableField ↑↑↑");
        log.info("↑↑↑ 9. 文件处理 ↑↑↑");

        return ResVo.success("创建表和实体类成功");
    }

    private static void sqlAppendColumn(StringBuilder sqlBuilder, TableDesignColumnDo tableDesignColumnDo) {
        sqlBuilder.append("\t`").append(tableDesignColumnDo.getColumnName()).append("`\t").append(tableDesignColumnDo.getFieldType());

        if (ObjectUtils.isEmpty(tableDesignColumnDo.getFieldLength())) {
            sqlBuilder.append("\t");
        } else {
            sqlBuilder.append("(").append(tableDesignColumnDo.getFieldLength()).append(")\t");
        }

        if ("N".equals(tableDesignColumnDo.getNullAbleYn())) {
            sqlBuilder.append("NOT NULL\t");
        }

//            if (!ObjectUtils.isEmpty(tableDesignColumnDo.getDefaultValue())) {
//                sqlBuilder.append("DEFAULT '").append(tableDesignColumnDo.getDefaultValue()).append("'\t");
//            }

        sqlBuilder.append("COMMENT '").append(tableDesignColumnDo.getColumnComment());
        if (ObjectUtils.isEmpty(tableDesignColumnDo.getFieldEnumArray())) {
            sqlBuilder.append("'");
        } else {
            String join = String.join(",", tableDesignColumnDo.getFieldEnumArray());
            sqlBuilder.append(":").append(join).append("'");
        }

    }

    /**
     * 1. 获取SessionUser
     * 2. 字段设计验证
     * 3. 查看表是否已经存在+表名校验+字段重复校验
     * 4. 拼接 COLUMN_ADD SQL
     * 5. 执行SQL
     * 6. 记录表设计SQL
     * 7. 更新列数据状态并落库
     * 8. 生成实体类
     * 9. 文件处理
     */
    @Override
    public ResVo addColumn(TableDesignColumnDo tableDesignColumnDo) {
        // 1. 获取SessionUser
        log.info("↓↓↓ 1. 获取SessionUser ↓↓↓");
        User sessionUser = SessionUtils.getSessionUser();
        String tableName = tableDesignColumnDo.getTableName();
        log.info("↑↑↑ 1. 获取SessionUser ↑↑↑");


        // 2. 字段设计验证
        log.info("↓↓↓ 2. 字段设计验证 ↓↓↓");
        if (!validateTableDesignColumn(tableDesignColumnDo)) return ResVo.fail("字段设计校验失败");
        log.info("↑↑↑ 2. 字段设计验证 ↑↑↑");

        // 3. 查看表是否已经存在+表名校验+字段重复校验
        log.info("↓↓↓ 3. 查看表是否已经存在+表名校验+字段重复校验 ↓↓↓");

        Boolean isTableExist = tableDesignMapper.isTableExist(tableName);
        if (!isTableExist) {
            throw new BusinessException("校验拒绝", "表不存在");
        }

        TableDesignDo tableDesignDo = tableDesignMapper.selectById(tableDesignColumnDo);
        if (!tableDesignDo.getTableName().equals(tableName)) {
            throw new BusinessException("校验拒绝", "表名对不上");
        }

        List<TableDesignColumnDo> columnDoList = tableDesignColumnMapper.selectByTableId(tableDesignColumnDo.getTableId());
        for (TableDesignColumnDo column : columnDoList) {
            if (column.getColumnName().equals(tableDesignColumnDo.getColumnName())) {
                throw new BusinessException("同名的列已经存在", tableDesignColumnDo.getColumnName());
            }
        }

        log.info("↑↑↑ 3. 查看表是否已经存在+表名校验+字段重复校验 ↑↑↑");


        // 4. 拼接 COLUMN_ADD SQL
        log.info("↓↓↓ 4. 拼接 COLUMN_ADD SQL ↓↓↓");
        StringBuilder sqlBuilder = new StringBuilder("ALTER TABLE `");
        sqlBuilder.append(tableName).append("` ADD COLUMN");
        sqlAppendColumn(sqlBuilder, tableDesignColumnDo);

        String sql = getSql(sqlBuilder);

        log.info("生成了SQL: {}", sql);

        log.info("↑↑↑ 4. 拼接 COLUMN_ADD SQL ↑↑↑");

        // 5. 执行SQL
        log.info("↓↓↓ 5. 执行SQL ↓↓↓");
        try {
            tableDesignMapper.addColumn(sql);
            log.info("执行SQL成功");
        } catch (Exception e) {
            throw new BusinessException("执行SQL时出现异常", e);
        }
        log.info("↑↑↑ 5. 执行SQL ↑↑↑");


        // 6. 记录表设计SQL
        log.info("↓↓↓ 6. 记录表设计SQL ↓↓↓");
        try {
            Map<String, Map<String, String>> lastCreateSqlMap = tableDesignMapper.showCreateTable(tableName);
            String lastCreateSql = lastCreateSqlMap.get(tableName).get("Create Table");

            TableDesignSqlDo tableDesignSqlDo = new TableDesignSqlDo();
            tableDesignSqlDo.setTableId(tableDesignColumnDo.getTableId()).setSqlType("COLUMN_ADD");
            tableDesignSqlDo.setExecuteSql(sql);
            tableDesignSqlDo.setLastCreateSql(lastCreateSql).setDataStatus("1").setCreateUser(sessionUser.getLoginCode()).setUpdateUser(sessionUser.getLoginCode());
            int insertNum = tableDesignSqlMapper.insertNextRecord(tableDesignSqlDo);
            log.info("记录表设计SQL成功");
        } catch (Exception e) {
            throw new BusinessException("执行SQL成功, 但记录表设计SQL异常", e);
        }
        log.info("↑↑↑ 6. 记录表设计SQL ↑↑↑");


        // 7. 更新列数据状态并落库
        log.info("↓↓↓ 7. 更新列数据状态并落库 ↓↓↓");
        tableDesignColumnDo.setDataStatus("1");
        tableDesignColumnDo.setCreateUser(sessionUser.getLoginCode());
        tableDesignColumnDo.setUpdateUser(sessionUser.getLoginCode());
        tableDesignColumnMapper.insert(tableDesignColumnDo);
        log.info("表设计之列设计保存成功: 表名: {}", tableName);
        log.info("↑↑↑ 7. 更新列数据状态并落库 ↑↑↑");

        // 8. 生成实体类
        log.info("↓↓↓ 8. 生成实体类 ↓↓↓");
        CodeGenerator.generator(tableName);
        log.info("↑↑↑ 8. 生成实体类 ↑↑↑");

        // 9. 文件处理
        log.info("↓↓↓ 9. 文件处理 ↓↓↓");

        // 9.1. 将Mapper.xml移动到resources目录下
        log.info("↓↓↓ 9.1. 将Mapper.xml移动到resources目录下 ↓↓↓");
        moveMapperXml(tableName);
        log.info("↑↑↑ 9.1. 将Mapper.xml移动到resources目录下 ↑↑↑");


        // 9.3 把Do第1个以后的@TableId替换为@TableField
        log.info("↓↓↓ 9.3. 把Do第1个以后的@TableId替换为@TableField ↓↓↓");
        if (tableDesignColumnDo.getKeyYn().equals("Y")) {
            replaceTableId2TableFieldWhenMultiple(tableName);
        }
        log.info("↑↑↑ 9.3. 把Do第1个以后的@TableId替换为@TableField ↑↑↑");
        log.info("↑↑↑ 9. 文件处理 ↑↑↑");

        return ResVo.success("添加字段成功");

    }

    /**
     * 1 入参检查
     * 2 查询主数据 列数据和sql数据 验证数据状态 主数据为0 列数据为0 sql没有数据
     * 3 查询表是否存在 存在则拒绝
     * 4 删除主数据 删除列数据
     */
    @Override
    public ResVo deleteTableDesign(TableDesignDo inDo) {

        // 1 入参检查
        log.info("↓↓↓ 1 入参检查 ↓↓↓");
        String tableId = inDo.getTableId();
        if (ObjectUtils.isEmpty(tableId)) {
            return ResVo.fail("入参有误");
        }
        log.info("↑↑↑ 1 入参检查 ↑↑↑");

        // 2 查询主数据 列数据和sql数据 验证数据状态 主数据为0 列数据为0 sql没有数据
        log.info("↓↓↓ 2 查询主数据 列数据和sql数据 验证数据状态 主数据为0 列数据为0 sql没有数据 ↓↓↓");
        TableDesignDo tableDesignDo = tableDesignMapper.selectById(inDo);
        if (tableDesignDo == null) {
            return ResVo.fail("表设计主表不存在");
        }
        if (!"0".equals(tableDesignDo.getDataStatus())) {
            throw new BusinessException("状态异常", "主表状态不是0");
        }
        List<TableDesignColumnDo> tableDesignColumnDos = tableDesignColumnMapper.selectByTableId(tableId);
        if (!ObjectUtils.isEmpty(tableDesignColumnDos)) {
            for (TableDesignColumnDo tableDesignColumnDo : tableDesignColumnDos) {
                if (!"0".equals(tableDesignColumnDo.getDataStatus())) {
                    throw new BusinessException("状态异常", "列状态不是0");
                }
            }
        }
        List<TableDesignSqlDo> tableDesignSqlDos = tableDesignSqlMapper.selectByTableId(tableId);
        if (!ObjectUtils.isEmpty(tableDesignSqlDos)) {
            throw new BusinessException("状态异常", "有SQL记录");
        }
        log.info("↑↑↑ 2 查询主数据 列数据和sql数据 验证数据状态 主数据为0 列数据为0 sql没有数据 ↑↑↑");


        // 3 查询表是否存在 存在则拒绝
        log.info("↓↓↓ 4 查询表是否存在 存在则拒绝 ↓↓↓");
        Boolean isTableExist = tableDesignMapper.isTableExist(tableDesignDo.getTableName());
        if (isTableExist) {
            throw new BusinessException("状态异常", "表已存在");
        }
        log.info("↑↑↑ 4 查询表是否存在 存在则拒绝 ↑↑↑");


        // 4 删除主数据 删除列数据
        log.info("↓↓↓ 5 删除主数据 删除列数据 ↓↓↓");
        tableDesignMapper.deleteById(tableDesignDo);
        tableDesignColumnMapper.deleteByTableId(tableId);
        log.info("↑↑↑ 5 删除主数据 删除列数据 ↑↑↑");

        return ResVo.success("删除表设计成功");
    }

    private static void replaceTableId2TableFieldWhenMultiple(String tableName) {
        String generateDoDir = "src/main/java/com/dc/ncsys_springboot/daoVo/";
        String doName = StrUtils.underLine2BigCamel(tableName.substring(tableName.indexOf("_") + 1)) + "Do.java";
        Path doPath = Path.of(generateDoDir, doName);
        List<String> doLines;
        try {
            doLines = Files.readAllLines(doPath);
            boolean hasTableFieldImport = doLines.stream()
                    .anyMatch(line -> line.contains("import com.baomidou.mybatisplus.annotation.TableField;"));

            // 添加TableField import（如果需要）
            if (!hasTableFieldImport) {
                int importInsertIndex = findImportInsertIndex(doLines);
                doLines.add(importInsertIndex, "import com.baomidou.mybatisplus.annotation.TableField;");
            }
            
            // 替换注解
            int tableIdIndex = 0;
            for (int i = 10; i < doLines.size() - 2; i++) {
                String line = doLines.get(i);
                if (line.contains("@TableId")) {
                    if ( ++tableIdIndex > 1) {
                        doLines.set(i, line.replace("@TableId", "@TableField"));
                    }
                }
            }
            Files.write(doPath, doLines);
            log.info("当前表包含多个key, 需要替换后面的@TableId为@TableField, 替换成功");
        } catch (IOException e) {
            log.error("当前表包含多个key, 需要替换后面的@TableId为@TableField, 替换出现异常: ", e);
            throw new BusinessException("当前表包含多个key, 需要替换后面的@TableId为@TableField, 替换出现异常");
        }
    }

    private static void moveMapperXml(String tableName) {
        String generateMapperXmlDir = "src/main/java/com/dc/ncsys_springboot/mapper/xml/";
        String targetMapperXmlDir = "src/main/resources/com/dc/ncsys_springboot/mapper/xml/";
        String mapperXmlName = StrUtils.underLine2BigCamel(tableName.substring(tableName.indexOf("_") + 1)) + "Mapper.xml";
        Path source = Path.of(generateMapperXmlDir, mapperXmlName);
        Path target = Path.of(targetMapperXmlDir).resolve(mapperXmlName);
        // 移动文件
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("移动代码生成器生成的Mapper.xml文件成功");
        } catch (IOException e) {
            throw new BusinessException("移动代码生成器生成的Mapper.xml文件出现异常", e);
        }
    }

    private static String getSql(StringBuilder sqlBuilder) {
        String sql = sqlBuilder.toString();

        Set<String> dangerWordsSet = Set.of(" select ", " * ", " from ", " where "
                , " set "
                , " delete "
                , " drop ", " truncate ");


        for (String word : dangerWordsSet) {
            if (sql.toLowerCase().contains(word.toLowerCase())) {
                throw new BusinessException("校验拒绝", "SQL中存在危险字符");
            }
        }
        return sql;
    }


    private final String COLUMN_NAME_REG = "^[a-z][a-z0-9]*(_[a-z0-9]+)*$";
    private final Pattern PATTERN_NOT_IN_COLUMN_COMMENT = Pattern.compile("[ ,:]"); // 匹配空格、冒号、逗号
    private final Set<String> YN_SET = Set.of("Y", "N");
    private final Set<String> FIELD_TYPE_SET = Set.of("varchar", "char", "int", "timestamp", "TEXT", "BLOB", "JSON");
    private final Set<String> NEED_LENGTH_SET = Set.of("varchar", "char");
    private final Set<String> CAN_ENUM_SET = Set.of("varchar", "char");
    private final Pattern PATTERN_NOT_IN_FIELD_ENUM = Pattern.compile("[ ,:;]"); // 匹配空格、冒号、逗号


    private boolean validateMixedTableDesign(MixedTableDesign mixedTableDesign) {

        // 如果没有TableId则生成一个
        String newTableId = "";
        String tableName = mixedTableDesign.getTableName();
        if (ObjectUtils.isEmpty(mixedTableDesign.getTableId())) {
            log.info("SVC混合表设计校验: 当前入参没有tableId, 表名: {}", tableName);
            newTableId = tableName + "_" + DateTimeUtil.getMinuteKey();
            mixedTableDesign.setTableId(newTableId);
        }

        // 表类型检查
        String tableType = mixedTableDesign.getTableType();
        Set<String> tableTypeSet = Set.of("s", "t", "l", "m", "ts");
        if (ObjectUtils.isEmpty(tableType)) {
            throw new BusinessException("校验拒绝", "校验拒绝 表类型 为空");
        }
        if (!tableTypeSet.contains(tableType)) {
            throw new BusinessException("校验拒绝", "校验拒绝 表类型不合法: " + tableType);
        }


        // 表名前缀检查
        String preTableName = mixedTableDesign.getPre_tableName();
        if (ObjectUtils.isEmpty(preTableName)) {
            throw new BusinessException("校验拒绝", "校验拒绝 表名前缀 为空");
        }
        if (!preTableName.equals(tableType + "_")) {
            throw new BusinessException("校验拒绝", "校验拒绝 表名前缀: " + preTableName);
        }

        // 表名后段检查
        String subTableName = mixedTableDesign.getSub_tableName();
        String subTableNameReg = "^[a-z0-9]+(_[a-z0-9]+)*$";
        if (ObjectUtils.isEmpty(subTableName)) {
            throw new BusinessException("校验拒绝", "校验拒绝 表名后段 为空");
        }
        if (!subTableName.matches(subTableNameReg)) {
            throw new BusinessException("校验拒绝", "校验拒绝 表名后段: " + subTableName + " 字符匹配");
        }
        if (subTableName.length() > 40) {
            throw new BusinessException("校验拒绝", "校验拒绝 表名后段: 长度大于40 " + subTableName);
        }
        if (subTableName.length() < 5) {
            throw new BusinessException("校验拒绝", "校验拒绝 表名后段: 长度小于5 " + subTableName);
        }

        // 表名检查
        // 正则表达式解释：
        // ^                  : 字符串开始
        // (s_|t_|l_|m_|ts_)  : 匹配指定前缀
        // ([a-zA-Z0-9]+      : 必须以字母/数字开头，避免连续下划线
        // (?:_[a-zA-Z0-9]+)* : 允许下划线，但必须后跟字母/数字（防止连续下划线）
        // )?                 : 中间部分可选（允许如"s_a"的短格式）
        // [a-zA-Z0-9]$       : 结尾必须是字母/数字（非下划线）

        // 表名检查
//        String tableNameReg = "^(s_|t_|l_|m_|ts_)([a-zA-Z0-9]+(?:_[a-zA-Z0-9]+)*)?[a-zA-Z0-9]$";
//        String tableNameReg = "^(s_|t_|l_|m_|ts_)([a-zA-Z0-9]+)(_[a-zA-Z0-9]+)*$";
//        String tableNameReg = "^(s_|t_|l_|m_|ts_)([a-zA-Z0-9](?:[a-zA-Z0-9]|_(?=[a-zA-Z0-9]))){4,}[a-zA-Z0-9]$";
        if (ObjectUtils.isEmpty(tableName)) {
            throw new BusinessException("校验拒绝", "校验拒绝 表名 为空");
        }
        if (!tableName.equals(preTableName + subTableName)) {
            throw new BusinessException("校验拒绝", "校验拒绝 表名: 不等于表名前缀+表名后段" + tableName);
        }

        // 表注释检查
        String tableComment = mixedTableDesign.getTableComment();
        if (ObjectUtils.isEmpty(tableComment)) {
            log.info("校验拒绝 表注释 为空");
            return false;
        }
        if (tableComment.contains(" ")) {
            log.info("校验拒绝 表注释: {} 有空格", tableComment);
            return false;
        }
        if (tableComment.length() > 40) {
            log.info("校验拒绝 表注释: {} 长度大于40", tableComment);
            return false;
        }

        // 字段检查
        List<TableDesignColumnDo> listTableDesignColumn = mixedTableDesign.getList_tableDesignColumn();
        if (ObjectUtils.isEmpty(listTableDesignColumn)) {
            throw new BusinessException("表设计中至少需要包含一个字段, 请添加字段");
        }


        Set<String> columnNameSet = new HashSet<>();

        for (TableDesignColumnDo tableDesignColumnDo : listTableDesignColumn) {
            // tableId
            if (!ObjectUtils.isEmpty(newTableId)) {
                if (!ObjectUtils.isEmpty(tableDesignColumnDo.getTableId())) {
                    throw new BusinessException("校验拒绝", "入参表设计没有tableId但列中有tableId");
                }
                tableDesignColumnDo.setTableId(newTableId);
            }

            if (!tableDesignColumnDo.getTableId().equals(mixedTableDesign.getTableId())) {
                throw new BusinessException("校验拒绝", "表设计tableId与列tableId不一致");
            }

            String columnName = tableDesignColumnDo.getColumnName();
            if (columnNameSet.contains(columnName)) {
                throw new BusinessException("字段名重复" + columnName);
            }
            columnNameSet.add(columnName);

            //dataStatus
            String dataStatus = tableDesignColumnDo.getDataStatus();
            if (!"0".equals(dataStatus)) {
                log.info("校验拒绝 dataStatus: {} 列状态不是待创建", dataStatus);
                return false;
            }

            if (!validateTableDesignColumn(tableDesignColumnDo)) return false;

        }

        return true;

    }

    private boolean validateTableDesignColumn(TableDesignColumnDo tableDesignColumnDo) {
        // tableId
        String tableId = tableDesignColumnDo.getTableId();
        if (ObjectUtils.isEmpty(tableId)) {
            throw new BusinessException("校验拒绝", "tableId 为空");
        }

        //fieldIndex 不校验, 这个字段后续就没用了
        //columnName
        String columnName = tableDesignColumnDo.getColumnName();
        if (ObjectUtils.isEmpty(columnName)) {
            throw new BusinessException("校验拒绝", "columnName 为空");
        }
        if (!columnName.matches(COLUMN_NAME_REG)) {
            throw new BusinessException("校验拒绝", "columnName 正则不匹配: " + columnName);
        }
        if (columnName.length() > 40) {
            throw new BusinessException("校验拒绝", "columnName 长度大于40: " + columnName);
        }


        //columnComment
        String columnComment = tableDesignColumnDo.getColumnComment();
        if (ObjectUtils.isEmpty(columnComment)) {
            throw new BusinessException("校验拒绝", "columnComment 为空");
        }
        if (PATTERN_NOT_IN_COLUMN_COMMENT.matcher(columnComment).find()) {
            throw new BusinessException("校验拒绝", "columnComment 有禁止字符: " + columnComment);
        }
        if (columnComment.length() > 40) {
            throw new BusinessException("校验拒绝", "columnComment 长度大于40: " + columnComment);
        }

        //keyYn
        String keyYn = tableDesignColumnDo.getKeyYn();
        if (ObjectUtils.isEmpty(keyYn)) {
            throw new BusinessException("校验拒绝", "keyYn 为空");
        }
        if (!YN_SET.contains(keyYn)) {
            throw new BusinessException("校验拒绝", "keyYn 非允许的值: " + keyYn);
        }

        //nullAbleYn
        String nullAbleYn = tableDesignColumnDo.getNullAbleYn();
        if (ObjectUtils.isEmpty(nullAbleYn)) {
            throw new BusinessException("校验拒绝", "nullAbleYn 为空");
        }
        if (!YN_SET.contains(nullAbleYn)) {
            throw new BusinessException("校验拒绝", "nullAbleYn 非允许的值: " + nullAbleYn);
        }
        if (keyYn.equals("Y") && nullAbleYn.equals("Y")) {
            throw new BusinessException("校验拒绝", "nullAbleYn 主键但可以空: " + nullAbleYn);
        }

        //fieldType
        String fieldType = tableDesignColumnDo.getFieldType();
        if (ObjectUtils.isEmpty(fieldType)) {
            throw new BusinessException("校验拒绝", "fieldType 为空");
        }
        if (!FIELD_TYPE_SET.contains(fieldType)) {
            throw new BusinessException("校验拒绝", "fieldType 非允许的值: " + fieldType);
        }


        //fieldLength
        Integer fieldLength = tableDesignColumnDo.getFieldLength();
        if (NEED_LENGTH_SET.contains(fieldType)) {
            if (fieldLength == null || fieldLength < 1) {
                throw new BusinessException("校验拒绝", "fieldLength 需要长度的字段没有长度: " + fieldLength);
            }
        } else {
            if (fieldLength != null) {
                throw new BusinessException("校验拒绝", "fieldLength 不需要长度的字段送来长度: " + fieldLength);
            }
        }

        //fieldEnum
        String fieldEnum = tableDesignColumnDo.getFieldEnum();
        if (!CAN_ENUM_SET.contains(fieldType)) {
            if (!ObjectUtils.isEmpty(fieldEnum)) {
                throw new BusinessException("校验拒绝", "fieldEnum 不可以枚举的字段送来枚举: " + fieldEnum);
            }
        }

        if (!ObjectUtils.isEmpty(fieldEnum)) {
            //fieldEnumArray
            List<String> fieldEnumArray = tableDesignColumnDo.getFieldEnumArray();
            if (ObjectUtils.isEmpty(fieldEnumArray)) {
                throw new BusinessException("校验拒绝", "fieldEnumArray 有枚举但没有枚举数组: " + fieldEnumArray);
            }

            Set<String> singleSet = new HashSet<>();
            Set<String> keySet = new HashSet<>();
            Set<String> valueSet = new HashSet<>();
            for (String oneEnum : fieldEnumArray) {
                if (ObjectUtils.isEmpty(oneEnum)) {
                    throw new BusinessException("校验拒绝", "oneEnum 为空");
                }
                if (PATTERN_NOT_IN_FIELD_ENUM.matcher(oneEnum).find()) {
                    throw new BusinessException("校验拒绝", "oneEnum 有禁止字符: " + oneEnum);
                }
                if (oneEnum.contains("-")) {
                    String[] split = oneEnum.split("-");
                    if (split.length != 2) {
                        throw new BusinessException("校验拒绝", "oneEnum key-value枚举格式不正确: " + oneEnum);
                    }
                    String key = split[0];
                    if (fieldLength != null && key.length() > fieldLength) {
                        throw new BusinessException("校验拒绝", "oneEnum key-value枚举key长度大于字段长度: " + oneEnum);
                    }
                    keySet.add(key);
                    valueSet.add(split[1]);
                } else {
                    if (fieldLength != null && oneEnum.length() > fieldLength) {
                        throw new BusinessException("校验拒绝", "oneEnum single枚举长度大于字段长度: " + oneEnum);
                    }
                    singleSet.add(oneEnum);
                }
            }

            if (singleSet.size() != 0) {
                log.info("当前是single枚举");
                if (singleSet.size() != fieldEnumArray.size()) {
                    throw new BusinessException("校验拒绝", "fieldEnumArray single枚举数量不正确: " + fieldEnumArray);
                }
                if (keySet.size() != 0 || valueSet.size() != 0) {
                    throw new BusinessException("校验拒绝", "fieldEnumArray 既有single枚举又有key-value枚举: " + fieldEnumArray);
                }
            } else {
                log.info("当前是key-value枚举");
                if (keySet.size() != fieldEnumArray.size() || valueSet.size() != fieldEnumArray.size()) {
                    throw new BusinessException("校验拒绝", "fieldEnumArray key-value枚举数量不正确: " + fieldEnumArray);
                }
            }
        }


        //defaultValue
        String defaultValue = tableDesignColumnDo.getDefaultValue();
        if (!ObjectUtils.isEmpty(defaultValue)) {
            if (defaultValue.length() > 2) {
                if (defaultValue.contains(" ")) {
                    throw new BusinessException("校验拒绝", "defaultValue 长度大于2的默认值有空格: " + defaultValue);
                }
                if (defaultValue.length() > 17) {
                    throw new BusinessException("校验拒绝", "defaultValue 默认值长度大于17: " + defaultValue);
                }
            }
        }
        return true;
    }

    // 辅助方法：查找import插入位置
    private static int findImportInsertIndex(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("import ") || line.startsWith("package ")) {
                continue;
            }
            if (line.isEmpty()) {
                return i;
            }
            break;
        }
        return 1; // 默认插入位置在package之后
    }

    // 辅助方法：查找类声明行
    private static int findClassDeclarationLine(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("public class ") ||
                    line.startsWith("public interface ") ||
                    line.startsWith("class ") ||
                    line.startsWith("interface ")) {
                return i;
            }
        }
        return -1;
    }
}
