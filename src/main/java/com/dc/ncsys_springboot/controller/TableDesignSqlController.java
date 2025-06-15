package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.service.TableDesignSqlService;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 表设计SQL 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-07 19:37
 */
@Slf4j
@RestController
@RequestMapping("/tableDesignSql")
public class TableDesignSqlController {

    @Autowired
    private TableDesignSqlService tableDesignSqlService;

    /**
     * 获取最后表设计SQL
     *
     * @return ResVo
     */
    @GetMapping("/getLastTableDesignSql")
    public ResVo getLastTableDesignSql(String tableId) {
        log.info("CONT入参: {}", tableId);
        return tableDesignSqlService.getLastTableDesignSql(tableId);
    }

}
