package com.dc.ncsys_springboot.vo;

import lombok.Data;

@Data
public class SimpleTableDesign {
    /**
     * 序数位置
     */
    private int ordinalPosition;
    /**
     * 字段名
     */
    private String columnName;
    /**
     * 字段注释
     */
    private String columnComment;

    /**
     * 默认值
     */
    private String columnDefault;

    /**
     * 列键
     */
    private String columnKey;
}
