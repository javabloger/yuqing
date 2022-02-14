package com.stonedt.intelligence.quartz;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.constant.ReportConstant;
import com.stonedt.intelligence.dao.ProjectDao;
import com.stonedt.intelligence.entity.Project;
import com.stonedt.intelligence.entity.ReportCustom;
import com.stonedt.intelligence.entity.ReportDetail;
import com.stonedt.intelligence.service.ReportCustomService;
import com.stonedt.intelligence.service.ReportDetailService;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.MyHttpRequestUtil;
import com.stonedt.intelligence.util.MyMathUtil;
import com.stonedt.intelligence.util.ProjectWordUtil;
import com.stonedt.intelligence.util.TextUtil;

/**
 * 数据报告定时任务
 */
@Component
public class ReportDataSchedule {

    // es搜索地址
    @Value("${es.search.url}")
    private String es_search_url;

    // 定时任务开关
    @Value("${schedule.report.open}")
    private Integer schedule_report_open;

    @Autowired
    private ReportCustomService reportCustomService;
    @Autowired
    private ReportDetailService reportDetailService;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private AnalysisDataRequest analysisDataRequest;
    
    

    //	@Scheduled(fixedRate = 10000000)
    //@Scheduled(cron = "0 0 3 * * ?")
    //@Scheduled(cron = "0 0/1 * * * ?")
    @Scheduled(cron = "0 0/1 * * * ?")
    public void start() {
        // 定时业务逻辑
        if (schedule_report_open == 1) {
            try {
                System.err.println("ReportDataSchedule start");
                List<ReportCustom> listReportCustomByStatus = reportCustomService.listReportCustomByStatus(1);
                ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(1);
                for (int k = 0; k < listReportCustomByStatus.size(); k++) {
                    try {
                        final int i = k;
                        newFixedThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                ReportCustom reportCustom = listReportCustomByStatus.get(i);
                                Long report_id = reportCustom.getReport_id();
                                Long project_id = reportCustom.getProject_id();
                                Project project = projectDao.getProject(project_id);
                                String keyword = project.getSubjectWord();
                                if (StringUtils.isNotBlank(keyword)) keyword = keyword.trim();
                                String stopword = project.getStopWord();
                                if (StringUtils.isNotBlank(stopword)) stopword = stopword.trim();
                                String characterWord = project.getCharacterWord();
                                if (StringUtils.isNotBlank(characterWord)) characterWord = characterWord.trim();
                                String eventWord = project.getEventWord();
                                if (StringUtils.isNotBlank(eventWord)) eventWord = eventWord.trim();
                                String regionalWord = project.getRegionalWord();
                                if (StringUtils.isNotBlank(regionalWord)) regionalWord = regionalWord.trim();
                                Integer projectType = project.getProjectType();
                                String highKeyword = keyword;
                                if (projectType == 2) {
                                    highKeyword = ProjectWordUtil.highProjectKeyword(keyword, regionalWord, characterWord, eventWord);
                                    stopword = ProjectWordUtil.highProjectStopword(stopword);
                                }
                                if (projectType == 1) {
                                	projectType = 2;
                                    highKeyword = ProjectWordUtil.QuickProjectKeyword(keyword);
                                    stopword = ProjectWordUtil.highProjectStopword(stopword);
                                }
                                String times = reportCustom.getReport_starttime();
                                String timee = reportCustom.getReport_endtime();
                                Integer report_type = reportCustom.getReport_type();
//                                 1数据概览逻辑处理 2、3资讯和社交数据逻辑处理
                                String dataOverview = dataOverview(report_type, highKeyword, stopword, times, timee, projectType);
//                                 4、情感分析
                                String emotionAnalysis = emotionAnalysis(highKeyword, stopword, times, timee, projectType);
//                                 5、热点事件排名
                                String hotEventRanking = hotEventRanking(highKeyword, stopword, times, timee, projectType);
//                                 10、媒体活跃度分析
                                String mediaActivityAnalysis = mediaActivityAnalysis(highKeyword, stopword, times, timee, projectType);
//                                 13、自媒体热度排名
                                String selfMediaRanking = selfMediaRanking(highKeyword, stopword, times, timee, projectType);
//                                 14、高频词指数
                                String highFrequencyWordIndex = highFrequencyWordIndex(highKeyword, stopword, times, timee, "", projectType);
//                                 15、热点地区排名
                                String hotSpotRanking = hotSpotRanking(highKeyword, stopword, times, timee, projectType);
                                // 6、7热点人物和地区
                                Map<String, String> hotPepoleAndSpot = hotPepoleAndSpot(highKeyword, stopword, times, timee, projectType);
                                String hotPeople = hotPepoleAndSpot.get("per");
                                String hotSpot = hotPepoleAndSpot.get("loc");
                                // 11、网民高频词云
                                String netizenWordCloud = wordCloud(highKeyword, stopword, times, timee, "2", projectType);
                                // 12、媒体高频词云
                                String mediaCordCloud = wordCloud(highKeyword, stopword, times, timee, "7", projectType);
                                
                               // 13、关键词高频分布统计
                                String wordCloud = analysisDataRequest.wordCloud(highKeyword, stopword, times, timee, projectType);
                                
                                // 14、高频词指数
                                //String keyWordIndex = analysisDataRequest.keyWordIndex(time_period, keywordsentimentFlagChart, times, timee, stopword, projectType);
                                String keyWordIndex = analysisDataRequest.keyWordReportIndex(report_type, highKeyword, times, timee, stopword, projectType,wordCloud);
                                //15、实体
                                String dataNer = analysisDataRequest.dataNer(highKeyword, stopword, times, timee, projectType);
                                
                                
                                
                                

                                ReportDetail reportDetail = new ReportDetail();
                                reportDetail.setCreate_time(DateUtil.nowTime());
                                reportDetail.setReport_id(report_id);
                                reportDetail.setData_overview(dataOverview);
                                reportDetail.setEmotion_analysis(emotionAnalysis);
                                reportDetail.setHot_event_ranking(hotEventRanking);
                                reportDetail.setMedia_activity_analysis(mediaActivityAnalysis);
                                reportDetail.setSelf_media_ranking(selfMediaRanking);
                                reportDetail.setHigh_word_index(highFrequencyWordIndex);
                                reportDetail.setHot_spot_ranking(hotSpotRanking);
                                reportDetail.setHot_people(hotPeople);
                                reportDetail.setHot_spots(hotSpot);
                                reportDetail.setHighword_cloud_index(keyWordIndex);
                                reportDetail.setHighword_cloud(wordCloud);
                                
                                reportDetail.setNetizen_word_cloud(netizenWordCloud);
                                reportDetail.setMedia_cord_cloud(mediaCordCloud);
                                reportDetail.setNer(dataNer);

                                int saveReportDetail = reportDetailService.saveReportDetail(reportDetail);
                                if (saveReportDetail > 0) {
                                    reportCustom.setReport_status(2);
                                } else {
                                    reportCustom.setReport_status(3);
                                }
                                int updateReportCustomStatus = reportCustomService.updateReportCustomStatus(reportCustom);
                                if (updateReportCustomStatus > 0) {
                                    System.err.println(DateUtil.nowTime() + " : " + report_id);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                newFixedThreadPool.shutdown();
                try {
                    newFixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println("ReportDataSchedule end");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 11、网民高频词云 12、媒体高频词云
     */
    public String wordCloud(String keyword, String stopword, String times, String timee, String classify, Integer projectType) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            String url = es_search_url + ReportConstant.es_api_keyword_list;
            String params = "times=" + times + "&timee=" + timee
                    + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&searchType=1&classify=" + classify + "&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            if (StringUtils.isNotBlank(sendPostEsSearch)) {
                Map<String, Integer> map = new HashMap<>();
                JSONObject parseObject = JSON.parseObject(sendPostEsSearch);
                JSONArray hitsArray = parseObject.getJSONObject("hits").getJSONArray("hits");
                for (int i = 0; i < hitsArray.size(); i++) {
                    try {
                        JSONObject jsonObject = hitsArray.getJSONObject(i).getJSONObject("_source").getJSONObject("key_words");
                        if (jsonObject != null) {
                            for (Entry<String, Object> entrySet : jsonObject.entrySet()) {
                                try {
                                    String x = entrySet.getKey().trim();
                                    int value = (int) entrySet.getValue();
                                    if (map.containsKey(x)) {
                                        map.put(x, map.get(x) + value);
                                    } else {
                                        map.put(x, value);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (Entry<String, Integer> entrySet : map.entrySet()) {
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("x", entrySet.getKey());
                    map2.put("value", entrySet.getValue());
                    result.add(map2);
                }
                result = result.stream().sorted((map1, map2) -> (int) map2.get("value") - (int) map1.get("value"))
                        .limit(100).collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(result);
    }

    /**
     * 6、7热点人物和地区
     */
    @SuppressWarnings("rawtypes")
    public Map<String, String> hotPepoleAndSpot(String keyword, String stopword, String times, String timee, Integer projectType) {
        Map<String, String> result = new HashMap<>();
        result.put("per", "[]");
        result.put("loc", "[]");
        try {
            String url = es_search_url + ReportConstant.es_api_ner;
            String params = "times=" + times + "&timee=" + timee
                    + "&keyword=" + keyword + "&stopword=" + stopword + "&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            Map<String, Object> tool = TextUtil.tool(sendPostEsSearch);
            Map<String, String> chainCycle = chainCycle(times, timee);
            params = "times=" + chainCycle.get("start") + "&timee=" + chainCycle.get("end")
                    + "&keyword=" + keyword + "&stopword=" + stopword + "&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
            sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            Map<String, Object> tool2 = TextUtil.tool(sendPostEsSearch);
            String ringRatioCycletool = TextUtil.RingRatioCycletool(tool, tool2);
            if (StringUtils.isNotBlank(ringRatioCycletool)) {
                try {
                    JSONObject parseObject = JSON.parseObject(ringRatioCycletool);
                    if (parseObject.containsKey("per")) {
                        JSONArray jsonArray = parseObject.getJSONArray("per");
                        List<Map> per = JSONObject.parseArray(jsonArray.toJSONString(), Map.class);
                        per = per.stream().limit(10).collect(Collectors.toList());
                        result.put("per", JSON.toJSONString(per));
                    }
                    if (parseObject.containsKey("loc")) {
                        JSONArray jsonArray = parseObject.getJSONArray("loc");
                        List<Map> loc = JSONObject.parseArray(jsonArray.toJSONString(), Map.class);
                        loc = loc.stream().limit(10).collect(Collectors.toList());
                        result.put("loc", JSON.toJSONString(loc));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取环比时间周期
     */
    public Map<String, String> chainCycle(String times, String timee) {
        Map<String, String> result = new HashMap<>();
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime start = LocalDateTime.parse(times, dateTimeFormatter);
            LocalDateTime end = LocalDateTime.parse(timee, dateTimeFormatter);
            Duration between = Duration.between(start, end);
            long days = between.toDays();
            if (days == 0) {
                LocalDateTime minusDays = start.minusDays(1);
                result.put("start", minusDays.format(dateTimeFormatter2) + " 00:00:00");
                result.put("end", minusDays.format(dateTimeFormatter2) + " 23:59:59");
            } else if (days == 7) {
                LocalDateTime minusWeeks = start.minusWeeks(1);
                LocalDateTime minusWeeks2 = end.minusWeeks(1);
                result.put("start", minusWeeks.format(dateTimeFormatter2) + " 00:00:00");
                result.put("end", minusWeeks2.format(dateTimeFormatter2) + " 23:59:59");
            } else {
                LocalDateTime minusMonths = start.minusMonths(1);
                result.put("start", minusMonths.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter2) + " 00:00:00");
                result.put("end", minusMonths.with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter2) + " 23:59:59");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 1数据概览逻辑处理 2、3资讯和社交数据逻辑处理
     */
    public String dataOverview(Integer report_type, String keyword, String stopword, String times, String timee, Integer projectType) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result.put("report_type", report_type);
            String params = "times=" + times + "&timee=" + timee
                    + "&keyword=" + keyword + "&stopword=" + stopword + "&emotionalIndex=1,2,3"
                    + "&projecttype=" + projectType;
            try {
                // 获取总资讯数、网站资讯数、客户端资讯数
                String url = es_search_url + ReportConstant.es_api_media_exposure;
                String sendPost = MyHttpRequestUtil.sendPostEsSearch(url, params);
                JSONObject parseObject = JSONObject.parseObject(sendPost);
                int total = parseObject.getJSONObject("hits").getIntValue("total");
                result.put("total", total);
                JSONArray jsonArray = parseObject.getJSONObject("aggregations")
                        .getJSONObject("top-terms-emotion").getJSONArray("buckets");
                int web_count = 0;
                int app_count = 0;
//			int information_count = 0;
                int social_count = 0;
                for (int i = 0; i < jsonArray.size(); i++) {
                    try {
                        int key = jsonArray.getJSONObject(i).getIntValue("key");
                        int doc_count = jsonArray.getJSONObject(i).getIntValue("doc_count");
                        // 总资讯数、网站资讯数、客户端资讯数逻辑处理
                        if (key == 7) app_count = doc_count;
                        if (key == 8) web_count = doc_count;
//					if (key == 5 || key == 8) information_count += doc_count;
//					if (key == 2 || key == 4 || key == 11) social_count += doc_count;
                        if (key == 2) social_count += doc_count;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                result.put("web_count", web_count);
                result.put("app_count", app_count);
                String web_rate = MyMathUtil.calculatedRatioWithPercentSign(web_count, total);
                String app_rate = MyMathUtil.calculatedRatioWithPercentSign(app_count, total);
                result.put("web_rate", web_rate);
                result.put("app_rate", app_rate);
                // 贴吧数量
                String params2 = "times=" + times + "&timee=" + timee
                        + "&keyword=" + keyword + "&stopword=" + stopword + "&sourceWebsite=百度贴吧&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
                String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params2);
                JSONObject parseObject2 = JSON.parseObject(sendPostEsSearch);
                social_count += parseObject2.getJSONObject("hits").getIntValue("total");
                result.put("information_count", total - social_count);
                result.put("social_count", social_count);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                // 获取敏感和非敏感资讯数
                String url = es_search_url + ReportConstant.es_api_emotion_rate;
                String sendPost = MyHttpRequestUtil.sendPostEsSearch(url, params);
                JSONObject parseObject = JSONObject.parseObject(sendPost);
                int emotion_total = parseObject.getJSONObject("hits").getIntValue("total");
                JSONArray jsonArray = parseObject.getJSONObject("aggregations")
                        .getJSONObject("group_by_tags").getJSONArray("buckets");
                int sensitive_count = 0;
                int non_sensitive_count = 0;
                for (int i = 0; i < jsonArray.size(); i++) {
                    try {
                        int key = jsonArray.getJSONObject(i).getIntValue("key");
                        int doc_count = jsonArray.getJSONObject(i).getIntValue("doc_count");
                        if (key == 1 || key == 2) non_sensitive_count += doc_count;
                        if (key == 3) sensitive_count += doc_count;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                String sensitive_rate = MyMathUtil.calculatedRatioWithPercentSign(sensitive_count, emotion_total);
                String non_sensitive_rate = MyMathUtil.calculatedRatioWithPercentSign(non_sensitive_count, emotion_total);
                result.put("sensitive_count", sensitive_count);
                result.put("non_sensitive_count", non_sensitive_count);
                result.put("sensitive_rate", sensitive_rate);
                result.put("non_sensitive_rate", non_sensitive_rate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(result);
    }

    /**
     * 4、情感分析
     */
    public String emotionAnalysis(String keyword, String stopword, String times, String timee, Integer projectType) {
        try {
            Map<String, Object> result = new HashMap<String, Object>();
            // 获取总资讯数、网站资讯数、客户端资讯数
            String url = es_search_url + ReportConstant.es_api_emotion_rate;
            String params = "times=" + times + "&timee=" + timee
                    + "&keyword=" + keyword + "&stopword=" + stopword + "&classify=1,2,3,4,5,6,7,8,9,10,11&emotionalIndex=1,2,3"
                    + "&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            JSONObject parseObject = JSON.parseObject(sendPostEsSearch);
            JSONArray jsonArray = parseObject.getJSONObject("aggregations")
                    .getJSONObject("group_by_tags").getJSONArray("buckets");
            int positive_count = 0;
            int neutral_count = 0;
            int negative_count = 0;
            for (int i = 0; i < jsonArray.size(); i++) {
                try {
                    int key = jsonArray.getJSONObject(i).getIntValue("key");
                    int doc_count = jsonArray.getJSONObject(i).getIntValue("doc_count");
                    if (key == 1) positive_count = doc_count;
                    if (key == 2) neutral_count = doc_count;
                    if (key == 3) negative_count = doc_count;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            result.put("positive_count", positive_count);
            result.put("neutral_count", neutral_count);
            result.put("negative_count", negative_count);
            List<Object[]> chart = new ArrayList<Object[]>();
            chart.add(new Object[]{"正面", positive_count});
            chart.add(new Object[]{"中性", neutral_count});
            chart.add(new Object[]{"负面", negative_count});
            result.put("chart", chart);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 5、热点事件排名
     */
    public String hotEventRanking(String keyword, String stopword, String times, String timee, Integer projectType) {
        Map<String, Object> result = new HashMap<String, Object>();
        String url = es_search_url + ReportConstant.es_api_similar_ids;
        try {
            // 全部情感
            String params = "times=" + times + "&timee=" + timee
                    + "&keyword=" + keyword + "&stopword=" + stopword + "&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            List<Map<String, Object>> all = hotEventRankingProcess(sendPostEsSearch);
            result.put("all", all);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("all", new ArrayList<>());
        }
        try {
            // 正面
            String params = "times=" + times + "&timee=" + timee
                    + "&keyword=" + keyword + "&stopword=" + stopword + "&emotionalIndex=1" + "&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            List<Map<String, Object>> positive = hotEventRankingProcess(sendPostEsSearch);
            result.put("positive", positive);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("positive", new ArrayList<>());
        }
        try {
            // 负面
            String params = "times=" + times + "&timee=" + timee
                    + "&keyword=" + keyword + "&stopword=" + stopword + "&emotionalIndex=3" + "&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            List<Map<String, Object>> negative = hotEventRankingProcess(sendPostEsSearch);
            result.put("negative", negative);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("negative", new ArrayList<>());
        }
        return JSON.toJSONString(result);
    }

    /**
     * 5、热点事件排名
     * 处理热点事件排名接口返回的数据
     */
    public List<Map<String, Object>> hotEventRankingProcess(String sendPostEsSearch) throws Exception {
        String url2 = es_search_url + ReportConstant.es_api_similar_list;
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> listMap = JSON.parseObject(sendPostEsSearch, List.class);
            String article_public_idstr = listMap.stream()
                    .sorted((map1, map2) -> Integer.valueOf(map2.get("similarvolume")) - Integer.valueOf(map1.get("similarvolume")))
                    .limit(10).map(map -> map.get("article_public_id"))
                    .collect(Collectors.joining(","));
            if (StringUtils.isBlank(article_public_idstr)) {
                return list;
            }
            String params2 = "article_public_idstr=" + article_public_idstr + "&searchType=2";
            String sendPostEsSearch2 = MyHttpRequestUtil.sendPostEsSearch(url2, params2);
            JSONObject parseObject2 = JSON.parseObject(sendPostEsSearch2);
            JSONArray jsonArray2 = parseObject2.getJSONArray("data");
            int total = 0;
            for (int i = 0; i < jsonArray2.size(); i++) {
                try {
                    JSONObject jsonObject = jsonArray2.getJSONObject(i).getJSONObject("_source");
                    Map<String, Object> map = new HashMap<>();
                    String article_public_id = jsonObject.getString("article_public_id");
                    String title = jsonObject.getString("title");
                    String content = jsonObject.getString("content");
                    String publish_time = jsonObject.getString("publish_time");
                    String source_name = jsonObject.getString("sourcewebsitename");
                    String key_words = jsonObject.getString("key_words");
                    int similarvolume = jsonObject.getIntValue("similarvolume");
                    int emotion = jsonObject.getIntValue("emotionalIndex");
                    map.put("article_public_id", article_public_id);
                    map.put("title", title);
                    map.put("content", content);
                    map.put("publish_time", publish_time);
                    map.put("source_name", source_name);
                    map.put("key_words", key_words);
                    map.put("emotion", emotion);
                    map.put("similarvolume", similarvolume);
                    total += similarvolume;
                    list.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < list.size(); i++) {
                try {
                    int similarvolume = Integer.parseInt(String.valueOf(list.get(i).get("similarvolume")));
                    list.get(i).put("rate", MyMathUtil.calculatedRatioWithPercentSign(similarvolume, total));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 10、媒体活跃度分析
     */
    public String mediaActivityAnalysis(String keyword, String stopword, String times, String timee, Integer projectType) {
        try {
            Map<String, Object> result = new HashMap<>();
            String url = es_search_url + ReportConstant.es_api_media_active;
            //String param = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword + "&classify=7&emotionalIndex=1,2,3"
            String param = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword + "&emotionalIndex=1,2,3"  
            + "&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, param);
            JSONObject parseObject = JSONObject.parseObject(sendPostEsSearch);
            int total = parseObject.getJSONObject("hits").getIntValue("total");
            JSONArray buckets = parseObject.getJSONObject("aggregations")
                    .getJSONObject("top-terms-sourcewebsitename").getJSONArray("buckets");
            int total_site = buckets.size();
            List<Map<String, Object>> sites = new ArrayList<>();
            for (int i = 0; i < total_site; i++) {
                try {
                    if (sites.size() == 10) {
                        break;
                    }
                    try {
                        Map<String, Object> map = new HashMap<>();
                        String name = buckets.getJSONObject(i).getString("key");
                        int count = buckets.getJSONObject(i).getIntValue("doc_count");
                        String rate = MyMathUtil.calculatedRatioWithPercentSign(count, total);
                        map.put("name", name);
                        map.put("count", count);
                        map.put("rate", rate);
                        JSONArray jsonArray = buckets.getJSONObject(i).getJSONObject("top_score_hits").getJSONObject("hits").getJSONArray("hits");
                        if (!jsonArray.isEmpty()) {
                            String id = jsonArray.getJSONObject(0).getString("_id");
                            String logo = jsonArray.getJSONObject(0).getJSONObject("_source").getString("websitelogo");
                            map.put("id", TextUtil.nullAsStringEmpty(id));
                            map.put("logo", TextUtil.nullAsStringEmpty(logo));
                        }
                        sites.add(map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            result.put("total", total);
            result.put("total_site", total_site);
            result.put("sites", sites);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 13、自媒体热度排名
     */
    public String selfMediaRanking(String keyword, String stopword, String times, String timee, Integer projectType) {
        try {
            String url = es_search_url + ReportConstant.es_api_media_list;
            String param = "classify=7&times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword + "&emotionalIndex=1,2,3"
                    + "&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, param);
            JSONObject parseObject = JSONObject.parseObject(sendPostEsSearch);
            JSONArray buckets = parseObject.getJSONObject("aggregations")
                    .getJSONObject("top-terms-aggregation").getJSONArray("buckets");
            List<Map<String, Object>> result = new ArrayList<>();
            for (int i = 0; i < buckets.size(); i++) {
                try {
                    String name = buckets.getJSONObject(i).getString("key");
                    if (StringUtils.isNotBlank(name)) {
                        JSONArray jsonArray = buckets.getJSONObject(i).getJSONObject("top-terms-emotion").getJSONArray("buckets");
                        for (int j = 0; j < jsonArray.size(); j++) {
                            try {
                                if (result.size() == 7) break;
                                String platform_name = jsonArray.getJSONObject(j).getString("key");
                                int volume = jsonArray.getJSONObject(j).getIntValue("doc_count");
                                Map<String, Object> map = new HashMap<>();
                                map.put("name", name);
                                map.put("volume", volume);
                                map.put("platform_name", platform_name);
                                result.add(map);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            result = result.stream()
                    .sorted((map1, map2) -> (int) map2.get("volume") - (int) map1.get("volume"))
                    .limit(7).collect(Collectors.toList());
            for (int i = 0; i < result.size(); i++) {
                String name = (String) result.get(i).get("name");
                String platform_name = (String) result.get(i).get("platform_name");
                url = es_search_url + ReportConstant.es_api_wemedia_info;
                param = "name=" + name + "&platform_name=" + platform_name;
                sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, param);
                parseObject = JSONObject.parseObject(sendPostEsSearch);
                JSONArray jsonArray2 = parseObject.getJSONArray("data");
                if (!jsonArray2.isEmpty()) {
                    JSONObject jsonObject = jsonArray2.getJSONObject(0).getJSONObject("_source");
                    String logo = jsonObject.getString("logo");
                    String slogan = jsonObject.getString("slogan");
                    String id = jsonObject.getString("id");
                    int release_count = jsonObject.getIntValue("production_count");
                    int fans_count = jsonObject.getIntValue("focus_count");
                    result.get(i).put("logo", TextUtil.nullAsStringEmpty(logo));
                    result.get(i).put("slogan", TextUtil.nullAsStringEmpty(slogan));
                    result.get(i).put("id", TextUtil.nullAsStringEmpty(id));
                    result.get(i).put("release_count", release_count);
                    result.get(i).put("fans_count", fans_count);
                } else {
                    result.get(i).put("logo", "");
                    result.get(i).put("slogan", "");
                    result.get(i).put("id", "");
                    result.get(i).put("release_count", "");
                    result.get(i).put("fans_count", "");
                }
            }
            return JSON.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 14、高频词指数
     */
    public String highFrequencyWordIndex(String keyword, String stopword, String times, String timee, String classify, Integer projectType) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            String url = es_search_url + ReportConstant.es_api_keyword_list;
            String params = "times=" + times + "&timee=" + timee
                    + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&searchType=1&classify=" + classify + "&emotionalIndex=1,2,3"
                    + "&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            if (StringUtils.isNotBlank(sendPostEsSearch)) {
                Map<String, Integer> map = new HashMap<>();
                JSONObject parseObject = JSON.parseObject(sendPostEsSearch);
                JSONArray hitsArray = parseObject.getJSONObject("hits").getJSONArray("hits");
                for (int i = 0; i < hitsArray.size(); i++) {
                    try {
                        JSONObject jsonObject = hitsArray.getJSONObject(i).getJSONObject("_source").getJSONObject("key_words");
                        if (jsonObject != null) {
                            for (Entry<String, Object> entrySet : jsonObject.entrySet()) {
                                try {
                                    String x = entrySet.getKey().trim();
                                    int value = (int) entrySet.getValue();
                                    if (map.containsKey(x)) {
                                        map.put(x, map.get(x) + value);
                                    } else {
                                        map.put(x, value);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (Entry<String, Integer> entrySet : map.entrySet()) {
                    try {
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("keyword", entrySet.getKey());
                        map2.put("value", entrySet.getValue());
                        result.add(map2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                result = result.stream().sorted((map1, map2) -> (int) map2.get("value") - (int) map1.get("value"))
                        .limit(10).collect(Collectors.toList());
                int total = result.stream().mapToInt(a -> (int) a.get("value")).sum();
                for (int i = 0; i < result.size(); i++) {
                    int value = (int) result.get(i).get("value");
                    result.get(i).put("rate", MyMathUtil.calculatedRatioWithPercentSign(value, total));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(result);
    }

    /**
     * 15、热点地区排名
     */
    public String hotSpotRanking(String keyword, String stopword, String times, String timee, Integer projectType) {
        try {
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> chart = new ArrayList<>();
            List<Map<String, Object>> list = new ArrayList<>();
            String url = es_search_url + ReportConstant.es_api_hot_spot_ranking;
            String param = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword + "&emotionalIndex=1,2,3"
                    + "&projecttype=" + projectType;
            String esOpinion = MyHttpRequestUtil.sendPostEsSearch(url, param);
            JSONObject parseObject = JSONObject.parseObject(esOpinion);
            int total = parseObject.getJSONObject("hits").getIntValue("total");
            JSONArray buckets = parseObject.getJSONObject("aggregations")
                    .getJSONObject("group_by_tags").getJSONArray("buckets");
            List<String> spots = new ArrayList<>(Arrays.asList(ReportConstant.spotArray));
            for (int i = 0; i < buckets.size(); i++) {
                try {
                    // 处理生成chart图的数据
                    String key = buckets.getJSONObject(i).getString("key");
                    int doc_count = buckets.getJSONObject(i).getIntValue("doc_count");
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", key);
                    map.put("value", doc_count);
                    spots.remove(key);
                    chart.add(map);
                    // 处理生成图下的排行前四的数据
                    if (i < 5) {
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("name", key);
                        map2.put("value", doc_count);
                        map2.put("rate", MyMathUtil.calculatedRatioWithPercentSign(doc_count, total));
                        list.add(map2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 处理数据量为0的省
//			for (int i = 0; i < spots.size(); i++) {
//				Map<String, Object> map = new HashMap<>();
//				map.put("name", spots.get(i));
//				map.put("value", 0);
//				chart.add(map);
//			}
            result.put("chart", chart);
            result.put("list", list);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
