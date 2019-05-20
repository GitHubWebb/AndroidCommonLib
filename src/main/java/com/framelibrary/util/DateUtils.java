package com.framelibrary.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 */

public class DateUtils {

    /**
     * time of last click
     */
    private static long lastClickTime;

    /**
     * switch of limit for click
     */
    private static boolean clickLimit = false;
    private static int intervalTime = 500; //间隔时长

    public static void setClickLimit(boolean clickLimitParam) {
        clickLimit = clickLimitParam;
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD > intervalTime)
            lastClickTime = time;
        if (!clickLimit)
            return timeD < 0;
        return timeD <= intervalTime;
    }

    /**
     * 日期格式
     */
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    /**
     * 时间格式
     */
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    /**
     * 格式化时间转换为日期 2003-01-01 00:00:00 转换为 2003-01-01
     *
     * @param time 时间
     * @return 日期
     */
    public static String formatTimeToDate(String time) {
        try {
            if (StringUtils.isBlank(time))
                return "";
            Date date = simpleDateFormat.parse(time);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 格式化时间转换为日期 2003-01-01 00:00:00 转换为 2003-01-01
     *
     * @param time 时间
     * @return 日期
     */
    public static String formatTimeToStr(Date time) {
        if (null == time)
            return "";
        return dateFormat.format(time);
    }


    /**
     * 时间日期转换
     *
     * @param string 需要转换的字符串
     * @return 结果
     */

    public static String birthdayDateConvert(String string, boolean showDay) {
        if (StringUtils.isBlank(string)) return "";
        StringBuilder result = new StringBuilder();
        if (string.contains("-")) {
            String[] strings = string.split("-");
            if (strings.length > 0) {
                result.append(strings[0]).append("年").append(strings[1]).append("月");
                if (showDay) {
                    String day = strings[2];
                    String[] dayTime = day.split(" ");
                    result.append(dayTime[0]).append("日");
                    try {
                        result.append(dayTime[1]);
                    } catch (Exception e) {
                    }
                }
            } else {
                //没有时间  将 - 忽略显示
                result.append("");
            }
        } else {
            int yearIndex = string.indexOf("年");
            if (yearIndex > 0) {
                result.append(string.substring(0, yearIndex)).append("-");
            }
            int monthIndex = string.indexOf("月");
            if (monthIndex > 0) {
                if (monthIndex - yearIndex == 2) {
                    result.append("0");
                }
                result.append(string.substring(yearIndex + 1, monthIndex)).append("-");

            }
            result.append("01");
        }
        String time = "";
        int existDay = result.lastIndexOf("日");
        if (existDay > 0)
            time = result.substring(0, existDay + 1);
        else
            time = result.toString();

        return time;
    }

    // 将传入时间与当前时间进行对比，是否今天\昨天\前天\同一年
    public static String getDaySDFTime(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) return "";
        String timeStr = dateStr;
        try {
            Date currenTimeZone = simpleDateFormat.parse(dateStr);
            Calendar calendar_old = Calendar.getInstance();
            calendar_old.setTime(currenTimeZone);
            Calendar calendar_now = Calendar.getInstance();
            int calendarYear_now = calendar_now.get(Calendar.YEAR);
            int calendarYear_old = calendar_old.get(Calendar.YEAR);
            int calendarMonth_now = calendar_now.get(Calendar.MONTH) + 1;
            int calendarMonth_old = calendar_old.get(Calendar.MONTH) + 1;
            int calendarDate_now = calendar_now.get(Calendar.DATE);
            int calendarDate_old = calendar_old.get(Calendar.DATE);
            int calendarhour_of_day_old = calendar_old.get(Calendar.HOUR_OF_DAY);
            int calendarminute_old = calendar_old.get(Calendar.MINUTE);

            if (calendarYear_old == calendarYear_now) {
                if (calendarMonth_old == calendarMonth_now) {
                    if (calendarDate_old == calendarDate_now) {
                        timeStr = (calendarhour_of_day_old < 10 ? "0" + calendarhour_of_day_old : calendarhour_of_day_old) + ":" +
                                (calendarminute_old < 10 ? "0" + calendarminute_old : calendarminute_old);
                        return timeStr;
                    } else {
                        if (calendarDate_now - (calendar_old.get(Calendar.DATE)) == 1) {
                            timeStr = "昨天";
                        }

                    }
                    return (calendarMonth_old < 10 ? "0" + calendarMonth_old : calendarMonth_old) + "-" + (calendarDate_old < 10 ? "0" + calendarDate_old : calendarDate_old)
                            + " " + (calendarhour_of_day_old < 10 ? "0" + calendarhour_of_day_old : calendarhour_of_day_old) + ":" +
                            (calendarminute_old < 10 ? "0" + calendarminute_old : calendarminute_old);
                }
                return (calendarMonth_old < 10 ? "0" + calendarMonth_old : calendarMonth_old) + "-" + (calendarDate_old < 10 ? "0" + calendarDate_old : calendarDate_old)
                        + " " + (calendarhour_of_day_old < 10 ? "0" + calendarhour_of_day_old : calendarhour_of_day_old) + ":" +
                        (calendarminute_old < 10 ? "0" + calendarminute_old : calendarminute_old);
            }

        } catch (ParseException e) {
            return "";
        }
        return timeStr;
    }

    // 根据时间 str 判断 是否传入时间为过期时间
    public static boolean isExpireTime(String expireTimeStr) {
        long expireTimeParse = parse(expireTimeStr);
        long currentTimeMillis = System.currentTimeMillis();
        if (expireTimeParse > currentTimeMillis) {
            return false;
        }
        return true;
    }

    /**
     * 将毫秒转化为 分钟：秒 的格式
     *
     * @param millisecond 毫秒
     * @return
     */
    public static String formatTime(long millisecond) {
        int minute;//分钟
        int second;//秒数
        minute = (int) ((millisecond / 1000) / 60);
        second = (int) ((millisecond / 1000) % 60);
        if (minute < 10) {
            if (second < 10) {
                return "0" + minute + ":" + "0" + second;
            } else {
                return "0" + minute + ":" + second;
            }
        } else {
            if (second < 10) {
                return minute + ":" + "0" + second;
            } else {
                return minute + ":" + second;
            }
        }
    }


    /**
     * 默认格式化时间规则
     */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 精确到天的时间规则
     */
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 精确到天的时间规则 年 月 日
     */
    public static final String DATE_PATTERN_STR = "yyyy年MM月dd日";

    public static final int ERROR_PARSE_VALUE = -1;
    /**
     * 仅带分秒的时间
     */
    public static final String PATTERN_ONLY_MS_SS = "mm:ss";
    /**
     * 带毫秒的时间
     */
    public static final String PATTERN_HAS_SS = "yyyy-MM-dd HH:mm:ss:SS";

    private static final SimpleDateFormat hourTimeFormat = new SimpleDateFormat("yyyy-MM-ddHH");


    public static String format(long milliseconds) {
        return formatDateByPattern(milliseconds, DEFAULT_PATTERN);
    }

    public static long parse(String string) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_PATTERN, Locale.getDefault());
            return dateFormat.parse(string).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ERROR_PARSE_VALUE;
    }

    /**
     * 根据转换类型 修改时间
     *
     * @param dateStr
     * @return
     */
    public static String parseDateByFormat(String dateStr, String patternStr) {
        try {
            Date currenTimeZone = dateFormat.parse(dateStr);
            return formatDateByPattern(currenTimeZone.getTime(), patternStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    //时间转换 yyyy-MM-dd
    public static String formatDatePattern(long milliseconds) {
        return formatDateByPattern(milliseconds, DATE_PATTERN);
    }

    //根据 指定时间转换格式 从时间戳转换String
    public static String formatDateByPattern(long milliseconds, String datePattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern, Locale.getDefault());
        return dateFormat.format(milliseconds);
    }

    public static String formatDateHasMs(long milliseconds) {
        return formatDateByPattern(milliseconds, PATTERN_HAS_SS);
    }

    public static String formatDateOnlyMs(long milliseconds) {
        return formatDateByPattern(milliseconds, PATTERN_ONLY_MS_SS);
    }

    public static String formatHourTime(long time) {
        String result = hourTimeFormat.format(new Date(time));
        return result;
    }

    public static void main(String[] args) {
        System.out.println(formatHourTime(System.currentTimeMillis() - 60 * 60 * 1000));
    }

}
