package com.stonedt.intelligence.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.stonedt.intelligence.constant.MonitorConstant;
import com.stonedt.intelligence.dao.ProjectDao;
import com.stonedt.intelligence.service.ArticleService;
import com.stonedt.intelligence.service.OpinionConditionService;
import com.stonedt.intelligence.util.MyHttpRequestUtil;
import com.stonedt.intelligence.util.URLUtil;

/**
 * @date 2020年4月17日 下午6:12:10
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    // es搜索地址
    @Value("${es.search.url}")
    private String es_search_url;
    @Autowired
    private OpinionConditionService opinionConditionService;
    @Autowired
    private ProjectDao projectDao;

    @Override
//    public Map<String, Object> articleDetail(String articleId, Long projectId, String page, String searchkeyword) {
    public Map<String, Object> articleDetail(String articleId, Long projectId,String relatedword,String publish_time) {
        Map<String, Object> ListparamMap = new HashMap<String, Object>(); // 列表请求参数
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("detail", "");
        result.put("emotionChart", new ArrayList<>(Arrays.asList(
                new Object[]{"正面", 0},
                new Object[]{"中性", 0},
                new Object[]{"负面", 0}
        )));
        result.put("emotionText", "");
        try {
            // 查询方案类型
//            Map<String, Object> projectParamMap = new HashMap<String, Object>();
//            projectParamMap.put("project_id", projectId);
//            Map<String, Object> projectInfo = projectDao.getProjectInfo(projectParamMap);
//            String project_type = String.valueOf(projectInfo.get("project_type"));
//
//            String subject_word = String.valueOf(projectInfo.get("subject_word"));
//            String stop_word = String.valueOf(projectInfo.get("stop_word"));
//            if (project_type.equals("2")) {
//
//            }
//
//            // 获取偏好设置
//            OpinionCondition opinionCondition = opinionConditionService.getOpinionConditionByProjectId(projectId);
//            Integer precise = opinionCondition.getPrecise();
//            Integer time = opinionCondition.getTime();
//            String emotion = opinionCondition.getEmotion();
//            Integer similar = opinionCondition.getSimilar();
//            Integer sort = opinionCondition.getSort();
//            Integer matchingmode = opinionCondition.getMatchs() - 1;
//            String times = "";
//            String timee = "";

//            JSONObject timeJson = new JSONObject();
//            switch (time) {
//                case 1:
//                    timeJson = DateUtil.dateRoll(new Date(), Calendar.HOUR, -24);
//                    times = timeJson.getString("times");
//                    timee = timeJson.getString("timee");
//                    break;
//                case 2:
//                    timeJson = DateUtil.getDifferOneDayTimes(0);
//                    times = timeJson.getString("times") + " 00:00:00";
//                    timee = timeJson.getString("timee") + " 23:59:59";
//                    break;
//                case 3:
//                    timeJson = DateUtil.getDifferOneDayTimes(-1);
//                    times = timeJson.getString("times") + " 00:00:00";
//                    timee = timeJson.getString("times") + " 23:59:59";
//                    break;
//                case 4:
//                    timeJson = DateUtil.getDifferOneDayTimes(-3);
//                    times = timeJson.getString("times") + " 00:00:00";
//                    timee = timeJson.getString("timee") + " 23:59:59";
//                    break;
//                case 5:
//                    timeJson = DateUtil.getDifferOneDayTimes(-7);
//                    times = timeJson.getString("times") + " 00:00:00";
//                    timee = timeJson.getString("timee") + " 23:59:59";
//                    break;
//                case 6:
//                    timeJson = DateUtil.getDifferOneDayTimes(-15);
//                    times = timeJson.getString("times") + " 00:00:00";
//                    timee = timeJson.getString("timee") + " 23:59:59";
//                    break;
//                case 7:
//                    timeJson = DateUtil.getDifferOneDayTimes(-30);
//                    times = timeJson.getString("times") + " 00:00:00";
//                    timee = timeJson.getString("timee") + " 23:59:59";
//                    break;
//                case 8:
//                    times = opinionCondition.getTimes();
//                    timee = opinionCondition.getTimee();
//                    break;
//            }


            if (StringUtils.isNotBlank(articleId)) {
                String url = es_search_url + MonitorConstant.es_api_article_newdetail;
                String params = "article_public_id=" + articleId + "&esindex=postal&estype=infor&publish_time="+publish_time;
                System.err.println(params);
                long startTime = System.currentTimeMillis();
                String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
                System.err.println("请求ES详情获取时间：" + (System.currentTimeMillis() - startTime) / 1000d + "s");
                if (StringUtils.isNotBlank(sendPostEsSearch)) {
                    Map<String, String> parseObject2 = JSON.parseObject(sendPostEsSearch, new TypeReference<Map<String, String>>() {
                    });
                    result.put("detail", parseObject2);
                    String text = parseObject2.get("text").toString();//详情html
                    String[] relatedwordsplit = relatedword.split("，");
                    if(!relatedword.equals("")) {
                    for (int i = 0; i < relatedwordsplit.length; i++) {
                    	text = text.replaceAll(relatedwordsplit[i], "<b class='key' style='color:red'>"+relatedwordsplit[i]+"</b>");
					}
                    }
                    
                    String emotionalIndex = parseObject2.get("emotionalIndex");
                    String positive_score = parseObject2.get("positive_score");
                    String neutrality_score = parseObject2.get("neutrality_score");
                    String negative_score = parseObject2.get("negative_score");
                    String title = parseObject2.get("title");
                    String sourcewebsitename = parseObject2.get("sourcewebsitename");
                    if (sourcewebsitename.contains("http")) {
                        sourcewebsitename = URLUtil.getDomainName(sourcewebsitename);
                        parseObject2.put("sourcewebsitename", sourcewebsitename);
                    }
                    
                    if(sourcewebsitename.equals("企鹅号")) {
                    	//Elements remove = Jsoup.parse(text).getElementsByClass("undefined").remove();
                    	Document parse = Jsoup.parse(text);
                    	
                    	parse.select("[class=undefined]").remove();
                    	
                    	//parse.getElementsByClass("undefined").clear();
                    	//parse.removeClass("undefined");
                    	text = parse.toString();
                    	text = text.replaceAll("src=\"https://mat1.gtimg.com/www/js/news/triangle.png\"", "");
                    }
                    
                    result.put("text", text);
                    

                    if (title.contains("_http://") || title.contains("_https://")) {
                        title = title.substring(0, title.indexOf("_"));
                        if(!relatedword.equals("")) {
                        for (int i = 0; i < relatedwordsplit.length; i++) {
                        	title = title.replaceAll(relatedwordsplit[i], "<b class='key' style='color:red'>"+relatedwordsplit[i]+"</b>");
    					}
                        }
                        result.put("title", title);
                    }else {
                    	if(!relatedword.equals("")) {
                    	for (int i = 0; i < relatedwordsplit.length; i++) {
                    		title = title.replaceAll(relatedwordsplit[i], "<b class='key' style='color:red'>"+relatedwordsplit[i]+"</b>");
    					}}
                        result.put("title", title);
                    }
                    if ("1".equals(emotionalIndex)) {
                        result.put("emotionText", percent(positive_score) + " 正面");
                    }
                    if ("2".equals(emotionalIndex)) {
                        result.put("emotionText", percent(neutrality_score) + " 中性");
                    }
                    if ("3".equals(emotionalIndex)) {
                        result.put("emotionText", percent(negative_score) + " 负面");
                    }
                    int p = new BigDecimal(positive_score).multiply(new BigDecimal(100000)).intValue();
                    int z = new BigDecimal(neutrality_score).multiply(new BigDecimal(100000)).intValue();
                    int n = new BigDecimal(negative_score).multiply(new BigDecimal(100000)).intValue();
                    List<Object[]> arrayList = new ArrayList<>(Arrays.asList(
                            new Object[]{"正面", p},
                            new Object[]{"中性", z},
                            new Object[]{"负面", n}
                    ));
                    result.put("emotionChart", arrayList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 字符串小数转百分比 带百分号 两位小数
     */
    public static String percent(String arg) {
        if (StringUtils.isBlank(arg)) {
            return "0.0%";
        }
        DecimalFormat df = new DecimalFormat("0.0%");
        return df.format(new BigDecimal(arg));
    }

    @Override
    public List<Map<String, Object>> relatedArticles(String keywords) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (StringUtils.isBlank(keywords)) return list;
//		String[] keywordArray = keywords.split(",");
        try {
            String url = es_search_url + MonitorConstant.es_api_relatearticle_list;
            Map<String, String> timeByDays = getTimeByDays(7);
            String start = timeByDays.get("start");
            String end = timeByDays.get("end");
            String params = "times=" + start + "&timee=" + end + "&keyword=" + keywords + "&searchType=2&matchingmode=1&esindex=postal&estype=infor&emotionalIndex=1,2,3";
            String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
            if (StringUtils.isNotBlank(sendPostEsSearch)) {
                JSONObject parseObject = JSON.parseObject(sendPostEsSearch);
                JSONArray jsonArray = parseObject.getJSONObject("aggregations")
                        .getJSONObject("top-terms-aggregation").getJSONArray("buckets");
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (i > 2) break;
                    JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("top_score_hits")
                            .getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
                            .getJSONObject("_source");
                    Map<String, Object> map = new HashMap<>();
                    String title = jsonObject.getString("title");
                    String content = jsonObject.getString("content");
                    map.put("article_public_id", jsonObject.getString("article_public_id"));
                    
                    title = jsonObject.getString("title");
                    map.put("title", title);
                    if (title.contains("_http://") || title.contains("_https://")) {
                        title = title.substring(0, title.indexOf("_"));
                        map.put("title", title);
                    }
                    map.put("content", content);
                    map.put("publishTime", jsonObject.getString("publish_time"));
                    map.put("sourceName", jsonObject.getString("sourcewebsitename"));
//					Set<String> relatedWords = new HashSet<>();
//					for (int j = 0; j < keywordArray.length; j++) {
//						if (StringUtils.isNotBlank(title) && title.contains(keywordArray[j])) {
//							relatedWords.add(keywordArray[j]);
//						}
//						if (StringUtils.isNotBlank(content) && content.contains(keywordArray[j])) {
//							relatedWords.add(keywordArray[j]);
//						}
//					}
//					map.put("relatedWords", relatedWords);
                    list.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /*
     * 	获取当前时间和{days}天前的时间
     */
    public Map<String, String> getTimeByDays(int days) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String end = now.format(dateTimeFormatter);
        LocalDateTime minusDays = now.minusDays(days);
        String start = minusDays.format(dateTimeFormatter);
        Map<String, String> map = new HashMap<>();
        map.put("start", start);
        map.put("end", end);
        return map;
    }

}
