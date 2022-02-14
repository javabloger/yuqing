package com.stonedt.intelligence.quartz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.stonedt.intelligence.constant.ReportConstant;
import com.stonedt.intelligence.constant.VolumeConstant;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.MyHttpRequestUtil;
import com.stonedt.intelligence.util.TextUtil;

/**
 * 声量监测数据请求
 */
@Component
public class VolumeDataRequest {

    // es搜索地址
    @Value("${es.search.url}")
    private String es_search_url;

    /**
     * 1关键词情感分析数据统计分布
     */
    public String keywordEmotionStatistical(String keyword, String highKeyword, String stopword, String times, String timee, Integer projectType) {
        JSONObject result = new JSONObject();
        try {
            String es_api_keyword_emotion_statistical = VolumeConstant.es_api_keyword_emotion_statistical;

            String url = es_search_url + es_api_keyword_emotion_statistical;
            String[] keywords = keyword.split(",");
            result.put("keyword_count", keywords.length);
            List<Map<String, Object>> chart = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> positive = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> negative = new ArrayList<Map<String, Object>>();
            int positive_total = 0;
            int negative_total = 0;
            List<Map<String, Object>> newchart = new ArrayList<Map<String, Object>>();
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
                    map.put("neutral_num", 0);
                    map.put("negative_num", 0);
                    Map<String, Object> newmap = new HashMap<>();
                    newmap.put("keyword", keywords[i]);
                    newmap.put("positive_num", 0);
                    newmap.put("neutral_num", 0);
                    newmap.put("negative_num", 0);
                    newmap.put("totalnum", 0);
                    for (int j = 0; j < bucketsArray.size(); j++) {
                        try {
                            Integer key = bucketsArray.getJSONObject(j).getInteger("key");
                            Integer count = bucketsArray.getJSONObject(j).getInteger("doc_count");
                            if (key == 1) {
                                positive_total += count;
                                map.put("positive_num", count);
                                newmap.put("positive_num", count);
                            }
                            if (key == 2) {
                                map.put("neutral_num", count);
                                newmap.put("neutral_num", count);
                            }
                            if (key == 3) {
                                negative_total += count;
                                map.put("negative_num", count);
                                newmap.put("negative_num", count);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    int totalnum = Integer.parseInt(parseObject.getJSONObject("hits").get("total").toString());
                    newmap.put("totalnum",totalnum);
                    chart.add(map);
                    positive.add(map);
                    negative.add(map);
                    newchart.add(newmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Collections.sort(positive, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        return (int) o2.get("positive_num") - (int) o1.get("positive_num");
                    }
                });
            } catch (Exception e) {
            }

            for (int i = 0; i < positive.size(); i++) {
                try {
//                    if (i > 1) {
//                        break;
//                    }
                    if (!positive.get(i).containsKey("positive_num")) {
                        positive.get(i).put("positive_num", 0);
                    }
                    String calculatedRatio = calculatedRatio((int) positive.get(i).get("positive_num"), positive_total);
                    positive.get(i).put("rate", calculatedRatio);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
           // result.put("positive", positive);
            result.put("positive", JSON.toJSON(positive));
            System.out.println("positive:"+positive);
            try {
                Collections.sort(negative, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        return (int) o2.get("negative_num") - (int) o1.get("negative_num");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < negative.size(); i++) {
                try {
//                    if (i > 1) {
//                        break;
//                    }
                    if (!negative.get(i).containsKey("negative_num")) {
                        negative.get(i).put("negative_num", 0);
                    }
                    String calculatedRatio = calculatedRatio(
                            Integer.valueOf(String.valueOf(negative.get(i).get("negative_num"))), negative_total);
                    negative.get(i).put("rate", calculatedRatio);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //result.put("negative", negative);
            result.put("negative", JSON.toJSON(negative));
            System.out.println("negative:"+negative);
            //取排名前十的关键词数据
            try {
                Collections.sort(newchart, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        return (int) o2.get("totalnum") - (int) o1.get("totalnum");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Map<String, Object>> chartdata = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < newchart.size()&&i<10; i++) {
            	chartdata.add(newchart.get(i));
            }
            
            result.put("chart", JSON.toJSON(chartdata));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
    }

    // 2关键词数据来源分布
    public String keywordmediaexposure(String keyword, String highKeyword, String stopword, String times, String timee, Integer projectType) {
        JSONObject result = new JSONObject();
        try {
            String es_api_keyword_emotion_statistical = VolumeConstant.es_api_keyword_mediaexposure;
            String url = es_search_url + es_api_keyword_emotion_statistical;
            String[] keywords = keyword.split(",");
            result.put("keyword_count", keywords.length);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> proportion = new ArrayList<Map<String, Object>>();// 占比
            Integer count = 0;// 总数量
            for (int i = 0; i < keywords.length; i++) {
                try {
                    String source[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
                    List<String> listt = Arrays.asList(source);
                    List<String> arrayList = new ArrayList<String>(listt);// 转换为ArrayLsit调用相关的remove方法
                    String params = "times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword=" + stopword
                            + "&searchkeyword=" + keywords[i] + "&origintype=0&emotionalIndex=1,2,3&projecttype=" + projectType;
                    String sendPost = sendPost(url, params);
                    JSONObject parseObject = JSONObject.parseObject(sendPost);
                    Integer total = parseObject.getJSONObject("hits").getInteger("total");// 单个关键词的数量
                    count += total;
                    JSONArray bucketsArray = parseObject.getJSONObject("aggregations").getJSONObject("top-terms-emotion")
                            .getJSONArray("buckets");
                    Map<String, Object> object = new HashMap<>();// chart map
                    Map<String, Object> proportionMap = new HashMap<>();// text map
                    object.put("keyword", keywords[i]);
                    object.put("total", total);
                    proportionMap.put("keyword", keywords[i]);
                    proportionMap.put("total", total);
                    Integer one = 0;
                    for (int j = 0; j < bucketsArray.size(); j++) {
                        try {
                            JSONObject jsonObject = JSONObject.parseObject(String.valueOf(bucketsArray.get(j)));
                            String keynum = jsonObject.getString("key");
                            String key = TextUtil.dataSourceClassificationEng(keynum);//
                            Integer doc_count = jsonObject.getInteger("doc_count");
                            if (doc_count > one) {
                                one = doc_count;
                                proportionMap.put("one", one);
                                proportionMap.put("key", TextUtil.dataSourceClassification(keynum));
                                proportionMap.put("classify", keynum);
                            }
                            object.put(key, doc_count);
                            arrayList.remove(keynum);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    for (int j = 0; j < arrayList.size(); j++) {
                        try {
                            String keynum = arrayList.get(j);
                            String key = TextUtil.dataSourceClassificationEng(keynum);
                            object.put(key, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    list.add(object);
                    proportion.add(proportionMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Collections.sort(proportion, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return (int) o2.get("total") - (int) o1.get("total");
                }
            });

            Collections.sort(list, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return (int) o2.get("total") - (int) o1.get("total");
                }
            });

            List<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < proportion.size(); i++) {
                try {
                    if (i > 1) {
                        break;
                    }
                    Map<String, Object> hashMap = new HashMap<String, Object>();
                    Map<String, Object> map = proportion.get(i);
                    Integer total = (Integer) map.get("total");
                    String calculatedRatio = calculatedRatio(total, count);

                    hashMap.put("key", String.valueOf(map.get("key")));
                    hashMap.put("keyword", String.valueOf(map.get("keyword")));
                    hashMap.put("rate", calculatedRatio);
                    hashMap.put("classify", Integer.valueOf(String.valueOf(map.get("classify"))));
                    arrayList.add(hashMap);
                } catch (Exception e) {
                }
            }
            result.put("chart", list);
            result.put("text", arrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toJSONString();
    }

    // 3关键词情感分析数据走势
    public String keywordsentimentFlagChart(String keyword, String stopword, String times, String timee,
                                            Integer timetype, Integer projectType) {
        JSONArray list = new JSONArray();
        try {
            String time_period = time_period(timetype);
            String es_api_keyword_emotion_statistical = VolumeConstant.es_api_keyword_sentimentFlagChart;
            String url = es_search_url + es_api_keyword_emotion_statistical;
            String params = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&timetype=" + time_period + "&emotionalIndex=1,2,3&projecttype=" + projectType;
            String sendPost = sendPost(url, params);
            JSONObject parseObject = JSONObject.parseObject(sendPost);
            JSONArray bucketsArray = parseObject.getJSONObject("aggregations").getJSONObject("group_by_grabTime")
                    .getJSONArray("buckets");
            for (int i = 0; i < bucketsArray.size(); i++) {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(String.valueOf(bucketsArray.get(i)));
                    String time = jsonObject.getString("key_as_string"); // 时间
                    if (timetype == 1 || timetype == 2) {
                        time = time.substring(0, 13);
                    }
                    if (timetype == 3 || timetype == 4) {
                        time = time.substring(0, 10);
                    }
                    JSONArray jsonArray = jsonObject.getJSONObject("top-terms-classify").getJSONArray("buckets");
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("time", time);
                    for (int j = 0; j < jsonArray.size(); j++) {
                        try {
                            JSONObject object = JSONObject.parseObject(String.valueOf(jsonArray.get(j)));
                            String keynum = object.getString("key");// 情感
                            if ("1".equals(keynum)) {
                                keynum = "positive_num";
                            } else if ("2".equals(keynum)) {
                                keynum = "neutral_num";
                            } else if ("3".equals(keynum)) {
                                keynum = "negative_num";
                            }
                            String doc_count = object.getString("doc_count");
                            map.put(keynum, doc_count);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    list.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list.toJSONString();
    }

    // 4关键词资讯数量排名
    public String keywordranking(String keyword, String highKeyword, String stopword, String times, String timee, Integer projectType) {
        JSONArray jsonArray = new JSONArray();
        try {
            String es_api_keyword_emotion_statistical = VolumeConstant.es_api_keyword_mediaexposure;
            String url = es_search_url + es_api_keyword_emotion_statistical;
            String[] keywords = keyword.split(",");
            JSONObject result = new JSONObject();
            result.put("keyword_count", keywords.length);
            Integer count = 0;// 总数量
            List<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < keywords.length; i++) {
                try {
                    Map<String, Object> map = new HashMap<String, Object>();
                    String params = "times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword=" + stopword
                            + "&searchkeyword=" + keywords[i] + "&origintype=0&emotionalIndex=1,2,3&projecttype=" + projectType;
                    String sendPost = sendPost(url, params);
                    JSONObject parseObject = JSONObject.parseObject(sendPost);
                    Integer total = parseObject.getJSONObject("hits").getInteger("total");// 单个关键词的数量
                    count += total;
                    map.put("keyword", keywords[i]);
                    map.put("count", total);
                    arrayList.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(arrayList, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return (int) o2.get("count") - (int) o1.get("count");
                }
            });
            for (int i = 0; i < arrayList.size(); i++) {
                try {
                    if (i > 9) { break; }
                    JSONObject jsonObject = new JSONObject();
                    Map<String, Object> map = arrayList.get(i);
                    int total = (int) map.get("count");
                    String calculatedRatio = calculatedRatio(total, count);
                    jsonObject.put("keyword", String.valueOf(map.get("keyword")));
                    jsonObject.put("count", total);
                    jsonObject.put("rate", calculatedRatio);
                    jsonArray.add(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray.toJSONString();
    }

    // 5关键词高频分布统计
    public String wordCloud(String keyword, String stopword, String times, String timee, String classify, Integer projectType) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            String url = es_search_url + ReportConstant.es_api_keyword_list;
            String params = "times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
                    + "&searchType=1&classify=" + classify + "&emotionalIndex=1,2,3&projecttype=" + projectType;
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            if (StringUtils.isNotBlank(sendPostEsSearch)) {
                Map<String, Integer> map = new HashMap<>();
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
                for (Entry<String, Integer> entrySet : map.entrySet()) {
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("keyword", entrySet.getKey());
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

    // 6关键词曝光度环比排行
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
                        String calculatedRatio = calculatedRatio(doc_count, total);
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
                    String calculatedRatio = calculatedRatio(momtotal, total);
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
        }
        return "";
    }


    // 7自媒体渠道声量排行

    public String selfMediaRanking(String keyword, String stopword, String times, String timee, Integer projectType) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            String url = es_search_url + ReportConstant.es_api_media_list;
            String param = "classify=7&times=" + times + "&timee=" + timee + "&keyword=" + keyword + "&stopword=" + stopword
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(result);
    }

//	public String cesString(String keyword, String stopword, String times, String timee) {
//		String url = es_search_url + ReportConstant.es_api_ner;
//		String param = "times=" + times + "&timee=" + timee + "&keyword=南京&stopword=&emotionalIndex=1,2,3";
//		String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, param);
//		Map<String, Object> tool = TextUtil.tool(sendPostEsSearch);
//		Map<String, String> ringRatioCycle = DateUtil.RingRatioCycle(times, timee);
//		param = "times=" + ringRatioCycle.get("startTime") + "&timee=" + ringRatioCycle.get("endTime")
//				+ "&keyword=南京&stopword=&emotionalIndex=1,2,3";
//		String sendPostEsSearch2 = MyHttpRequestUtil.sendPostEsSearch(url, param);
//		Map<String, Object> tool2 = TextUtil.tool(sendPostEsSearch2);
//		String ringRatioCycletool = TextUtil.RingRatioCycletool(tool, tool2);
//		return ringRatioCycletool;
//	}

    /**
     * 获取指定时间周期开始和结束时间 yyyy-MM-dd HH:mm:ss
     */
    public Map<String, String> time(Integer timePeriod) {
        Map<String, String> time = new HashMap<String, String>();
        try {
            if (timePeriod == null) {
                timePeriod = 1;
            }
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

    /**
     * 计算占比 a/b 有百分号
     */
    public String calculatedRatio(Integer a, Integer b) {
        String result = "";
        try {
            if (b == 0 || a == 0) {
                result = "0.00%";
            } else {
                BigDecimal bigDecimala = new BigDecimal(a);
                BigDecimal bigDecimalb = new BigDecimal(b);
                BigDecimal divide = bigDecimala.divide(bigDecimalb, 4, BigDecimal.ROUND_HALF_UP);
                NumberFormat numberFormat = NumberFormat.getPercentInstance();
                numberFormat.setMaximumFractionDigits(2);
                result = numberFormat.format(divide.doubleValue()).replaceAll(",", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /***
     * 走势
     *
     */
    public String time_period(Integer timetype) {
        String type = "1H";
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    /**
     * 发送post请求
     *
     * @date 2020年4月13日 下午4:02:23
     */
    public String sendPost(String url, String params) {
        System.err.println(url + "?" + params);
        try {
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
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
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
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
