package com.dc.ncsys_springboot.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Map;

@Data
@AllArgsConstructor
public class Field {
    private int ordinalPosition;
    private String columnName;
    private String columnComment;
    private String type; //text, textArea, lv, type, ymd
    private Map<String, String> lvs;
    private ArrayList<String> types;
    private String columnDefault;

}
