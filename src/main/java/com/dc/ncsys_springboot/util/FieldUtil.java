package com.dc.ncsys_springboot.util;

import com.dc.ncsys_springboot.mapper.TableDesignColumnMapper;
import com.dc.ncsys_springboot.vo.SimpleTableDesign;

import java.util.*;

public class FieldUtil {


    /**
     * 下划线转驼峰
     * @param field 字段名
     * @return 驼峰字段名
     */
    public static String underLineToCamel(String field){
        String[] s = field.split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(s[0].toLowerCase());
        for (int i = 1; i < s.length; i++) {
            stringBuilder.append(s[i].substring(0,1).toUpperCase()).append(s[i].substring(1).toLowerCase());
        }
        return stringBuilder.toString();
    }

    public static List<String> getEnumList(String tableName, String columnName) {
        TableDesignColumnMapper tableDesignColumnMapper = SpringContextUtil.getBean(TableDesignColumnMapper.class);
        SimpleTableDesign column = tableDesignColumnMapper.getColumn(tableName, columnName);
        if (column == null) {
            return null;
        }
        String columnComment = column.getColumnComment();
        if (columnComment.contains(":")) {
            String optionStr = columnComment.split(":")[1];
            List<String> enumList = new ArrayList<>();
            for (String option : optionStr.split(",")) {
                enumList.add(option.split("-")[0]);
            }
            return enumList;
        } else {
            return null;
        }

    }


}
