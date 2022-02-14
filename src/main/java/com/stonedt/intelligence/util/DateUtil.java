package com.stonedt.intelligence.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;

/**
 * @param
 * @description: 时间工具类 <br>
 * @version: 1.0 <br>
 * @date: 2020/4/14 13:37 <br>
 * @author: huajiancheng <br>
 * @return
 */

public class DateUtil {

    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";
    
    
    
    public static Timestamp getZero() {
    	 long current=System.currentTimeMillis();//当前时间毫秒数
         long zero=current/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
         return new Timestamp(zero);
	}

    /**
     * 获取当前时间 yyyy-MM-dd HH:mm:ss
     */
    public static String nowTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowTimeString = LocalDateTime.now().format(dateTimeFormatter);
        return nowTimeString;
    }

    /**
     * @param [time 日期]
     * @return long
     * @description: 日期转时间戳 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/14 13:42 <br>
     * @author: huajiancheng <br>
     */

    public static long toTimestamp(String time) {
        long times = 0;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(time);
            times = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }


    public static Date parse(String str, String pattern, Locale locale) {
        if (str == null || pattern == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(pattern, locale).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param [time 时间]
     * @return java.lang.String
     * @description: 时间转时间戳 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/14 13:43 <br>
     * @author: huajiancheng <br>
     */

    public static String stampToDate(String time) {
        String res = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long lt = new Long(time);
            Date date = new Date(lt);
            res = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }


    /**
     * @param timeStr
     * @param format
     * @param toformat
     * @return
     */
    public static String convertYear(String timeStr, String format, String toformat) {
        try {
            Date date = new SimpleDateFormat(format).parse(timeStr);
            String now = new SimpleDateFormat(toformat).format(date);
            return now;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 格式化明镜的时间时间转几小时前
     *
     * @param args
     */
    //时间转换
    public static String format(Date date) {
        long delta = new Date().getTime() - date.getTime();
        if (delta < 1L * ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }


    /**
     * 获取当前年月日时分秒
     *
     * @return (String)年月日时分秒
     */
    public static String getDate() {
        //获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();

        //当前时间starttimeDate
        calendar.setTime(new Date());
        Date starttimeDate = calendar.getTime();

        return format.format(starttimeDate);
    }

    /**
     * 获取当前年月日
     *
     * @return (String)年月日
     */
    public static String getDateday() {
        //获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        //当前时间starttimeDate
        calendar.setTime(new Date());
        Date starttimeDate = calendar.getTime();

        return format.format(starttimeDate);
    }

    /**
     * 获取当前年
     *
     * @return (String)年
     */
    public static String getDateyear() {
        //获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        Calendar calendar = Calendar.getInstance();

        //当前时间starttimeDate
        calendar.setTime(new Date());
        Date starttimeDate = calendar.getTime();

        return format.format(starttimeDate);
    }

    /**
     * 根据传入的参数，推算当前时间-参数分钟后的时间
     *
     * @param num
     * @return (String)年月日时分秒
     */
    public static String minusMinute(int num) {
        //获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        //当前时间starttimeDate
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, -num);
        Date starttimeDate = calendar.getTime();

        return format.format(starttimeDate);
    }

    /**
     * 根据传入的参数，推算当前时间-参数小时后的时间
     *
     * @param num
     * @return (String)年月日时分秒
     */
    public static String minusHour(int num) {
        //获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        //当前时间starttimeDate
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, -num);
        Date starttimeDate = calendar.getTime();

        return format.format(starttimeDate);
    }

    /**
     * 根据传入的参数，推算当前时间-参数天数后的时间
     *
     * @param num
     * @return (String)年月日时分秒
     */
    public static String minusDay(int num) {
        //获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        //当前时间starttimeDate
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -num);
        Date starttimeDate = calendar.getTime();

        return format.format(starttimeDate);
    }

    /**
     * 获取数字
     *
     * @param str
     * @return (String)字符串中的数字
     */
    public static String getNumber(String str) {
        String regEx = "[^0-9]";
        Pattern pattern = Pattern.compile(regEx);
        return pattern.matcher(str).replaceAll("").trim();
    }

    /**
     * 判断字符串是否包含中文
     *
     * @param str
     * @return boolean
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }


    /**
     * 判断输入时间是否超过当前时间，或小于1999年，如果符合则返回当前时间
     *
     * @param strSuccess
     * @return strSuccess or nowDate
     */
    public static String exceedToDay(String strSuccess) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date nowDate = df.parse(getDate());
            Date inputDate = df.parse(strSuccess);
            Date oldDate = df.parse("1998-12-31 23:59:59");

            if (nowDate.getTime() > inputDate.getTime() && inputDate.getTime() > oldDate.getTime())//比较时间大小,如果输入时间小于当前时间，且大于1998年12月31日之后
            {
                return strSuccess;
            }
            return getDate();
        } catch (Exception e) {
            return getDate();
        }
    }


    /**
     * 传入各种类型的时间，返回统一的格式
     *
     * @param dateStr
     * @return
     */
    public static String FormatDate(String dateStr) throws Exception {

        //如果为null或为空字符串，则返回当前时间
        if (dateStr == null || dateStr.equals("")) {
            return DateUtil.getDate();
        }
        //如果为xxx前，则分别判断并根据当前时间反推正确时间
        if (dateStr.contains("前")) {
            try {
                if (dateStr.contains("分钟")) {
                    dateStr = dateStr.substring(0, dateStr.indexOf("分钟"));
                    return DateUtil.minusMinute(Integer.parseInt(dateStr));
                } else if (dateStr.contains("小时")) {
                    dateStr = dateStr.substring(0, dateStr.indexOf("小时"));
                    return DateUtil.minusHour(Integer.parseInt(dateStr));
                } else if (dateStr.contains("天")) {
                    dateStr = dateStr.substring(0, dateStr.indexOf("天"));
                    return DateUtil.minusDay(Integer.parseInt(dateStr));
                } else {
                    return DateUtil.getDate();
                }
            } catch (Exception e) {
                return DateUtil.getDate();
            }
        }
        //如果文章包含关键词"来源"，则将直接提取年份之后的时间
        if (dateStr.contains("来源")) {
            try {
                dateStr = dateStr.substring(dateStr.indexOf(DateUtil.getDateyear()));
            } catch (Exception e) {
                try {
                    dateStr = dateStr.substring(dateStr.indexOf(DateUtil.getDateyear().substring(2)));
                } catch (Exception e2) {
                    // TODO: handle exception
                }
            }

        }
        //如果不为xxxx年xx月xx日的格式，则将空缺处补0或20
        if (dateStr.indexOf("日") != 10 && dateStr.indexOf("日") != -1) {
            try {
                if (dateStr.indexOf("年") != 4) {
                    dateStr = "20" + dateStr;
                }
                if (dateStr.indexOf("月") != 7) {
                    dateStr = dateStr.substring(0, 5) + "0" + dateStr.substring(5);
                }
                if (dateStr.indexOf("日") != 10) {
                    dateStr = dateStr.substring(0, 8) + "0" + dateStr.substring(8);
                }
            } catch (Exception e) {
                return DateUtil.getDate();
            }

        }
        //经过以上处理之后，如果还有中文，则全部转换为数字类型。
        if (DateUtil.isContainChinese(dateStr)) {
            dateStr = DateUtil.getNumber(dateStr);
        }

        HashMap<String, String> dateRegFormat = new HashMap<String, String>();
        //以-或/等字符分隔的时间格式
        dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$", "yyyy-MM-dd-HH-mm-ss");//2014年3月12日 13时5分34秒，2014-03-12 12:05:34，2014/3/12 12:5:34
        dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}$", "yyyy-MM-dd-HH-mm");//2014-03-12 12:05
        dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}$", "yyyy-MM-dd-HH");//2014-03-12 12
        dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}$", "yyyy-MM-dd");//2014-03-12
        dateRegFormat.put("^\\d{4}\\D+\\d{1,2}$", "yyyy-MM");//2014-03
        dateRegFormat.put("^\\d{4}$", "yyyy");//2014
        //以年月日 时分秒分隔的格式。
        dateRegFormat.put("^\\d{4}\\u5e74+\\d{1,2}\\u6708+\\d{1,2}\\u65e5+\\d{1,2}", "yyyy-MM-dd-HH");//2014年03月12日 12
        dateRegFormat.put("^\\d{4}\\u5e74+\\d{1,2}\\u6708+\\d{1,2}\\u65e5$", "yyyy-MM-dd");//2014年03月12日
        dateRegFormat.put("^\\d{4}\\u5e74+\\d{1,2}\\u6708$", "yyyy-MM");//2014年03月
        dateRegFormat.put("^\\d{4}\\u5e74$", "yyyy");//2014年
        //没有间隔的格式
        dateRegFormat.put("^\\d{14}$", "yyyyMMddHHmmss");//20140312120534
        dateRegFormat.put("^\\d{12}$", "yyyyMMddHHmm");//201403121205
        dateRegFormat.put("^\\d{10}$", "yyyyMMddHH");//2014031212
        dateRegFormat.put("^\\d{8}$", "yyyyMMdd");//20140312
        dateRegFormat.put("^\\d{6}$", "yyyyMM");//201403
        dateRegFormat.put("^\\d{2}\\s*:\\s*\\d{2}\\s*:\\s*\\d{2}$", "yyyy-MM-dd-HH-mm-ss");//13:05:34 拼接当前日期
        dateRegFormat.put("^\\d{2}\\s*:\\s*\\d{2}$", "yyyy-MM-dd-HH-mm");//13:05 拼接当前日期
        dateRegFormat.put("^\\d{2}\\D+\\d{1,2}\\D+\\d{1,2}$", "yy-MM-dd");//14.10.18(年.月.日)
        dateRegFormat.put("^\\d{1,2}\\D+\\d{1,2}$", "yyyy-dd-MM");//30.12(日.月) 拼接当前年份
        dateRegFormat.put("^\\d{1,2}\\D+\\d{1,2}\\D+\\d{4}$", "dd-MM-yyyy");//12.21.2013(日.月.年)

        String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat formatter2;
        String dateReplace;
        String strSuccess = "";
        try {
            for (String key : dateRegFormat.keySet()) {
                if (Pattern.compile(key).matcher(dateStr).matches()) {
                    formatter2 = new SimpleDateFormat(dateRegFormat.get(key));
                    if (key.equals("^\\d{2}\\s*:\\s*\\d{2}\\s*:\\s*\\d{2}$")
                            || key.equals("^\\d{2}\\s*:\\s*\\d{2}$")) {//13:05:34 或 13:05 拼接当前日期
                        dateStr = curDate + "-" + dateStr;
                    } else if (key.equals("^\\d{1,2}\\D+\\d{1,2}$")) {//21.1 (日.月) 拼接当前年份
                        dateStr = curDate.substring(0, 4) + "-" + dateStr;
                    }
                    dateReplace = dateStr.replaceAll("\\D+", "-");
                    strSuccess = formatter1.format(formatter2.parse(dateReplace));
                    break;
                }
            }
        } catch (Exception e) {
            //如果异常，则返回当前时间
            return DateUtil.getDate();
        } finally {
            if (strSuccess.length() == 0) {
                //如果赋值的字符串长度为0，则代表不正常。返回当前时间
                return DateUtil.getDate();
            }
            return DateUtil.exceedToDay(strSuccess);
        }
    }


    public static String getDateTimeNow() {
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ofPattern.format(LocalDateTime.now());
    }

    public static Map<String, String> twentyFourHours() {
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String endTime = ofPattern.format(localDateTime);
        String startTime = ofPattern.format(localDateTime.minusHours(24));
        Map<String, String> time = new HashMap<String, String>();
        time.put("startTime", startTime);
        time.put("endTime", endTime);
        return time;
    }

    //历史走势时间处理
    public static List<Map<String, String>> historicalTrend(String startTime, String endTime) {
        List<Map<String, String>> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        Calendar yearCalendar = Calendar.getInstance();
        int days = 0;
        try {
            startCalendar.setTime(sdf.parse(startTime));
            endCalendar.setTime(sdf.parse(endTime));
            int day1 = startCalendar.get(Calendar.DAY_OF_YEAR);
            int day2 = endCalendar.get(Calendar.DAY_OF_YEAR);
            int year1 = startCalendar.get(Calendar.YEAR);
            int year2 = endCalendar.get(Calendar.YEAR);
            if (year1 != year2) {  //不同年
                int timeDistance = 0;
                for (int i = year1; i < year2; i++) {
                    if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {  //闰年
                        timeDistance += 366;
                    } else {  //平年
                        timeDistance += 365;
                    }
                }
                days = timeDistance + (day2 - day1);
            } else { //同一年
                days = day2 - day1;
            }
            //按天算
            for (int i = 0; i <= days; i++) {
                String nowDate = sdf.format(endCalendar.getTime());
                yearCalendar.setTime(endCalendar.getTime());
                yearCalendar.add(Calendar.YEAR, -1);
                String yearDate = sdf.format(yearCalendar.getTime());
                endCalendar.add(Calendar.DATE, -1);
                Map<String, String> map = new HashMap<String, String>();
                map.put("nowDate", nowDate);
                map.put("yearDate", yearDate);
                result.add(map);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 数据监测
    public static String dataMonitorYYYYMMDDHHMMSS(Integer timeType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String times = "";
        String timee = "";
        if (timeType == 0) {
            timee = sdf.format(calendar.getTime());
            times = timee.substring(0, 10) + " 00:00:00";
        } else if (timeType == 1) {
            calendar.add(Calendar.DATE, -1);
            timee = sdf2.format(calendar.getTime()) + " 23:59:59";
            times = sdf2.format(calendar.getTime()) + " 00:00:00";
        } else if (timeType == 2) {
            timee = sdf.format(calendar.getTime());
            calendar.add(Calendar.DATE, -7);
            times = sdf.format(calendar.getTime());
        } else if (timeType == 3) {
            timee = sdf.format(calendar.getTime());
            calendar.add(Calendar.DATE, -30);
            times = sdf.format(calendar.getTime());
        } else if (timeType == 5) {
            timee = sdf.format(calendar.getTime());
            calendar.add(Calendar.DATE, -1);
            times = sdf.format(calendar.getTime());
        }
        return times + "&" + timee;
    }


    /**
     * 年月日时间转换
     * 2010年09月07日转成2010-09-07
     */
    public static String convertYear(String timeStr) {
        try {
            Date date = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss").parse(timeStr);
            String now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
            return now;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 取出当前的日期和昨天日期
     * hjc
     *
     * @param
     * @return
     */
    public static JSONObject getDifferOneDayTimes(Integer num) {
        JSONObject returnTimes = new JSONObject();
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate = new Date();
        String endTime = simple.format(endDate); // 结束时间
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, num);
        Date date = cal.getTime();
        String startTime = simple.format(date);//开始时间
        returnTimes.put("times", startTime);
        returnTimes.put("TIMEBEGIN", startTime);
        returnTimes.put("timee", endTime);
        returnTimes.put("TIMEEND", endTime);
        return returnTimes;
    }


    // 相似文章列表使用
    public static String similaritySimpleDateFormat(Integer timeType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日");
        Calendar calendar = Calendar.getInstance();
        String times = "";
        String timee = "";
        if (timeType == 0) {
            timee = sdf.format(calendar.getTime());
            times = timee.substring(0, 11) + " 00:00:00";
        } else if (timeType == 1) {
            calendar.add(Calendar.DATE, -1);
            timee = sdf2.format(calendar.getTime()) + " 23:59:59";
            times = sdf2.format(calendar.getTime()) + " 00:00:00";
        } else if (timeType == 2) {
            timee = sdf.format(calendar.getTime());
            calendar.add(Calendar.DATE, -7);
            times = sdf.format(calendar.getTime());
        } else if (timeType == 3) {
            timee = sdf.format(calendar.getTime());
            calendar.add(Calendar.DATE, -30);
            times = sdf.format(calendar.getTime());
        }
        return times.substring(0, 11) + "&" + timee.substring(0, 11);
    }


    // 全文搜索
    public static String fullTextYYYYMMDDHHMMSS(Integer timeType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String times = "";
        String timee = "";
        if (timeType == 0) {
            timee = sdf.format(calendar.getTime());
            times = timee.substring(0, 10) + " 00:00:00";
        } else if (timeType == 1) {
            timee = sdf.format(calendar.getTime());
            calendar.add(Calendar.DATE, -1);
            times = sdf.format(calendar.getTime());
        } else if (timeType == 2) {
            timee = sdf.format(calendar.getTime());
            calendar.add(Calendar.DATE, -2);
            times = sdf.format(calendar.getTime());
        } else if (timeType == 3) {
            timee = sdf.format(calendar.getTime());
            calendar.add(Calendar.DATE, -3);
            times = sdf.format(calendar.getTime());
        } else if (timeType == 4) {
            timee = sdf.format(calendar.getTime());
            calendar.add(Calendar.DATE, -7);
            times = sdf.format(calendar.getTime());
        }
        return times + "&" + timee;
    }

    //截止时间往前推两分钟
    public static String PushForwardTwoMinute(String timee) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = sdf.parse(timee);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
            calendar.add(Calendar.MINUTE, -2);
            timee = sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timee;
    }


    /**
     * 华建成
     * 2019/09/17
     * 计算时间差
     */

    public static int calculateTimeDiff(String startTime, String endTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTimeDate = null;
        Date endTimeDate = null;
        int days = 0;
        try {
            startTimeDate = format.parse(startTime);
            endTimeDate = format.parse(endTime);
            days = (int) ((endTimeDate.getTime() - startTimeDate.getTime()) / (1000 * 3600 * 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 开始时间每次增加两个小时
     * 华建成
     * 2019/09/17
     *
     * @param startTime
     * @param endTime
     */
    public static List<Date> addTime(String startTime, String endTime) {
        int days = calculateTimeDiff(startTime, endTime);
        int count = days * 12;
        List<Date> timeList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTimeDate = null;
        //Date endTimeDate = null;
        String startTimeStr = "";
        try {
            startTimeDate = format.parse(startTime);
            Calendar c = Calendar.getInstance();
            c.setTime(startTimeDate);
            for (int i = 0; i < count; i++) {
                c.add(Calendar.HOUR_OF_DAY, 2);
                Date newDate = c.getTime();
                startTimeStr = format.format(newDate);
                timeList.add(newDate);
                if (startTimeStr.equals(endTime)) {
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeList;
    }


    public static String addEndTime(String startTime, Integer reportNav) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTimeDate = null;
        //Date endTimeDate = null;
        String endTimeStr = "";
        Random random = new Random();


        int step = 0;
        if (reportNav == 1) {
            step = 1 * 24;
        } else if (reportNav == 2) {
            step = 7 * 24;
        } else if (reportNav == 3) {
            step = 30 * 24;
        } else if (reportNav == 4) {
            step = random.nextInt(10) * 24;
        }

        try {
            startTimeDate = format.parse(startTime);
            Calendar c = Calendar.getInstance();
            c.setTime(startTimeDate);
            c.add(Calendar.HOUR_OF_DAY, step);
            Date newDate = c.getTime();
            endTimeStr = format.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endTimeStr;
    }

    /**
     * @param []
     * @return java.lang.String
     * @description: 获取当前时间 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/14 13:48 <br>
     * @author: huajiancheng <br>
     */

    public static String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return sdf.format(calendar.getTime());
    }

    //同比周期
    public static Map<String, String> YearOnYearCycle(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(startTime));
            calendar.add(Calendar.YEAR, -1);
            startTime = sdf.format(calendar.getTime());
            calendar.setTime(sdf.parse(endTime));
            calendar.add(Calendar.YEAR, -1);
            endTime = sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return map;
    }

    //环比周期
    public static Map<String, String> RingRatioCycle(String startTime, String endTime) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        int days = 0;
        try {
            startCalendar.setTime(sdf1.parse(startTime));
            endCalendar.setTime(sdf1.parse(endTime));
            int day1 = startCalendar.get(Calendar.DAY_OF_YEAR);
            int day2 = endCalendar.get(Calendar.DAY_OF_YEAR);
            int year1 = startCalendar.get(Calendar.YEAR);
            int year2 = endCalendar.get(Calendar.YEAR);
            if (year1 != year2) {  //不同年
                int timeDistance = 0;
                for (int i = year1; i < year2; i++) {
                    if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {  //闰年
                        timeDistance += 366;
                    } else {  //平年
                        timeDistance += 365;
                    }
                }
                days = timeDistance + (day2 - day1);
            } else { //同一年
                days = day2 - day1;
            }
            startCalendar.add(Calendar.DATE, -days);
            endCalendar.add(Calendar.DATE, -days);
            startTime = sdf1.format(startCalendar.getTime());
            endTime = sdf1.format(endCalendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return map;
    }


    /**
     * @param oldDateStr 需要转换的字符串
     * @description 日期格式转换yyyy-MM-dd'T'HH:mm:ss.SSSXXX  (yyyy-MM-dd'T'HH:mm:ss.SSSZ) TO  yyyy-MM-dd HH:mm:ss
     * 2020-03-17T17:11:19.000+08:00
     * @author hjc
     */
    public static String dealDateFormat(String oldDateStr) {
        String returnStr = "";
        try {
            //此格式只有  jdk 1.7才支持  yyyy-MM-dd‘T‘HH:mm:ss.SSSXXX
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            Date date = df.parse(oldDateStr);
            SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
            Date date1 = df1.parse(date.toString());
            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            returnStr = df2.format(date1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnStr;
    }


    public static JSONObject dateRoll(Date date, int i, int d) {
        JSONObject timeJson = new JSONObject();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取Calendar对象并以传进来的时间为准
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 将现在的时间滚动固定时长,转换为Date类型赋值
        calendar.add(i, d);
        // 转换为Date类型再赋值
        date = calendar.getTime();

        String timee = format.format(new Date());
        String times = format.format(date);
        timeJson.put("times", times);
        timeJson.put("timee", timee);
        return timeJson;
    }
    
    /**
	 * 获取当前时间前一小时的时间
	 * @return (String)年月日时分秒
	 */
	public static List<String> beforeOneHourToNowDate() {
		Calendar calendar = Calendar.getInstance();
		List<String> listTime = new ArrayList<>();
		/* HOUR_OF_DAY 指示一天中的小时 */
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:05:00");
		
		listTime.add(df.format(calendar.getTime()));
		listTime.add(df.format(new Date()));
//		System.out.println("一个小时前的时间：" + df.format(calendar.getTime()));

//		System.out.println("当前的时间：" + df.format(new Date()));
		return listTime;
	}


//    public static JSONObject getTimeByHour() {
//        JSONObject timeJson = new JSONObject();
//        try {
//        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateRoll(new Date(), Calendar.HOUR, -24));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return timeJson;
//    }
	
	/**
	 * 获取开始时间，结束时间
	 * @param date
	 * @param i
	 * @param d
	 * @return
	 */
	 public static JSONObject dateRoll2(Date date, int i, int d) {
	        JSONObject timeJson = new JSONObject();
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        // 获取Calendar对象并以传进来的时间为准
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(date);
	        // 将现在的时间滚动固定时长,转换为Date类型赋值
	        calendar.add(i, d);
	        // 转换为Date类型再赋值
	        Date date1 = calendar.getTime();

	        String timee = format.format(date);
	        String times = format.format(date1);
	        timeJson.put("times", times);
	        timeJson.put("timee", timee);
	        return timeJson;
	  }
	 
	 /**
	  * 获取前某一年
	  * @param num
	  * @return
	  */
	 public static Date getDateByYear(int num) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, num);
        Date date = cal.getTime();
        return date;
	 }
	 
	 
	 /**
	     * 根据传入的参数，推算指定时间-参数天数后的时间
	     *
	     * @param num
	     * @return (String)年月日时分秒
	     */
	    public static Timestamp timeMinusDay(Timestamp nowTime,int num) {
	        //获取当前时间
//	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Calendar calendar = Calendar.getInstance();
	        //当前时间starttimeDate
	        calendar.setTime( new Date(nowTime.getTime()));
	        calendar.add(Calendar.DATE, -num);
	        Date starttimeDate = calendar.getTime();

	        return new Timestamp(starttimeDate.getTime());
	    }
	    /**
	     * 时间戳转时间
	     * @param time
	     * @return
	     */
	    public static String timeStamp2Date(Long timeLong) {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//要转换的时间格式
	        Date date;
	        try {
	            date = sdf.parse(sdf.format(timeLong));
	            return sdf.format(date);
	        } catch (ParseException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }  
	 
    public static void main(String[] args) throws ParseException {
    	
    	System.out.println(timeMinusDay(new Timestamp(new Date().getTime()),0));
    }

}
