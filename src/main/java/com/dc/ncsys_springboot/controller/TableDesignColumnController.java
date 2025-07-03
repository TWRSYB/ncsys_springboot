package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.service.TableDesignColumnService;
import com.dc.ncsys_springboot.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 数据库设计表 前端控制器
 * </p>
 *
 * @author sysAdmin
 * @since 2025-06-02 19:53
 */
@Slf4j
@RestController
@RequestMapping("/tableDesignColumn")
public class TableDesignColumnController {

    @Autowired
    private TableDesignColumnService tableDesignColumnService;

    @GetMapping("/getOption")
    public ResVo getOption(String tableName, String columnName) {
        log.info("CONT入参: tableName={}, columnName={}", tableName, columnName);
        return tableDesignColumnService.getOption(tableName, columnName);
    }
}
