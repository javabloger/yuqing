package com.stonedt.intelligence.quartz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.stonedt.intelligence.constant.MonitorConstant;
import com.stonedt.intelligence.constant.ReportConstant;
import com.stonedt.intelligence.constant.VolumeConstant;
import com.stonedt.intelligence.dao.SystemDao;
import com.stonedt.intelligence.entity.WarningSetting;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.MyHttpRequestUtil;
import com.stonedt.intelligence.util.MyMathUtil;
import com.stonedt.intelligence.util.SimhashAlgoService;
import com.stonedt.intelligence.util.StopWordsUtil;
import com.stonedt.intelligence.util.TextUtil;

/**
 * 监测分析数据请求
 */
@Component
public class AnalysisDataRequest {

    // es搜索地址
    @Value("${es.search.url}")
    private String es_search_url;

    @Value("${kafuka.url}")
    private String kafuka_url;

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(String.valueOf(AnalysisDataRequest.class));

    @Autowired
    private SystemDao systemDao;
    
    @Value("${stopwords}")
    private String stopwords;
    
    

    public static final String searchearlywarningApi = "/yqsearch/searchlist"; //预警文章获取

    //  数据概览
    public String dataOverview(Long projectId, String highKeyword, String times, String timee, String stopword, Integer projectType) {
        String param = "times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword=" + stopword
                + "&emotionalIndex=1,2,3&projecttype=" + projectType;
        try {
            String response = sendPost(es_search_url + "/yqtindutry/totalDatasearch", param);
            JSONArray resultArray = JSONObject.parseObject(response).getJSONObject("aggregations")
                    .getJSONObject("group_by_tags").getJSONArray("buckets");
            int positive = 0; // 正面
            int negative = 0; // 负面
            int neutral = 0; // 中性
            for (int i = 0; i < resultArray.size(); i++) {
                try {
                    String key = resultArray.getJSONObject(i).getString("key");
                    int doc_count = resultArray.getJSONObject(i).getIntValue("doc_count");
                    if ("1".equals(key))
                        positive = doc_count;
                    if ("2".equals(key))
                        neutral = doc_count;
                    if ("3".equals(key))
                        negative = doc_count;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 总数
            int total = JSONObject.parseObject(response).getJSONObject("hits").getIntValue("total");
            Map<String, Object> allMap = new HashMap<>();
            allMap.put("count", total);
            allMap.put("rate", 100);
            Map<String, Object> sensitiveMap = new HashMap<>();
            sensitiveMap.put("count", negative);
            sensitiveMap.put("rate", MyMathUtil.calculatedRatioNoPercentSign(negative, total));
            Map<String, Object> noSensitiveMap = new HashMap<>();
            noSensitiveMap.put("count", positive + neutral);
            noSensitiveMap.put("rate", MyMathUtil.calculatedRatioNoPercentSign(positive + neutral, total));

            int warningCount = 0;
            String dataSwitch = "close";
            WarningSetting warningSetting = systemDao.getWarningByProjectid(projectId);
            if (warningSetting.getWarning_status() == 1) {
                warningCount = dataOverviewHelper(warningSetting, times, timee, highKeyword, stopword, projectType);
                dataSwitch = "open";
            }
            Map<String, Object> earlyWarningMap = new HashMap<>();
            earlyWarningMap.put("count", warningCount);
            earlyWarningMap.put("rate", MyMathUtil.calculatedRatioNoPercentSign(warningCount, total));
            earlyWarningMap.put("dataSwitch", dataSwitch);
            Map<String, Object> result = new HashMap<>();
            result.put("all", allMap);
            result.put("sensitive", sensitiveMap);
            result.put("noSensitive", noSensitiveMap);
            result.put("earlyWarning", earlyWarningMap);
            return JSON.toJSONString(result);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 数据概览助手 获取预警文章数
     */
    public int dataOverviewHelper(WarningSetting warningSetting, String times, String timee, String highKeyword, String stopword, Integer projectType) {
        int count = 0;
        try {
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
            String params = "keyword=" + highKeyword + "&searchkeyword=" + yjword + "&emotionalIndex=" + emotionalIndex + "&times=" + times
                    + "&timee=" + timee + "&stopword=" + stopword + "&page=1&size=1&origintype=" + origintype
                    + "&classify=" + classify + "&projecttype=" + projectType;
            //相似文章合并（0：取消合并 1：合并）
            if (warningSetting.getWarning_similar() == 0) {
                String urls = es_search_url + searchearlywarningApi;
                System.err.println(urls + "?" + params);
                String esEarlywarning = MyHttpRequestUtil.sendPostEsSearch(urls, params);
                JSONObject earlywarnings = JSONObject.parseObject(esEarlywarning);
                count = earlywarnings.getInteger("count");
            } else {
                String similarUrl = es_search_url + MonitorConstant.es_api_similarsearch_content;
                String esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
                if (StringUtils.isNotBlank(esSimilarResponse)) {
                    JSONArray similarArray = JSON.parseArray(esSimilarResponse);
                    count = similarArray.size();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // 情感占比
    public String emotional(String keyword, String times, String timee, String stopword, Integer projectType) {
        JSONObject resultJson = new JSONObject();
        String param = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                + "&emotionalIndex=1,2,3&projecttype=" + projectType;
        try {
            String result = sendPost(es_search_url + "/yqtindutry/totalDatasearch", param);
            JSONArray resultArray = JSONObject.parseObject(result).getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
            int positive = 0; // 正面
            int negative = 0; // 负面
            int neutral = 0; // 中性
            for (int i = 0; i < resultArray.size(); i++) {
                try {
                    String key = resultArray.getJSONObject(i).getString("key");
                    int doc_count = resultArray.getJSONObject(i).getIntValue("doc_count");
                    if ("1".equals(key))
                        positive = doc_count;
                    if ("2".equals(key))
                        neutral = doc_count;
                    if ("3".equals(key))
                        negative = doc_count;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 总数
            int total = JSONObject.parseObject(result).getJSONObject("hits").getIntValue("total");
            NumberFormat numberFormat = NumberFormat.getInstance();
            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(2);
            // 正面率
            String posrate = MyMathUtil.calculatedRatioWithPercentSign(positive, total);
            // 负面率
            String negrate = MyMathUtil.calculatedRatioWithPercentSign(negative, total);
            // 中兴率
            String neurate = MyMathUtil.calculatedRatioWithPercentSign(neutral, total);
            JSONObject rateJson = new JSONObject();
            JSONArray countArray = new JSONArray();
            for (int i = 0; i < 3; i++) {
                try {
                    JSONArray countArray1 = new JSONArray();
                    if (i == 0) {
                        countArray1.add("正面");
                        countArray1.add(positive);
                    } else if (i == 1) {
                        countArray1.add("负面");
                        countArray1.add(negative);
                    } else {
                        countArray1.add("中性");
                        countArray1.add(neutral);
                    }
                    countArray.add(countArray1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            rateJson.put("positive", posrate);
            rateJson.put("neutral", neurate);
            rateJson.put("negative", negrate);
            resultJson.put("rate", rateJson);
            resultJson.put("chart", countArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJson.toJSONString();
    }

    // 方案命中主体词
    public String fangan(String keyword, String highKeyword, String times, String timee,
                         String stopword, Integer projectType) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String a[] = keyword.split(",");
            int total = 0;
            for (int i = 0; i < a.length; i++) {
                try {
                    total += getfanganJSon(a[i], highKeyword, times, timee, stopword, projectType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < a.length; i++) {
                try {
                    int count = getfanganJSon(a[i], highKeyword, times, timee, stopword, projectType);
                    String rate = MyMathUtil.calculatedRatioWithPercentSign(count, total);
                    Map<String, Object> map = new HashMap<>();
                    map.put("keyword", a[i]);
                    map.put("count", count);
                    map.put("rate", rate);
                    list.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            list = list.stream().sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(5)
                    .collect(Collectors.toList());
            return JSON.toJSONString(list);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 方案词结合情感占比接口,计算出各自数据量
     */
    public int getfanganJSon(String searchkeyword, String highKeyword, String time, String timee, String stopword,
                             Integer projectType) {
        int total = 0;
        try {
            String param = "times=" + time + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword=" + stopword
                    + "&searchkeyword=" + searchkeyword + "&origintype=0&emotionalIndex=1,2,3" + "&projecttype="
                    + projectType;
            try {
                String result = sendPost(es_search_url + "/yqtindutry/totalDatasearch", param);
                total = JSONObject.parseObject(result).getJSONObject("hits").getIntValue("total");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    // 关键词情感分析数据走势
    public String keywordsentimentFlagChart(String keyword, String highKeyword, String stopword, String times,
                                            String timee, Integer timetype, Integer projectType) {
        try {
            Map<String, Object> result = new HashMap<>();
            String time_period = time_period(timetype);
            String es_api_keyword_emotion_statistical = VolumeConstant.es_api_keyword_sentimentFlagChart;
            String url = es_search_url + es_api_keyword_emotion_statistical;
            String params = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&timetype=" + time_period + "&emotionalIndex=1,2,3&projecttype=" + projectType;
            String sendPost = sendPost(url, params);
            JSONObject parseObject = JSONObject.parseObject(sendPost);
            JSONArray bucketsArray = parseObject.getJSONObject("aggregations").getJSONObject("group_by_grabTime")
                    .getJSONArray("buckets");
            List<Object[]> chart = new ArrayList<>();
            for (int i = 0; i < bucketsArray.size(); i++) {
                Object[] objects = new Object[]{"", 0, 0};
                try {
                    JSONObject jsonObject = JSONObject.parseObject(String.valueOf(bucketsArray.get(i)));
                    String time = jsonObject.getString("key_as_string"); // 时间
                    if (timetype == 1 || timetype == 2) {
                        time = time.substring(0, 13);
                    }
                    if (timetype == 3 || timetype == 4) {
                        time = time.substring(0, 10);
                    }
                    objects[0] = time;
                    JSONArray jsonArray = jsonObject.getJSONObject("top-terms-classify").getJSONArray("buckets");

                    Integer count = 0;
                    for (int j = 0; j < jsonArray.size(); j++) {
                        try {
                            JSONObject object = JSONObject.parseObject(String.valueOf(jsonArray.get(j)));
                            String key = object.getString("key");// 情感
                            Integer doc_count = object.getInteger("doc_count");
                            if ("1".equals(key) || "2".equals(key)) {
//                            	objects[1] = doc_count;
                                count += doc_count;
                            }

                            if ("3".equals(key)) {
                                objects[2] = doc_count;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    objects[1] = count;
                    chart.add(objects);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String keywordsentimentFlagChartHelper = keywordsentimentFlagChartHelper(keyword, highKeyword, stopword, times, timee, projectType);
            result.put("text", keywordsentimentFlagChartHelper);
            result.put("chart", chart);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 关键词情感分析数据走势类型
     */
    public String time_period(Integer timetype) {
        String type = "1H";
        switch (timetype) {
            case 1:
                type = "1H";
                break;
            case 2:
                type = "3H";
                break;
            case 3:
                type = "day";
                break;
            case 4:
                type = "day";
                break;
            default:
                break;
        }
        return type;
    }

    /**
     * 关键词情感分析数据走势文字
     */
    public String keywordsentimentFlagChartHelper(String keyword, String highKeyword, String stopword, String times, String timee, Integer projectType) {
        StringBuilder result = new StringBuilder();
        try {
            String[] keywords = keyword.split(",");
            result.append("您一共设置了" + keywords.length + "个关键词。");
            String es_api_keyword_emotion_statistical = VolumeConstant.es_api_keyword_emotion_statistical;
            String url = es_search_url + es_api_keyword_emotion_statistical;
            List<Map<String, Object>> keyworCount = new ArrayList<Map<String, Object>>();
            int positive_total = 0;
            int negative_total = 0;
            for (int i = 0; i < keywords.length; i++) {
                try {
                    String params = "times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword=" + stopword
                            + "&searchkeyword=" + keywords[i] + "&origintype=0&emotionalIndex=1,2,3&projecttype=" + projectType;
                    String sendPost = sendPost(url, params);
                    JSONObject parseObject = JSONObject.parseObject(sendPost);
                    JSONArray bucketsArray = parseObject.getJSONObject("aggregations").getJSONObject("top-terms-emotion")
                            .getJSONArray("buckets");
                    Map<String, Object> map = new HashMap<>();
                    map.put("keyword", keywords[i]);
                    map.put("positive_num", 0);
                    map.put("negative_num", 0);
                    for (int j = 0; j < bucketsArray.size(); j++) {
                        try {
                            Integer key = bucketsArray.getJSONObject(j).getInteger("key");
                            Integer count = bucketsArray.getJSONObject(j).getInteger("doc_count");
                            if (key == 1) {
                                positive_total += count;
                                map.put("positive_num", count);
                            }
                            if (key == 3) {
                                negative_total += count;
                                map.put("negative_num", count);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    keyworCount.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(keyworCount, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return (int) o2.get("positive_num") - (int) o1.get("positive_num");
                }
            });

            if (keyworCount.size() > 0) {
                JSONObject object = JSONObject.parseObject(JSON.toJSONString(keyworCount.get(0)));
                int intValue = object.getIntValue("positive_num");
                result.append("正面占比最高的是【" + object.getString("keyword") + "】")
                        .append("到达" + MyMathUtil.calculatedRatioWithPercentSign(intValue, positive_total) + "。");
            }
            if (keyworCount.size() > 1) {
                JSONObject object = JSONObject.parseObject(JSON.toJSONString(keyworCount.get(1)));
                int intValue = object.getIntValue("positive_num");
                result.append("其次是【" + object.getString("keyword") + "】")
                        .append("到达" + MyMathUtil.calculatedRatioWithPercentSign(intValue, positive_total) + "。");
            }
            Collections.sort(keyworCount, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return (int) o2.get("negative_num") - (int) o1.get("negative_num");
                }
            });
            if (keyworCount.size() > 0) {
                JSONObject object = JSONObject.parseObject(JSON.toJSONString(keyworCount.get(0)));
                int intValue = object.getIntValue("negative_num");
                result.append("负面占比最高的是【" + object.getString("keyword") + "】")
                        .append("到达" + MyMathUtil.calculatedRatioWithPercentSign(intValue, negative_total) + "。");
            }
            if (keyworCount.size() > 1) {
                JSONObject object = JSONObject.parseObject(JSON.toJSONString(keyworCount.get(1)));
                int intValue = object.getIntValue("negative_num");
                result.append("其次是【" + object.getString("keyword") + "】")
                        .append("到达" + MyMathUtil.calculatedRatioWithPercentSign(intValue, negative_total) + "。");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
    

    // 热点事件排名
    public String hotEventRanking(String highKeyword, String stopword, String times, String timee, Integer projectType) {
        Map<String, Object> result = new HashMap<String, Object>();
        String url = es_search_url + ReportConstant.es_api_search_list;
        //  String url = "http://192.168.71.81:8123"+ReportConstant.es_api_search_list;
        try {
            // 全部情感
            String params = "times=" + times + "&timee=" + timee
                    + "&keyword=" + highKeyword + "&stopword=" + stopword + "&emotionalIndex=1,2,3" + "&projecttype=" + projectType+"&searchType=2&size=30";
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
                    + "&keyword=" + highKeyword + "&stopword=" + stopword + "&emotionalIndex=1" + "&projecttype=" + projectType+"&searchType=2&size=30";
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
                    + "&keyword=" + highKeyword + "&stopword=" + stopword + "&emotionalIndex=3" + "&projecttype=" + projectType+"&searchType=2&size=30";
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            List<Map<String, Object>> negative = hotEventRankingProcess(sendPostEsSearch);
            result.put("negative", negative);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("negative", new ArrayList<>());
        }
        return JSON.toJSONString(result);
    }
    
    
    
    
    
    
//    public String hotEventRanking(String highKeyword, String stopword, String times, String timee, Integer projectType) {
//        Map<String, Object> result = new HashMap<String, Object>();
//        String url = es_search_url + ReportConstant.es_api_similar_ids;
//        try {
//            // 全部情感
//            String params = "times=" + times + "&timee=" + timee
//                    + "&keyword=" + highKeyword + "&stopword=" + stopword + "&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
//            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
//            List<Map<String, Object>> all = hotEventRankingProcess(sendPostEsSearch);
//            result.put("all", all);
//        } catch (Exception e) {
//            e.printStackTrace();
//            result.put("all", new ArrayList<>());
//        }
//        try {
//            // 正面
//            String params = "times=" + times + "&timee=" + timee
//                    + "&keyword=" + highKeyword + "&stopword=" + stopword + "&emotionalIndex=1" + "&projecttype=" + projectType;
//            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
//            List<Map<String, Object>> positive = hotEventRankingProcess(sendPostEsSearch);
//            result.put("positive", positive);
//        } catch (Exception e) {
//            e.printStackTrace();
//            result.put("positive", new ArrayList<>());
//        }
//        try {
//            // 负面
//            String params = "times=" + times + "&timee=" + timee
//                    + "&keyword=" + highKeyword + "&stopword=" + stopword + "&emotionalIndex=3" + "&projecttype=" + projectType;
//            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
//            List<Map<String, Object>> negative = hotEventRankingProcess(sendPostEsSearch);
//            result.put("negative", negative);
//        } catch (Exception e) {
//            e.printStackTrace();
//            result.put("negative", new ArrayList<>());
//        }
//        return JSON.toJSONString(result);
//    }

    /**
     * 热点事件排名 处理热点事件排名接口返回的数据
     */
    public List<Map<String, Object>> hotEventRankingProcess(String sendPostEsSearch) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            JSONObject parseObject2 = JSON.parseObject(sendPostEsSearch);
            JSONArray jsonArray2 = parseObject2.getJSONArray("data");
            for (int i = 0; i < jsonArray2.size(); i++) {
                try {
                    JSONObject jsonObject = jsonArray2.getJSONObject(i).getJSONObject("_source");
                    double _score = Double.parseDouble(jsonArray2.getJSONObject(i).get("_score").toString());
                    Map<String, Object> map = new HashMap<>();
                    String article_public_id = jsonObject.getString("article_public_id");
                    
                    
                    
                    
                    String title = jsonObject.getString("title");
                    
                    if (title.contains("_http://") || title.contains("_https://")) {
                        title = title.substring(0, title.indexOf("_"));
                    }
                    
                    
                    
                    
                    String content = jsonObject.getString("content");
                    if (content.contains("_http://") || content.contains("_https://")) {
                    	content = content.substring(0, content.indexOf("_"));
                    }
                    String publish_time = jsonObject.getString("publish_time");
                    String source_name = jsonObject.getString("sourcewebsitename");
                    String key_words = jsonObject.getString("key_words");
                    int similarvolume = jsonObject.getIntValue("similarvolume");
                    int emotion = jsonObject.getIntValue("emotionalIndex");
                    map.put("article_public_id", article_public_id);
                    map.put("title", title);
                    map.put("content", content);
                    map.put("_score", fun4(_score));
                    map.put("publish_time", publish_time);
                    map.put("source_name", source_name);
                    map.put("key_words", key_words);
                    map.put("emotion", emotion);
                    map.put("similarvolume", similarvolume);
                   //total += similarvolume;
                    list.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        //去除重复
        List<Map<String, Object>> resultlist = new ArrayList<>();
        
		
        for (int i = 0; i < list.size(); i++) {
        	Map<String, Object> map = list.get(i);//暂未去重的标题
        	
        	if(resultlist.size()>=10)break;
        	if(resultlist.size()==0) {
        		resultlist.add(map);continue;
        	}else {
        		for(int j=0;j < resultlist.size();j++) {
        			   Map<String,Object> resultmap = resultlist.get(j);
        			    SimhashAlgoService simhashAlgoService = new SimhashAlgoService();
        			    List<String> fingerPrints1 = simhashAlgoService.simHash(map.get("title").toString(),64);
        			    //System.out.println("暂未去重的标题:"+map.get("title")+",article_public_id:"+map.get("article_public_id").toString());
        		        SimhashAlgoService simhashAlgoService2 = new SimhashAlgoService();
        		        List<String> fingerPrints2 = simhashAlgoService2.simHash(resultmap.get("title").toString(),64);
        		        //System.out.println("已经驱虫去重的标题:"+resultmap.get("title")+",article_public_id:"+resultmap.get("article_public_id").toString());
        		        int x = simhashAlgoService.hammingDistance(simhashAlgoService2);
        		        if(x<17)break;
        		        if(j==resultlist.size()-1) {
        		        	resultlist.add(map);break;
        		        }
        		}
        		
        		
        		
        	}
        	
        	
        	
			
		}
        
        
        
        
        
        
        
        
        return resultlist;
    }

	public static String fun4(double data) {
		String format = "0.00";
		try {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(2);
			format = nf.format(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return format;
	}

    
    
//    public List<Map<String, Object>> hotEventRankingProcess(String sendPostEsSearch) throws Exception {
//        String url2 = es_search_url + ReportConstant.es_api_similar_list;
//        List<Map<String, Object>> list = new ArrayList<>();
//        try {
//            @SuppressWarnings("unchecked")
//            List<Map<String, String>> listMap = JSON.parseObject(sendPostEsSearch, List.class);
//            String article_public_idstr = listMap.stream()
//                    .sorted((map1, map2) -> Integer.valueOf(map2.get("similarvolume")) - Integer.valueOf(map1.get("similarvolume")))
//                    .limit(10).map(map -> map.get("article_public_id"))
//                    .collect(Collectors.joining(","));
//            if (StringUtils.isBlank(article_public_idstr)) {
//                return list;
//            }
//            String params2 = "article_public_idstr=" + article_public_idstr + "&searchType=2";
//            String sendPostEsSearch2 = MyHttpRequestUtil.sendPostEsSearch(url2, params2);
//            JSONObject parseObject2 = JSON.parseObject(sendPostEsSearch2);
//            JSONArray jsonArray2 = parseObject2.getJSONArray("data");
//            int total = 0;
//            for (int i = 0; i < jsonArray2.size(); i++) {
//                try {
//                    JSONObject jsonObject = jsonArray2.getJSONObject(i).getJSONObject("_source");
//                    Map<String, Object> map = new HashMap<>();
//                    String article_public_id = jsonObject.getString("article_public_id");
//                    String title = jsonObject.getString("title");
//                    String content = jsonObject.getString("content");
//                    String publish_time = jsonObject.getString("publish_time");
//                    String source_name = jsonObject.getString("sourcewebsitename");
//                    String key_words = jsonObject.getString("key_words");
//                    int similarvolume = jsonObject.getIntValue("similarvolume");
//                    int emotion = jsonObject.getIntValue("emotionalIndex");
//                    map.put("article_public_id", article_public_id);
//                    map.put("title", title);
//                    map.put("content", content);
//                    map.put("publish_time", publish_time);
//                    map.put("source_name", source_name);
//                    map.put("key_words", key_words);
//                    map.put("emotion", emotion);
//                    map.put("similarvolume", similarvolume);
//                    total += similarvolume;
//                    list.add(map);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            for (int i = 0; i < list.size(); i++) {
//                try {
//                    int similarvolume = Integer.parseInt(String.valueOf(list.get(i).get("similarvolume")));
//                    list.get(i).put("rate", MyMathUtil.calculatedRatioWithPercentSign(similarvolume, total));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }

//	public static void main(String[] args) {
//		String times = "2021-04-13 00:00:00";
//		String timee = "2021-04-14 00:00:00";
//		//String highKeyword = "(诚迈科技 OR 第九公司 OR 微软 OR 腾讯控股 OR 宁德时代 OR 三安电光 OR 工商银行 OR 怪兽充电 OR 沙钢 OR 中国电信 OR 中国移动 OR 建设银行 OR 联合丽格 OR 苏州贝康医疗器械有限公司 OR 北京云鸟科技有限公司 OR 北京易泊安科技有限公司 OR 北京伍方兴业科技有限公司)";
//		//String highKeyword = "(北京 OR 南京 OR 上海)";
//		String highKeyword = "(制药 OR 临床试验 OR 药物 OR 药厂 OR 医药公司 OR 医药集团 OR 药品 OR 药效 OR 药业集团 OR 药品监督 OR 制药行业 OR 制药厂 OR 药剂 OR 药膏 OR 药价 OR 药监局 OR 医药股份 OR 医药有限公司 OR 医药控股 OR 药房 OR 药店 OR 注射剂 OR 西药 OR 医药 OR 国药 OR 国家药品 OR 药品流通 OR 药品监督管理局 OR 临床药学 OR 药学科学 OR 药学 OR 制剂 OR 特效药 OR 万灵药 OR 靶向药 OR 抗癌药 OR 避孕药 OR 胶囊 OR 口服液 OR 药丸 OR 处方药 OR 麻醉剂 OR 感冒药 OR 医药机构 OR 药津贴 OR 生物药 OR 抗生素 OR 创新药 OR 停药 OR 药师 OR 药业 OR 鼻炎药 OR 试剂 OR 消炎药 OR 药学 OR 用药 OR FDA OR 新药 OR 靶向 OR 药监机构 OR cde OR pmda OR 输液 OR 药品质量 OR 药历 OR 口服药 OR 中成药 OR 生物制剂 OR 麻醉药 OR 精神药 OR 毒性药 OR 特殊药 OR 药品生产 OR 医药贸易 OR 连锁药店 OR 药性 OR 静脉输液 OR 中药 OR 药草 OR 药方 OR 药材 OR 中药文化 OR 药源基地 OR 药材基地 OR 药材品种 OR 入药 OR 中药行业)";
//		String stopword = "";
//		
//		
//		String wordCloud = wordCloud(highKeyword,stopword,times,timee,2);
//		System.out.println(wordCloud);
//	}
	
	
	
    // 关键词高频分布统计
    public String wordCloud(String highKeyword, String stopword, String times, String timee, Integer projectType) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            String url = es_search_url + ReportConstant.es_api_keyword_list;
        	
        	//String url = "http://192.168.71.81:8123" + ReportConstant.es_api_keyword_list;
        	
            String params = "times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword=" + stopword
                    + "&searchType=1&emotionalIndex=1,2,3&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            if (StringUtils.isNotBlank(sendPostEsSearch)) {
                Map<String, Integer> map = new HashMap<>();
                JSONObject parseObject = JSON.parseObject(sendPostEsSearch);
                JSONArray hitsArray = parseObject.getJSONObject("hits").getJSONArray("hits");
                for (int i = 0; i < hitsArray.size(); i++) {
                    try {
                    	//解析keywords
                        JSONObject jsonObject = hitsArray.getJSONObject(i).getJSONObject("_source")
                                .getJSONObject("key_words");
                        if (jsonObject != null) {
                            for (Entry<String, Object> entrySet : jsonObject.entrySet()) {
                                try {
                                    String x = entrySet.getKey().trim();
                                    if(isLockWordsMonster(x)==false) {
                                    	int value = (int) entrySet.getValue();
                                        if (map.containsKey(x)) {
                                            map.put(x, map.get(x) + value);
                                        } else {
                                            map.put(x, value);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        //解析ner
                        JSONObject nerObject = hitsArray.getJSONObject(i).getJSONObject("_source");
                        String nerstring = nerObject.get("ner").toString();
                        if(!nerstring.equals("")&&nerstring != null) {
                        	JSONObject parseObject2 = JSONObject.parseObject(nerstring);
                        	for (Entry<String, Object> entrySet : parseObject2.entrySet()) {
                        		String value = entrySet.getValue().toString();
                        		JSONObject parseObject3 = JSONObject.parseObject(value);
                        		for (Entry<String, Object> entrySet1 : parseObject3.entrySet()) {
                        			String key = entrySet1.getKey();
                        			if(isLockWordsMonster(key)==false) {
                        				String key_value = entrySet1.getValue().toString();
                            			JSONObject parseObject4 = JSONObject.parseObject(key_value);
                            			int num = Integer.parseInt(parseObject4.get("num").toString());
                            			if(!jsonObject.containsKey(key)) {
                            				 if (map.containsKey(key)) {
                                                 map.put(key, map.get(key) + num);
                                             } else {
                                                 map.put(key, num);
                                             }
                            			}
                        			}
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
                        .limit(200).collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(result);
    }

    // 高频词指数
//    public String keyWordIndex(Integer timePeriod, String keyword, String times, String timee, String stopword,
//                               Integer projectType) {
//        try {
//            List<Map<String, Object>> keyWordListB = new ArrayList<>();
//            Map<String, Integer> mapB = keyWordList(keyword, stopword, times, timee, projectType);
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            String format = "";
//            if (timePeriod == 1) {
//                format = LocalDateTime.parse(times, dateTimeFormatter).minusDays(1).format(dateTimeFormatter);
//            }
//            if (timePeriod == 2) {
//                format = LocalDateTime.parse(times, dateTimeFormatter).minusDays(3).format(dateTimeFormatter);
//            }
//            if (timePeriod == 3) {
//                format = LocalDateTime.parse(times, dateTimeFormatter).minusDays(7).format(dateTimeFormatter);
//            }
//            if (timePeriod == 4) {
//                format = LocalDateTime.parse(times, dateTimeFormatter).minusDays(15).format(dateTimeFormatter);
//            }
//            Map<String, Integer> mapA = keyWordList(keyword, stopword, format, times, projectType);
//            // 获取排序前十
//            for (Entry<String, Integer> entrySet : mapB.entrySet()) {
//                try {
//                    Map<String, Object> map2 = new HashMap<>();
//                    map2.put("keyword", entrySet.getKey());
//                    map2.put("count", entrySet.getValue());
//                    keyWordListB.add(map2);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            keyWordListB = keyWordListB.stream()
//                    .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
//                    .collect(Collectors.toList());
//            int total = keyWordListB.stream().mapToInt(a -> (int) a.get("count")).sum();
//            String message = "";
//            for (int i = 0; i < keyWordListB.size(); i++) {
//                try {
//                    int count = (int) keyWordListB.get(i).get("count");
//                    String keywordB = (String) keyWordListB.get(i).get("keyword");
//                    if (i == 0) {
//                        message = keywordB;
//                    } else {
//                        message = "," + keywordB;
//                    }
//                    Integer countA = mapA.get(keywordB);
//                    if (null == countA)
//                        countA = 0;
//                    if (count > countA) {
//                        keyWordListB.get(i).put("trend", 1);
//                    } else if (count < countA) {
//                        keyWordListB.get(i).put("trend", 3);
//                    } else {
//                        keyWordListB.get(i).put("trend", 2);
//                    }
//                    keyWordListB.get(i).put("index", MyMathUtil.calculatedRatioWithPercentSign(count, total));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            try {
//                MyHttpRequestUtil.doPostKafka("ikHotWords", message, kafuka_url);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return JSON.toJSONString(keyWordListB);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//    }
    
    
    
//    public static void main(String[] args) {
//    	String keyWordIndex = keyWordIndex(1,"南京","2021-01-20 00:00:01","2021-03-20 00:00:01","",1,"[{\"x\":\"净利润\",\"value\":742},{\"x\":\"百分点\",\"value\":684},{\"x\":\"人民币\",\"value\":658},{\"x\":\"上年末\",\"value\":656},{\"x\":\"数字化\",\"value\":567},{\"x\":\"浦发银行\",\"value\":540},{\"x\":\"一季度\",\"value\":528},{\"x\":\"金刚玻璃\",\"value\":503},{\"x\":\"小微企业\",\"value\":501},{\"x\":\"建设银行\",\"value\":446},{\"x\":\"中信银行\",\"value\":430},{\"x\":\"公募基金\",\"value\":421},{\"x\":\"基金公司\",\"value\":390},{\"x\":\"财富管理\",\"value\":388},{\"x\":\"民生银行\",\"value\":384},{\"x\":\"彭士禄\",\"value\":376},{\"x\":\"子公司\",\"value\":359},{\"x\":\"董事会\",\"value\":354},{\"x\":\"兴业银行\",\"value\":344},{\"x\":\"碳中和\",\"value\":336},{\"x\":\"中小银行\",\"value\":327},{\"x\":\"制造业\",\"value\":319},{\"x\":\"金融机构\",\"value\":312},{\"x\":\"投资者\",\"value\":297},{\"x\":\"互联网\",\"value\":295},{\"x\":\"资产质量\",\"value\":286},{\"x\":\"分别为\",\"value\":286},{\"x\":\"营业收入\",\"value\":276},{\"x\":\"普惠金融\",\"value\":274},{\"x\":\"金融服务\",\"value\":270},{\"x\":\"金融科技\",\"value\":259},{\"x\":\"中国人寿\",\"value\":255},{\"x\":\"庄毓新\",\"value\":254},{\"x\":\"管理费\",\"value\":248},{\"x\":\"贷款余额\",\"value\":248},{\"x\":\"信用卡\",\"value\":238},{\"x\":\"不良贷款率\",\"value\":234},{\"x\":\"新能源\",\"value\":233},{\"x\":\"京东数科\",\"value\":228},{\"x\":\"中国银行\",\"value\":219},{\"x\":\"总资产\",\"value\":213},{\"x\":\"乡村振兴\",\"value\":213},{\"x\":\"商业银行\",\"value\":213},{\"x\":\"哈尔滨银行\",\"value\":212},{\"x\":\"光大银行\",\"value\":203},{\"x\":\"一卡易\",\"value\":201},{\"x\":\"上市公司\",\"value\":199},{\"x\":\"公积金\",\"value\":192},{\"x\":\"绿色金融\",\"value\":185},{\"x\":\"工商银行\",\"value\":183},{\"x\":\"邮储银行\",\"value\":182},{\"x\":\"房地产\",\"value\":182},{\"x\":\"交通银行\",\"value\":176},{\"x\":\"高质量\",\"value\":175},{\"x\":\"报告期\",\"value\":168},{\"x\":\"银保监会\",\"value\":167},{\"x\":\"房地产贷款\",\"value\":165},{\"x\":\"托管费\",\"value\":164},{\"x\":\"戴志康\",\"value\":157},{\"x\":\"招商银行\",\"value\":154},{\"x\":\"副总经理\",\"value\":147},{\"x\":\"副市长\",\"value\":147},{\"x\":\"净息差\",\"value\":146},{\"x\":\"副书记\",\"value\":145},{\"x\":\"资本充足率\",\"value\":140},{\"x\":\"零售业务\",\"value\":140},{\"x\":\"不良贷款\",\"value\":137},{\"x\":\"净买入\",\"value\":135},{\"x\":\"解决方案\",\"value\":134},{\"x\":\"大数据\",\"value\":132},{\"x\":\"比亚迪\",\"value\":131},{\"x\":\"货币政策\",\"value\":128},{\"x\":\"企业家\",\"value\":126},{\"x\":\"以下简称\",\"value\":123},{\"x\":\"吉利汽车\",\"value\":120},{\"x\":\"住房贷款\",\"value\":118},{\"x\":\"平安银行\",\"value\":118},{\"x\":\"长三角\",\"value\":117},{\"x\":\"归母净利润\",\"value\":117},{\"x\":\"总经理\",\"value\":114},{\"x\":\"证券日报\",\"value\":114},{\"x\":\"被告人\",\"value\":114},{\"x\":\"两面针\",\"value\":114},{\"x\":\"金融市场\",\"value\":112},{\"x\":\"大学生\",\"value\":109},{\"x\":\"碳达峰\",\"value\":109},{\"x\":\"资本市场\",\"value\":107},{\"x\":\"工作人员\",\"value\":105},{\"x\":\"沃尔沃\",\"value\":104},{\"x\":\"贷款增速\",\"value\":104},{\"x\":\"供应链\",\"value\":97},{\"x\":\"中信证券\",\"value\":95},{\"x\":\"募集资金\",\"value\":95},{\"x\":\"亿美元\",\"value\":95},{\"x\":\"净值型\",\"value\":94},{\"x\":\"基础上\",\"value\":92},{\"x\":\"消费贷款\",\"value\":92},{\"x\":\"贺华强\",\"value\":91},{\"x\":\"重庆农商行\",\"value\":90},{\"x\":\"年度报告\",\"value\":89}]");
//    	System.out.println(keyWordIndex);
//	}   
 // 高频词指数
    public String keyWordIndex(Integer timePeriod, String keyword, String times, String timee, String stopword,
                               Integer projectType,String wordCloud) {
    	
    	JSONArray parseArray = JSONArray.parseArray(wordCloud);
    	List<Map<String, Object>> keyWordListB = new ArrayList<>();
    	for (int i=0;i<parseArray.size()&&i<10;i++) {
    		JSONObject parseObject = JSONObject.parseObject(parseArray.get(i).toString());
    		String x = parseObject.getString("x");
    		Map<String, Integer> mapcurrent = keyWordAndSearchKeywordList(keyword, stopword, times, timee, projectType,x);
    		 Map<String, Object> map2 = new HashMap<>();
             map2.put("keyword", x);
             map2.put("count", mapcurrent.get(x));
             
             DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
             String format = "";
             if (timePeriod == 1) {
                 format = LocalDateTime.parse(times, dateTimeFormatter).minusDays(1).format(dateTimeFormatter);
             }
             if (timePeriod == 2) {
                 format = LocalDateTime.parse(times, dateTimeFormatter).minusDays(3).format(dateTimeFormatter);
             }
             if (timePeriod == 3) {
                 format = LocalDateTime.parse(times, dateTimeFormatter).minusDays(7).format(dateTimeFormatter);
             }
             if (timePeriod == 4) {
                 format = LocalDateTime.parse(times, dateTimeFormatter).minusDays(15).format(dateTimeFormatter);
             }
             
             //环比数据
             Map<String, Integer> mapA = keyWordAndSearchKeywordList(keyword, stopword, format, times, projectType,x);
             Integer value_chain = mapA.get(x);
             map2.put("value_chain", value_chain);
             keyWordListB.add(map2);
		}
    	 keyWordListB = keyWordListB.stream()
                 .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                 .collect(Collectors.toList());
         int total = keyWordListB.stream().mapToInt(a -> (int) a.get("count")).sum();
         
      try {
      
      String message = "";
      for (int i = 0; i < keyWordListB.size(); i++) {
          try {
              int count = (int) keyWordListB.get(i).get("count");
              String keywordB = (String) keyWordListB.get(i).get("keyword");
              if (i == 0) {
                  message = keywordB;
              } else {
                  message = "," + keywordB;
              }
              Integer countA = (int) keyWordListB.get(i).get("value_chain");
              if (null == countA)
                  countA = 0;
              if (count > countA) {
                  keyWordListB.get(i).put("trend", 1);
              } else if (count < countA) {
                  keyWordListB.get(i).put("trend", 3);
              } else {
                  keyWordListB.get(i).put("trend", 2);
              }
              keyWordListB.get(i).put("index", MyMathUtil.calculatedRatioWithPercentSign(count, total));
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
//      try {
//          MyHttpRequestUtil.doPostKafka("ikHotWords", message, kafuka_url);
//      } catch (Exception e) {
//          e.printStackTrace();
//      }
      return JSON.toJSONString(keyWordListB);
  } catch (Exception e) {
      e.printStackTrace();
      return "";
  }
    }
    // 高频词指数
    public String keyWordReportIndex(Integer timePeriod, String keyword, String times, String timee, String stopword,
                               Integer projectType,String wordCloud) {
    	
    	JSONArray parseArray = JSONArray.parseArray(wordCloud);
    	List<Map<String, Object>> keyWordListB = new ArrayList<>();
    	for (int i=0;i<parseArray.size()&&i<10;i++) {
    		JSONObject parseObject = JSONObject.parseObject(parseArray.get(i).toString());
    		String x = parseObject.getString("x");
    		Map<String, Integer> mapcurrent = keyWordAndSearchKeywordList(keyword, stopword, times, timee, projectType,x);
    		 Map<String, Object> map2 = new HashMap<>();
             map2.put("keyword", x);
             map2.put("count", mapcurrent.get(x));
             
             DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
             String format = "";
             if (timePeriod == 1) {
                 format = LocalDateTime.parse(times, dateTimeFormatter).minusDays(1).format(dateTimeFormatter);
             }
             if (timePeriod == 2) {
                 format = LocalDateTime.parse(times, dateTimeFormatter).minusDays(7).format(dateTimeFormatter);
             }
             if (timePeriod == 3) {
                 format = LocalDateTime.parse(times, dateTimeFormatter).minusDays(30).format(dateTimeFormatter);
             }
             
             
             //环比数据
             Map<String, Integer> mapA = keyWordAndSearchKeywordList(keyword, stopword, format, times, projectType,x);
             Integer value_chain = mapA.get(x);
             map2.put("value_chain", value_chain);
             keyWordListB.add(map2);
		}
    	 keyWordListB = keyWordListB.stream()
                 .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                 .collect(Collectors.toList());
         int total = keyWordListB.stream().mapToInt(a -> (int) a.get("count")).sum();
         
      try {
      
      String message = "";
      for (int i = 0; i < keyWordListB.size(); i++) {
          try {
              int count = (int) keyWordListB.get(i).get("count");
              String keywordB = (String) keyWordListB.get(i).get("keyword");
              if (i == 0) {
                  message = keywordB;
              } else {
                  message = "," + keywordB;
              }
              Integer countA = (int) keyWordListB.get(i).get("value_chain");
              if (null == countA)
                  countA = 0;
              if (count > countA) {
                  keyWordListB.get(i).put("trend", 1);
              } else if (count < countA) {
                  keyWordListB.get(i).put("trend", 3);
              } else {
                  keyWordListB.get(i).put("trend", 2);
              }
              keyWordListB.get(i).put("index", MyMathUtil.calculatedRatioWithPercentSign(count, total));
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
//      try {
//          MyHttpRequestUtil.doPostKafka("ikHotWords", message, kafuka_url);
//      } catch (Exception e) {
//          e.printStackTrace();
//      }
      return JSON.toJSONString(keyWordListB);
  } catch (Exception e) {
      e.printStackTrace();
      return "";
  }
    }  
    
    
    
    /**
     * 关键词高频词
     */
    public Map<String, Integer> keyWordList(String keyword, String stopword, String times, String timee,
                                            Integer projectType) {
        Map<String, Integer> map = new HashMap<>();
        try {
            String url = es_search_url + ReportConstant.es_api_keyword_list;
            String params = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&searchType=1&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            if (StringUtils.isNotBlank(sendPostEsSearch)) {
                JSONObject parseObject = JSON.parseObject(sendPostEsSearch);
                JSONArray hitsArray = parseObject.getJSONObject("hits").getJSONArray("hits");
                for (int i = 0; i < hitsArray.size(); i++) {
                    try {
                        JSONObject jsonObject = hitsArray.getJSONObject(i).getJSONObject("_source")
                                .getJSONObject("key_words");
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    
    /**
     * 关键词高频词-搜索词
     */
    public Map<String, Integer> keyWordAndSearchKeywordList(String keyword, String stopword, String times, String timee,
                                            Integer projectType,String searchkeyword) {
        Map<String, Integer> map = new HashMap<>();
        map.put(searchkeyword, 0);
        try {
            String url = es_search_url + ReportConstant.es_api_media_exposure;
            //String url = "http://dx2.stonedt.com:7120" + ReportConstant.es_api_media_exposure;
            String params = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&searchType=1&emotionalIndex=1,2,3" + "&projecttype=" + projectType+"&origintype=0&searchkeyword="+searchkeyword;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            if (StringUtils.isNotBlank(sendPostEsSearch)) {
                JSONObject parseObject = JSON.parseObject(sendPostEsSearch);
                Integer total = parseObject.getJSONObject("hits").getInteger("total");
              map.put(searchkeyword, total);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return map;
    }




    public static void main(String[] args) {
        AnalysisDataRequest analysisDataRequest = new AnalysisDataRequest();
        String nj = analysisDataRequest.mediaActivityAnalysis("南京", "", "2020-07-07 16:15:00", "2021-08-05 16:16:00", 2);
        System.out.println(nj);


    }
    
    
    

    // 媒体活跃度分析
    public String mediaActivityAnalysis(String highKeyword, String stopword, String times, String timee, Integer projectType) {
        try {
            Map<String, Object> result = new HashMap<>();
  String abc = "http://dx2.stonedt.com:7121";
        es_search_url = abc;
            String url = es_search_url + ReportConstant.es_api_media_active;
            String param = "times=" + times + "&timee=" + timee + "&keyword=" + highKeyword
                    + "&stopword=" + stopword + "&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
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

    // 热点地区排名
    public String hotSpotRanking(String highKeyword, String stopword, String times, String timee, Integer projectType) {
        try {
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> chart = new ArrayList<>();
            List<Map<String, Object>> list = new ArrayList<>();
            String url = es_search_url + ReportConstant.es_api_hot_spot_ranking;
            String param = "times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword=" + stopword + "&emotionalIndex=1,2,3"
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

    // 数据来源分布
    public String dataSourceDistribution(String highKeyword, String stopword, String times, String timee, Integer projectType) {
        String url = es_search_url + "/yqsearch/sourcewebsitesearch";
        String param = "times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword="
                + stopword + "&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
        try {
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, param);
            List<Map<String, Object>> list = new ArrayList<>();
            if (StringUtils.isNotBlank(sendPostEsSearch)) {
                Map<String, String> initSourceZh = initSource("zh");
                JSONObject parseObject = JSON.parseObject(sendPostEsSearch);
                JSONArray jsonArray = parseObject.getJSONObject("aggregations")
                        .getJSONObject("group_by_tags").getJSONArray("buckets");
                for (int i = 0; i < jsonArray.size(); i++) {
                    Map<String, Object> map = new HashMap<>();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String key = jsonObject.getString("key");
                    Integer count = jsonObject.getInteger("doc_count");
                    map.put("name", initSourceZh.get(key));
                    map.put("value", count);
                    list.add(map);
                }
            }
            return JSON.toJSONString(list);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public Map<String, String> initSource(String type) {
        Map<String, String> map = new HashMap<>();
        if ("zh".equals(type)) {
            map.put("1", "微信");
            map.put("2", "微博");
            map.put("3", "政务");
            map.put("4", "论坛");
            map.put("5", "新闻");
            map.put("6", "报刊");
            map.put("7", "客户端");
            map.put("8", "网站");
            map.put("9", "外媒");
            map.put("10", "视频");
            map.put("11", "博客");
        } else if ("en".equals(type)) {
            map.put("1", "wechat");
            map.put("2", "weibo");
            map.put("3", "gov");
            map.put("4", "bbs");
            map.put("5", "news");
            map.put("6", "newspaper");
            map.put("7", "app");
            map.put("8", "web");
            map.put("9", "foreign_media");
            map.put("10", "video");
            map.put("11", "blog");
        }
        return map;
    }






    // 数据来源分析
    public String dataSourceAnalysis(String highKeyword, String stopword, String times, String timee, Integer projectType) {

        String abc = "http://dx2.stonedt.com:7121";
        es_search_url = abc;

        String url = es_search_url + "/yqsearch/datasourceanalysis";
        String param = "classify=1,2,3,4,5,6,7,8,9,10,11&times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword="
                + stopword + "&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
        try {
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, param);
            Map<String, Object> result = new HashMap<>();
            if (StringUtils.isNotBlank(sendPostEsSearch)) {
                JSONObject parseObject = JSON.parseObject(sendPostEsSearch);
                JSONArray jsonArray = parseObject.getJSONObject("aggregations")
                        .getJSONObject("group_by_tags").getJSONArray("buckets");

                Map<String, String> initSourceZh = initSource("zh");
                Map<String, String> initSourceEn = initSource("en");
                List<Map<String, Object>> all = new ArrayList<>();
                List<Map<String, Object>> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    List<Map<String, Object>> list2 = new ArrayList<>();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String key = jsonObject.getString("key");

                    //2021.8.6修改，将视频/微博/微信信息替换

                    //微信替换
                    if("1".equals(key)){
                        //获取作者
                        String authorUrl = es_search_url + "/yqsearch/statisticsauthor";
                        String aParam = "classify=1&times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword="
                                + stopword + "&emotionalIndex=1,2,3" + "&projecttype=" + projectType;


                        String authorParam = MyHttpRequestUtil.sendPostEsSearch(authorUrl, aParam);

                        JSONObject authorObject = JSON.parseObject(authorParam);
                        JSONObject authorJson = authorObject.getJSONObject("aggregations")
                                .getJSONObject("group_by_tags");

                        //替换数组中的group_by_tags
                        jsonObject.put("group_by_tags" , authorJson);


                    //微博
                    }else if("2".equals(key)){

                        //获取作者
                        String authorUrl = es_search_url + "/yqsearch/statisticsauthor";
                        String aParam = "classify=2&times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword="
                                + stopword + "&emotionalIndex=1,2,3" + "&projecttype=" + projectType;


                        String authorParam = MyHttpRequestUtil.sendPostEsSearch(authorUrl, aParam);

                        JSONObject authorObject = JSON.parseObject(authorParam);
                        JSONObject authorJson = authorObject.getJSONObject("aggregations")
                                .getJSONObject("group_by_tags");

                        //替换数组中的group_by_tags
                        jsonObject.put("group_by_tags" , authorJson);


                    //视频
                    }else if("10".equals(key)){

                        //获取作者
                        String authorUrl = es_search_url + "/yqsearch/statisticsauthor";
                        String aParam = "classify=10&times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword="
                                + stopword + "&emotionalIndex=1,2,3" + "&projecttype=" + projectType;


                        String authorParam = MyHttpRequestUtil.sendPostEsSearch(authorUrl, aParam);

                        JSONObject authorObject = JSON.parseObject(authorParam);
                        JSONObject authorJson = authorObject.getJSONObject("aggregations")
                                .getJSONObject("group_by_tags");

                        //替换数组中的group_by_tags
                        jsonObject.put("group_by_tags" , authorJson);
                    }
                    //2021.8.6




                    String type = initSourceZh.get(key);
                    Map<String, Object> mapa = new HashMap<>();
                    mapa.put("name", type);


                        mapa.put("key", key);


                    list.add(mapa);
                    JSONArray jsonArray2 = jsonObject.getJSONObject("group_by_tags").getJSONArray("buckets");

                    int jsonArray2Size = jsonArray2.size();
                    if (jsonArray2Size > 10){
                        jsonArray2Size = 10;
                    }

                    for (int j = 0; j < jsonArray2Size; j++) {
                        Map<String, Object> map = new HashMap<>();
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(j);
                        String key2 = jsonObject2.getString("key");
                        int allCount = jsonObject2.getInteger("doc_count");

                            map.put("name", key2);

                        map.put("type", type);
                        map.put("allCount", allCount);

                        JSONArray jsonArray3 = null;
                        //替换后的数据获取名字不一样，进行判断
                        if(jsonObject2.getJSONObject("group_by_emotion") != null){
                            jsonArray3 = jsonObject2.getJSONObject("group_by_emotion").getJSONArray("buckets");
                        }else{
                            jsonArray3 = jsonObject2.getJSONObject("top-terms-emotion").getJSONArray("buckets");
                        }

                        int sensitiveCount = 0;
                        for (int k = 0; k < jsonArray3.size(); k++) {
                            JSONObject jsonObject3 = jsonArray3.getJSONObject(k);
                            String key3 = jsonObject3.getString("key");
                            Integer count3 = jsonObject3.getInteger("doc_count");
                            if ("3".equals(key3)) {
                                sensitiveCount = count3;
                            }
                        }
                        map.put("sensitiveCount", sensitiveCount);
                        list2.add(map);
                        all.add(map);
                    }

                    String bigKey = initSourceEn.get(key);
                    if (bigKey != null) {
                        result.put(bigKey , list2);
                    }
                }
                List<Map<String, Object>> collect = all.stream().sorted((map1, map2) -> (int) map2.get("allCount") - (int) map1.get("allCount"))
                        .limit(10).collect(Collectors.toList());
                result.put("all", collect);
                result.put("list", list);
            }
            return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // 关键词曝光度环比排行
    public String keywordExposure(String keyword, String highKeyword, String stopword, String times, String timee, Integer projectType) {
        try {
            String es_api_keyword_emotion_statistical = VolumeConstant.es_api_keyword_emotion_statistical;
            String url = es_search_url + es_api_keyword_emotion_statistical;
            String[] keywords = keyword.split(",");
            JSONObject result = new JSONObject();
            result.put("keyword_count", keywords.length);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < keywords.length; i++) {
                try {
                    String params = "times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword=" + stopword
                            + "&searchkeyword=" + keywords[i] + "&origintype=0&emotionalIndex=1,2,3&projecttype=" + projectType;
                    String sendPost = sendPost(url, params);
                    Map<String, Object> map = new HashMap<String, Object>();
                    JSONObject parseObject = JSONObject.parseObject(sendPost);
                    Integer total = parseObject.getJSONObject("hits").getInteger("total");
                    map.put("keyword", keywords[i]);
                    map.put("total", total);
                    JSONArray bucketsArray = parseObject.getJSONObject("aggregations").getJSONObject("top-terms-emotion")
                            .getJSONArray("buckets");
                    for (int j = 0; j < bucketsArray.size(); j++) {
                        JSONObject jsonObject = JSONObject.parseObject(String.valueOf(bucketsArray.get(j)));
                        String keynum = jsonObject.getString("key");
                        Integer doc_count = jsonObject.getInteger("doc_count");
                        if ("1".equals(keynum)) {
                            keynum = "positive_rate";
                        } else if ("2".equals(keynum)) {
                            keynum = "neutral_rate";
                        } else if ("3".equals(keynum)) {
                            keynum = "negative_rate";
                        }
                        String calculatedRatio = MyMathUtil.calculatedRatioWithPercentSign(doc_count, total);
                        map.put(keynum, calculatedRatio);
                    }
                    // 环比
                    Map<String, String> ringRatioCycle = DateUtil.RingRatioCycle(times, timee);
                    params = "times=" + ringRatioCycle.get("startTime") + "&timee=" + ringRatioCycle.get("endTime")
                            + "&keyword=" + highKeyword + "&stopword=" + stopword + "&emotionalIndex=1,2,3&projecttype=" + projectType
                            + "&searchkeyword=" + keywords[i] + "&origintype=0";
                    sendPost = sendPost(url, params);
                    JSONObject jsonObject = JSONObject.parseObject(sendPost);
                    Integer momtotal = jsonObject.getJSONObject("hits").getInteger("total");// 环比数量
                    int type = 2;
                    if (total < momtotal) {
                        type = 1;
                    } else if (total == momtotal) {
                        type = 2;
                    } else if (total > momtotal) {
                        type = 3;
                    }
                    String calculatedRatio = MyMathUtil.calculatedRatioWithPercentSign(momtotal, total);
                    map.put("trend", type);
                    map.put("chain_growth", calculatedRatio);
                    list.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return (int) o2.get("total") - (int) o1.get("total");
                }
            });
            JSONArray array = JSONArray.parseArray(JSON.toJSONString(list));
            return array.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // 自媒体渠道声量排行
    public String selfMediaRanking(String highKeyword, String stopword, String times, String timee, Integer projectType) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            String url = es_search_url + ReportConstant.es_api_media_list;
            String param = "classify=7&times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword=" + stopword
                    + "&emotionalIndex=1,2,3&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, param);
            JSONObject parseObject = JSONObject.parseObject(sendPostEsSearch);
            JSONArray buckets = parseObject.getJSONObject("aggregations").getJSONObject("top-terms-aggregation")
                    .getJSONArray("buckets");
            for (int i = 0; i < buckets.size(); i++) {
                try {
                    if (result.size() == 5) break;
                    String name = buckets.getJSONObject(i).getString("key");
                    int total = buckets.getJSONObject(i).getIntValue("doc_count");
                    if (StringUtils.isNotBlank(name)) {
                        JSONArray jsonArray = buckets.getJSONObject(i).getJSONObject("top-terms-emotion")
                                .getJSONArray("buckets");
                        if (jsonArray.isEmpty()) continue;
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("volume", total);
                        String logo = "";
                        List<String> platform_names = new ArrayList<>();
                        for (int j = 0; j < jsonArray.size(); j++) {
                            try {
                                if (result.size() == 5) break;
                                String platform_name = jsonArray.getJSONObject(j).getString("key");
                                platform_names.add(platform_name);
                                if (j == 0) {
                                    url = es_search_url + ReportConstant.es_api_wemedia_info;
                                    param = "name=" + name + "&platform_name=" + platform_name;
                                    sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, param);
                                    parseObject = JSONObject.parseObject(sendPostEsSearch);
                                    JSONArray jsonArray2 = parseObject.getJSONArray("data");
                                    if (!jsonArray2.isEmpty()) {
                                        JSONObject jsonObject = jsonArray2.getJSONObject(0).getJSONObject("_source");
                                        logo = jsonObject.getString("logo");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        map.put("logo", TextUtil.nullAsStringEmpty(logo));
                        map.put("platform_names", StringUtils.join(platform_names, ","));
                        result.add(map);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return JSON.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


// 历史代码****************	

    // 4拼接热门资讯
    public JSONArray ZiXun(String keyword, String highKeyword, String times, String timee, String stopword,
                           Integer projectType) {
        // 获取总数量
        JSONArray resultArray = new JSONArray();
        try {
            int total = 0;
            // Map<Integer, Object> map = new HashMap<>();
            JSONObject json = new JSONObject();
            try {
                String ziXunJSon = getZiXunJSon("", highKeyword, times, timee, stopword, projectType);
                json = JSONObject.parseObject(ziXunJSon);
                if (json != null) {
                    total += json.getIntValue("total");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONArray jsonArray = json.getJSONArray("listdata");
            // JSONArray resultarray = new JSONArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                try {
                    JSONObject parseObject = JSONObject.parseObject(jsonArray.get(i).toString());
                    if (json != null) {
                        // 获取占比
                        String rate = MyMathUtil.calculatedRatioWithPercentSign(parseObject.getIntValue("total"),
                                total);
                        JSONObject jsonResult = new JSONObject();
                        jsonResult.put("article_public_id", parseObject.get("article_public_id"));
                        jsonResult.put("title", parseObject.get("title"));
                        jsonResult.put("publish_time", parseObject.get("publish_time"));
                        jsonResult.put("emotion", parseObject.get("emotionalIndex"));
                        jsonResult.put("source_name", TextUtil
                                .removeParenthesesAndContents(String.valueOf(parseObject.get("sourcewebsitename"))));
                        jsonResult.put("rate", rate);
                        jsonResult.put("count", parseObject.getIntValue("total"));
                        resultArray.add(jsonResult);
                        // map.put(json.getIntValue("total"), jsonResult);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            //根据数据量进行排名前十条数据
//            List<Map.Entry<Integer, Object>> list = new ArrayList<Map.Entry<Integer, Object>>(map.entrySet());
//            // 然后通过比较器来实现排序
//            Collections.sort(list, new Comparator<Map.Entry<Integer, Object>>() {
//                // 降序排序
//                public int compare(Map.Entry<Integer, Object> o1, Map.Entry<Integer, Object> o2) {
//                    return o1.getKey().compareTo(o2.getKey());
//                }
//            });
//            // 这里是将排好的进行降序排序
//            Collections.reverse(list);
//            for (Map.Entry<Integer, Object> key : list) {
//                try {
//                    if (resultArray.size() > 10) {
//                        break;
//                    }
//                    resultArray.add(key.getValue());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultArray;
    }

    // 4热门资讯
    public String getZiXunJSon(String searchkeyword, String keyword, String time, String timee, String stopword,
                               Integer projectType) {
        JSONObject datajson = new JSONObject();
        try {
            int total = 0;
            String param = "times=" + time + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&searchkeyword=" + searchkeyword + "&origintype=0&emotionalIndex=1,2,3&searchType=3"
                    + "&projecttype=" + projectType;
            JSONArray arraylist = new JSONArray();
            try {
                String result = sendPost(es_search_url + "/yqsearch/hottopic", param);
                JSONArray ja = JSONObject.parseObject(result).getJSONObject("aggregations").getJSONObject("top_score")
                        .getJSONArray("buckets");

                if (ja != null && !ja.isEmpty()) {
                    for (int i = 0; i < ja.size(); i++) {
                        JSONObject json = new JSONObject();
                        try {
                            JSONObject jsonResult = ja.getJSONObject(i);
                            String title = jsonResult.get("key").toString();
                            // String title = (String) jsonResult.get("title");
                            if (StringUtils.isNotBlank(title)) {
                                JSONObject parseObject1 = JSONObject
                                        .parseObject(jsonResult.getJSONObject("top_score_hits").getJSONObject("hits")
                                                .getJSONArray("hits").get(0).toString());
                                JSONObject parseObject = parseObject1.getJSONObject("_source");
                                json.put("total", Integer.parseInt(jsonResult.get("doc_count").toString()));
                                json.put("title", title);
                                json.put("article_public_id", parseObject.get("article_public_id"));
                                json.put("publish_time", String.valueOf(parseObject.get("publish_time")));
                                json.put("sourcewebsitename", parseObject.get("sourcewebsitename"));
                                json.put("emotionalIndex", parseObject.get("emotionalIndex"));
                                total += Integer.parseInt(jsonResult.get("doc_count").toString());
                                arraylist.add(json);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            datajson.put("listdata", arraylist);
            datajson.put("total", total);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(datajson, SerializerFeature.DisableCircularReferenceDetect);
    }

    public String hotData(JSONArray jsonArray, String keyword, String stopword, String times, String timee,
                          Integer projectType) {
        JSONArray orgjsonArray = new JSONArray();
        try {
            String message = "";
            for (int j = 0; j < jsonArray.size(); j++) {
                try {
                    if (j > 9)
                        break;
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    String searchkeyword = jsonObject.getString("keyword");
                    if (j == 0) {
                        message = searchkeyword;
                    } else {
                        message = "," + searchkeyword;
                    }
                    Map<String, Object> searchkeywordMap = searchkeyword(searchkeyword, keyword, stopword, timee, times,
                            projectType);
                    jsonObject.put("article_public_id", searchkeywordMap.get("article_public_id"));
                    jsonObject.put("title", searchkeywordMap.get("title"));
                    jsonObject.put("emotionalIndex", searchkeywordMap.get("emotionalIndex"));
                    orgjsonArray.add(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                MyHttpRequestUtil.doPostKafka("ikHotWords", message, kafuka_url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(orgjsonArray);
    }

    // 获取相关资讯
    public JSONArray XiangGuan(String keyword, String time, String timee, String stopword, Integer projectType) {
        JSONArray array = new JSONArray();
        try {
            String param = "times=" + time + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&page=1&size=5&searchType=1&esindex=postal&estype=infor&emotionalIndex=1,2,3" + "&projecttype="
                    + projectType;
            try {
                String result = sendPost(es_search_url + "/yqsearch/searchlist", param);
                JSONObject json = JSONObject.parseObject(result);
                JSONArray resultArray = json.getJSONArray("data");
                for (int i = 0; i < resultArray.size(); i++) {
                    try {
                        JSONObject resultJson = new JSONObject();
                        String article_public_id = resultArray.getJSONObject(i).getString("_id");
                        String title = resultArray.getJSONObject(i).getJSONObject("_source").getString("title");
                        int emotion = resultArray.getJSONObject(i).getJSONObject("_source")
                                .getIntValue("emotionalIndex");
                        String publish_time = resultArray.getJSONObject(i).getJSONObject("_source")
                                .getString("publish_time");
                        String rate;
                        if (emotion == 1) {
                            rate = resultArray.getJSONObject(i).getJSONObject("_source").getString("positive_score");
                        } else if (emotion == 2) {
                            rate = resultArray.getJSONObject(i).getJSONObject("_source").getString("neutrality_score");
                        } else {
                            rate = resultArray.getJSONObject(i).getJSONObject("_source").getString("negative_score");
                        }
                        NumberFormat nt = NumberFormat.getPercentInstance();
                        nt.setMinimumFractionDigits(2);
                        rate = nt.format(Double.parseDouble(rate));
                        String source_name = resultArray.getJSONObject(i).getJSONObject("_source")
                                .getString("source_name");
                        resultJson.put("article_public_id", article_public_id);
                        resultJson.put("title", title);
                        resultJson.put("emotion", emotion);
                        resultJson.put("emotion_rate", rate);
                        resultJson.put("publish_time", publish_time);
                        resultJson.put("source_name", source_name);
                        array.add(resultJson);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }


    // 热门地区人物机构
    public String getHot(String keyword, String time, String timee, String stopword, Integer timePerioda,
                         Integer projectType) {
        String json = "";
        try {
            String param = "times=" + time + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&emotionalIndex=1,2,3&projecttype=" + projectType;
            Map<String, String> chainCycle = chainCycle(time, timePerioda);
            String zuo_time = chainCycle.get("start");
            String param2 = "times=" + zuo_time + "&timee=" + time + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&emotionalIndex=1,2,3&projecttype=" + projectType;
            String result = "";
            String result2 = "";
            Map<String, Object> map;
            Map<String, Object> map2;
            try {
                result = sendPost(es_search_url + "/yqsearch/ner", param);
                result2 = sendPost(es_search_url + "/yqsearch/ner", param2);
                map = TextUtil.tool(result);
                map2 = TextUtil.tool(result2);
                json = TextUtil.RingRatioCycletool(map, map2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    // 热门实体相关资讯
    public Map<String, Object> searchkeyword(String searchkeyword, String keyword, String stopword, String timee,
                                             String times, Integer projectType) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            if (StringUtils.isNotBlank(searchkeyword)) {
                searchkeyword = searchkeyword.replaceAll("\"", "");
            }
            String url = es_search_url + ReportConstant.es_api_search_list;
            String param = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&searchType=1&origintype=0&searchType=3&searchkeyword=" + searchkeyword + "&page=1&size=1"
                    + "&emotionalIndex=1,2,3&projecttype=" + projectType;
            String esOpinion = MyHttpRequestUtil.sendPostEsSearch(url, param);
            JSONObject parseObject = JSONObject.parseObject(esOpinion);
            JSONArray jsonArray = parseObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject source = jsonObject.getJSONObject("_source");
                    if (source.containsKey("article_public_id")) {
                        String article_public_id = source.getString("article_public_id");
                        String title = source.getString("title");
                        Integer emotionalIndex = source.getInteger("emotionalIndex");
                        map.put("article_public_id", article_public_id);
                        map.put("title", title);
                        map.put("emotionalIndex", emotionalIndex);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("article_public_id", "");
            map.put("title", "");
            map.put("emotionalIndex", "");
            return map;
        }
    }

    /**
     * 获取环比时间周期
     */
    public Map<String, String> chainCycle(String times, Integer timePerioda) {
        Map<String, String> result = new HashMap<>();
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int days = 1;
            if (timePerioda == 1)
                days = 1;
            if (timePerioda == 2)
                days = 3;
            if (timePerioda == 3)
                days = 7;
            if (timePerioda == 4)
                days = 15;
            LocalDateTime localTimes = LocalDateTime.parse(times, dateTimeFormatter);
            String format = localTimes.minusDays(days).format(dateTimeFormatter);
            result.put("start", format);
            result.put("end", times);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取指定时间周期开始和结束时间 yyyy-MM-dd HH:mm:ss
     */
    public Map<String, String> time(Integer timePeriod) {
        Map<String, String> time = new HashMap<String, String>();
        try {
            if (timePeriod == null)
                timePeriod = 1;
            int days = 1;
            switch (timePeriod) {
                case 1:
                    days = 1;
                    break;
                case 2:
                    days = 3;
                    break;
                case 3:
                    days = 7;
                    break;
                case 4:
                    days = 15;
                    break;
                default:
                    break;
            }
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime nowDateTime = LocalDateTime.now();
            String now = nowDateTime.format(dateTimeFormatter);
            String start = nowDateTime.minusDays(days).format(dateTimeFormatter);

            time.put("start", start);
            time.put("end", now);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    // 发送请求
    public static String sendPost(String url, String params) throws IOException {
        StringBuilder response = new StringBuilder();
        try {
            System.err.println(url + "?" + params);
            PrintWriter out = null;
            BufferedReader in = null;
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(params);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                try {
                    response.append(line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public List<Map<String, Object>> orgIndex(String keyword, String time, String timee, String stopword,
                                              Integer projectType) {
        List<Map<String, Object>> keyWordListB = new ArrayList<>();
        try {
            // 最近24小时
            Map<String, Integer> mapB = orgList(keyword, stopword, time, timee, "", projectType);
            // 环比24小时
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String format = LocalDateTime.parse(time, dateTimeFormatter).minusDays(1).format(dateTimeFormatter);
            Map<String, Integer> mapA = orgList(keyword, stopword, format, time, "", projectType);
            // 获取排序前十
            for (Entry<String, Integer> entrySet : mapB.entrySet()) {
                try {
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("keyword", entrySet.getKey());
                    map2.put("count", entrySet.getValue());
                    keyWordListB.add(map2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            keyWordListB = keyWordListB.stream()
                    .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                    .collect(Collectors.toList());
            int total = keyWordListB.stream().mapToInt(a -> (int) a.get("count")).sum();
            String message = "";
            for (int i = 0; i < keyWordListB.size(); i++) {
                try {
                    int count = (int) keyWordListB.get(i).get("count");
                    String keywordB = (String) keyWordListB.get(i).get("keyword");
                    if (i == 0) {
                        message = keywordB;
                    } else {
                        message = "," + keywordB;
                    }
                    Integer countA = mapA.get(keywordB);
                    if (null == countA)
                        countA = 0;
                    if (count > countA) {
                        keyWordListB.get(i).put("trend", 1);
                    } else if (count < countA) {
                        keyWordListB.get(i).put("trend", 3);
                    } else {
                        keyWordListB.get(i).put("trend", 2);
                    }
                    keyWordListB.get(i).put("index", MyMathUtil.calculatedRatioWithPercentSign(count, total));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                MyHttpRequestUtil.doPostKafka("ikHotWords", message, kafuka_url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyWordListB;
    }

    // 热门机构高频词
    @SuppressWarnings("static-access")
    public Map<String, Integer> orgList(String keyword, String stopword, String times, String timee, String classify,
                                        Integer projectType) {
        Map<String, Integer> map = new HashMap<>();
        try {
            String url = es_search_url + ReportConstant.es_api_ner;
            String params = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&searchType=1&classify=" + classify + "&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            if (StringUtils.isNotBlank(sendPostEsSearch)) {
                JSONObject parseObject = JSON.parseObject(sendPostEsSearch);
                JSONArray hitsArray = parseObject.getJSONObject("hits").getJSONArray("hits");
                for (int i = 0; i < hitsArray.size(); i++) {
                    try {
                        JSONObject jsonObject = hitsArray.getJSONObject(i).getJSONObject("_source");
                        if (!jsonObject.containsKey("ner"))
                            continue;
                        String ner = jsonObject.get("ner").toString();
                        if (ner.equals(""))
                            continue;
                        JSONObject parseObject2 = jsonObject
                                .parseObject(jsonObject.parseObject(ner).get("org").toString());
                        if (parseObject2 != null) {
                            for (Entry<String, Object> entrySet : parseObject2.entrySet()) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

	public String dataNer(String keyword, String stopword, String times, String timee, Integer projectType) {
		JSONObject resultjson = new JSONObject();
		Map<String,Object> map =new HashMap<String,Object>();
		JSONObject locjson = new JSONObject();
		JSONObject bankjson = new JSONObject();
		JSONObject orgjson = new JSONObject();
		JSONObject schooljson = new JSONObject();
		JSONObject ntojson = new JSONObject();
		JSONObject IPOjson = new JSONObject();
		//国内上市
		JSONObject IPO_chinajson = new JSONObject();
		//国外上市
		JSONObject IPO_foreignjson = new JSONObject();
		
		JSONObject perjson = new JSONObject();
		JSONObject hospitaljson = new JSONObject();
        try {
           String url = es_search_url + ReportConstant.es_api_ner;
        	//String url = "http://192.168.71.81:8123/yqsearch/ner";
            String params = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&searchType=1&classify=&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            if (StringUtils.isNotBlank(sendPostEsSearch)) {
                JSONObject parseObject = JSON.parseObject(sendPostEsSearch);
                JSONArray hitsArray = parseObject.getJSONObject("hits").getJSONArray("hits");
                for (int i = 0; i < hitsArray.size(); i++) {
                    try {
                        JSONObject jsonObject = hitsArray.getJSONObject(i).getJSONObject("_source");
                        if (!jsonObject.containsKey("ner"))
                            continue;
                        String ner = jsonObject.get("ner").toString();
                        if (ner.equals(""))
                            continue;
                        //地点
                        JSONObject parseObject2 = jsonObject.parseObject(jsonObject.parseObject(ner).get("loc").toString());
                        if (parseObject2 != null) {
                            for (Entry<String, Object> entrySet : parseObject2.entrySet()) {
                                try {
                                    String x = entrySet.getKey().trim();
                                    //String valuedata =  (String)entrySet.getValue();
                                   // JSONObject locparseObject = jsonObject.parseObject(valuedata);
                                    try {
                                    	JSONObject locparseObject = (JSONObject)entrySet.getValue();
                                        //int value = Integer.parseInt(locparseObject.get("num").toString());通过词频的方式
                                    	int value = 1;//通过关键词匹配文章数的形式
                                        
                                        if (locjson.containsKey(x)) {
                                        	locjson.put(x, Integer.parseInt(locjson.get(x).toString()) + value);
                                        	
                                        } else {
                                        	locjson.put(x, value);
                                        }
									} catch (Exception e) {
										e.printStackTrace();
									}
                                    
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                      //银行bank
                        JSONObject parseObjectbank = jsonObject.parseObject(jsonObject.parseObject(ner).get("bank").toString());
                        if (parseObjectbank != null) {
                            for (Entry<String, Object> entrySet : parseObjectbank.entrySet()) {
                                try {
                                    String x = entrySet.getKey().trim();
                                   // String valuedata =  (String)entrySet.getValue();
                                   // JSONObject locparseObject = jsonObject.parseObject(valuedata);
                                    try {
                                    	 JSONObject locparseObject = (JSONObject)entrySet.getValue();
                                    	//int value = Integer.parseInt(locparseObject.get("num").toString());通过词频的方式
                                     	int value = 1;//通过关键词匹配文章数的形式
                                         if (bankjson.containsKey(x)) {
                                         	bankjson.put(x, Integer.parseInt(bankjson.get(x).toString()) + value);
                                         } else {
                                         	bankjson.put(x, value);
                                         }
									} catch (Exception e) {
										e.printStackTrace();
									}
                                   
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        
                      //机构org
                        JSONObject parseObjectorg = jsonObject.parseObject(jsonObject.parseObject(ner).get("org").toString());
                        if (parseObjectbank != null) {
                            for (Entry<String, Object> entrySet : parseObjectorg.entrySet()) {
                                try {
                                    String x = entrySet.getKey().trim();
                                    //String valuedata =  (String)entrySet.getValue();
                                    //JSONObject locparseObject = jsonObject.parseObject(valuedata);
                                    try {
                                    	 JSONObject locparseObject = (JSONObject)entrySet.getValue();
                                    	//int value = Integer.parseInt(locparseObject.get("num").toString());通过词频的方式
                                     	int value = 1;//通过关键词匹配文章数的形式
                                         if (orgjson.containsKey(x)) {
                                         	orgjson.put(x, Integer.parseInt(orgjson.get(x).toString()) + value);
                                         } else {
                                         	orgjson.put(x, value);
                                         }
									} catch (Exception e) {
										e.printStackTrace();
									}
                                   
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        
                      //学校school
                        JSONObject parseObjectschool = jsonObject.parseObject(jsonObject.parseObject(ner).get("school").toString());
                        if (parseObjectschool != null) {
                            for (Entry<String, Object> entrySet : parseObjectschool.entrySet()) {
                                try {
                                    String x = entrySet.getKey().trim();
                                   // String valuedata =  (String)entrySet.getValue();
                                   // JSONObject locparseObject = jsonObject.parseObject(valuedata);
                                    try {
                                    	JSONObject locparseObject = (JSONObject)entrySet.getValue();
                                    	//int value = Integer.parseInt(locparseObject.get("num").toString());通过词频的方式
                                    	int value = 1;//通过关键词匹配文章数的形式
                                        if (schooljson.containsKey(x)) {
                                        	schooljson.put(x, Integer.parseInt(schooljson.get(x).toString()) + value);
                                        } else {
                                        	schooljson.put(x, value);
                                        }
									} catch (Exception e) {
										e.printStackTrace();
									}
                                    
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        
                      //政府部门nto
                        JSONObject parseObjectnto = jsonObject.parseObject(jsonObject.parseObject(ner).get("nto").toString());
                        if (parseObjectnto != null) {
                            for (Entry<String, Object> entrySet : parseObjectnto.entrySet()) {
                                try {
                                    String x = entrySet.getKey().trim();
                                   // String valuedata =  (String)entrySet.getValue();
                                    
                                    try {
                                    	JSONObject locparseObject = (JSONObject)entrySet.getValue();
                                    	//int value = Integer.parseInt(locparseObject.get("num").toString());通过词频的方式
                                    	int value = 1;//通过关键词匹配文章数的形式
                                        if (ntojson.containsKey(x)) {
                                        	
                                        	//政府部门插入词典接口
                                        	ntojson.put(x, Integer.parseInt(ntojson.get(x).toString()) + value);
                                        } else {
                                        	ntojson.put(x, value);
                                        }
									} catch (Exception e) {
										e.printStackTrace();
									}
                                    
                                    
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        
                      //上市公司IPO
                        JSONObject parseObjectIPO = jsonObject.parseObject(jsonObject.parseObject(ner).get("IPO").toString());
                        if (parseObjectIPO != null) {
                            for (Entry<String, Object> entrySet : parseObjectIPO.entrySet()) {
                                try {
                                    String x = entrySet.getKey().trim();
                                    //String valuedata =  (String)entrySet.getValue();
                                    try {
                                    	JSONObject locparseObject = (JSONObject)entrySet.getValue();
                                    	//int value = Integer.parseInt(locparseObject.get("num").toString());通过词频的方式
                                    	int value = 1;//通过关键词匹配文章数的形式
                                        if (IPOjson.containsKey(x)) {
                                        	IPOjson.put(x, Integer.parseInt(IPOjson.get(x).toString()) + value);
                                        } else {
                                        	IPOjson.put(x, value);
                                        }
									} catch (Exception e) {
										// TODO: handle exception
									}
                                    
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        
                      //人物per
                        JSONObject parseObjectper = jsonObject.parseObject(jsonObject.parseObject(ner).get("per").toString());
                        if (parseObjectper != null) {
                            for (Entry<String, Object> entrySet : parseObjectper.entrySet()) {
                                try {
                                    String x = entrySet.getKey().trim();
                                    //String valuedata =  (String)entrySet.getValue();
                                    //JSONObject locparseObject = jsonObject.parseObject(valuedata);
                                    try {
                                    	JSONObject locparseObject = (JSONObject)entrySet.getValue();
                                    	//int value = Integer.parseInt(locparseObject.get("num").toString());通过词频的方式
                                    	int value = 1;//通过关键词匹配文章数的形式
                                        if (perjson.containsKey(x)) {
                                        	perjson.put(x, Integer.parseInt(perjson.get(x).toString()) + value);
                                        } else {
                                        	perjson.put(x, value);
                                        }
									} catch (Exception e) {
										e.printStackTrace();
									}
                                    
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                      //医院hospital
                        JSONObject parseObjecthospital = jsonObject.parseObject(jsonObject.parseObject(ner).get("hospital").toString());
                        if (parseObjecthospital != null) {
                            for (Entry<String, Object> entrySet : parseObjecthospital.entrySet()) {
                                try {
                                    String x = entrySet.getKey().trim();
                                    //String valuedata =  (String)entrySet.getValue();
                                    //JSONObject locparseObject = jsonObject.parseObject(valuedata);
                                    try {
                                    	JSONObject locparseObject = (JSONObject)entrySet.getValue();
                                    	//int value = Integer.parseInt(locparseObject.get("num").toString());通过词频的方式
                                    	int value = 1;//通过关键词匹配文章数的形式
                                        if (hospitaljson.containsKey(x)) {
                                        	hospitaljson.put(x, Integer.parseInt(hospitaljson.get(x).toString()) + value);
                                        } else {
                                        	hospitaljson.put(x, value);
                                        }
									} catch (Exception e) {
										e.printStackTrace();
									}
                                    
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        
                      //上市公司IPO_国内(SH上交所、SZ深交所、KCB科创板、XSB新三板)
                        parseObjectIPO = jsonObject.parseObject(jsonObject.parseObject(ner).get("IPO").toString());
                        if (parseObjectIPO != null) {
                            for (Entry<String, Object> entrySet : parseObjectIPO.entrySet()) {
                                try {
                                    String x = entrySet.getKey().trim();
                                    try {
                                    	JSONObject locparseObject = (JSONObject)entrySet.getValue();
                                    	//int value = Integer.parseInt(locparseObject.get("num").toString());通过词频的方式
                                    	int value = 1;//通过关键词匹配文章数的形式
                                        String chara = locparseObject.getString("chara");//标签
                                        if (IPO_chinajson.containsKey(x)) {
                                        	String[] split = chara.split("\\|");
                                        	for (int j = 0; j < split.length; j++) {
                                        		String chara_str = split[j];
                                        		if(chara_str.equals("SH")||chara_str.equals("SZ")||chara_str.equals("KCB")) {
                                        			IPO_chinajson.put(x, Integer.parseInt(IPO_chinajson.get(x).toString()) + value);
                                        			continue;
                                        		}
											}
                                        } else {
                                        	String[] split = chara.split("\\|");
                                        	for (int j = 0; j < split.length; j++) {
                                        		String chara_str = split[j];
                                        		if(chara_str.equals("SH")||chara_str.equals("SZ")||chara_str.equals("KCB")) {
                                        			IPO_chinajson.put(x, value);
                                        			continue;
                                        		}
											}
                                        }
									} catch (Exception e) {
										// TODO: handle exception
									}
                                    
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        
                        
                      //上市公司IPO_国外(NYSE---纽交所、NASDAQ---纳斯达克、HK---香港上市)
                        parseObjectIPO = jsonObject.parseObject(jsonObject.parseObject(ner).get("IPO").toString());
                        if (parseObjectIPO != null) {
                            for (Entry<String, Object> entrySet : parseObjectIPO.entrySet()) {
                                try {
                                    String x = entrySet.getKey().trim();
                                    try {
                                    	JSONObject locparseObject = (JSONObject)entrySet.getValue();
                                    	//int value = Integer.parseInt(locparseObject.get("num").toString());通过词频的方式
                                    	int value = 1;//通过关键词匹配文章数的形式
                                        String chara = locparseObject.getString("chara");//标签
                                        if (IPO_foreignjson.containsKey(x)) {
                                        	String[] split = chara.split("\\|");
                                        	for (int j = 0; j < split.length; j++) {
                                        		String chara_str = split[j];
                                        		if(chara_str.equals("NYSE")||chara_str.equals("NASDAQ")||chara_str.equals("HK")) {
                                        			IPO_foreignjson.put(x, Integer.parseInt(IPO_foreignjson.get(x).toString()) + value);
                                        			continue;
                                        		}
											}
                                        } else {
                                        	String[] split = chara.split("\\|");
                                        	for (int j = 0; j < split.length; j++) {
                                        		String chara_str = split[j];
                                        		if(chara_str.equals("NYSE")||chara_str.equals("NASDAQ")||chara_str.equals("HK")) {
                                        			IPO_foreignjson.put(x, value);
                                        			continue;
                                        		}
											}
                                        }
									} catch (Exception e) {
										// TODO: handle exception
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
        
        
        List<Map<String,Object>> keyWordListLoc =new ArrayList<Map<String,Object>>();
        for(String str:locjson.keySet()){
        	Map locmap = new HashMap<>();
        	locmap.put("keyword", str);
        	locmap.put("count", Integer.parseInt(locjson.get(str).toString()));
        	keyWordListLoc.add(locmap);
        }
        keyWordListLoc = keyWordListLoc.stream()
                .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                .collect(Collectors.toList());
        int loctotal = keyWordListLoc.stream().mapToInt(a -> (int) a.get("count")).sum();
        for (Map<String, Object> map3 : keyWordListLoc) {
        	int count = Integer.parseInt(map3.get("count").toString());
        	String calculatedRatioWithPercentSign = MyMathUtil.calculatedRatioWithPercentSign(count, loctotal);
        	map3.put("calculatedRatioWithPercentSign", calculatedRatioWithPercentSign);
        	map3.put("total", loctotal);
		}
        JSONArray array= JSONArray.parseArray(JSON.toJSONString(keyWordListLoc));
        resultjson.put("loc", array);
        
		
		List<Map<String,Object>> keyWordListBank =new ArrayList<Map<String,Object>>();
        for(String str:bankjson.keySet()){
        	Map bankmap = new HashMap<>();
        	bankmap.put("keyword", str);
        	bankmap.put("count", Integer.parseInt(bankjson.get(str).toString()));
        	keyWordListBank.add(bankmap);
        }
        keyWordListBank = keyWordListBank.stream()
                .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                .collect(Collectors.toList());
        int banktotal = keyWordListBank.stream().mapToInt(a -> (int) a.get("count")).sum();
        for (Map<String, Object> map3 : keyWordListBank) {
        	int count = Integer.parseInt(map3.get("count").toString());
        	String calculatedRatioWithPercentSign = MyMathUtil.calculatedRatioWithPercentSign(count, loctotal);
        	map3.put("calculatedRatioWithPercentSign", calculatedRatioWithPercentSign);
        	map3.put("total", loctotal);
		}
        
        JSONArray bankarray= JSONArray.parseArray(JSON.toJSONString(keyWordListBank));
        resultjson.put("bank", bankarray);
        
		
		List<Map<String,Object>> keyWordListOrg =new ArrayList<Map<String,Object>>();
        for(String str:orgjson.keySet()){
        	Map orgmap = new HashMap<>();
        	orgmap.put("keyword", str);
        	orgmap.put("count", Integer.parseInt(orgjson.get(str).toString()));
        	keyWordListOrg.add(orgmap);
        }
        keyWordListOrg = keyWordListOrg.stream()
                .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                .collect(Collectors.toList());
        int orgtotal = keyWordListOrg.stream().mapToInt(a -> (int) a.get("count")).sum();
        for (Map<String, Object> map3 : keyWordListOrg) {
        	int count = Integer.parseInt(map3.get("count").toString());
        	String calculatedRatioWithPercentSign = MyMathUtil.calculatedRatioWithPercentSign(count, orgtotal);
        	map3.put("calculatedRatioWithPercentSign", calculatedRatioWithPercentSign);
        	map3.put("total", orgtotal);
		}
        
        
        JSONArray orgarray= JSONArray.parseArray(JSON.toJSONString(keyWordListOrg));
        resultjson.put("org", orgarray);
		
		
		List<Map<String,Object>> keyWordListSchool =new ArrayList<Map<String,Object>>();
        for(String str:schooljson.keySet()){
        	Map schoolmap = new HashMap<>();
        	schoolmap.put("keyword", str);
        	schoolmap.put("count", Integer.parseInt(schooljson.get(str).toString()));
        	keyWordListSchool.add(schoolmap);
        }
        keyWordListSchool = keyWordListSchool.stream()
                .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                .collect(Collectors.toList());
        int schooltotal = keyWordListSchool.stream().mapToInt(a -> (int) a.get("count")).sum();
        for (Map<String, Object> map3 : keyWordListSchool) {
        	int count = Integer.parseInt(map3.get("count").toString());
        	String calculatedRatioWithPercentSign = MyMathUtil.calculatedRatioWithPercentSign(count, schooltotal);
        	map3.put("calculatedRatioWithPercentSign", calculatedRatioWithPercentSign);
        	map3.put("total", schooltotal);
		}
		
        JSONArray schoolarray= JSONArray.parseArray(JSON.toJSONString(keyWordListSchool));
        resultjson.put("school", schoolarray);
		
		
		List<Map<String,Object>> keyWordListNto =new ArrayList<Map<String,Object>>();
        for(String str:ntojson.keySet()){
        	Map ntomap = new HashMap<>();
        	ntomap.put("keyword", str);
        	ntomap.put("count", Integer.parseInt(ntojson.get(str).toString()));
        	keyWordListNto.add(ntomap);
        }
        keyWordListNto = keyWordListNto.stream()
                .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                .collect(Collectors.toList());
        int ntototal = keyWordListNto.stream().mapToInt(a -> (int) a.get("count")).sum();
        for (Map<String, Object> map3 : keyWordListNto) {
        	int count = Integer.parseInt(map3.get("count").toString());
        	String calculatedRatioWithPercentSign = MyMathUtil.calculatedRatioWithPercentSign(count, ntototal);
        	map3.put("calculatedRatioWithPercentSign", calculatedRatioWithPercentSign);
        	map3.put("total", ntototal);
		}
		
        JSONArray ntoarray= JSONArray.parseArray(JSON.toJSONString(keyWordListNto));
        resultjson.put("nto", ntoarray);
		
		
		
		
		List<Map<String,Object>> keyWordListIPO =new ArrayList<Map<String,Object>>();
        for(String str:IPOjson.keySet()){
        	Map ipomap = new HashMap<>();
        	ipomap.put("keyword", str);
        	ipomap.put("count", Integer.parseInt(IPOjson.get(str).toString()));
        	keyWordListIPO.add(ipomap);
        }
        keyWordListIPO = keyWordListIPO.stream()
                .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                .collect(Collectors.toList());
        int ipototal = keyWordListIPO.stream().mapToInt(a -> (int) a.get("count")).sum();
        for (Map<String, Object> map3 : keyWordListIPO) {
        	int count = Integer.parseInt(map3.get("count").toString());
        	String calculatedRatioWithPercentSign = MyMathUtil.calculatedRatioWithPercentSign(count, ipototal);
        	map3.put("calculatedRatioWithPercentSign", calculatedRatioWithPercentSign);
        	map3.put("total", ipototal);
		}
		
        JSONArray ipoarray= JSONArray.parseArray(JSON.toJSONString(keyWordListIPO));
        resultjson.put("ipo", ipoarray);
        
        
        
        //国内上市公司
		
		List<Map<String,Object>> keyWordListIPO_china =new ArrayList<Map<String,Object>>();
        for(String str:IPO_chinajson.keySet()){
        	Map ipomap = new HashMap<>();
        	ipomap.put("keyword", str);
        	ipomap.put("count", Integer.parseInt(IPOjson.get(str).toString()));
        	keyWordListIPO_china.add(ipomap);
        }
        keyWordListIPO_china = keyWordListIPO_china.stream()
                .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                .collect(Collectors.toList());
        int ipo_chinatotal = keyWordListIPO_china.stream().mapToInt(a -> (int) a.get("count")).sum();
        for (Map<String, Object> map3 : keyWordListIPO_china) {
        	int count = Integer.parseInt(map3.get("count").toString());
        	String calculatedRatioWithPercentSign = MyMathUtil.calculatedRatioWithPercentSign(count, ipo_chinatotal);
        	map3.put("calculatedRatioWithPercentSign", calculatedRatioWithPercentSign);
        	map3.put("total", ipo_chinatotal);
		}
		
        JSONArray ipo_china_array= JSONArray.parseArray(JSON.toJSONString(keyWordListIPO_china));
        resultjson.put("ipo_china", ipo_china_array);
        
        
        
        
      //国外上市公司
		
		List<Map<String,Object>> keyWordListIPO_foreign =new ArrayList<Map<String,Object>>();
        for(String str:IPO_foreignjson.keySet()){
        	Map ipomap = new HashMap<>();
        	ipomap.put("keyword", str);
        	ipomap.put("count", Integer.parseInt(IPOjson.get(str).toString()));
        	keyWordListIPO_foreign.add(ipomap);
        }
        keyWordListIPO_foreign = keyWordListIPO_foreign.stream()
                .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                .collect(Collectors.toList());
        int ipo_foreigntotal = keyWordListIPO_foreign.stream().mapToInt(a -> (int) a.get("count")).sum();
        for (Map<String, Object> map3 : keyWordListIPO_foreign) {
        	int count = Integer.parseInt(map3.get("count").toString());
        	String calculatedRatioWithPercentSign = MyMathUtil.calculatedRatioWithPercentSign(count, ipo_foreigntotal);
        	map3.put("calculatedRatioWithPercentSign", calculatedRatioWithPercentSign);
        	map3.put("total", ipo_foreigntotal);
		}
		
        JSONArray ipo_foreign_array= JSONArray.parseArray(JSON.toJSONString(keyWordListIPO_foreign));
        resultjson.put("ipo_foreign", ipo_foreign_array);
        
		
		
		
		
		List<Map<String,Object>> keyWordListPER =new ArrayList<Map<String,Object>>();
        for(String str:perjson.keySet()){
        	Map permap = new HashMap<>();
        	permap.put("keyword", str);
        	permap.put("count", Integer.parseInt(perjson.get(str).toString()));
        	keyWordListPER.add(permap);
        }
        keyWordListPER = keyWordListPER.stream()
                .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                .collect(Collectors.toList());
        int pertotal = keyWordListPER.stream().mapToInt(a -> (int) a.get("count")).sum();
        for (Map<String, Object> map3 : keyWordListPER) {
        	int count = Integer.parseInt(map3.get("count").toString());
        	String calculatedRatioWithPercentSign = MyMathUtil.calculatedRatioWithPercentSign(count, pertotal);
        	map3.put("calculatedRatioWithPercentSign", calculatedRatioWithPercentSign);
        	map3.put("total", pertotal);
		}
		
        JSONArray perarray= JSONArray.parseArray(JSON.toJSONString(keyWordListPER));
        resultjson.put("per", perarray);
        
		List<Map<String,Object>> keyWordListHospital =new ArrayList<Map<String,Object>>();
        for(String str:hospitaljson.keySet()){
        	Map hospitalmap = new HashMap<>();
        	hospitalmap.put("keyword", str);
        	hospitalmap.put("count", Integer.parseInt(hospitaljson.get(str).toString()));
        	keyWordListHospital.add(hospitalmap);
        }
        keyWordListHospital = keyWordListHospital.stream()
                .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                .collect(Collectors.toList());
        int hospitaltotal = keyWordListHospital.stream().mapToInt(a -> (int) a.get("count")).sum();
        for (Map<String, Object> map3 : keyWordListHospital) {
        	int count = Integer.parseInt(map3.get("count").toString());
        	String calculatedRatioWithPercentSign = MyMathUtil.calculatedRatioWithPercentSign(count, hospitaltotal);
        	map3.put("calculatedRatioWithPercentSign", calculatedRatioWithPercentSign);
        	map3.put("total", hospitaltotal);
		}
        JSONArray hospitalarray= JSONArray.parseArray(JSON.toJSONString(keyWordListHospital));
        resultjson.put("hospital", hospitalarray);
        return resultjson.toJSONString();
	}
	
	

	public boolean isLockWordsMonster(String words) {
		return Pattern.matches("(分别为|一季度|高质量|本公司|进出口|同花顺|分公司|进小区|以下简称|新势力|互联网|创始人|实际上|经济观察网|大股东|批准后|一季报|二季报|三季报|四季报|不存在|注册资本|百分点|经营范围|新浪网|解决方案|情况下|新时代|科技有限公司|净利润|董事长|上市公司|集团有限公司|分公司|股东大会|上交所|深交所|以下简称|消费者|委员会|竞争力|关联交易|监事会|年比年|上海证券交易所|副总裁|投资者|创业板|产业链|现代化|营业额|科创板|国务院|中共中央|证监会|注册资本|国家社会|一些人|中国共产党|习近平|总书记|新时代|中国共产党|重要讲话|上年同期|营业收入|第一季度|同期增长|发达国家|管理部门|人民币|营业时间|产业发展|中工网|查看更多|负责人|新华社|基础上|社会主义|董事会|范围内|募集资金|办公室|通讯员|影响力|毛泽东|办实事|副书记|国内外|的通知|标准化|影响力|范围内|可能会|孩子们|股份有限公司|国内外|有关部门|工作人员|高水平|安全性|管理局|有效性|子公司|副主任|很多人|新浪财经|毛利率|总经理|浏览器|第三方|全资子公司|增值税|农产品|收益率|全方位|总股本|可以通过|一段时间|第一时间|一段时间|主营业务|当地时间|工程有限公司|为公司|二季度|三季度|四季度|管理有限公司|白马股|按规定|家门口|稳定性|总投资|母公司|每日经济新闻|副总经理|成交额|获得感|综合性|重仓股|于一体|专业化|最重要|年度报告|法定代表人|最重要|基本面|类产品|联系人|很容易|阶段性|回购股份|生产成本|真实性|长时间|注意事项|党组织|业内人士|领导小组|历史上|短期内|基本情况|有没有|营业执照|发生率|产品成本|便利化|针对性|业绩增长|集中度|感受到|世界上|经营者|非公开发行|公开发行|高标准|业绩增长|实质性|多中心|招股书|道副总经理|达号|第一季度|改革委|国家级|高质量|财政部|改革委|基础设施|中国中免|消费者|进一步|项目建设|次会议|当事人|二选一|轮融资|情况下|地方政府|经销商|事务所|本公司|嫌疑人|不存在|改革委|都市圈|监事会|有条件|智能化|产业化|中小企业|委员会|责任公司|财务总监|说明会|民营企业|开发商|安全生产|主办方|主办单位|分中心|各部门|净买入|美联储|成交量|换手率|辨识度|工作会议|分公司|首席代表|董事局|国际化|市场化|小标题|中药材|性价比|年金计划|发行人|混合型证券投资基金|投资基金|证券投资).*", words);
	}
//	public static void main(String[] args) {
//		String dataNer = dataCategory("(南京银行 OR 交通银行 OR 广发银行股份有限公司 OR 工商银行 OR 中国银行 OR 建设银行 OR 光大银行 OR 华夏银行 OR 兴业银行 OR 民生银行 OR 平安银行 OR 广发银行 OR 浦发银行 OR 中信银行 OR 北京银行 OR 中国农业银行 OR 中国邮政储蓄银行 OR 微众银行 OR 上海银行 OR 东莞农商行 OR 广州农商行 OR 重庆农商行 OR 常熟农商行 OR 武汉农商行 OR 无锡农商行 OR 宜人贷 OR 宜信 OR 钱包金服 OR 秦苍 OR 小米金融 OR 小赢普惠 OR 快钱 OR 360金融 OR 美团金融 OR 中邮消费金融 OR 钱牛牛 OR 人人贷 OR 汇中财富 OR 达飞金融 OR 吉利汽车 OR 广发信用卡 OR 度小满 OR 新心金融 OR 平安普惠 OR 京东数科 OR 资产雷达 OR 银客 OR 友信 OR 长安汽车金融 OR 招行个贷 OR 平安易贷 OR 小雨点信用贷款 OR 小雨点金融 OR 哈尔滨银行 OR 广州银行)", "", "2021-04-10 17:03:01", "2021-04-20 17:50:01",2);
//		System.out.println("dataNer:"+dataNer);
//	}
	public String dataCategory(String highKeyword, String stopword, String times, String timee, Integer projectType) {
		
		List<Map<String, Object>> list = new ArrayList<>();
        try {
            int total = 0;
            String articleCategoryJSon = getArticleCategoryJSon(highKeyword,stopword,times,timee,projectType);
            JSONArray jsonArray = JSONObject.parseObject(articleCategoryJSon).getJSONObject("hits").getJSONArray("hits");
            Map<String,Object> map = new HashMap<String,Object>();
            for (Object object : jsonArray) {
            	JSONObject parseObject = JSONObject.parseObject(object.toString());
            	JSONArray jsonArray2 = parseObject.getJSONObject("_source").getJSONArray("article_category");
            	if(jsonArray2!=null) {
            		for (int i=0;i<jsonArray2.size()&&i<1;i++) {
                		//total +=1;
                		JSONObject parseObject2 = JSONObject.parseObject(jsonArray2.getString(i).toString());
                		//类型
                		String type = parseObject2.get("type").toString();
                		//分数
                		String score = parseObject2.get("score").toString();
                		if(map.containsKey(type)) {
                			map.put(type, Integer.parseInt(map.get(type).toString())+1);
                		}else {
                			map.put(type, 1);
                		}
    				}
            	}
            	
			}
            
            for(String key : map.keySet()){
            	total+=Integer.parseInt(map.get(key).toString());
            	
            }
            
            for(String key : map.keySet()){
            	Map<String,Object> resultmap = new HashMap<String,Object>();
            	resultmap.put("keyword", key);
            	int count = Integer.parseInt(map.get(key).toString());
            	double rate = MyMathUtil.calculatedRatioNoPercentSign(count, total);
            	resultmap.put("rate", rate);
            	resultmap.put("count", count);
            	list.add(resultmap);
            }
            list = list.stream().sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                    .collect(Collectors.toList());
            return JSON.toJSONString(list);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
		
	}
	
	 /**
     * 分类接口
     */
    public String getArticleCategoryJSon(String highKeyword, String stopword,String time, String timee, 
                             Integer projectType) {
    	String result = "";
        try {
            String param = "times=" + time + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword=" + stopword
                    + "&origintype=0&emotionalIndex=" + "&projecttype="
                    + projectType;
            try {
            	
            	
            	//String es_search_url1 = "http://192.168.71.81:8123";
            	//result = sendPost(es_search_url1 + ReportConstant.es_api_category_list, param);
            	
               result = sendPost(es_search_url + ReportConstant.es_api_category_list, param);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //政策法规
	public String dataPolicy(String keyword, String stopword, String times, String timee, Integer projectType) {

 String abc = "http://dx2.stonedt.com:7121";
        es_search_url = abc;
		  String url = es_search_url + ReportConstant.es_api_policy;
		//String url = "http://192.168.71.81:8123" + ReportConstant.es_api_policy;
		  
		  String params = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                  + "&searchType=1&classify=&emotionalIndex=1,2,3" + "&projecttype=" + projectType+"&policylableflag=1";
          String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
          
          JSONObject policyjson = new JSONObject();
          
          
          if (StringUtils.isNotBlank(sendPostEsSearch)) {
              JSONObject parseObject = JSON.parseObject(sendPostEsSearch);
              JSONArray hitsArray = parseObject.getJSONObject("hits").getJSONArray("hits");
              for (int i = 0; i < hitsArray.size(); i++) {
                      JSONObject jsonObject = hitsArray.getJSONObject(i).getJSONObject("_source");
                      if (!jsonObject.containsKey("policylable"))
                          continue;
                      String policylable = jsonObject.get("policylable").toString();
                      JSONArray parseArray = JSONArray.parseArray(policylable);
                      for (Object object : parseArray) {
                    	  JSONObject parseObject2 = jsonObject.parseObject(object.toString());
                    	  String policy = parseObject2.get("policy").toString();
                    	  
                    	  if(policyjson.containsKey(policy)) {
                    		  policyjson.put(policy, Integer.parseInt(policyjson.get(policy).toString())+1);
                    	  }else {

                              boolean matches = policy.matches(".*[~!@#$%^&*()_+|<>,，.?/:;'\\[\\]{}\"]+.*");

                              if (!matches){
                                  policyjson.put(policy, 1);
                              }

                    	  }
                    	  
                    	  
                    	  
						
					}
                      
                      
                      
                      
                      
              }
          }
          
          JSONObject resultjson = new JSONObject();
          
          List<Map<String,Object>> keyWordListPolicy =new ArrayList<Map<String,Object>>();
          for(String str:policyjson.keySet()){
          	Map ntomap = new HashMap<>();




                  ntomap.put("keyword", str);
                  ntomap.put("count", Integer.parseInt(policyjson.get(str).toString()));
                  keyWordListPolicy.add(ntomap);





          }
          keyWordListPolicy = keyWordListPolicy.stream()
                  .sorted((map1, map2) -> (int) map2.get("count") - (int) map1.get("count")).limit(10)
                  .collect(Collectors.toList());
          int ntototal = keyWordListPolicy.stream().mapToInt(a -> (int) a.get("count")).sum();
          for (Map<String, Object> map3 : keyWordListPolicy) {
          	int count = Integer.parseInt(map3.get("count").toString());
          	String calculatedRatioWithPercentSign = MyMathUtil.calculatedRatioWithPercentSign(count, ntototal);
          	map3.put("calculatedRatioWithPercentSign", calculatedRatioWithPercentSign);
          	map3.put("total", ntototal);
  		}
  		
          JSONArray ntoarray= JSONArray.parseArray(JSON.toJSONString(keyWordListPolicy));
          resultjson.put("policy", ntoarray);
		return resultjson.toJSONString();
	}










    //行业分布统计
    public String industrialDistribution(String keyword, String stopword, String times, String timee, Integer projectType) {

//       String abc = "http://dx2.stonedt.com:7121";
//        es_search_url = abc;
        String url = es_search_url + ReportConstant.es_api_search_industry_list;
        //String url = "http://192.168.71.81:8123" + ReportConstant.es_api_policy;

        String params = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                + "&searchType=1&classify=&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
        String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);



        String industrialDistribution = "";


        if (StringUtils.isNotBlank(sendPostEsSearch)) {

            JSONObject object = JSON.parseObject(sendPostEsSearch);
            JSONObject aggregations = object.getJSONObject("aggregations");
            JSONObject group_by_tags = aggregations.getJSONObject("group_by_tags");
            JSONArray buckets = group_by_tags.getJSONArray("buckets");

            int qt = -1;
            for (int i = 0; i < buckets.size(); i++) {
                JSONObject bucket = (JSONObject) buckets.get(i);
                String key = bucket.getString("key");
                if ("其它".equals(key)){
                    qt = i;

                }
            }

            if(qt != -1){
                buckets.remove(qt);
            }


            industrialDistribution = buckets.toString();

            industrialDistribution = industrialDistribution.replaceAll("key" , "name");
            industrialDistribution = industrialDistribution.replaceAll("doc_count" , "value");
            
            List<Map> list = JSONObject.parseArray(industrialDistribution, Map.class);
            list = list.stream()
                    .sorted((map1, map2) -> (int) map2.get("value") - (int) map1.get("value")).limit(10)
                    .collect(Collectors.toList());
            industrialDistribution = JSON.toJSONString(list);
        }



        return industrialDistribution;
    }





    //事件统计
    public String eventStudy(String keyword, String stopword, String times, String timee, Integer projectType) {

       /* String abc = "http://dx2.stonedt.com:7121";
        es_search_url = abc;*/
        String url = es_search_url + ReportConstant.es_api_search_event_list;
        //String url = "http://192.168.71.81:8123" + ReportConstant.es_api_policy;

        String params = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                + "&searchType=1&classify=&emotionalIndex=1,2,3" + "&projecttype=" + projectType;
        String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);

        String eventStudy = "";


        if (StringUtils.isNotBlank(sendPostEsSearch)) {

            JSONObject object = JSON.parseObject(sendPostEsSearch);

            JSONObject aggregations = object.getJSONObject("aggregations");

            JSONObject group_by_tags = aggregations.getJSONObject("group_by_tags");

            JSONArray buckets = group_by_tags.getJSONArray("buckets");

            int qt = -1;
            int wsxs = -1;
            for (int i = 0; i < buckets.size(); i++) {
                JSONObject bucket = (JSONObject) buckets.get(i);
                String key = bucket.getString("key");
                //如果事件名是其他就不存
                if("其它".equals(key)){
                    qt = i;
                    /*buckets.remove(i);*/
                }
                if("网事小说".equals(key)){
                    wsxs = i;
                    /*buckets.remove(i);*/
                }

            }

            if (qt != -1){
                buckets.remove(qt);
            }

            if(wsxs != -1){
                buckets.remove(wsxs);
            }



            eventStudy = buckets.toString();

            eventStudy = eventStudy.replaceAll("key" , "name");

            eventStudy = eventStudy.replaceAll("doc_count" , "value");
            List<Map> list = JSONObject.parseArray(eventStudy, Map.class);
            list = list.stream()
                    .sorted((map1, map2) -> (int) map2.get("value") - (int) map1.get("value")).limit(8)
                    .collect(Collectors.toList());
            eventStudy = JSON.toJSONString(list);
        }



        return eventStudy;
    }


	
//	public static void main(String[] args) {
//	String times = "2021-05-19 00:00:00";
//	String timee = "2021-05-24 00:00:00";
//	//String highKeyword = "(诚迈科技 OR 第九公司 OR 微软 OR 腾讯控股 OR 宁德时代 OR 三安电光 OR 工商银行 OR 怪兽充电 OR 沙钢 OR 中国电信 OR 中国移动 OR 建设银行 OR 联合丽格 OR 苏州贝康医疗器械有限公司 OR 北京云鸟科技有限公司 OR 北京易泊安科技有限公司 OR 北京伍方兴业科技有限公司)";
//	//String highKeyword = "(北京 OR 南京 OR 上海)";
//	String highKeyword = "";
//	String stopword = "";
//	String jsonstr = "{\"loc\":[{\"calculatedRatioWithPercentSign\":\"14.54%\",\"total\":399,\"count\":58,\"keyword\":\"广州市\"},{\"calculatedRatioWithPercentSign\":\"11.53%\",\"total\":399,\"count\":46,\"keyword\":\"广东省\"},{\"calculatedRatioWithPercentSign\":\"11.03%\",\"total\":399,\"count\":44,\"keyword\":\"中华人民共和国\"},{\"calculatedRatioWithPercentSign\":\"10.03%\",\"total\":399,\"count\":40,\"keyword\":\"东南亚\"},{\"calculatedRatioWithPercentSign\":\"9.77%\",\"total\":399,\"count\":39,\"keyword\":\"中国同联合国\"},{\"calculatedRatioWithPercentSign\":\"9.52%\",\"total\":399,\"count\":38,\"keyword\":\"荔湾区\"},{\"calculatedRatioWithPercentSign\":\"9.02%\",\"total\":399,\"count\":36,\"keyword\":\"台湾地区\"},{\"calculatedRatioWithPercentSign\":\"9.02%\",\"total\":399,\"count\":36,\"keyword\":\"评论区\"},{\"calculatedRatioWithPercentSign\":\"7.77%\",\"total\":399,\"count\":31,\"keyword\":\"山东省\"},{\"calculatedRatioWithPercentSign\":\"7.77%\",\"total\":399,\"count\":31,\"keyword\":\"中国区\"}],\"bank\":[{\"calculatedRatioWithPercentSign\":\"5.26%\",\"total\":399,\"count\":21,\"keyword\":\"中国银行\"},{\"calculatedRatioWithPercentSign\":\"4.76%\",\"total\":399,\"count\":19,\"keyword\":\"中国人民银行\"},{\"calculatedRatioWithPercentSign\":\"3.76%\",\"total\":399,\"count\":15,\"keyword\":\"渤海银行\"},{\"calculatedRatioWithPercentSign\":\"3.51%\",\"total\":399,\"count\":14,\"keyword\":\"华夏银行\"},{\"calculatedRatioWithPercentSign\":\"3.26%\",\"total\":399,\"count\":13,\"keyword\":\"招商银行\"},{\"calculatedRatioWithPercentSign\":\"2.51%\",\"total\":399,\"count\":10,\"keyword\":\"建设银行\"},{\"calculatedRatioWithPercentSign\":\"2.26%\",\"total\":399,\"count\":9,\"keyword\":\"投资银行\"},{\"calculatedRatioWithPercentSign\":\"2.26%\",\"total\":399,\"count\":9,\"keyword\":\"兴业银行\"},{\"calculatedRatioWithPercentSign\":\"1.75%\",\"total\":399,\"count\":7,\"keyword\":\"工商银行\"},{\"calculatedRatioWithPercentSign\":\"1.5%\",\"total\":399,\"count\":6,\"keyword\":\"农业银行\"}],\"org\":[{\"calculatedRatioWithPercentSign\":\"15.22%\",\"total\":92,\"count\":14,\"keyword\":\"华夏银行股份有限公司\"},{\"calculatedRatioWithPercentSign\":\"15.22%\",\"total\":92,\"count\":14,\"keyword\":\"招商银行股份有限公司\"},{\"calculatedRatioWithPercentSign\":\"15.22%\",\"total\":92,\"count\":14,\"keyword\":\"中国银行股份有限公司\"},{\"calculatedRatioWithPercentSign\":\"10.87%\",\"total\":92,\"count\":10,\"keyword\":\"华夏银行渤海银行股份有限公司\"},{\"calculatedRatioWithPercentSign\":\"9.78%\",\"total\":92,\"count\":9,\"keyword\":\"招商银行）东亚银行（中国）有限公司\"},{\"calculatedRatioWithPercentSign\":\"7.61%\",\"total\":92,\"count\":7,\"keyword\":\"惠而浦(中国)股份有限公司\"},{\"calculatedRatioWithPercentSign\":\"6.52%\",\"total\":92,\"count\":6,\"keyword\":\"广厦传媒有限公司\"},{\"calculatedRatioWithPercentSign\":\"6.52%\",\"total\":92,\"count\":6,\"keyword\":\"国家电力投资集团有限公司\"},{\"calculatedRatioWithPercentSign\":\"6.52%\",\"total\":92,\"count\":6,\"keyword\":\"深圳极视角科技有限公司\"},{\"calculatedRatioWithPercentSign\":\"6.52%\",\"total\":92,\"count\":6,\"keyword\":\"容南大置业有限公司\"}],\"school\":[{\"calculatedRatioWithPercentSign\":\"17.91%\",\"total\":201,\"count\":36,\"keyword\":\"中小学\"},{\"calculatedRatioWithPercentSign\":\"13.93%\",\"total\":201,\"count\":28,\"keyword\":\"清华大学\"},{\"calculatedRatioWithPercentSign\":\"12.94%\",\"total\":201,\"count\":26,\"keyword\":\"中国科学院\"},{\"calculatedRatioWithPercentSign\":\"10.95%\",\"total\":201,\"count\":22,\"keyword\":\"中国人民大学\"},{\"calculatedRatioWithPercentSign\":\"8.96%\",\"total\":201,\"count\":18,\"keyword\":\"步兵学院\"},{\"calculatedRatioWithPercentSign\":\"8.46%\",\"total\":201,\"count\":17,\"keyword\":\"北京大学\"},{\"calculatedRatioWithPercentSign\":\"7.46%\",\"total\":201,\"count\":15,\"keyword\":\"中小学校\"},{\"calculatedRatioWithPercentSign\":\"6.47%\",\"total\":201,\"count\":13,\"keyword\":\"民办学校\"},{\"calculatedRatioWithPercentSign\":\"6.47%\",\"total\":201,\"count\":13,\"keyword\":\"义务教育学校\"},{\"calculatedRatioWithPercentSign\":\"6.47%\",\"total\":201,\"count\":13,\"keyword\":\"上小学\"}],\"nto\":[{\"calculatedRatioWithPercentSign\":\"15.02%\",\"total\":426,\"count\":64,\"keyword\":\"证监会\"},{\"calculatedRatioWithPercentSign\":\"13.38%\",\"total\":426,\"count\":57,\"keyword\":\"国务院\"},{\"calculatedRatioWithPercentSign\":\"10.33%\",\"total\":426,\"count\":44,\"keyword\":\"中国政府\"},{\"calculatedRatioWithPercentSign\":\"10.33%\",\"total\":426,\"count\":44,\"keyword\":\"欧盟委员会\"},{\"calculatedRatioWithPercentSign\":\"9.86%\",\"total\":426,\"count\":42,\"keyword\":\"教育部\"},{\"calculatedRatioWithPercentSign\":\"9.86%\",\"total\":426,\"count\":42,\"keyword\":\"银保监会\"},{\"calculatedRatioWithPercentSign\":\"9.15%\",\"total\":426,\"count\":39,\"keyword\":\"中共中央政治局\"},{\"calculatedRatioWithPercentSign\":\"8.45%\",\"total\":426,\"count\":36,\"keyword\":\"国家统计局\"},{\"calculatedRatioWithPercentSign\":\"7.04%\",\"total\":426,\"count\":30,\"keyword\":\"外交部\"},{\"calculatedRatioWithPercentSign\":\"6.57%\",\"total\":426,\"count\":28,\"keyword\":\"中央政治局\"}],\"ipo\":[{\"calculatedRatioWithPercentSign\":\"14.49%\",\"total\":414,\"count\":60,\"keyword\":\"新媒体\"},{\"calculatedRatioWithPercentSign\":\"12.56%\",\"total\":414,\"count\":52,\"keyword\":\"时代中国控股\"},{\"calculatedRatioWithPercentSign\":\"11.35%\",\"total\":414,\"count\":47,\"keyword\":\"人民网\"},{\"calculatedRatioWithPercentSign\":\"11.11%\",\"total\":414,\"count\":46,\"keyword\":\"只有一个\"},{\"calculatedRatioWithPercentSign\":\"11.11%\",\"total\":414,\"count\":46,\"keyword\":\"新能源汽车\"},{\"calculatedRatioWithPercentSign\":\"10.39%\",\"total\":414,\"count\":43,\"keyword\":\"北京时间\"},{\"calculatedRatioWithPercentSign\":\"8.21%\",\"total\":414,\"count\":34,\"keyword\":\"同花顺\"},{\"calculatedRatioWithPercentSign\":\"7.73%\",\"total\":414,\"count\":32,\"keyword\":\"嘉美包装\"},{\"calculatedRatioWithPercentSign\":\"6.76%\",\"total\":414,\"count\":28,\"keyword\":\"长安汽车\"},{\"calculatedRatioWithPercentSign\":\"6.28%\",\"total\":414,\"count\":26,\"keyword\":\"视觉中国\"}],\"ipo_china\":[{\"calculatedRatioWithPercentSign\":\"19.67%\",\"total\":239,\"count\":47,\"keyword\":\"人民网\"},{\"calculatedRatioWithPercentSign\":\"14.23%\",\"total\":239,\"count\":34,\"keyword\":\"同花顺\"},{\"calculatedRatioWithPercentSign\":\"13.39%\",\"total\":239,\"count\":32,\"keyword\":\"嘉美包装\"},{\"calculatedRatioWithPercentSign\":\"11.72%\",\"total\":239,\"count\":28,\"keyword\":\"长安汽车\"},{\"calculatedRatioWithPercentSign\":\"10.04%\",\"total\":239,\"count\":24,\"keyword\":\"宁德时代\"},{\"calculatedRatioWithPercentSign\":\"7.11%\",\"total\":239,\"count\":17,\"keyword\":\"新华网\"},{\"calculatedRatioWithPercentSign\":\"6.28%\",\"total\":239,\"count\":15,\"keyword\":\"美的集团\"},{\"calculatedRatioWithPercentSign\":\"5.86%\",\"total\":239,\"count\":14,\"keyword\":\"浙商证券\"},{\"calculatedRatioWithPercentSign\":\"5.86%\",\"total\":239,\"count\":14,\"keyword\":\"华夏银行\"},{\"calculatedRatioWithPercentSign\":\"5.86%\",\"total\":239,\"count\":14,\"keyword\":\"新产业\"}],\"ipo_foreign\":[{\"calculatedRatioWithPercentSign\":\"23.85%\",\"total\":218,\"count\":52,\"keyword\":\"时代中国控股\"},{\"calculatedRatioWithPercentSign\":\"11.93%\",\"total\":218,\"count\":26,\"keyword\":\"中国移动\"},{\"calculatedRatioWithPercentSign\":\"9.63%\",\"total\":218,\"count\":21,\"keyword\":\"碧桂园\"},{\"calculatedRatioWithPercentSign\":\"9.17%\",\"total\":218,\"count\":20,\"keyword\":\"建设银行\"},{\"calculatedRatioWithPercentSign\":\"8.26%\",\"total\":218,\"count\":18,\"keyword\":\"东亚银行\"},{\"calculatedRatioWithPercentSign\":\"8.26%\",\"total\":218,\"count\":18,\"keyword\":\"拼多多\"},{\"calculatedRatioWithPercentSign\":\"7.8%\",\"total\":218,\"count\":17,\"keyword\":\"中信证券\"},{\"calculatedRatioWithPercentSign\":\"7.8%\",\"total\":218,\"count\":17,\"keyword\":\"台积电\"},{\"calculatedRatioWithPercentSign\":\"6.88%\",\"total\":218,\"count\":15,\"keyword\":\"渤海银行\"},{\"calculatedRatioWithPercentSign\":\"6.42%\",\"total\":218,\"count\":14,\"keyword\":\"招商银行\"}],\"per\":[{\"calculatedRatioWithPercentSign\":\"52.63%\",\"total\":475,\"count\":250,\"keyword\":\"习近平\"},{\"calculatedRatioWithPercentSign\":\"9.47%\",\"total\":475,\"count\":45,\"keyword\":\"赵立坚\"},{\"calculatedRatioWithPercentSign\":\"8.21%\",\"total\":475,\"count\":39,\"keyword\":\"毛泽东\"},{\"calculatedRatioWithPercentSign\":\"5.68%\",\"total\":475,\"count\":27,\"keyword\":\"邓小平\"},{\"calculatedRatioWithPercentSign\":\"5.05%\",\"total\":475,\"count\":24,\"keyword\":\"李世辉\"},{\"calculatedRatioWithPercentSign\":\"4%\",\"total\":475,\"count\":19,\"keyword\":\"王女士\"},{\"calculatedRatioWithPercentSign\":\"4%\",\"total\":475,\"count\":19,\"keyword\":\"李政道\"},{\"calculatedRatioWithPercentSign\":\"3.79%\",\"total\":475,\"count\":18,\"keyword\":\"王沪宁\"},{\"calculatedRatioWithPercentSign\":\"3.79%\",\"total\":475,\"count\":18,\"keyword\":\"郭某某\"},{\"calculatedRatioWithPercentSign\":\"3.37%\",\"total\":475,\"count\":16,\"keyword\":\"李克强\"}],\"hospital\":[{\"calculatedRatioWithPercentSign\":\"24.43%\",\"total\":176,\"count\":43,\"keyword\":\"荔湾区中心医院\"},{\"calculatedRatioWithPercentSign\":\"18.18%\",\"total\":176,\"count\":32,\"keyword\":\"汇鑫阁东座荔湾中心医院\"},{\"calculatedRatioWithPercentSign\":\"17.61%\",\"total\":176,\"count\":31,\"keyword\":\"广州市第八人民医院\"},{\"calculatedRatioWithPercentSign\":\"14.77%\",\"total\":176,\"count\":26,\"keyword\":\"公立医院\"},{\"calculatedRatioWithPercentSign\":\"5.68%\",\"total\":176,\"count\":10,\"keyword\":\"营口市第三人民医院\"},{\"calculatedRatioWithPercentSign\":\"5.68%\",\"total\":176,\"count\":10,\"keyword\":\"辽宁省集中救治中心大连中心\"},{\"calculatedRatioWithPercentSign\":\"5.11%\",\"total\":176,\"count\":9,\"keyword\":\"定点医院\"},{\"calculatedRatioWithPercentSign\":\"2.84%\",\"total\":176,\"count\":5,\"keyword\":\"清华大学美院\"},{\"calculatedRatioWithPercentSign\":\"2.84%\",\"total\":176,\"count\":5,\"keyword\":\"上海崇明\"},{\"calculatedRatioWithPercentSign\":\"2.84%\",\"total\":176,\"count\":5,\"keyword\":\"专科医院\"}],\"policy\":[{\"calculatedRatioWithPercentSign\":\"14.39%\",\"total\":132,\"count\":19,\"keyword\":\"关于进一步减轻义务教育阶段学生作业负担和校外培训负担的意见\"},{\"calculatedRatioWithPercentSign\":\"14.39%\",\"total\":132,\"count\":19,\"keyword\":\"关于在城乡建设中加强历史文化保护传承的若干意见\"},{\"calculatedRatioWithPercentSign\":\"14.39%\",\"total\":132,\"count\":19,\"keyword\":\"关于完善科技成果评价机制的指导意见\"},{\"calculatedRatioWithPercentSign\":\"14.39%\",\"total\":132,\"count\":19,\"keyword\":\"关于深化生态保护补偿制度改革的意见\"},{\"calculatedRatioWithPercentSign\":\"12.88%\",\"total\":132,\"count\":17,\"keyword\":\"广州市商务局关于做好住宿餐饮行业新冠肺炎疫情防控有关工作的通知\"},{\"calculatedRatioWithPercentSign\":\"8.33%\",\"total\":132,\"count\":11,\"keyword\":\"保险公司城乡居民大病保险业务管理办法\"},{\"calculatedRatioWithPercentSign\":\"6.82%\",\"total\":132,\"count\":9,\"keyword\":\"关于延续实施部分减负稳岗扩就业政策措施的通知\"},{\"calculatedRatioWithPercentSign\":\"5.3%\",\"total\":132,\"count\":7,\"keyword\":\"关于新时代推动中部地区高质量发展的指导意见\"},{\"calculatedRatioWithPercentSign\":\"4.55%\",\"total\":132,\"count\":6,\"keyword\":\"关于深入推进文化金融合作的意见\"},{\"calculatedRatioWithPercentSign\":\"4.55%\",\"total\":132,\"count\":6,\"keyword\":\"关于防范代币发行融资风险的公告\"}]}";
//	JSONObject parseObject = JSONObject.parseObject(jsonstr);
//	
//	JSONObject dataNerArticleData = dataNerArticleData(highKeyword,stopword,times,timee,2,parseObject);
//	System.out.println(dataNerArticleData);
//}	
	
	

	public JSONObject dataNerArticleData(String highKeyword, String stopword, String times, String timee,
			Integer projectType, JSONObject nersjon) {
		
		
		JSONObject resultjson = new JSONObject();
		
		try {
            Iterator iter = nersjon.entrySet().iterator();
            String message = "";
            
            List<String> list = new ArrayList<String>();
            
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = entry.getKey().toString();
                
                JSONArray parseArray = JSONArray.parseArray(entry.getValue().toString());
                JSONArray resultArray = new JSONArray();
                for (Object object : parseArray) {
            	   JSONObject parseObject = JSONObject.parseObject(object.toString());
            	   String datakeyword = parseObject.get("keyword").toString();
            	   JSONObject jSonData = getJSonData(datakeyword,highKeyword,times,timee,stopword,projectType,list);
            	   if(!jSonData.isEmpty()) {
            		   if(jSonData.getString("titlekeyword")!=null&&!jSonData.getString("titlekeyword").equals("")) {
                		   list.add(jSonData.getString("titlekeyword"));
                	   }
                	   parseObject.put("data", jSonData);
                	   resultArray.add(parseObject);
            	   }
			}
                resultjson.put(key, resultArray);
                
                
            }
        } catch (Exception e) {
        	e.printStackTrace();
		}
		
		return resultjson;
	}
	
	/**
	 * 返回实体数据
	 * @param searchkeyword
	 * @param highKeyword
	 * @param time
	 * @param timee
	 * @param stopword
	 * @param projectType
	 * @return
	 */
    public JSONObject getJSonData(String searchkeyword, String highKeyword, String time, String timee, String stopword,
                             Integer projectType,List<String> list) {
        String url = es_search_url + ReportConstant.searchlistnottitlekeyword;
    	
    	String str = list.stream().collect(Collectors.joining(","));
    	
    	//String url = "http://192.168.71.81:8123" + "/yqsearch/searchlistnottitlekeyword";
    	JSONObject resultjson = new JSONObject();
            try {
            	 String param = "times=" + time + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword=" + stopword
                         + "&searchkeyword=" + searchkeyword + "&searchType=2&size=1&origintype=0&emotionalIndex=" + "&projecttype="
                         + projectType+"&forbiddentitlekeyword="+str;
                String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, param);
                JSONObject parseObject = JSONObject.parseObject(sendPostEsSearch);
                JSONArray jsonArray = parseObject.getJSONArray("data");
                if(jsonArray.size()>0) {
                for (Object object : jsonArray) {
                	JSONObject articleobject = JSONObject.parseObject(object.toString());
                	JSONObject jsonObject = articleobject.getJSONObject("_source");
                	resultjson.put("article_public_id", jsonObject.get("article_public_id"));
                	
                	String title = jsonObject.get("title").toString();
                	if (title.contains("_http://") || title.contains("_https://")) {
                        title = title.substring(0, title.indexOf("_"));
                        resultjson.put("title", title);
                    }else {
                    	resultjson.put("title", title);
                    }
                	resultjson.put("content", jsonObject.get("content"));
                	resultjson.put("publish_time", jsonObject.get("publish_time"));
                	resultjson.put("emotionalIndex", jsonObject.get("emotionalIndex"));
                	resultjson.put("websitelogo", jsonObject.get("websitelogo"));
                	resultjson.put("industrylable", jsonObject.get("industrylable"));
                	resultjson.put("eventlable", jsonObject.get("eventlable"));
                	resultjson.put("sourcewebsitename", jsonObject.get("sourcewebsitename"));
                	resultjson.put("titlekeyword", jsonObject.getString("titlekeyword"));
                	resultjson.put("source_name", TextUtil.removeParenthesesAndContents(String.valueOf(jsonObject.get("sourcewebsitename"))));
                    String _score = articleobject.getString("_score");//分数
                    DecimalFormat df = new DecimalFormat("#.00");
                    resultjson.put("_score", df.format(Double.parseDouble(_score)));
				}
                
                }    
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        return resultjson;
    }











	
	
	
	

}
