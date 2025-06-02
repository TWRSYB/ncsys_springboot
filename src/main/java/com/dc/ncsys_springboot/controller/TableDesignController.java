package com.dc.ncsys_springboot.controller;

import com.dc.ncsys_springboot.service.TableDesignService;
import com.dc.ncsys_springboot.vo.ResVo;
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
 * @since 2025-06-02 11:08
 */
@RestController
@RequestMapping("/tableDesign")
public class TableDesignController {

    @Autowired
    private TableDesignService tableDesignService;

    @GetMapping("/getTableDesign")
    public ResVo getTableDesign() {
        return tableDesignService.getTableDesign();
    }

}
