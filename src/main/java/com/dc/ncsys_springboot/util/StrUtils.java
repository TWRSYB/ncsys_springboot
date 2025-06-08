package com.dc.ncsys_springboot.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StrUtils {

    public static String underLine2BigCamel(String inputStr) {
        if (inputStr == null || inputStr.isEmpty()) return inputStr;
        return Arrays.stream(inputStr.split("_"))
                .filter(s -> !s.isEmpty())
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                .collect(Collectors.joining());
    }
}
