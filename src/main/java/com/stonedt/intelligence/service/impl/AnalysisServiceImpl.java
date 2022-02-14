package com.stonedt.intelligence.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.stonedt.intelligence.constant.MonitorConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.dao.AnalysisDao;
import com.stonedt.intelligence.dao.OpinionConditionDao;
import com.stonedt.intelligence.dao.ProjectDao;
import com.stonedt.intelligence.entity.Analysis;
import com.stonedt.intelligence.entity.OpinionCondition;
import com.stonedt.intelligence.entity.Project;
import com.stonedt.intelligence.service.AnalysisService;
import com.stonedt.intelligence.util.ProjectWordUtil;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Value("${es.search.url}")
    private String es_search_url;

    @Autowired
    private AnalysisDao analysisDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private OpinionConditionDao opinionConditionDao;

    @Override
    public Analysis getAanlysisByProjectidAndTimeperiod(Long projectId, Integer timePeriod) {
        Analysis aanlysisByProjectidAndTimeperiod = analysisDao.getAanlysisByProjectidAndTimeperiod(projectId, timePeriod);
        return aanlysisByProjectidAndTimeperiod;
    }


    // 历史代码
    @Override
    public int insert(Analysis a) {
        return analysisDao.insert(a);
    }

    @Override
    public Analysis getInfoByProjectid(Long projectid, Integer timePeriod) {
        return analysisDao.getInfoByProjectid(projectid, timePeriod);
    }

    @Override
    public Analysis getAnalysisMonitorProjectid(Long projectId, Integer timePeriod) {
        if (timePeriod == null)
            timePeriod = 1;
        Analysis analysisMonitorProjectid = analysisDao.getAnalysisMonitorProjectid(projectId, timePeriod);
        Date createTime = analysisMonitorProjectid.getCreateTime();
        if (null != createTime) {
            try {
                Instant instant = createTime.toInstant();
                ZoneId zoneId = ZoneId.systemDefault();
                LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                analysisMonitorProjectid.setUpdateTime(localDateTime.format(dateTimeFormatter));
            } catch (Exception e) {
                e.printStackTrace();
                analysisMonitorProjectid.setUpdateTime("");
            }
        } else {
            analysisMonitorProjectid.setUpdateTime("");
        }
        return analysisMonitorProjectid;
    }

    @SuppressWarnings("unused")
    @Override
    public List<Map<String, Object>> latestnews(Long projectId, Integer timePeriod) {
        Map<String, String> timeMap = time(timePeriod);
        String times = timeMap.get("start");
        String timee = timeMap.get("end");
        Project project = projectDao.getProject(projectId);
        String keyword = project.getSubjectWord();
        if (StringUtils.isNotBlank(keyword))
            keyword = keyword.trim();
        String stopword = project.getStopWord();
        if (StringUtils.isNotBlank(stopword))
            stopword = stopword.trim();
        String characterWord = project.getCharacterWord();
        if (StringUtils.isNotBlank(characterWord))
            characterWord = characterWord.trim();
        String eventWord = project.getEventWord();
        if (StringUtils.isNotBlank(eventWord))
            eventWord = eventWord.trim();
        String regionalWord = project.getRegionalWord();
        if (StringUtils.isNotBlank(regionalWord))
            regionalWord = regionalWord.trim();
        Integer projectType = project.getProjectType();
        if (projectType == 2) {
            keyword = ProjectWordUtil.highProjectKeyword(keyword, regionalWord, characterWord, eventWord);
            stopword = ProjectWordUtil.highProjectStopword(stopword);
        }
        if (projectType == 1) {
        	projectType = 2;
        	keyword = ProjectWordUtil.QuickProjectKeyword(keyword);
        	stopword = ProjectWordUtil.highProjectStopword(stopword);
        }
        // 获取偏好设置参数
        OpinionCondition opinionConditionByProjectId = opinionConditionDao.getOpinionConditionByProjectId(projectId);
        // 解析情感指数
//		JSONArray emotionArray = JSONArray.parseArray(opinionConditionByProjectId.getEmotion());
//        String emotionalIndex = StringUtils.join(emotionArray, ",");
//        if (StringUtils.isBlank(emotionalIndex)) {
//            emotionalIndex = "1,2,3";
//        }
        // matchingmode=0 全文 matchingmode 标题 matchingmode=2正文
        Integer matchingmode = 0;
        String emotionalIndex = "1,2,3";
//        Integer matchs = opinionConditionByProjectId.getMatchs();//matchs=1全文 matchs=2标题  matchs=3正文
//        matchingmode = matchs -1;

        // 精准筛选（0：关闭：1打开）
//        Integer precise = opinionConditionByProjectId.getPrecise();
//        if(precise==0) {
//        	stopword = "";
//        }
        // 相似文章(0:取消合并 1：合并文章)
        Integer similar = opinionConditionByProjectId.getSimilar();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        // String param =
        // "times="+times+"&timee="+timee+"&keyword="+keyword+"&stopword="+stopword
        String param = "keyword=" + keyword + "&stopword=" + stopword
                + "&page=1&size=20&searchType=1&esindex=postal&estype=infor&emotionalIndex=" + emotionalIndex
                + "&projecttype=" + projectType + "&matchingmode=" + matchingmode;
//       if(1==1) {
        // if(similar==0) {
        try {
            String result = sendPost(es_search_url + "/yqsearch/searchsimplelist", param);
            JSONObject json = JSONObject.parseObject(result);
            JSONArray resultArray = json.getJSONArray("data");
            Map<String, Object> datamap = new HashMap<String, Object>();// 用户去除重复数据
            for (int i = 0; i < resultArray.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                String article_public_id = resultArray.getJSONObject(i).getString("_id");
                String title = resultArray.getJSONObject(i).getJSONObject("_source").getString("title");
                if (datamap.containsValue(title))
                    continue;
                datamap.put("title", title);
                int emotion = resultArray.getJSONObject(i).getJSONObject("_source").getIntValue("emotionalIndex");
                String publish_time = resultArray.getJSONObject(i).getJSONObject("_source").getString("publish_time");
                String source_name = resultArray.getJSONObject(i).getJSONObject("_source").getString("source_name");

                if (source_name.equals("微博")) {
                    source_name = source_name + "-" + title;

                    String params = "article_public_id=" + article_public_id + "&esindex=postal&estype=infor";
                    String response = sendPost(es_search_url + MonitorConstant.es_api_article_newdetail, params);
                    JSONObject responseJson = JSON.parseObject(response);
                    if (responseJson.containsKey("content")) {
                        String content = responseJson.getString("content");
                        if (content.length() > 30) {
                            content = content.substring(0, 30);
                        }
                        title = content;
                    }
                }

                map.put("article_public_id", article_public_id);
                map.put("title", title);
                map.put("emotion", emotion);
                map.put("publish_time", publish_time);
                map.put("source_name", source_name);
                list.add(map);
                if (list.size() >= 5)
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }
        // else {
//        	
//        	param = "times="+times+"&timee="+timee+"&keyword="+keyword+"&stopword="+stopword
//    				+"&page=1&searchType=1&esindex=postal&estype=infor&emotionalIndex="+emotionalIndex+"&projecttype="+projectType+"&matchingmode="+matchingmode;
//        	String similarUrl = es_search_url + MonitorConstant.es_api_similarsearch_content;
//        	String esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, param);
//
//            String article_public_idStr = "";
//            if (!esSimilarResponse.equals("")) {
//                List article_public_idList = new ArrayList();
//                JSONArray similarArray = JSON.parseArray(esSimilarResponse);
//                if(similarArray.size()==0) {
//                	return list;
//                }
//                for (int i = 0; i < similarArray.size(); i++) {
//                    JSONObject similarJson = (JSONObject) similarArray.get(i);
//                    String article_public_id = similarJson.getString("article_public_id");
//                    article_public_idList.add(article_public_id);
//                    if (i < 10) {
//                        article_public_idStr += article_public_id + ",";
//                    }
//                }
//               
//            }
//            article_public_idStr = param+"&article_public_idstr="+article_public_idStr;
//            String getcontenturl = es_search_url + MonitorConstant.es_api_similar_contentlist;
//            String articleResponse = MyHttpRequestUtil.sendPostEsSearch(getcontenturl, article_public_idStr);
//            JSONObject articleResponseJson = JSON.parseObject(articleResponse);
//            String code = articleResponseJson.getString("code");
//            if (code.equals("200")) {
//            	JSONArray esDataArray = articleResponseJson.getJSONArray("data");
//            	for (int i = 0; i < esDataArray.size()&&i<5; i++) {
//	            		Map<String, Object> map = new HashMap<>();
//	            		JSONObject dataJson = (JSONObject) esDataArray.get(i);
//	                    JSONObject _sourceJson = dataJson.getJSONObject("_source");
//	                    String article_public_id = _sourceJson.get("article_public_id").toString();
//	                    String title = _sourceJson.get("title").toString();
//	                    String emotion = _sourceJson.get("emotionalIndex").toString();
//	                    String publish_time = _sourceJson.get("publish_time").toString();
//	                    String source_name = _sourceJson.get("sourcewebsitename").toString();
//	                    map.put("article_public_id", article_public_id);
//	     				map.put("title", title);
//	     				map.put("emotion", emotion);
//	     				map.put("publish_time", publish_time);
//	     				map.put("source_name", source_name);
//	     				list.add(map);
//                     
//            	}
//            }
//            
//        }

        return list;
    }

    /**
     * 获取指定时间周期开始和结束时间 yyyy-MM-dd HH:mm:ss
     */
    public Map<String, String> time(Integer timePeriod) {
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
        Map<String, String> time = new HashMap<String, String>();
        time.put("start", start);
        time.put("end", now);
        return time;
    }

    // 发送请求
    public String sendPost(String url, String params) throws IOException {
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
    }

}
