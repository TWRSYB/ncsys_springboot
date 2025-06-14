package com.dc.ncsys_springboot.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StrUtils {

    public static void main(String[] args) {
        String result = camel2Constant("patternNotInFieldEnum");
        System.out.println("result = " + result);
    }

    public static String underLine2BigCamel(String inputStr) {
        if (inputStr == null || inputStr.isEmpty()) return inputStr;
        return Arrays.stream(inputStr.split("_"))
                .filter(s -> !s.isEmpty())
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                .collect(Collectors.joining());
    }


    public static String underLine2Camel(String inputStr) {
        if (inputStr == null || inputStr.isEmpty()) return inputStr;
        List<String> strings = Arrays.stream(inputStr.toLowerCase().split("_"))
                .filter(s -> !s.isEmpty()).toList();
        StringBuilder sb = new StringBuilder(strings.get(0));
        for (int i = 1; i < strings.size(); i++) {
            String s = strings.get(i);
            sb.append(s.substring(0, 1).toUpperCase());
            if (s.length()>1) {
                sb.append(s.substring(1));
            }
        }
        return sb.toString();
    }

    public static String camel2Constant (String inputStr) {
        if (inputStr == null || inputStr.isEmpty()) return inputStr;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inputStr.length(); i++) {
            char currentChar = inputStr.charAt(i);

            if (i > 0) {
                char prevChar = inputStr.charAt(i - 1);
                // 处理连续大写字母（如HTTPRequest → HTTP_REQUEST）
                if ( Character.isUpperCase(currentChar)) {

                    if (!Character.isUpperCase(prevChar)) {
                        sb.append('_');
                    }
                }
                // 处理数字后的字母（如test123Example → TEST123_EXAMPLE）
                else if (Character.isDigit(prevChar) && Character.isLetter(currentChar)) {
                    sb.append('_');
                }
            }
            sb.append(Character.toUpperCase(currentChar));
        }
        return sb.toString();
    }

}
