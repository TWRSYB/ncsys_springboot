package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.daoVo.MixedTableDesign;
import com.dc.ncsys_springboot.daoVo.TableDesignColumnDo;
import com.dc.ncsys_springboot.daoVo.TableDesignDo;
import com.dc.ncsys_springboot.daoVo.TableDesignUniqueKeyDo;
import com.dc.ncsys_springboot.service.TableDesignService;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/getTableDesignList")
    public ResVo getTableDesignList() {
        return tableDesignService.getTableDesignList();
    }

    @GetMapping("/getTableDesign")
    public ResVo getTableDesign(String tableName) {
        return tableDesignService.getTableDesign(tableName);
    }

    @PostMapping("/saveTableDesign")
    public ResVo saveTableDesign(@RequestBody MixedTableDesign mixedTableDesign) {
        log.info("保存数据库设计接口收到请求: {}", mixedTableDesign);
        return tableDesignService.saveTableDesign(mixedTableDesign);
    }

    @PostMapping("/deleteTableDesign")
    public ResVo deleteTableDesign(@RequestBody TableDesignDo TableDesignDo) {
        log.info("CONT入参: {}", TableDesignDo);
        return tableDesignService.deleteTableDesign(TableDesignDo);
    }

    @GetMapping("/getTableDesignDetail")
    public ResVo getTableDesignDetail(String tableName) {
        return tableDesignService.getTableDesignDetail(tableName);
    }

    @PostMapping("/createTableAndEntity")
    public ResVo createTableAndEntity(@RequestBody MixedTableDesign mixedTableDesign) {
        log.info("CONT入参: {}", mixedTableDesign);
        return tableDesignService.createTableAndEntity(mixedTableDesign);
    }

    @PostMapping("/addColumn")
    public ResVo addColumn(@RequestBody TableDesignColumnDo tableDesignColumnDo) {
        log.info("CONT入参: {}", tableDesignColumnDo);
        return tableDesignService.addColumn(tableDesignColumnDo);
    }

    @PostMapping("/addUniqueKey")
    public ResVo addUniqueKey(@RequestBody TableDesignUniqueKeyDo tableDesignUniqueKeyDo) {
        log.info("CONT入参: {}", tableDesignUniqueKeyDo);
        return tableDesignService.addUniqueKey(tableDesignUniqueKeyDo);
    }

    @PostMapping("/deleteUniqueKey")
    public ResVo deleteUniqueKey(@RequestBody TableDesignUniqueKeyDo tableDesignUniqueKeyDo) {
        log.info("CONT入参: {}", tableDesignUniqueKeyDo);
        return tableDesignService.deleteUniqueKey(tableDesignUniqueKeyDo);
    }


    @GetMapping("/generateTableDesign")
    public ResVo generateTableDesign(String tableName) {
        log.info("CONT入参: {}", tableName);
        return tableDesignService.generateTableDesign(tableName);
    }

}
