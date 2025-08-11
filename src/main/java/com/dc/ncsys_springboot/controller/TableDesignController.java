package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.MixedTableDesign;
import com.dc.ncsys_springboot.daoVo.TableDesignColumnDo;
import com.dc.ncsys_springboot.daoVo.TableDesignDo;
import com.dc.ncsys_springboot.daoVo.TableDesignUniqueKeyDo;
import com.dc.ncsys_springboot.service.TableDesignService;
import com.dc.ncsys_springboot.vo.Field;
import com.dc.ncsys_springboot.vo.PageQueryVo;
import com.dc.ncsys_springboot.vo.PageResVo;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 数据库设计表 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-02 11:08
 */
@Slf4j
@RestController
@RequestMapping("/tableDesign")
public class TableDesignController {

    @Autowired
    private TableDesignService tableDesignService;


    /**
     * 分页查询数据库设计
     * @param pageQueryVo 分页查询参数
     * @return 数据库设计列表
     */
    @PostMapping("/pageQuery")
    public PageResVo<TableDesignDo> pageQuery(@RequestBody PageQueryVo<TableDesignDo> pageQueryVo) {
        return tableDesignService.pageQuery(pageQueryVo);
    }

    /**
     * 获取表字段设计
     * @param tableName 数据库表名
     * @return 表字段列表
     */
    @GetMapping("/getTableDesign")
    public ResVo<List<Field>> getTableDesign(String tableName) {
        return tableDesignService.getTableDesign(tableName);
    }

    /**
     * 保存表设计
     * @param mixedTableDesign 表设计
     * @return 无
     */
    @PostMapping("/saveTableDesign")
    public ResVo<Object> saveTableDesign(@RequestBody MixedTableDesign mixedTableDesign) {
        log.info("保存数据库设计接口收到请求: {}", mixedTableDesign);
        return tableDesignService.saveTableDesign(mixedTableDesign);
    }

    /**
     * 删除表设计
     * @param TableDesignDo 表设计
     * @return 无
     */
    @PostMapping("/deleteTableDesign")
    public ResVo<Object> deleteTableDesign(@RequestBody TableDesignDo TableDesignDo) {
        log.info("CONT入参: {}", TableDesignDo);
        return tableDesignService.deleteTableDesign(TableDesignDo);
    }

    /**
     * 获取表设计详情
     * @param tableName 表名
     * @return 表设计详情
     */
    @GetMapping("/getTableDesignDetail")
    public ResVo<MixedTableDesign> getTableDesignDetail(String tableName) {
        log.info("CONT入参: {}", tableName);
        return tableDesignService.getTableDesignDetail(tableName);
    }

    /**
     * 创建表和实体类
     * @param mixedTableDesign 混合表设计
     * @return 无
     */
    @PostMapping("/createTableAndEntity")
    public ResVo<Object> createTableAndEntity(@RequestBody MixedTableDesign mixedTableDesign) {
        log.info("CONT入参: {}", mixedTableDesign);
        return tableDesignService.createTableAndEntity(mixedTableDesign);
    }

    /**
     * 新增字段
     * @param tableDesignColumnDo 字段信息
     * @return 字段信息
     */
    @PostMapping("/addColumn")
    public ResVo<TableDesignColumnDo> addColumn(@RequestBody TableDesignColumnDo tableDesignColumnDo) {
        log.info("CONT入参: {}", tableDesignColumnDo);
        return tableDesignService.addColumn(tableDesignColumnDo);
    }

    /**
     * 新增唯一约束
     * @param tableDesignUniqueKeyDo 唯一约束信息
     * @return 唯一约束信息
     */
    @PostMapping("/addUniqueKey")
    public ResVo<TableDesignUniqueKeyDo> addUniqueKey(@RequestBody TableDesignUniqueKeyDo tableDesignUniqueKeyDo) {
        log.info("CONT入参: {}", tableDesignUniqueKeyDo);
        return tableDesignService.addUniqueKey(tableDesignUniqueKeyDo);
    }

    /**
     * 删除唯一约束
     * @param tableDesignUniqueKeyDo 唯一约束信息
     * @return 无
     */
    @PostMapping("/deleteUniqueKey")
    public ResVo<Object> deleteUniqueKey(@RequestBody TableDesignUniqueKeyDo tableDesignUniqueKeyDo) {
        log.info("CONT入参: {}", tableDesignUniqueKeyDo);
        return tableDesignService.deleteUniqueKey(tableDesignUniqueKeyDo);
    }

    /**
     * 从现有表生成表设计
     * @param tableName 表名
     * @return 混合表设计
     */
    @GetMapping("/generateTableDesign")
    public ResVo<MixedTableDesign> generateTableDesign(String tableName) {
        log.info("CONT入参: {}", tableName);
        return tableDesignService.generateTableDesign(tableName);
    }

    /**
     * 修改字段
     * @param tableDesignColumnDo 字段信息
     * @return 字段信息
     */
    @PostMapping("/changeColumn")
    public ResVo<TableDesignColumnDo> changeColumn(@RequestBody TableDesignColumnDo tableDesignColumnDo) {
        log.info("CONT入参: {}", tableDesignColumnDo);
        return tableDesignService.changeColumn(tableDesignColumnDo);
    }
}
