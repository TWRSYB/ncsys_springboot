package com.dc.ncsys_springboot.util;

import java.util.regex.Pattern;

/**
 * 正则表达式工具类 - 封装常用验证规则
 * 版本：1.1
 * 日期：2025-08-02
 */
public class RegexUtils {

    // 预编译常用正则表达式（提高性能）
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern TEL_PATTERN = Pattern.compile("^(?:(?:\\d{3,4}-)?\\d{7,8}(?:-\\d{1,6})?|1[3-9]\\d{9})(?:;\\s*(?:(?:\\d{3,4}-)?\\d{7,8}(?:-\\d{1,6})?|1[3-9]\\d{9}))*$");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
    private static final Pattern CHINESE_PATTERN = Pattern.compile("^[\u4E00-\u9FA5]+$");
    private static final Pattern LICENSE_PLATE_PATTERN = Pattern.compile("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼][A-HJ-NP-Z][A-HJ-NP-Z0-9]{4,5}[A-HJ-NP-Z0-9挂学警港澳]$");
    private static final Pattern POST_CODE_PATTERN = Pattern.compile("^[1-9]\\d{5}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z\u4E00-\u9FA5][\\w\u4E00-\u9FA5]{3,19}$");

    /**
     * 验证手机号（中国大陆）
     * @param mobile 手机号码
     * @return 验证结果
     */
    public static boolean isMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }

    /**
     * 验证固定电话号码（带区号）
     * 格式示例：010-12345678、0512-1234567、1234567
     * @param tel 电话号码
     * @return 验证结果
     */
    public static boolean isTel(String tel) {
        return tel != null && TEL_PATTERN.matcher(tel).matches();
    }

    /**
     * 验证身份证号码（18位）
     * @param idCard 身份证号码
     * @return 验证结果
     */
    public static boolean isIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return false;
        }
        return ID_CARD_PATTERN.matcher(idCard).matches();
    }

    /**
     * 验证邮箱地址
     * @param email 邮箱地址
     * @return 验证结果
     */
    public static boolean isEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证URL地址
     * @param url URL地址
     * @return 验证结果
     */
    public static boolean isUrl(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }

    /**
     * 验证中文字符（纯中文）
     * @param chinese 中文字符串
     * @return 验证结果
     */
    public static boolean isChinese(String chinese) {
        return chinese != null && CHINESE_PATTERN.matcher(chinese).matches();
    }

    /**
     * 验证中国大陆车牌号
     * @param licensePlate 车牌号
     * @return 验证结果
     */
    public static boolean isLicensePlate(String licensePlate) {
        return licensePlate != null && LICENSE_PLATE_PATTERN.matcher(licensePlate).matches();
    }

    /**
     * 验证邮政编码（中国大陆）
     * @param postCode 邮政编码
     * @return 验证结果
     */
    public static boolean isPostCode(String postCode) {
        return postCode != null && POST_CODE_PATTERN.matcher(postCode).matches();
    }

    /**
     * 验证用户名（4-20位，支持中文、字母、数字、下划线）
     * @param username 用户名
     * @return 验证结果
     */
    public static boolean isUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * 通用正则验证方法
     * @param input 输入字符串
     * @param regex 正则表达式
     * @return 验证结果
     */
    public static boolean matches(String input, String regex) {
        return input != null && regex != null && Pattern.compile(regex).matcher(input).matches();
    }

    /**
     * 提取匹配的第一个结果
     * @param input 输入字符串
     * @param regex 正则表达式
     * @return 匹配结果（未找到返回null）
     */
    public static String extractFirst(String input, String regex) {
        if (input == null || regex == null) return null;
        java.util.regex.Matcher matcher = Pattern.compile(regex).matcher(input);
        return matcher.find() ? matcher.group() : null;
    }

    /**
     * 替换所有匹配项
     * @param input 输入字符串
     * @param regex 正则表达式
     * @param replacement 替换内容
     * @return 替换后的字符串
     */
    public static String replaceAll(String input, String regex, String replacement) {
        return input != null && regex != null ? input.replaceAll(regex, replacement) : input;
    }
}