package com.dc.ncsys_springboot.util;

public class FieldUtil {
    public static String underLineToCamel(String field){
        String[] s = field.split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(s[0].toLowerCase());
        for (int i = 1; i < s.length; i++) {
            stringBuilder.append(s[i].substring(0,1).toUpperCase()).append(s[i].substring(1).toLowerCase());
        }
        return stringBuilder.toString();
    }
}
