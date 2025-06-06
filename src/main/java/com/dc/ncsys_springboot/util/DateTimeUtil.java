package com.dc.ncsys_springboot.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
public class DateTimeUtil {

    // 线程安全的格式化工具（预定义常用格式）
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter KEY_FORMATTER_MINUTE = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
    public static final DateTimeFormatter KEY_FORMATTER_SECOND = DateTimeFormatter.ofPattern("yyyMMdd_HHmmss");
    public static final DateTimeFormatter KEY_FORMATTER_MILLISECOND = DateTimeFormatter.ofPattern("yyyMMdd_HHmmssSSS");

    // 私有构造防止实例化
    private DateTimeUtil() {}

    // ====================== 获取当前时间 ======================
    /**
     * 获取当前时间字符串（默认格式：yyyy-MM-dd HH:mm:ss）
     */
    public static String now() {
        return LocalDateTime.now().format(DEFAULT_FORMATTER);
    }

    /**
     * 获取当前时间字符串（自定义格式）
     * @param pattern 格式模式（如："yyyy/MM/dd HH:mm"）
     */
    public static String now(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取指定时区的当前时间
     * @param zoneId 时区ID（如："Asia/Shanghai", "UTC"）
     */
    public static String now(ZoneId zoneId) {
        return ZonedDateTime.now(zoneId).format(DEFAULT_FORMATTER);
    }

    // ====================== 获取一个当前时间的KEY ======================
    /**
     * 分钟精度的KEY
     */
    public static String getMinuteKey() {
        return LocalDateTime.now().format(KEY_FORMATTER_MINUTE);
    }

   /**
     * 秒精度的KEY
     */
    public static String getSecondKey() {
        return LocalDateTime.now().format(KEY_FORMATTER_SECOND);
    }

   /**
     * 毫秒精度的KEY
     */
    public static String getMilliSecondKey() {
        return LocalDateTime.now().format(KEY_FORMATTER_MILLISECOND);
    }

    // ====================== 字符串转日期 ======================
    /**
     * 解析日期字符串（自动匹配预定义格式）
     * @param text 日期字符串
     * @return 解析后的LocalDateTime（失败返回null）
     */
    public static LocalDateTime parse(String text) {
        return parse(text, DEFAULT_FORMATTER, DATE_FORMATTER, TIME_FORMATTER);
    }

    /**
     * 解析日期字符串（指定格式）
     * @param text 日期字符串
     * @param pattern 格式模式
     */
    public static LocalDateTime parse(String text, String pattern) {
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // 递归尝试多个格式（私有方法）
    private static LocalDateTime parse(String text, DateTimeFormatter... formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(text, formatter);
            } catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    // ====================== 日期计算 ======================
    /**
     * 日期加减操作
     * @param date 原始日期
     * @param amount 数量（可为负数）
     * @param unit 时间单位（ChronoUnit枚举）
     */
    public static LocalDateTime calculate(LocalDateTime date, long amount, ChronoUnit unit) {
        return date.plus(amount, unit);
    }

    /**
     * 获取某月的最后一天
     */
    public static LocalDateTime lastDayOfMonth(LocalDateTime date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    // ====================== 日期比较 ======================
    /**
     * 计算两个日期的间隔
     */
    public static long between(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
        return unit.between(start, end);
    }

    // ====================== 时区转换 ======================
    /**
     * 转换为指定时区的时间
     */
    public static ZonedDateTime convertZone(LocalDateTime dateTime, ZoneId fromZone, ZoneId toZone) {
        return dateTime.atZone(fromZone).withZoneSameInstant(toZone);
    }

    // ====================== 格式转换 ======================
    /**
     * 转换日期格式
     */
    public static String convertFormat(LocalDateTime dateTime, String fromPattern, String toPattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(toPattern));
    }

    // ====================== 实用方法 ======================
    /**
     * 获取今日开始时间（00:00:00）
     */
    public static LocalDateTime startOfToday() {
        return LocalDateTime.now().with(LocalTime.MIN);
    }

    /**
     * 获取今日结束时间（23:59:59.999）
     */
    public static LocalDateTime endOfToday() {
        return LocalDateTime.now().with(LocalTime.MAX);
    }

    /**
     * 获取本周第一天（周一）
     */
    public static LocalDateTime firstDayOfWeek() {
        return LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
}