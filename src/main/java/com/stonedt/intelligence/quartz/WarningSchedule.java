package com.stonedt.intelligence.quartz;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.constant.MonitorConstant;
import com.stonedt.intelligence.dao.SystemDao;
import com.stonedt.intelligence.entity.WarningSetting;
import com.stonedt.intelligence.service.EarlyWarningService;
import com.stonedt.intelligence.service.ProjectService;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.MyHttpRequestUtil;
import com.stonedt.intelligence.util.ProjectWordUtil;
import com.stonedt.intelligence.util.SendMailFox;
import com.stonedt.intelligence.util.SnowFlake;
import com.stonedt.intelligence.util.TextUtil;

/**
 * <p>预警定时任务</p>
 * <p>Title: WarningSchedule</p>
 * <p>Description: </p>
 *
 * @author Mapeng
 * @date Apr 18, 2020
 */
@Component
public class WarningSchedule {

    public static final Logger logger = LoggerFactory.getLogger(WarningSchedule.class);

    public static final String searchearlywarningApi = "/yqsearch/searchlist"; //预警文章获取

    public SnowFlake snowFlake = new SnowFlake();

    @Value("${es.search.url}")
    private String es_search_url;
    @Value("${schedule.warning.open}")
    private int warning_popup;
    @Value("${system.url}")
    private String system_url;

    @Autowired
    private SystemDao systemDao;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private EarlyWarningService earlyWarningService;

    //	@Scheduled(fixedRate = 10000000)
    @Scheduled(cron = "0 0/20 * * * ?")
    public void start() throws ParseException {
        try {
            if (warning_popup != 1) return;
            logger.info("预警开始......");
            List<WarningSetting> listWarning = systemDao.listWarningMsg();//预警列表
            if (listWarning.size() > 0) {
                Date date = new Date();
                String nowTime = DateUtil.nowTime();
                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                for (int i = 0; i < listWarning.size(); i++) {
                    try {
                        Integer weekend_warning = listWarning.get(i).getWeekend_warning();
                        if (weekend_warning == 0) {
                            if (isWeekend(nowTime)) {
                                continue;
                            }
                        }
                        JSONObject warning_receive_time = JSONObject.parseObject(listWarning.get(i).getWarning_receive_time());
                        String start = warning_receive_time.getString("start");
                        String end = warning_receive_time.getString("end");
                        // 判断 当前时间 是否在预警时间范围内
                        if (belongCalendar(df.parse(df.format(date)), start, end)) {
                            JSONObject warning_interval = JSONObject.parseObject(listWarning.get(i).getWarning_interval());
                            int interval_type = warning_interval.getIntValue("type");
                            if (interval_type == 1) {
                                // 实时预警
                                search(listWarning.get(i), nowTime, 2000);
                            } else {
                                // 定时预警
                                int interval_time = warning_interval.getIntValue("time");//预警间隔
                                //判断当前时间 是否 等于 预警开启时间
                                String nowHhMm = df.format(date);
                                if (nowHhMm.equals(start)) {
                                    search(listWarning.get(i), nowTime, 1440);//第一次预警，开始时间为前一天预警结束时间
                                } else {
                                    //判断启动时间和现在的时间差
                                    String dateday2 = DateUtil.getDateday();
                                    String dateday = dateday2 + " " + start + ":00";
                                    int distanceTime = (int) getDistanceTime(nowTime, dateday);
                                    if (distanceTime % interval_time == 0) {//判断预警间隔
                                        if (distanceTime == 0) {
                                            search(listWarning.get(i), nowTime, 60);
                                        } else {
                                            search(listWarning.get(i), nowTime, (distanceTime * 60));
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            logger.info("预警开始......");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询预警消息
     */
    public void search(WarningSetting warningSetting, String nowtime, Integer time) {
        try {
            Map<String, Object> projectByProId = projectService.getProjectByProId(warningSetting.getProject_id());
            projectByProId.get("subject_word");
            String subject_word = String.valueOf(projectByProId.get("subject_word"));
            String character_word = String.valueOf(projectByProId.get("character_word"));
            String event_word = String.valueOf(projectByProId.get("event_word"));
            String regional_word = String.valueOf(projectByProId.get("regional_word"));
            String stop_word = String.valueOf(projectByProId.get("stop_word"));
            Integer projectType = Integer.parseInt(String.valueOf(projectByProId.get("project_type")));
//            String listKeywords = "";
//            if (subject_word.length() > 0 && !subject_word.equals("null")) {
//                listKeywords = listKeywords + subject_word + ",";
//            }
//            if (character_word.length() > 0 && !character_word.equals("null")) {
//                listKeywords = listKeywords + character_word + ",";
//            }
//            if (event_word.length() > 0 && !event_word.equals("null")) {
//                listKeywords = listKeywords + event_word + ",";
//            }
//            if (regional_word.length() > 0 && !regional_word.equals("null")) {
//                listKeywords = listKeywords + regional_word + ",";
//            }
            String highKeyword = subject_word;
            if (projectType == 2) {
                highKeyword = ProjectWordUtil.highProjectKeyword(subject_word, regional_word, character_word, event_word);
                stop_word = ProjectWordUtil.highProjectStopword(stop_word);
            }
            if (projectType == 1) {
            	projectType = 2;
                highKeyword = ProjectWordUtil.QuickProjectKeyword(subject_word);
                stop_word = ProjectWordUtil.highProjectStopword(stop_word);
            }
            
            String listStopwords = stop_word;
            //预警词
            String yjword = warningSetting.getWarning_word();
            //分类
            String classify = warningSetting.getWarning_classify();
            //匹配方式  0全文 1标题 2正文
            String sitesearchtype = String.valueOf(warningSetting.getWarning_match());
            String origintype = sitesearchtype;
            //预警内容 0 全部 1敏感
            String jycon = String.valueOf(warningSetting.getWarning_content());
            String emotionalIndex = "1";
            if ("0".equals(jycon)) {
                emotionalIndex = "1,2,3";
            }
            String province = "国家";
            String city = "国家";
            if (city.indexOf("市辖区") != -1) {
                city = province;
            } else if (city.indexOf("省直辖县级行政区划") != -1) {
                city = "";
            }
            if ("国家".equals(province)) {
                province = "";
                city = "";
            } else {
                if (province.indexOf("新疆兵团") != -1) {
                    province = "新疆维吾尔自治区";
                    city = "";
                }
            }
            //相似文章合并（0：取消合并 1：合并）
           
            String params = "keyword=" + highKeyword + "&searchkeyword=" + yjword + "&emotionalIndex=" + emotionalIndex + "&times=" + getTimee(nowtime, time)
                    + "&timee=" + nowtime + "&searchType=0&stopword=" + listStopwords + "&page=1&size=10&origintype=" + origintype
                    + "&classify=" + classify + "&province=" + province + "&city=" + city+"&projecttype="+projectType;
            if(warningSetting.getWarning_similar()==0) {
            String urls = es_search_url + searchearlywarningApi;
            System.err.println(urls + "?" + params);
            logger.info("预警查询es开始......");
            String esEarlywarning = MyHttpRequestUtil.sendPostEsSearch(urls, params);
            JSONObject Earlywarnings = JSONObject.parseObject(esEarlywarning);
            logger.info("预警查询结束......共计：{}", Earlywarnings.getInteger("count"));
            if (Earlywarnings.getInteger("code") == 200 && Earlywarnings.getInteger("count") > 0) {
                JSONArray jsonArray = Earlywarnings.getJSONArray("data");
                JSONObject warning_source = JSONObject.parseObject(warningSetting.getWarning_source());
                int email_type = warning_source.getIntValue("type");

                Boolean emailpushboolean = false;//是否是邮箱预警
                Boolean systempush = false;//是否是系统预警
                if (email_type == 2) {
                    emailpushboolean = true;
                } else {
                    systempush = true;
                }
                String emailHtml = emailHtml(nowtime, warningSetting, jsonArray.size());
                for (int i = 0; i < jsonArray.size(); i++) {
                    try {
                        JSONObject Earlywarning = jsonArray.getJSONObject(i).getJSONObject("_source");
                        String title = Earlywarning.getString("title").split("_http")[0];
                        String content = Earlywarning.getString("content");
                        if (content.length() > 255) {
                            content = content.substring(0, 254);
                        }
//                        String text = title + content;
//                        String relatedWords = TextUtil.getRelatedWords(listKeywords, yjword, text);
//                        if (org.apache.commons.lang3.StringUtils.isNotEmpty(relatedWords)) {
//                            relatedWords = relatedWords.substring(0, relatedWords.length() - 1);
//                        }
                        String sourcewebsitename = Earlywarning.getString("sourcewebsitename");
                        String publish_time = Earlywarning.getString("publish_time");
                        String article_public_id = Earlywarning.getString("article_public_id");
                        Integer similarvolume = Earlywarning.getInteger("similarvolume");
                        String emotionalIndex1 = Earlywarning.getString("emotionalIndex");
                        String url = system_url + "/monitor/detail/" + article_public_id;
                        if (systempush) {//系统预警
                            Map<String, Object> warning_popup = new HashMap<>();
                            warning_popup.put("create_time", DateUtil.nowTime());
                            warning_popup.put("warning_article_id", snowFlake.getId());
                            warning_popup.put("user_id", warningSetting.getUser_id());
                            warning_popup.put("popup_id", snowFlake.getId());
                            warning_popup.put("popup_content", content);
                            warning_popup.put("popup_time", DateUtil.nowTime());
                            warning_popup.put("article_id", article_public_id);
                            warning_popup.put("article_time", publish_time);
                            warning_popup.put("article_title", title);
                            warning_popup.put("article_emotion", emotionalIndex1);
                            warning_popup.put("status", 0);
                            warning_popup.put("project_id", warningSetting.getProject_id());
                            warning_popup.put("read_status", 0);
                            Map<String, Object> article_detail = new HashMap<>();
                            article_detail.put("sourcewebsitename", sourcewebsitename);
                            warning_popup.put("article_detail", JSON.toJSONString(article_detail));
                            // 入库
                            try {
                                boolean warning_popupresult = earlyWarningService.saveWarningPopup(warning_popup);
                                if (warning_popupresult) {
                                    logger.info("预警推送插入成功");
                                } else {
                                    logger.error("预警推送插入失败");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (emailpushboolean) { //邮箱预警
                            if (content.length() > 180) {
                                content = content.substring(0, 180) + "...";
                            }
                            String econtent = jsonArray.getJSONObject(i).getJSONObject("highlight").getString("content");
                            if (econtent == null || "".equals(econtent.trim())) {
                                econtent = content;
                            }
                            JSONArray maillist = new JSONArray();
                            JSONObject maildetail = new JSONObject();
                            maildetail.put("publish_time", publish_time);
                            maildetail.put("similarvolume", similarvolume);
                            maildetail.put("sourcewebsitename", sourcewebsitename);
                            maildetail.put("article_id", article_public_id);
                            maildetail.put("title", title);
                            maildetail.put("econtent", econtent);
                            maillist.add(maildetail);
                            emailHtml += "<div class=\"content\">\r\n" +
                                    "<p>" + publish_time + " 相似文章:" + similarvolume + " 来自:" + sourcewebsitename + "</p>\r\n" +
                                    "<a href=\"" + url + "\" target=\"_blank\">" + title + "</a>\r\n" +
                                    "<div class=\"con\"> " + econtent + "\r\n" +
                                    "</div>\r\n" +
                                    "</div>";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (emailpushboolean) {
                    emailHtml += "</div> </body> </html>";
                    try {
                        String email_user = warning_source.getString("email");
                        SendMailFox.Send(email_user, "预警推送", emailHtml);
                        logger.info("预警邮件发送成功......");
                    } catch (Exception e) {
                        logger.info("预警邮件发送失败......{}", e.getMessage());
                    }
                }
            } else {
                logger.info("预警查询结束...未查询到数据......");
            }
            }else {
            	
            	String similarUrl = es_search_url + MonitorConstant.es_api_similarsearch_content;
            	String esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
            	String article_public_idStr = "";
                if (!esSimilarResponse.equals("")) {
                    List article_public_idList = new ArrayList();
                    JSONArray similarArray = JSON.parseArray(esSimilarResponse);
                    if(similarArray.size()==0) {
                        logger.info("预警查询结束...未查询到数据......");
                    	return ;
                    }
                    for (int i = 0; i < similarArray.size(); i++) {
                        JSONObject similarJson = (JSONObject) similarArray.get(i);
                        String article_public_id = similarJson.getString("article_public_id");
                        article_public_idList.add(article_public_id);
                        if (i < 10) {
                            article_public_idStr += article_public_id + ",";
                        }
                    }
                   
                }
                article_public_idStr = "&article_public_idstr="+article_public_idStr;
                String getcontenturl = es_search_url + MonitorConstant.es_api_similar_contentlist;
                String articleResponse = MyHttpRequestUtil.sendPostEsSearch(getcontenturl, article_public_idStr);
                JSONObject articleResponseJson = JSON.parseObject(articleResponse);
                logger.info("预警查询结束......共计：{}", articleResponseJson.getInteger("count"));
                if (articleResponseJson.getInteger("code") == 200 && articleResponseJson.getInteger("count") > 0) {
                    JSONArray jsonArray = articleResponseJson.getJSONArray("data");
                    JSONObject warning_source = JSONObject.parseObject(warningSetting.getWarning_source());
                    int email_type = warning_source.getIntValue("type");

                    Boolean emailpushboolean = false;//是否是邮箱预警
                    Boolean systempush = false;//是否是系统预警
                    if (email_type == 2) {
                        emailpushboolean = true;
                    } else {
                        systempush = true;
                    }
                    String emailHtml = emailHtml(nowtime, warningSetting, jsonArray.size());
                    for (int i = 0; i < jsonArray.size(); i++) {
                        try {
                            JSONObject Earlywarning = jsonArray.getJSONObject(i).getJSONObject("_source");
                            String title = Earlywarning.getString("title").split("_http")[0];
                            String content = Earlywarning.getString("content");
                            if (content.length() > 255) {
                                content = content.substring(0, 254);
                            }
//                            String text = title + content;
//                            String relatedWords = TextUtil.getRelatedWords(listKeywords, yjword, text);
//                            if (org.apache.commons.lang3.StringUtils.isNotEmpty(relatedWords)) {
//                                relatedWords = relatedWords.substring(0, relatedWords.length() - 1);
//                            }
                            String sourcewebsitename = Earlywarning.getString("sourcewebsitename");
                            String publish_time = Earlywarning.getString("publish_time");
                            String article_public_id = Earlywarning.getString("article_public_id");
                            Integer similarvolume = Earlywarning.getInteger("similarvolume");
                            String emotionalIndex1 = Earlywarning.getString("emotionalIndex");
                            String url = system_url + "/monitor/detail/" + article_public_id;
                            if (systempush) {//系统预警
                                Map<String, Object> warning_popup = new HashMap<>();
                                warning_popup.put("create_time", DateUtil.nowTime());
                                warning_popup.put("warning_article_id", snowFlake.getId());
                                warning_popup.put("user_id", warningSetting.getUser_id());
                                warning_popup.put("popup_id", snowFlake.getId());
                                warning_popup.put("popup_content", content);
                                warning_popup.put("popup_time", DateUtil.nowTime());
                                warning_popup.put("article_id", article_public_id);
                                warning_popup.put("article_time", publish_time);
                                warning_popup.put("article_title", title);
                                warning_popup.put("article_emotion", emotionalIndex1);
                                warning_popup.put("status", 0);
                                warning_popup.put("project_id", warningSetting.getProject_id());
                                warning_popup.put("read_status", 0);
                                Map<String, Object> article_detail = new HashMap<>();
                                article_detail.put("sourcewebsitename", sourcewebsitename);
                                warning_popup.put("article_detail", JSON.toJSONString(article_detail));
                                // 入库
                                try {
                                    boolean warning_popupresult = earlyWarningService.saveWarningPopup(warning_popup);
                                    if (warning_popupresult) {
                                        logger.info("预警推送插入成功");
                                    } else {
                                        logger.error("预警推送插入失败");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (emailpushboolean) { //邮箱预警
                                if (content.length() > 180) {
                                    content = content.substring(0, 180) + "...";
                                }
                                String econtent = jsonArray.getJSONObject(i).getJSONObject("highlight").getString("content");
                                if (econtent == null || "".equals(econtent.trim())) {
                                    econtent = content;
                                }
                                JSONArray maillist = new JSONArray();
                                JSONObject maildetail = new JSONObject();
                                maildetail.put("publish_time", publish_time);
                                maildetail.put("similarvolume", similarvolume);
                                maildetail.put("sourcewebsitename", sourcewebsitename);
                                maildetail.put("article_id", article_public_id);
                                maildetail.put("title", title);
                                maildetail.put("econtent", econtent);
                                maillist.add(maildetail);
                                emailHtml += "<div class=\"content\">\r\n" +
                                        "<p>" + publish_time + " 相似文章:" + similarvolume + " 来自:" + sourcewebsitename + "</p>\r\n" +
                                        "<a href=\"" + url + "\" target=\"_blank\">" + title + "</a>\r\n" +
                                        "<div class=\"con\"> " + econtent + "\r\n" +
                                        "</div>\r\n" +
                                        "</div>";
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (emailpushboolean) {
                        emailHtml += "</div> </body> </html>";
                        try {
                            String email_user = warning_source.getString("email");
                            SendMailFox.Send(email_user, "预警推送", emailHtml);
                            logger.info("预警邮件发送成功......");
                        } catch (Exception e) {
                            logger.info("预警邮件发送失败......{}", e.getMessage());
                        }
                    }
                } else {
                    logger.info("预警查询结束...未查询到数据......");
                }
            	
            	
            	
            	
            	
            	
            	
            	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param nowtime
     * @param warningSetting
     * @param warnSize       预警数量
     */
    private static String emailHtml(String nowtime, WarningSetting warningSetting, int warnSize) {
        String emailcontent = "<html>\r\n" +
                "	<head>\r\n" +
                "		<meta charset=\"utf-8\">\r\n" +
                "		<title>email</title>\r\n" +
                "		<style type=\"text/css\">\r\n" +
                "			body{\r\n" +
                "				background: url(https://cdn-a-files.yqt365.com/images/wap/bg.jpg) no-repeat;\r\n" +
                "				background-size: cover;\r\n" +
                "			}\r\n" +
                "			.main{\r\n" +
                "				width: 750px;\r\n" +
                "				margin: 0 auto;\r\n" +
                "				background: #ffffff;\r\n" +
                "				padding: 15px;\r\n" +
                "			}\r\n" +
                "			.imglogo{\r\n" +
                "				border-bottom: dashed 1px #ccc;\r\n" +
                "			}\r\n" +
                "			.title{\r\n" +
                "				margin-top:20px;\r\n" +
                "				border-bottom: #CCCCCC dashed 1px;\r\n" +
                "			}\r\n" +
                "			.content{\r\n" +
                "				width: 100%;\r\n" +
                "				border-bottom: #CCCCCC dashed 1px;\r\n" +
                "				padding-bottom: 10px;\r\n" +
                "			}\r\n" +
                "			.content:hover{\r\n" +
                "				background: #f2f2f2;\r\n" +
                "			}\r\n" +
                "			.content p{\r\n" +
                "				color: #9f9f9f;\r\n" +
                "				font-size: 10px;\r\n" +
                "			}\r\n" +
                "			.con{\r\n" +
                "				margin-top:10px;\r\n" +
                "				color: #676767;\r\n" +
                "			}\r\n" +
                "			.content a{\r\n" +
                "				text-decoration:  none;\r\n" +
                "				color: #0e7cd8;\r\n" +
                "			}\r\n" +
                "			.time{\r\n" +
                "				color:#e9610f !important;\r\n" +
                "			}\r\n" +
                "			.title span{\r\n" +
                "				color:#e02222;\r\n" +
                "			}\r\n" +
                "		</style>\r\n" +
                "	</head>\r\n" +
                "	<body>\r\n" +
                "		<div class=\"main\">\r\n" +
                "			<div class=\"imglogo\">\r\n" +
                "				<img src=\"http://www.chinapost.com.cn/res/chinapostplan/structure/181041269.png\" >\r\n" +
                "			</div>\r\n" +
                "			<div class=\"title\">" +
                "				<p>截止时间:<span class=\"time\">" + nowtime + "</span></p>	\r\n" +
                "				<p>您的监测方案<span>［" + warningSetting.getProject_name() + "］</span>新增<span>" + warnSize + "</span>条预警。显示如下:</p>	\r\n" +
                "			</div>";
        return emailcontent;
    }

    /**
     * 是否是周末
     *
     * @param time
     * @return
     * @throws ParseException
     */
    private static boolean isWeekend(String time) throws ParseException {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                return true;
            } else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断时间是否在范围内
     *
     * @param nowTime
     * @param begintime
     * @param endtime
     * @return
     * @throws ParseException
     */
    private static boolean belongCalendar(Date nowTime, String begintime, String endtime) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date beginTime = df.parse(begintime);
            Date endTime = df.parse(endtime);
            // 设置当前时间
            Calendar date = Calendar.getInstance();
            date.setTime(nowTime);
            // 设置开始时间
            Calendar begin = Calendar.getInstance();
            begin.setTime(beginTime);
            // 设置结束时间
            Calendar end = Calendar.getInstance();
            end.setTime(endTime);
            // 处于开始时间之后，和结束时间之前的判断
            if (date.after(begin) && date.before(end)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static long getDistanceTime(String str1, String str2) {
        long day = 0;
        long hour = 0;
        long min = 0;
        @SuppressWarnings("unused")
        long sec = 0;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date one;
            Date two;
            try {
                one = df.parse(str1);
                two = df.parse(str2);
                long time1 = one.getTime();
                long time2 = two.getTime();
                long diff;
                if (time1 < time2) {
                    diff = time2 - time1;
                } else {
                    diff = time1 - time2;
                }
                day = diff / (24 * 60 * 60 * 1000);
                hour = (diff / (60 * 60 * 1000) - day * 24);
                min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
                sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(hour + "小时" + min + "分" + sec + "秒");
        return hour;
    }

    public static String getTimee(String time, Integer interval) {
        String format = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parse = sdf.parse(time);
            Calendar nowTime = Calendar.getInstance();
            nowTime.setTime(parse);
            nowTime.add(Calendar.MINUTE, -interval);
            format = sdf.format(nowTime.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return format;
    }
}
