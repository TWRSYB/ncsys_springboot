package com.dc.ncsys_springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dc.ncsys_springboot.daoVo.*;
import com.dc.ncsys_springboot.exception.BusinessException;
import com.dc.ncsys_springboot.mapper.TableDesignColumnMapper;
import com.dc.ncsys_springboot.mapper.TableDesignMapper;
import com.dc.ncsys_springboot.mapper.TableDesignSqlMapper;
import com.dc.ncsys_springboot.mapper.TableDesignUniqueKeyMapper;
import com.dc.ncsys_springboot.service.TableDesignColumnService;
import com.dc.ncsys_springboot.service.TableDesignService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.ncsys_springboot.util.*;
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
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private TableDesignUniqueKeyMapper tableDesignUniqueKeyMapper;

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

    /**
     * 1_校验表设计
     * 2_保存表设计
     */
    @Override
    public ResVo saveTableDesign(MixedTableDesign mixedTableDesign) {
        if (!validateMixedTableDesign(mixedTableDesign)) return ResVo.fail("表设计校验失败");
        saveMixedTableDesign(mixedTableDesign);
        return ResVo.success("保存表设计成功");
    }

    /**
     * 1_获取当前登录账号
     * 2_保存或更新表数据主表
     * 3_删除表设计列后再插入
     * 4_删除表设计唯一约束后再插入
     */
    private void saveMixedTableDesign(MixedTableDesign mixedTableDesign) {

        // 1_获取当前登录账号
        log.info("↓↓↓ 1_获取当前登录账号 ↓↓↓");
        User sessionUser = SessionUtils.getSessionUser();
        String nowUserLoginCode = sessionUser.getLoginCode();
        log.info("↑↑↑ 1_获取当前登录账号 ↑↑↑");

        // 2_保存或更新表数据主表
        log.info("↓↓↓ 2_保存或更新表数据主表 ↓↓↓");
        mixedTableDesign.setUpdateUser(nowUserLoginCode);
        boolean insertOrUpdateTableDesign = tableDesignMapper.insertOrUpdate(mixedTableDesign);
        if (!insertOrUpdateTableDesign) {
            throw new BusinessException("保存表设计失败");
        }
        log.info("↑↑↑ 2_保存或更新表数据主表 ↑↑↑");

        // 3_删除表设计列后再插入
        log.info("↓↓↓ 3_删除表设计列后再插入 ↓↓↓");
        LambdaQueryWrapper<TableDesignColumnDo> tdcQueryWrapper = new LambdaQueryWrapper<>();
        tdcQueryWrapper.eq(TableDesignColumnDo::getTableId, mixedTableDesign.getTableId());
        int delete = tableDesignColumnMapper.delete(tdcQueryWrapper);
        log.info("SVC保存表设计: 重新插入列之前删除了{}个列", delete);

        List<TableDesignColumnDo> listTableDesignColumn = mixedTableDesign.getList_tableDesignColumn();
        int fieldIndex = 0;
        for (TableDesignColumnDo tableDesignColumn : listTableDesignColumn) {
            tableDesignColumn.setFieldIndex(++fieldIndex);
            tableDesignColumn.setUpdateUser(nowUserLoginCode);
            tableDesignColumnMapper.insert(tableDesignColumn);
            log.info("SVC保存表设计: 保存列完成, 列名: {}", tableDesignColumn.getColumnName());
        }
        log.info("↑↑↑ 3_删除表设计列后再插入 ↑↑↑");

        // 4_删除表设计唯一约束后再插入
        log.info("↓↓↓ 4_删除表设计唯一约束后再插入 ↓↓↓");
        LambdaQueryWrapper<TableDesignUniqueKeyDo> tdukQueryWrapper = new LambdaQueryWrapper<>();
        tdukQueryWrapper.eq(TableDesignUniqueKeyDo::getTableId, mixedTableDesign.getTableId());
        int deleteTduk = tableDesignUniqueKeyMapper.delete(tdukQueryWrapper);
        log.info("SVC保存表设计: 重新插入列之前删除了{}个唯一约束", deleteTduk);

        List<TableDesignUniqueKeyDo> listUniqueKey = mixedTableDesign.getList_uniqueKey();
        for (TableDesignUniqueKeyDo tableDesignUniqueKeyDo : listUniqueKey) {
            tableDesignUniqueKeyDo.setUpdateUser(nowUserLoginCode);
            tableDesignUniqueKeyMapper.insert(tableDesignUniqueKeyDo);
            log.info("SVC保存表设计: 保存唯一约束完成, 列名: {}", tableDesignUniqueKeyDo.getUniqueKeyName());
        }
        log.info("↑↑↑ 4_删除表设计唯一约束后再插入 ↑↑↑");

    }

    /**
     * 1 查询主表数据
     * 2 查询列列表
     * 3 查询最后SQL
     * 4 查询唯一约束列表
     */
    @Override
    public ResVo getTableDesignDetail(String tableName) {

        MixedTableDesign mixedTableDesign = new MixedTableDesign();
        // 1 查询主表数据
        log.info("↓↓↓ 1 查询主表数据 ↓↓↓");
        LambdaQueryWrapper<TableDesignDo> tdQueryWrapper = new LambdaQueryWrapper<>();
        tdQueryWrapper.eq(TableDesignDo::getTableName, tableName);
        TableDesignDo tableDesignDo = tableDesignMapper.selectOne(tdQueryWrapper);
        if (ObjectUtils.isEmpty(tableDesignDo)) {
            return ResVo.fail("未能查询到当前表的详细设计信息");
        }
        BeanUtils.copyProperties(tableDesignDo, mixedTableDesign);
        log.info("↑↑↑ 1 查询主表数据 ↑↑↑");

        // 2 查询列列表
        log.info("↓↓↓ 2 查询列列表 ↓↓↓");
        List<TableDesignColumnDo> tableDesignColumnDos = tableDesignColumnMapper.getByTableId(mixedTableDesign.getTableId());
        mixedTableDesign.setList_tableDesignColumn(tableDesignColumnDos);
        log.info("↑↑↑ 2 查询列列表 ↑↑↑");

        // 3 查询最后SQL
        log.info("↓↓↓ 3 查询最后SQL ↓↓↓");
        TableDesignSqlDo tableDesignSqlDo = tableDesignSqlMapper.selectLast(mixedTableDesign.getTableId());
        mixedTableDesign.setLast_tableDesignSql(tableDesignSqlDo);
        log.info("↑↑↑ 3 查询最后SQL ↑↑↑");

        // 4 查询唯一约束列表
        log.info("↓↓↓ 4 查询唯一约束列表 ↓↓↓");
        List<TableDesignUniqueKeyDo> tableDesignUniqueKeyDos = tableDesignUniqueKeyMapper.selectByTableId(mixedTableDesign.getTableId());
        mixedTableDesign.setList_uniqueKey(tableDesignUniqueKeyDos);
        log.info("↑↑↑ 4 查询唯一约束列表 ↑↑↑");

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

        List<TableDesignUniqueKeyDo> listUniqueKey = mixedTableDesign.getList_uniqueKey();
        if (!ObjectUtils.isEmpty(listUniqueKey)) {
            for (TableDesignUniqueKeyDo tableDesignUniqueKeyDo : listUniqueKey) {
                sqlBuilder.append(",").append(System.lineSeparator()).append("\tUNIQUE KEY ").append(tableDesignUniqueKeyDo.getUniqueKeyName()).append(" (`").append(String.join("`, `", tableDesignUniqueKeyDo.getUniqueKeyColumnArray())).append("`)");
            }
        }


        sqlBuilder.append(System.lineSeparator()).append(") ENGINE=InnoDB COMMENT='").append(mixedTableDesign.getTableComment()).append("'");

        String sql = getSql(sqlBuilder, null);

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

        for (TableDesignUniqueKeyDo tableDesignUniqueKeyDo : listUniqueKey) {
            tableDesignUniqueKeyDo.setDataStatus("1");
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
                    log.info("为第 {} 个key {} 替换注解", k + 1, fieldName);
                    // 替换注解
                    for (int i = 10; i < doLines.size() - 2; i++) {
                        String line = doLines.get(i);
                        if (line.contains("@TableId")) {
                            String nextLine = doLines.get(i + 1);
                            log.info("在第 {} 行扫描的@TableId注解: {}, 其下一行内容为: {}", i + 1, line, nextLine);
                            if (nextLine.contains(" " + StrUtils.underLine2Camel(fieldName) + ";")) {
                                log.info("找到key {} 了, 为其替换注解", fieldName);
                                doLines.set(i, line.replace("@TableId", "@TableField"));
                            }
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

        String sql = getSql(sqlBuilder, null);

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

        return ResVo.success("添加字段成功", tableDesignColumnDo);

    }

    /**
     * 1 入参检查
     * 2 查询主数据 列数据和sql数据 验证数据状态 主数据为0 列数据为0 唯一约束为0 sql没有数据
     * 3 查询表是否存在 存在则拒绝
     * 4 删除主数据 删除列数据 删除唯一约束数据
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

        List<TableDesignUniqueKeyDo> tableDesignUniqueKeyDos = tableDesignUniqueKeyMapper.selectByTableId(tableId);
        if (!ObjectUtils.isEmpty(tableDesignUniqueKeyDos)) {
            for (TableDesignUniqueKeyDo tableDesignUniqueKeyDo : tableDesignUniqueKeyDos) {
                if (!"0".equals(tableDesignUniqueKeyDo.getDataStatus())) {
                    throw new BusinessException("状态异常", "唯一约束状态不是0");
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
        tableDesignUniqueKeyMapper.deleteByTableId(tableId);
        log.info("↑↑↑ 5 删除主数据 删除列数据 ↑↑↑");

        return ResVo.success("删除表设计成功");
    }

    /**
     * 1. 获取SessionUser
     * 2. 唯一约束设计验证
     * 3. 查看表是否已经存在
     * 4. 约束重复校验
     * 5 重新排序约束字段并生成约束名
     * 6. 拼接 UNIQUEKEY_ADD SQL
     * 7. 执行SQL
     * 8. 记录表设计SQL
     * 9. 更新唯一约束数据状态并落库
     */
    @Override
    public ResVo addUniqueKey(TableDesignUniqueKeyDo tableDesignUniqueKeyDo) {
        // 1. 获取SessionUser
        log.info("↓↓↓ 1. 获取SessionUser ↓↓↓");
        User sessionUser = SessionUtils.getSessionUser();
        String tableName = tableDesignUniqueKeyDo.getTableName();
        log.info("↑↑↑ 1. 获取SessionUser ↑↑↑");

        // 2. 唯一约束设计验证
        log.info("↓↓↓ 2. 唯一约束设计验证 ↓↓↓");
        if (!validateTableDesignUniqueKey(tableDesignUniqueKeyDo)) return ResVo.fail("唯一约束设计验证失败");
        log.info("↑↑↑ 2. 唯一约束设计验证 ↑↑↑");

        // 3. 查看表是否已经存在
        log.info("↓↓↓ 3. 查看表是否已经存在 ↓↓↓");
        Boolean isTableExist = tableDesignMapper.isTableExist(tableName);
        if (!isTableExist) {
            throw new BusinessException("校验拒绝", "表不存在");
        }
        TableDesignDo tableDesignDo = tableDesignMapper.selectById(tableDesignUniqueKeyDo);
        if (!tableDesignDo.getTableName().equals(tableName)) {
            throw new BusinessException("校验拒绝", "表名对不上");
        }

        List<TableDesignColumnDo> columnDoList = tableDesignColumnMapper.selectByTableId(tableDesignUniqueKeyDo.getTableId());
        List<String> columnNameList = columnDoList.stream().map(TableDesignColumnDo::getColumnName).toList();
        List<String> uniqueKeyColumnArray = tableDesignUniqueKeyDo.getUniqueKeyColumnArray();
        if (!new HashSet<>(columnNameList).containsAll(uniqueKeyColumnArray)) {
            throw new BusinessException("校验拒绝", "约束的列在列设计中不存在");
        }
        log.info("↑↑↑ 3. 查看表是否已经存在 ↑↑↑");

        // 4_重新排序约束字段并生成约束名
        log.info("↓↓↓ 4_重新排序约束字段并生成约束名 ↓↓↓");
        List<SimpleTableDesign> simpleTableDesigns = tableDesignMapper.getTableDesign(tableName);
        List<String> uniqueKeyColumnArrayOrdered = new ArrayList<>();
        for (SimpleTableDesign simpleTableDesign : simpleTableDesigns) {
            if (uniqueKeyColumnArray.contains(simpleTableDesign.getColumnName())) {
                uniqueKeyColumnArrayOrdered.add(simpleTableDesign.getColumnName());
            }
        }
        System.out.println("uniqueKeyColumnArrayOrdered = " + uniqueKeyColumnArrayOrdered);
        tableDesignUniqueKeyDo.setUniqueKeyColumnArray(uniqueKeyColumnArrayOrdered);
        tableDesignUniqueKeyDo.setUniqueKeyColumn(String.join(",", uniqueKeyColumnArrayOrdered));

        String uniqueKeyName = "UNIQUE_KEY_" + String.join("_", uniqueKeyColumnArrayOrdered);
        tableDesignUniqueKeyDo.setUniqueKeyName(uniqueKeyName);
        log.info("↑↑↑ 4_重新排序约束字段并生成约束名 ↑↑↑");


        // 5_约束重复校验
        log.info("↓↓↓ 5_约束重复校验 ↓↓↓");
        List<String> keys = simpleTableDesigns.stream().filter(item -> "PRI".equals(item.getColumnKey())).map(SimpleTableDesign::getColumnName).toList();
        // 统计频率并比较
        Map<String, Long> freq_uniqueKeyNew = uniqueKeyColumnArray.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<String, Long> freq_key = keys.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        if (freq_uniqueKeyNew.equals(freq_key)) {
            throw new BusinessException("校验拒绝", "约束的列与主键一致");
        }
        List<TableDesignUniqueKeyDo> tableDesignUniqueKeyDos = tableDesignUniqueKeyMapper.selectByTableId(tableDesignUniqueKeyDo.getTableId());
        Set<String> uniqueKeyNameSet = tableDesignUniqueKeyDos.stream().map(TableDesignUniqueKeyDo::getUniqueKeyName).collect(Collectors.toSet());
        // 与已有唯一约束比对
        if (uniqueKeyNameSet.contains(uniqueKeyName)) {
            throw new BusinessException("校验拒绝", "已经存在相同的唯一约束");
        }
        uniqueKeyNameSet.add(uniqueKeyName);
        log.info("↑↑↑ 5_约束重复校验 ↑↑↑");

        // 6. 拼接 UNIQUEKEY_ADD SQL
        log.info("↓↓↓ 6. 拼接 UNIQUEKEY_ADD SQL ↓↓↓");
        StringBuilder sqlBuilder = new StringBuilder("ALTER TABLE `");
        sqlBuilder.append(tableName).append("` ADD UNIQUE KEY `").append(uniqueKeyName).append("` (`").append(String.join("`, `", uniqueKeyColumnArrayOrdered)).append("`)");
        String sql = getSql(sqlBuilder, null);
        log.info("生成了SQL: {}", sql);
        log.info("↑↑↑ 6. 拼接 UNIQUEKEY_ADD SQL ↑↑↑");


        // 7. 执行SQL
        log.info("↓↓↓ 7. 执行SQL ↓↓↓");
        try {
            tableDesignMapper.addUniqueKey(sql);
            log.info("执行SQL成功");
        } catch (Exception e) {
            throw new BusinessException("执行SQL时出现异常", e);
        }
        log.info("↑↑↑ 7. 执行SQL ↑↑↑");

        // 8. 记录表设计SQL
        log.info("↓↓↓ 8. 记录表设计SQL ↓↓↓");
        try {
            Map<String, Map<String, String>> lastCreateSqlMap = tableDesignMapper.showCreateTable(tableName);
            String lastCreateSql = lastCreateSqlMap.get(tableName).get("Create Table");

            TableDesignSqlDo tableDesignSqlDo = new TableDesignSqlDo();
            tableDesignSqlDo.setTableId(tableDesignUniqueKeyDo.getTableId()).setSqlType("UNIQUEKEY_ADD");
            tableDesignSqlDo.setExecuteSql(sql);
            tableDesignSqlDo.setLastCreateSql(lastCreateSql).setDataStatus("1").setCreateUser(sessionUser.getLoginCode()).setUpdateUser(sessionUser.getLoginCode());
            int insertNum = tableDesignSqlMapper.insertNextRecord(tableDesignSqlDo);
            log.info("记录表设计SQL成功");
        } catch (Exception e) {
            throw new BusinessException("执行SQL成功, 但记录表设计SQL异常", e);
        }
        log.info("↑↑↑ 8. 记录表设计SQL ↑↑↑");

        // 9. 更新唯一约束数据状态并落库
        log.info("↓↓↓ 9. 更新唯一约束数据状态并落库 ↓↓↓");
        tableDesignUniqueKeyDo.setDataStatus("1");
        tableDesignUniqueKeyDo.setCreateUser(sessionUser.getLoginCode());
        tableDesignUniqueKeyDo.setUpdateUser(sessionUser.getLoginCode());
        tableDesignUniqueKeyMapper.insert(tableDesignUniqueKeyDo);
        log.info("表设计之列设计保存成功: 表名: {}", tableName);
        log.info("↑↑↑ 9. 更新唯一约束数据状态并落库 ↑↑↑");

        return ResVo.success("添加唯一约束成功", tableDesignUniqueKeyDo);

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
                    if (++tableIdIndex > 1) {
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

    private static String getSql(StringBuilder sqlBuilder, Set<String> allowedWordsSet) {
        String sql = sqlBuilder.toString();

        Set<String> dangerWordsSet = new HashSet<>(Set.of(" select ", " * ", " from ", " where "
                , " set "
                , " delete "
                , " drop ", " truncate "));

        if (!ObjectUtils.isEmpty(allowedWordsSet)) {
            dangerWordsSet.removeAll(allowedWordsSet);
        }


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

        User sessionUser = SessionUtils.getSessionUser();
        String nowUserLoginCode = sessionUser.getLoginCode();

        // 如果没有TableId则生成一个
        String newTableId = "";
        String tableName = mixedTableDesign.getTableName();
        if (ObjectUtils.isEmpty(mixedTableDesign.getTableId())) {
            log.info("SVC混合表设计校验: 当前入参没有tableId, 表名: {}", tableName);
            newTableId = tableName + "_" + DateTimeUtil.getMinuteKey();
            mixedTableDesign.setTableId(newTableId);
            mixedTableDesign.setCreateUser(nowUserLoginCode);
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
        List<String> keys = new ArrayList<>();

        for (TableDesignColumnDo tableDesignColumnDo : listTableDesignColumn) {

            // tableId
            if (!ObjectUtils.isEmpty(newTableId)) {
                if (!ObjectUtils.isEmpty(tableDesignColumnDo.getTableId())) {
                    throw new BusinessException("校验拒绝", "入参表设计没有tableId但列中有tableId");
                }
                tableDesignColumnDo.setTableId(newTableId);
                tableDesignColumnDo.setCreateUser(nowUserLoginCode);
            } else if (ObjectUtils.isEmpty(tableDesignColumnDo.getTableId())) {
                tableDesignColumnDo.setTableId(mixedTableDesign.getTableId());
                tableDesignColumnDo.setCreateUser(nowUserLoginCode);
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

            if ("Y".equals(tableDesignColumnDo.getKeyYn())) {
                keys.add(tableDesignColumnDo.getColumnName());
            }

            if (!validateTableDesignColumn(tableDesignColumnDo)) return false;

        }

        Set<String> uniqueKeyNameSet = new HashSet<>();
        List<TableDesignUniqueKeyDo> listUniqueKey = mixedTableDesign.getList_uniqueKey();
        for (TableDesignUniqueKeyDo tableDesignUniqueKeyDo : listUniqueKey) {
            // tableId
            if (!ObjectUtils.isEmpty(newTableId)) {
                if (!ObjectUtils.isEmpty(tableDesignUniqueKeyDo.getTableId())) {
                    throw new BusinessException("校验拒绝", "入参表设计没有tableId但唯一约束中有tableId");
                }
                tableDesignUniqueKeyDo.setTableId(newTableId);
                tableDesignUniqueKeyDo.setCreateUser(nowUserLoginCode);
            } else if (ObjectUtils.isEmpty(tableDesignUniqueKeyDo.getTableId())) {
                tableDesignUniqueKeyDo.setTableId(mixedTableDesign.getTableId());
                tableDesignUniqueKeyDo.setCreateUser(nowUserLoginCode);
            }

            if (!tableDesignUniqueKeyDo.getTableId().equals(mixedTableDesign.getTableId())) {
                throw new BusinessException("校验拒绝", "表设计tableId与唯一约束tableId不一致");
            }

            //dataStatus
            String dataStatus = tableDesignUniqueKeyDo.getDataStatus();
            if (!"0".equals(dataStatus)) {
                log.info("校验拒绝 dataStatus: {} 唯一约束状态不是待创建", dataStatus);
                return false;
            }

            // 重新排序约束字段并生成约束名
            List<String> uniqueKeyColumnArray = tableDesignUniqueKeyDo.getUniqueKeyColumnArray();
            List<String> uniqueKeyColumnArrayOrdered = new ArrayList<>();
            for (TableDesignColumnDo tableDesignColumnDo : listTableDesignColumn) {
                if (uniqueKeyColumnArray.contains(tableDesignColumnDo.getColumnName())) {
                    uniqueKeyColumnArrayOrdered.add(tableDesignColumnDo.getColumnName());
                }
            }
            System.out.println("uniqueKeyColumnArrayOrdered = " + uniqueKeyColumnArrayOrdered);
            tableDesignUniqueKeyDo.setUniqueKeyColumnArray(uniqueKeyColumnArrayOrdered);
            tableDesignUniqueKeyDo.setUniqueKeyColumn(String.join(",", uniqueKeyColumnArrayOrdered));

            String uniqueKeyName = "UNIQUE_KEY_" + String.join("_", uniqueKeyColumnArrayOrdered);
            tableDesignUniqueKeyDo.setUniqueKeyName(uniqueKeyName);

            // 5_约束重复校验
            log.info("↓↓↓ 5_约束重复校验 ↓↓↓");
            // 统计频率并比较
            Map<String, Long> freq_uniqueKeyNew = uniqueKeyColumnArray.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Map<String, Long> freq_key = keys.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            if (freq_uniqueKeyNew.equals(freq_key)) {
                throw new BusinessException("校验拒绝", "约束的列与主键一致");
            }
            // 与已有唯一约束比对
            if (uniqueKeyNameSet.contains(uniqueKeyName)) {
                throw new BusinessException("校验拒绝", "存在相同的唯一约束");
            }
            uniqueKeyNameSet.add(uniqueKeyName);
            log.info("↑↑↑ 5_约束重复校验 ↑↑↑");

            if (!validateTableDesignUniqueKey(tableDesignUniqueKeyDo)) return false;

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

    private boolean validateTableDesignUniqueKey(TableDesignUniqueKeyDo tableDesignUniqueKeyDo) {
        // tableId
        String tableId = tableDesignUniqueKeyDo.getTableId();
        if (ObjectUtils.isEmpty(tableId)) {
            throw new BusinessException("校验拒绝", "tableId 为空");
        }

        // uniqueKeyColumn
        String uniqueKeyColumn = tableDesignUniqueKeyDo.getUniqueKeyColumn();
        if (ObjectUtils.isEmpty(uniqueKeyColumn)) {
            throw new BusinessException("校验拒绝", "uniqueKeyColumn 为空");
        }

        //uniqueKeyColumnArray
        List<String> uniqueKeyColumnArray = tableDesignUniqueKeyDo.getUniqueKeyColumnArray();
        if (ObjectUtils.isEmpty(uniqueKeyColumnArray)) {
            throw new BusinessException("校验拒绝", "uniqueKeyColumnArray 为空");
        }

        HashSet<String> uniqueKeySet = new HashSet<>(uniqueKeyColumnArray);
        if (uniqueKeySet.size() != uniqueKeyColumnArray.size()) {
            throw new BusinessException("校验拒绝", "uniqueKeyColumnArray 中存在重复元素" + uniqueKeyColumnArray);
        }

        for (String column : uniqueKeyColumnArray) {
            if (ObjectUtils.isEmpty(column)) {
                throw new BusinessException("校验拒绝", "uniqueKeyColumnArray 中存在空元素" + uniqueKeyColumnArray);
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

    @Override
    public ResVo deleteUniqueKey(TableDesignUniqueKeyDo tableDesignUniqueKeyDo) {
        // 1. 获取SessionUser
        log.info("↓↓↓ 1. 获取SessionUser ↓↓↓");
        User sessionUser = SessionUtils.getSessionUser();
        String tableName = tableDesignUniqueKeyDo.getTableName();
        log.info("↑↑↑ 1. 获取SessionUser ↑↑↑");

        // 2. 唯一约束设计验证
        log.info("↓↓↓ 2. 唯一约束设计验证 ↓↓↓");
        if (!validateTableDesignUniqueKey(tableDesignUniqueKeyDo)) return ResVo.fail("唯一约束设计验证失败");
        log.info("↑↑↑ 2. 唯一约束设计验证 ↑↑↑");

        // 3. 查看表是否已经存在+唯一约束是否存在
        log.info("↓↓↓ 3. 查看表是否已经存在+唯一约束是否存在 ↓↓↓");
        Boolean isTableExist = tableDesignMapper.isTableExist(tableName);
        if (!isTableExist) {
            throw new BusinessException("校验拒绝", "表不存在");
        }
        TableDesignDo tableDesignDo = tableDesignMapper.selectById(tableDesignUniqueKeyDo);
        if (!tableDesignDo.getTableName().equals(tableName)) {
            throw new BusinessException("校验拒绝", "表名对不上");
        }
        LambdaQueryWrapper<TableDesignUniqueKeyDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TableDesignUniqueKeyDo::getTableId, tableDesignUniqueKeyDo.getTableId())
                .eq(TableDesignUniqueKeyDo::getUniqueKeyName, tableDesignUniqueKeyDo.getUniqueKeyName());
        TableDesignUniqueKeyDo existingUniqueKey = tableDesignUniqueKeyMapper.selectOne(queryWrapper);
        if (existingUniqueKey == null) {
            throw new BusinessException("校验拒绝", "唯一约束不存在");
        }
        log.info("↑↑↑ 3. 查看表是否已经存在+唯一约束是否存在 ↑↑↑");

        // 4. 拼接 UNIQUEKEY_DELETE SQL
        log.info("↓↓↓ 4. 拼接 UNIQUEKEY_DELETE SQL ↓↓↓");
        StringBuilder sqlBuilder = new StringBuilder("ALTER TABLE `");
        sqlBuilder.append(tableName).append("` DROP INDEX `").append(tableDesignUniqueKeyDo.getUniqueKeyName()).append("`");
        String sql = getSql(sqlBuilder, new HashSet<>(Set.of(" drop ")));

        log.info("生成了SQL: {}", sql);

        log.info("↑↑↑ 4. 拼接 UNIQUEKEY_DELETE SQL ↑↑↑");

        // 5. 执行SQL
        log.info("↓↓↓ 5. 执行SQL ↓↓↓");
        try {
            tableDesignMapper.deleteUniqueKey(sql);
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
            tableDesignSqlDo.setTableId(tableDesignUniqueKeyDo.getTableId()).setSqlType("UNIQUEKEY_DROP");
            tableDesignSqlDo.setExecuteSql(sql);
            tableDesignSqlDo.setLastCreateSql(lastCreateSql).setDataStatus("1").setCreateUser(sessionUser.getLoginCode()).setUpdateUser(sessionUser.getLoginCode());
            int insertNum = tableDesignSqlMapper.insertNextRecord(tableDesignSqlDo);
            log.info("记录表设计SQL成功");
        } catch (Exception e) {
            throw new BusinessException("执行SQL成功, 但记录表设计SQL异常", e);
        }
        log.info("↑↑↑ 6. 记录表设计SQL ↑↑↑");

        // 7. 删除唯一约束数据状态并落库
        log.info("↓↓↓ 7. 删除唯一约束数据状态并落库 ↓↓↓");
        int deleteCount = tableDesignUniqueKeyMapper.delete(queryWrapper);
        if (deleteCount == 0) {
            throw new BusinessException("校验拒绝", "唯一约束不存在");
        }
        log.info("表设计之唯一约束设计删除完成: 表名: {}", tableName);
        log.info("↑↑↑ 7. 删除唯一约束数据状态并落库 ↑↑↑");

        return ResVo.success("删除唯一约束成功");
    }


    @Override
    public ResVo generateTableDesign(String tableName) {
        // 1. 获取SessionUser
        log.info("↓↓↓ 1. 获取SessionUser ↓↓↓");
        User sessionUser = SessionUtils.getSessionUser();
        String nowUserLoginCode = sessionUser.getLoginCode();
        log.info("↑↑↑ 1. 获取SessionUser ↑↑↑");

        // 2. 检查表是否存在
        log.info("↓↓↓ 2. 检查表是否存在 ↓↓↓");
        Boolean isTableExist = tableDesignMapper.isTableExist(tableName);
        if (!isTableExist) {
            throw new BusinessException("校验拒绝", "表不存在");
        }
        // 检查表设计是否存在
        log.info("↓↓↓ 2. 检查表设计是否存在 ↓↓↓");        // 检查表设计是否存在
        LambdaQueryWrapper<TableDesignDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TableDesignDo::getTableName, tableName);
//        TableDesignDo tableDesignDo = tableDesignMapper.selectOne(queryWrapper);
//        if (tableDesignDo != null) {
//            throw new BusinessException("校验拒绝", "表设计已存在");
//        }
        // 生成 tableId（这里假设使用表名 + 时间戳生成唯一ID）
        MixedTableDesign mixedTableDesign = new MixedTableDesign();
        String tableId = tableName + "_" + DateTimeUtil.getMinuteKey();
        log.info("↑↑↑ 2. 检查表是否存在 ↑↑↑");


        // 4. 获取列信息
        log.info("↓↓↓ 4. 获取列信息 ↓↓↓");
        List<SimpleTableDesign> simpleTableDesigns = tableDesignMapper.getTableDesign(tableName);
        // 字段设计
        List<TableDesignColumnDo> columnDos = new ArrayList<>();
        for (SimpleTableDesign std : simpleTableDesigns) {
            TableDesignColumnDo columnDo = new TableDesignColumnDo();
            columnDo.setTableId(tableId)
                    .setColumnName(std.getColumnName())
                    .setColumnComment(std.getColumnComment())
                    .setFieldEnum(getFieldEnum(std.getColumnComment()))
                    .setKeyYn("PRI".equals(std.getColumnKey()) ? "Y" : "N")
                    .setNullAbleYn("YES".equals(std.getColumnDefault()) ? "Y" : "N") // 简化处理
                    .setFieldType(determineFieldType(std.getColumnType())) // 类型映射函数
                    .setFieldLength(determineFieldLength(std.getColumnType()))
                    .setDefaultValue(std.getColumnDefault())
                    .setDataStatus("1")
                    .setCreateUser(nowUserLoginCode)
                    .setUpdateUser(nowUserLoginCode)
                    .setFieldIndex(std.getOrdinalPosition());
            columnDos.add(columnDo);
        }
        log.info("↑↑↑ 4. 获取列信息 ↑↑↑");

        // 6. 获取唯一约束信息
        log.info("↓↓↓ 6. 获取唯一约束信息 ↓↓↓");
        Map<String, Map<String, String>> createTableMap = tableDesignMapper.showCreateTable(tableName);
        String createTableSql = createTableMap.get(tableName).get("Create Table");
        List<TableDesignUniqueKeyDo> uniqueKeys = new ArrayList<>();

        // 解析SQL语句中的UNIQUE KEY部分
        Pattern pattern = Pattern.compile("UNIQUE KEY `([^`]+)` \\(([^)]+)");
        Stream<String> sqlLines = createTableSql.lines();
        sqlLines.forEach(line -> {
            if (line.contains("UNIQUE KEY")) {
                System.out.println("line = " + line);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String uniqueKeyName = matcher.group(1);
                    String columnsStr = matcher.group(2).replaceAll("`", "");
                    System.out.println("columnsStr = " + columnsStr);
                    List<String> columns = Arrays.asList(columnsStr.replaceAll("[`]", "").split(","));
                    System.out.println("uniqueKeyName = " + uniqueKeyName);
                    TableDesignUniqueKeyDo keyDo = new TableDesignUniqueKeyDo();
                    keyDo.setTableId(tableId)
                            .setUniqueKeyName(uniqueKeyName)
                            .setUniqueKeyColumn(columnsStr) // JSON字符串转换
                            .setDataStatus("1") // 初始状态为未生效
                            .setCreateUser(nowUserLoginCode)
                            .setUpdateUser(nowUserLoginCode);
                    uniqueKeys.add(keyDo);
                }
            } else if (line.contains(") ENGINE")) {
                System.out.println("line = " + line);
                mixedTableDesign.setTableComment(line.substring(line.indexOf("COMMENT='") + 9, line.length() - 1));
            }

        });


        log.info("↑↑↑ 6. 获取唯一约束信息 ↑↑↑");

        // 7. 构造 MixedTableDesign 对象
        log.info("↓↓↓ 7. 构造 MixedTableDesign 对象 ↓↓↓");


        // 主表设计
        mixedTableDesign.setTableId(tableId)
                .setTableName(tableName)
                .setTableType(tableName.substring(0, tableName.indexOf("_")))
                .setDataStatus("1") // 初始状态为未生效
                .setCreateUser(nowUserLoginCode)
                .setUpdateUser(nowUserLoginCode)
        ;


        mixedTableDesign.setList_tableDesignColumn(columnDos);


        mixedTableDesign.setList_uniqueKey(uniqueKeys);
        log.info("↑↑↑ 7. 构造 MixedTableDesign 对象 ↑↑↑");

        // 8. 保存表设计
        log.info("↓↓↓ 8. 保存表设计 ↓↓↓");
        log.info("表设计: {}", mixedTableDesign);
        saveMixedTableDesign(mixedTableDesign);
        log.info("↑↑↑ 8. 保存表设计 ↑↑↑");

        return ResVo.success("逆向生成并保存表设计成功", mixedTableDesign);
    }

    private String getFieldEnum(String columnComment) {
        if (!columnComment.contains(":")) {
            return null;
        }
        String[] split = columnComment.split(":")[1].split(",");
        return String.join(",", split);
    }

    private Integer determineFieldLength(String columnType) {
        if (columnType.contains("(")) {
            return Integer.parseInt(columnType.substring(columnType.indexOf("(") + 1, columnType.indexOf(")")));
        }
        return null;
    }

    private String determineFieldType(String columnType) {
        // 根据columnType做简单类型映射
        if (columnType.contains("varchar")) return "varchar";
        else if (columnType.contains("char")) return "char";
        else if (columnType.contains("int")) return "int";
        else if (columnType.contains("timestamp")) return "timestamp";
        else if (columnType.contains("TEXT")) return "TEXT";
        else if (columnType.contains("BLOB")) return "BLOB";
        else if (columnType.contains("JSON")) return "JSON";
        else return "varchar"; // 默认类型
    }


}
