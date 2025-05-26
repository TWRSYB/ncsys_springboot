package com.simpleJavaProgram;

import java.util.regex.Pattern;

public class ZZTest {

    public static void main(String[] args) {
        String zz = "^[sS][kK][yY][-]?\\d{3}$";
        String[] strs = new String[]{"sky002", "sky-009", "sky--008", "sky90", "skyy088", "N44", "n1234", "N002c", "N0000", "ssky000"};
        for (String str : strs) {
            boolean matches = Pattern.matches(zz, str);
            System.out.println(str + " 匹配正则 " + zz + " 结果为 " + matches);
        }

    }
}
