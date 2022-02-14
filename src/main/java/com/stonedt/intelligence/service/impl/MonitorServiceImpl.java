package com.stonedt.intelligence.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.constant.MonitorConstant;
import com.stonedt.intelligence.dao.ProjectDao;
import com.stonedt.intelligence.entity.OpinionCondition;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.MonitorService;
import com.stonedt.intelligence.service.OpinionConditionService;
import com.stonedt.intelligence.service.ProjectService;
import com.stonedt.intelligence.util.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;

/**
 * description: MonitorServiceImpl <br>
 * date: 2020/4/16 18:48 <br>
 * author: huajiancheng <br>
 * version: 1.0 <br>
 */
@Service
public class MonitorServiceImpl implements MonitorService {
    @Autowired
    OpinionConditionService opinionConditionService;
    // es搜索地址
    @Value("${es.search.url}")
    private String es_search_url;
    @Autowired
    ProjectService projectService;
    @Autowired
    ProjectDao projectDao;
    @Autowired
    ExportUtil exportUtil;
    @Autowired
    UserUtil userUtil;
    @Autowired
    private RedisUtil redisUtil;
    

    /**
     * @param [paramJson]
     * @return com.alibaba.fastjson.JSONObject
     * @description: 获取第一次加载的用户的默认条件配置 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/16 18:49 <br>
     * @author: huajiancheng <br>
     */

    @Override
    public JSONObject getCondition(JSONObject paramJson) {
        JSONObject response = new JSONObject();
        JSONObject timesJson = new JSONObject();
        Long projectId = paramJson.getLong("projectId");
        Map<String, Object> projectInfo = projectService.getProjectByProId(projectId);
        String project_name = String.valueOf(projectInfo.get("project_name"));
        String group_name = String.valueOf(projectInfo.get("group_name"));

        OpinionCondition opinionCondition = opinionConditionService.getOpinionConditionByProjectId(projectId);
        Integer time = opinionCondition.getTime();
        if (time == 1) {
            timesJson = DateUtil.getDifferOneDayTimes(-3);
        } else if (time == 2) {
            timesJson = DateUtil.getDifferOneDayTimes(-7);
        } else if (time == 3) {
            timesJson = DateUtil.getDifferOneDayTimes(-16);
        } else if (time == 4) {
            timesJson = DateUtil.getDifferOneDayTimes(-30);
        }


        String times = timesJson.getString("times");
        String timee = timesJson.getString("timee");
        JSONObject conditionJson = (JSONObject) JSON.toJSON(opinionCondition);
//        conditionJson.put("times", times);
//        conditionJson.put("timee", timee);
        conditionJson.put("timeType", time);
        if (opinionCondition != null) {
            response.put("code", 200);
            response.put("msg", "获取条件成功！");
            response.put("data", conditionJson);
            response.put("project_name", project_name);
            response.put("group_name", group_name);
        } else {
            response.put("code", 500);
            response.put("msg", "获取条件失败！");
            response.put("data", new JSONObject());
            response.put("project_name", project_name);
            response.put("group_name", group_name);
        }
        return response;
    }

    /**
     * @param [paramJson]
     * @return com.alibaba.fastjson.JSONObject
     * @description: 获取文章列表数据 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/16 19:38 <br>
     * @author: huajiancheng <br>
     */

    @Override
    public JSONObject getArticleList(JSONObject paramJson) {
        JSONObject response = new JSONObject();
        JSONObject dataGroupJson = new JSONObject();
        Long projectid = paramJson.getLong("projectid");
        Map<String, Object> projectParamMap = new HashMap<String, Object>();
        projectParamMap.put("project_id", projectid);
        Map<String, Object> projectInfo = projectDao.getProjectInfo(projectParamMap);
        if (projectInfo == null) {
            response.put("code", 200);
            response.put("msg", "舆情列表es返回成功");
            dataGroupJson.put("data", new ArrayList<>());
            response.put("data", dataGroupJson);
            return response;
        }
        int projectType = (int) projectInfo.get("project_type");
        String subject_word = String.valueOf(projectInfo.get("subject_word"));
        String regional_word = String.valueOf(projectInfo.get("regional_word"));
        String character_word = String.valueOf(projectInfo.get("character_word"));
        String event_word = String.valueOf(projectInfo.get("event_word"));
        String stop_word = String.valueOf(projectInfo.get("stop_word"));
        String searchkeyword = paramJson.getString("searchkeyword");
        String user_id = projectInfo.get("user_id").toString();
        if (StringUtils.isNotBlank(subject_word)) subject_word = subject_word.trim();
        if (StringUtils.isNotBlank(stop_word)) stop_word = stop_word.trim();
        if (StringUtils.isNotBlank(searchkeyword)) searchkeyword = searchkeyword.trim();
//        if (projectid == 1) {
//            paramJson.put("keyword", searchkeyword);
//            paramJson.put("searchkeyword", "");
//        } else {
//            paramJson.put("keyword", subject_word);
//        }
        paramJson.put("keyword", subject_word);
        paramJson.put("stopword", stop_word);
        if (projectType == 2) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.highProjectKeyword(subject_word, regional_word, character_word, event_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        
        if (projectType == 1) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.QuickProjectKeyword(subject_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        Integer similar = paramJson.getInteger("similar");
        Integer matchingmode = paramJson.getInteger("matchingmode") - 1; // 关键词匹配规则
        JSONArray emotionArray = paramJson.getJSONArray("emotionalIndex");
        String emotionalIndex = StringUtils.join(emotionArray, ",");
        if (StringUtils.isBlank(emotionalIndex)) {
            emotionalIndex = "1,2,3";
        }
        //机构类型
        JSONArray orgarray = paramJson.getJSONArray("organizationtype");
        String orgtypelist = StringUtils.join(orgarray, ",");
        if (StringUtils.isBlank(orgtypelist)) {
        	orgtypelist = "0";
        }
        
        paramJson.put("orgtypelist", orgtypelist);
        paramJson.remove("organizationtype");
        
        
        //文章分类
        JSONArray categorylabledataarray = paramJson.getJSONArray("categorylabledata");
        String categorylable = StringUtils.join(categorylabledataarray, ",");
        if (StringUtils.isBlank(categorylable)) {
        	categorylable = "0";
        }
        paramJson.put("categorylable", categorylable);
        paramJson.remove("categorylabledata");
        
        
        //企业类型
        
        JSONArray enterprisetypelistarray = paramJson.getJSONArray("enterprisetypelist");
        String enterprisetypelist = StringUtils.join(enterprisetypelistarray, ",");
        if (StringUtils.isBlank(enterprisetypelist)) {
        	enterprisetypelist = "0";
        }
        paramJson.put("enterprisetypelist", enterprisetypelist);
        
        
        
        //高科技型企业
        JSONArray hightechtypelistarray = paramJson.getJSONArray("hightechtypelist");
        String hightechtypelist = StringUtils.join(hightechtypelistarray, ",");
        if (StringUtils.isBlank(hightechtypelist)) {
        	hightechtypelist = "0";
        }
        paramJson.put("hightechtypelist", hightechtypelist);
        
       //政策
        JSONArray policylableflagarray = paramJson.getJSONArray("policylableflag");
        String policylableflag = StringUtils.join(policylableflagarray, ",");
        if (StringUtils.isBlank(policylableflag)) {
        	policylableflag = "0";
        }
        paramJson.put("policylableflag", policylableflag);

        //数据来源
        JSONArray classify = paramJson.getJSONArray("classify");
        String classifylist= StringUtils.join(classify, ",");
        if (StringUtils.isBlank(classifylist)) {
            classifylist = "0";
        }
        paramJson.put("classify", classifylist);

        /*数据品类*/
        JSONArray datasource_type = paramJson.getJSONArray("datasource_type");
        String dataCategoryList = StringUtils.join(datasource_type, ",");
        if (StringUtils.isBlank(dataCategoryList)){
            dataCategoryList = "0";
        }
        paramJson.put("datasource_type" , dataCategoryList);


        Integer searchType = paramJson.getInteger("searchType");
        String times = "";
        String timee = "";
        Integer timeType = paramJson.getInteger("timeType");
        JSONObject timeJson = new JSONObject();
        switch (timeType) {
            case 1:
                timeJson = DateUtil.dateRoll(new Date(), Calendar.HOUR, -24);
                times = timeJson.getString("times");
                timee = timeJson.getString("timee");
                break;
            case 2:
                timeJson = DateUtil.getDifferOneDayTimes(0);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 3:
                timeJson = DateUtil.getDifferOneDayTimes(-1);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("times") + " 23:59:59";
                break;
            case 4:
                timeJson = DateUtil.getDifferOneDayTimes(-3);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 5:
                timeJson = DateUtil.getDifferOneDayTimes(-7);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 6:
                timeJson = DateUtil.getDifferOneDayTimes(-15);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 7:
                timeJson = DateUtil.getDifferOneDayTimes(-30);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 8:
                times = paramJson.getString("times") + " 00:00:00";
                timee = paramJson.getString("timee") + " 23:59:59";
                break;
        }
        paramJson.put("times", times);
        paramJson.put("timee", timee);
        paramJson.put("matchingmode", matchingmode);
        paramJson.put("emotionalIndex", emotionalIndex);
        paramJson.put("searchType", searchType);
        paramJson.put("size", 30);
        
        JSONArray eventArray = paramJson.getJSONArray("eventIndex");
        String eventlable = "";
        if(eventArray!=null) {
        	eventlable = StringUtils.join(eventArray, ",");
            eventlable = eventlable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
            if(eventlable.endsWith(",")) {
            	eventlable = eventlable.substring(0, eventlable.length()-1);
            }
        }
        paramJson.put("eventlable", eventlable);
        
        paramJson.remove("eventIndex");
        JSONArray industryArray = paramJson.getJSONArray("industryIndex");
        String industrylable = "";
        if(industryArray!=null) {
        	industrylable = StringUtils.join(industryArray, ",");
            industrylable = industrylable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
            if(industrylable.endsWith(",")) {
            	industrylable = industrylable.substring(0, industrylable.length()-1);
            }
        }
        paramJson.put("industrylable", industrylable);
        paramJson.remove("industryIndex");
        
        String province = "";
        JSONArray provinceArray = paramJson.getJSONArray("province");
        if(provinceArray!=null) {
        	province = StringUtils.join(provinceArray, ",");
            if(!StringUtils.isEmpty(province)) {
            	province = province.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
            }else {
            	province = "";
            }
            if(province.endsWith(",")) {
            	province = province.substring(0, province.length()-1);
            }
        }
        
        
        paramJson.put("province", province);
        
        
        
        JSONArray cityArray = paramJson.getJSONArray("city");
        String city = StringUtils.join(cityArray, ",");
        
        if(!StringUtils.isEmpty(city)) {
        	city = city.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        }else {
        	city = "";
        }
        if(city.endsWith(",")) {
        	city = city.substring(0, city.length()-1);
        }
        paramJson.put("city", city);
        
        
        // 更新偏好设置值
        @SuppressWarnings("unused")
        Integer opinionCount = opinionConditionService.updateOpinionConditionByMap(paramJson);
        int precise = paramJson.getIntValue("precise");
        if (precise == 0) {
            // precise == 0 为精准关闭，即不传屏蔽词
            paramJson.put("stopword", "");
        }
        paramJson.remove("projectid");
        paramJson.remove("group_id");
        paramJson.remove("precise");
        paramJson.remove("similar");
        paramJson.remove("groupid");
        paramJson.remove("projectId");
        switch (searchType) {
            case 1:
                searchType = 1;
                break;
            case 2:
                searchType = 0;
                break;
            case 3:
                searchType = 2;
                break;
        }
        paramJson.put("searchType", searchType);
        // 查询es数据
        String url = "";

        if (similar == 1) {  // 合并
            Integer totalCount = 0;
            Integer totalNum = 0;
            Integer currentPage = paramJson.getInteger("page");
            String article_public_idStr = paramJson.getString("article_public_idstr");
            if (article_public_idStr == null) {
                article_public_idStr = "";
            }
            /*String key = projectid.toString()+paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword")+timeType+matchingmode+searchType+emotionalIndex+searchkeyword;*/
            /*2021.6.25修改*/                                                                                                                                                                                                                                                                                                                                                                                                                                    /*数据品类*/
            String key = projectid.toString()+paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getString("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword")+paramJson.getString("classify")+paramJson.getString("datasource_type")+paramJson.getString("sourceWebsite")+"author"+paramJson.getString("author")+"timeType"+timeType+matchingmode+"searchtype"+searchType+"emotion"+emotionalIndex+searchkeyword;

            /*2021.6.25修改*/
            if (currentPage == 1) {
                String params = MapUtil.getUrlParamsByMap(paramJson);
                String similarUrl = es_search_url + MonitorConstant.es_api_similar_titlekeyword_search_content;
                String esSimilarResponse = null;
                if(redisUtil.existsKey(key)) {
                	esSimilarResponse = redisUtil.getKey(key);
                }else {
                	esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
                    redisUtil.filteritemset(key, esSimilarResponse);
                }
                if (!esSimilarResponse.equals("")) {
                    List article_public_idList = new ArrayList();
                    JSONObject parseObject = JSONObject.parseObject(esSimilarResponse);
                    //JSONArray similarArray = JSON.parseArray(esSimilarResponse);
                    JSONArray similarArray = parseObject.getJSONArray("data");
                    for (int i = 0; i < similarArray.size(); i++) {
                        JSONObject similarJson = (JSONObject) similarArray.get(i);
                        String article_public_id = similarJson.getString("article_public_id");
                        article_public_idList.add(article_public_id);
                        if (i < 30) {
                            article_public_idStr += article_public_id + ",";
                        }
                    }
                    totalCount = article_public_idList.size();
                    
                    totalNum = parseObject.getInteger("totalnum");
                    dataGroupJson.put("article_public_idList", article_public_idList);
                    /*2021.7.2*/
                    dataGroupJson.put("totalNum", totalNum);
                    /*2021.7.2*/
                }
            }
            if (!article_public_idStr.equals("")) {
                paramJson.put("article_public_idstr", article_public_idStr);
                paramJson.remove("stopword");
                String params1 = MapUtil.getUrlParamsByMap(paramJson);
                url = es_search_url + MonitorConstant.es_api_similar_contentlist;
                String articleResponse = MyHttpRequestUtil.sendPostEsSearch(url, params1);
                JSONObject articleResponseJson = JSON.parseObject(articleResponse);
                String code = articleResponseJson.getString("code");
                if (code.equals("200")) {
                    Integer page_count = articleResponseJson.getInteger("page_count");
                    Integer count = articleResponseJson.getInteger("count");
                    Integer page = articleResponseJson.getInteger("page");
                    if (currentPage == 1) {
                        dataGroupJson.put("totalCount", totalCount);
                        dataGroupJson.put("totalNum", JSONObject.parseObject(redisUtil.getKey(key)).getString("totalnum"));
                        if (totalCount % 30 == 0) {
                            page_count = totalCount / 30;
                        } else {
                            page_count = totalCount / 30 + 1;
                        }
                        dataGroupJson.put("totalPage", page_count);
                    } else {
                        Integer totalPage = paramJson.getInteger("totalPage");
                        totalCount = paramJson.getInteger("totalCount");
                        dataGroupJson.put("totalPage", totalPage);
                        dataGroupJson.put("totalCount", totalCount);
                        dataGroupJson.put("totalNum", JSONObject.parseObject(redisUtil.getKey(key)).getString("totalnum"));
                    }
                    dataGroupJson.put("currentPage", page);
                    JSONArray esDataArray = articleResponseJson.getJSONArray("data");
                    JSONArray dataArray = new JSONArray();
                    for (int i = 0; i < esDataArray.size(); i++) {
                        JSONObject dataJson = (JSONObject) esDataArray.get(i);
                        JSONObject highlightJson = dataJson.getJSONObject("highlight"); // 高亮
                        JSONObject _sourceJson = dataJson.getJSONObject("_source");
                        Set<String> relatedWord = new HashSet<>();
                        if(projectType==2) {
                        	relatedWord = ProjectWordUtil
                                    .projectRelatedWord(_sourceJson.getString("title") + _sourceJson.getString("content"), subject_word, regional_word, character_word, event_word);
                            _sourceJson.put("relatedWord", relatedWord);
                        }else if(projectType==1) {
                        	relatedWord = ProjectWordUtil
                                    .CommononprojectRelatedWord(_sourceJson.getString("title") + _sourceJson.getString("content"), subject_word, regional_word, character_word, event_word);
                            _sourceJson.put("relatedWord", relatedWord);
                        }
                        
                        String title = "";
                        if (highlightJson.containsKey("title")) {
                            title = highlightJson.getJSONArray("title").getString(0);
                            title = title.replaceAll("\"", "");
                            
                            //title = ProjectWordUtil.highlightcontent(title, subject_word, regional_word, character_word, event_word);
                            
                            _sourceJson.put("title", title);
                        }
                        String content = "";
                        if (highlightJson.containsKey("content")) {
                            content = highlightJson.getJSONArray("content").getString(0);
                           // content = ProjectWordUtil.highlightcontent(content, subject_word, regional_word, character_word, event_word);
                            _sourceJson.put("content", content);
                        }
//                        content = content.replaceAll("\\s*", "");
//                        _sourceJson.put("content", content);
                        title = _sourceJson.getString("title");
                        if (title.contains("_http://") || title.contains("_https://")) {
                            title = title.substring(0, title.indexOf("_"));
                            _sourceJson.put("title", title);
                        }
//                        title = title.replaceAll("\\s*", "");
//                        _sourceJson.put("title", title);

                        if (_sourceJson.containsKey("extend_string_one")) {
                            String extend_string_one = _sourceJson.getString("extend_string_one");
                            if (!extend_string_one.equals("")) {
                                JSONObject extend_string_oneJson = JSON.parseObject(extend_string_one);
                                JSONArray imglist = extend_string_oneJson.getJSONArray("imglist");
                                extend_string_oneJson.put("imglist", imglist);
                                _sourceJson.put("extend_string_one", extend_string_oneJson);
                            }
                        } else {
                            _sourceJson.put("extend_string_one", "");
                        }
                      //事件标签
                        if (_sourceJson.containsKey("eventlable")) {
                        	_sourceJson.put("eventlable", _sourceJson.get("eventlable").toString());
                        }else {
                        	 _sourceJson.put("eventlable", "");
                        }
                        //行业标签
                        if (_sourceJson.containsKey("industrylable")) {
                        	_sourceJson.put("industrylable", _sourceJson.get("industrylable").toString());
                        }else {
                        	 _sourceJson.put("industrylable", "");
                        }
                        //文章分类
                        if (_sourceJson.containsKey("article_category")) {
                        	_sourceJson.put("article_category", _sourceJson.get("article_category").toString());
                        }else {
                        	 _sourceJson.put("article_category", "");
                        }
                        //相似文章数量
                        _sourceJson.put("num", 1);
                        JSONArray similarArray = JSON.parseArray(JSONObject.parseObject(redisUtil.getKey(key)).getString("data"));
                        for (Object object : similarArray) {
                        	JSONObject parseObject = JSONObject.parseObject(object.toString());
                        	if(parseObject.get("article_public_id").equals(_sourceJson.getString("article_public_id"))) {
                        		_sourceJson.put("num", Integer.parseInt(parseObject.getString("num")));
                        	}
						}
                        String key_words = _sourceJson.getString("key_words");
                        if (!key_words.equals("")) {
                            String sb = "";
                            JSONObject key_wordsJson = JSON.parseObject(key_words);
                            Integer index = 0;
                            for (Map.Entry entry : key_wordsJson.entrySet()) {
                                if (index < 5 && index >= 0) {
                                    String keywords = String.valueOf(entry.getKey());
                                    if (index < 4) {
                                        sb += keywords + "，";
                                    } else {
                                        sb += keywords;
                                    }
                                    index++;
                                } else {
                                    break;
                                }
                            }
                            if (sb.endsWith("，")) {
                                sb = sb.substring(0, sb.lastIndexOf("，"));
                            }
                            _sourceJson.put("key_words", sb);
                        }
                        dataArray.add(_sourceJson);
                    }
                    dataGroupJson.put("data", dataArray);
                    response.put("code", 200);
                    response.put("msg", "舆情列表es返回成功");
                    response.put("data", dataGroupJson);
                } else {
                    response.put("code", 500);
                    response.put("msg", "舆情列表es返回错误");
                    response.put("data", dataGroupJson);
                }
            } else {
                dataGroupJson.put("data", new JSONArray());
                dataGroupJson.put("totalPage", 1);
                dataGroupJson.put("totalCount", 0);
                dataGroupJson.put("currentPage", 1);
                response.put("code", 200);
                response.put("msg", "舆情列表es返回成功");
                response.put("data", dataGroupJson);
            }
        } else {
        	redisUtil.deleteKey(user_id);
            paramJson.put("esindex", "postal");
            paramJson.put("estype", "infor");
            String params = MapUtil.getUrlParamsByMap(paramJson);
            url = es_search_url + MonitorConstant.es_api_search_list;
            /**
             * 查询ES时间
             */
            System.out.println("ES查询开始时间：" + TimeUtil.getCurrenttime());
            String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
            System.out.println("ES查询结束开始时间：" + TimeUtil.getCurrenttime());
            if (!esResponse.equals("")) {
                JSONObject esResponseJson = JSON.parseObject(esResponse);
                String code = esResponseJson.getString("code");
                if (code.equals("200")) {
                    Integer page_count = esResponseJson.getInteger("page_count");
                    Integer count = esResponseJson.getInteger("count");
                    Integer page = esResponseJson.getInteger("page");
                    JSONArray esDataArray = esResponseJson.getJSONArray("data");
                    dataGroupJson.put("totalPage", page_count);
                    dataGroupJson.put("totalCount", count);
                    dataGroupJson.put("currentPage", page);
                    JSONArray dataArray = new JSONArray();
                    System.out.println("后台计算开始时间：" + TimeUtil.getCurrenttime());

                    for (int i = 0; i < esDataArray.size(); i++) {
                        JSONObject dataJson = (JSONObject) esDataArray.get(i);
                        JSONObject highlightJson = dataJson.getJSONObject("highlight"); // 高亮
                        
                        
                        JSONObject _sourceJson = dataJson.getJSONObject("_source");
                        String _score = dataJson.getString("_score");//分数
                        _sourceJson.put("_score", _score);
                        
                        Set<String> relatedWord = new HashSet<>();
                        if(projectType==2) {
                        	relatedWord = ProjectWordUtil
                                    .projectRelatedWord(_sourceJson.getString("title") + _sourceJson.getString("content"), subject_word, regional_word, character_word, event_word);
                            _sourceJson.put("relatedWord", relatedWord);
                        }else if(projectType==1) {
                        	relatedWord = ProjectWordUtil
                                    .CommononprojectRelatedWord(_sourceJson.getString("title") + _sourceJson.getString("content"), subject_word, regional_word, character_word, event_word);
                            _sourceJson.put("relatedWord", relatedWord);
                        }
                        String title = "";
                        if (highlightJson.containsKey("title")) {
                            title = highlightJson.getJSONArray("title").getString(0);
                            title = title.replace("\"", "");
                            _sourceJson.put("title", title);
                        }
                        String content = "";
                        if (highlightJson.containsKey("content")) {
                            content = highlightJson.getJSONArray("content").getString(0);
                            _sourceJson.put("content", content);
                        }
                        String ner = "";
                        if(_sourceJson.containsKey("ner")) {
                        	ner = _sourceJson.getString("ner");
                        }

                        title = _sourceJson.getString("title");
                        if (title.contains("_http://") || title.contains("_https://")) {
                            title = title.substring(0, title.indexOf("_"));
                            _sourceJson.put("title", title);
                        }
                        _sourceJson.put("title", title);

                        if (_sourceJson.containsKey("extend_string_one")) {
                            String extend_string_one = _sourceJson.getString("extend_string_one");
                            if (!extend_string_one.equals("")) {
                                JSONObject extend_string_oneJson = JSON.parseObject(extend_string_one);
                                try {
                                	 JSONArray imglist = extend_string_oneJson.getJSONArray("imglist");
                                     extend_string_oneJson.put("imglist", imglist);
                                     _sourceJson.put("extend_string_one", extend_string_oneJson);
								} catch (Exception e) {
									_sourceJson.put("extend_string_one", "");
								}
                               
                            }
                        } else {
                            _sourceJson.put("extend_string_one", "");
                        }
                        //事件标签
                        if (_sourceJson.containsKey("eventlable")) {
                        	_sourceJson.put("eventlable", _sourceJson.get("eventlable").toString());
                        }else {
                        	 _sourceJson.put("eventlable", "");
                        }
                        //行业标签
                        if (_sourceJson.containsKey("industrylable")) {
                        	_sourceJson.put("industrylable", _sourceJson.get("industrylable").toString());
                        }else {
                        	 _sourceJson.put("industrylable", "");
                        }
                        
                        
                        
                        
                        
                        String key_words = _sourceJson.getString("key_words");
                        if (!key_words.equals("")) {
                            String sb = "";
                            JSONObject key_wordsJson = JSON.parseObject(key_words);
                            Integer index = 0;
                            for (Map.Entry entry : key_wordsJson.entrySet()) {
                                if (index < 5 && index >= 0) {
                                    String keywords = String.valueOf(entry.getKey());
                                    if (index < 4) {
                                        sb += keywords + "，";
                                    } else {
                                        sb += keywords;
                                    }
                                    index++;
                                } else {
                                    break;
                                }
                            }
                            if (sb.endsWith("，")) {
                                sb = sb.substring(0, sb.lastIndexOf("，"));
                            }
                            _sourceJson.put("key_words", sb);
                        }
                        dataArray.add(_sourceJson);
                    }
                    System.out.println("后台计算结束时间：" + TimeUtil.getCurrenttime());
                    dataGroupJson.put("data", dataArray);
                    response.put("code", 200);
                    response.put("msg", "舆情列表es返回成功");
                    response.put("data", dataGroupJson);
                } else {
                    response.put("code", 500);
                    response.put("msg", "舆情列表es返回错误");
                    response.put("data", dataGroupJson);
                }
            } else {
                response.put("code", 500);
                response.put("msg", "舆情列表es返回错误");
                response.put("data", dataGroupJson);
            }
        }
        JSONObject dataResponseJson = response.getJSONObject("data");
//        if (dataResponseJson.containsKey("totalCount")) {
//            Integer totalCount = dataResponseJson.getInteger("totalCount");
//            if (totalCount > 5000) {
//                totalCount = 5000;
//            }
//            dataResponseJson.put("totalCount", totalCount);
//        }
        if (dataResponseJson.containsKey("totalPage")) {
            Integer totalPage = dataResponseJson.getInteger("totalPage");
            if (totalPage > 500) {
                totalPage = 500;
            }
            dataResponseJson.put("totalPage", totalPage);
        }
        return response;
    }
    
    
    
    /**
     * @param [paramJson]
     * @return com.alibaba.fastjson.JSONObject
     * @description: 获取文章列表数据 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/16 19:38 <br>
     * @author: huajiancheng <br>
     */

    @Override
    public JSONObject getArticleSynthesizeList(JSONObject paramJson) {
        JSONObject response = new JSONObject();
        JSONObject dataGroupJson = new JSONObject();
        Long projectid = paramJson.getLong("projectid");
        Map<String, Object> projectParamMap = new HashMap<String, Object>();
        projectParamMap.put("project_id", projectid);
        Map<String, Object> projectInfo = projectDao.getProjectInfo(projectParamMap);
        if (projectInfo == null) {
            response.put("code", 200);
            response.put("msg", "舆情列表es返回成功");
            dataGroupJson.put("data", new ArrayList<>());
            response.put("data", dataGroupJson);
            return response;
        }
        int projectType = (int) projectInfo.get("project_type");
        String subject_word = String.valueOf(projectInfo.get("subject_word"));
        String regional_word = String.valueOf(projectInfo.get("regional_word"));
        String character_word = String.valueOf(projectInfo.get("character_word"));
        String event_word = String.valueOf(projectInfo.get("event_word"));
        String stop_word = String.valueOf(projectInfo.get("stop_word"));
        String searchkeyword = paramJson.getString("searchkeyword");
        if (StringUtils.isNotBlank(subject_word)) subject_word = subject_word.trim();
        if (StringUtils.isNotBlank(stop_word)) stop_word = stop_word.trim();
        if (StringUtils.isNotBlank(searchkeyword)) searchkeyword = searchkeyword.trim();
        paramJson.put("keyword", subject_word);
        paramJson.put("stopword", stop_word);
        if (projectType == 2) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.highProjectKeyword(subject_word, regional_word, character_word, event_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        
        if (projectType == 1) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.QuickProjectKeyword(subject_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        
        
        
        Integer similar = paramJson.getInteger("similar");
        Integer matchingmode = paramJson.getInteger("matchingmode") - 1; // 关键词匹配规则
        JSONArray emotionArray = paramJson.getJSONArray("emotionalIndex");
        String emotionalIndex = StringUtils.join(emotionArray, ",");
        if (StringUtils.isBlank(emotionalIndex)) {
            emotionalIndex = "1,2,3";
        }
        Integer searchType = paramJson.getInteger("searchType");
        String times = "";
        String timee = "";
        Integer timeType = paramJson.getInteger("timeType");
        JSONObject timeJson = new JSONObject();
        switch (timeType) {
            case 1:
                timeJson = DateUtil.dateRoll(new Date(), Calendar.HOUR, -24);
                times = timeJson.getString("times");
                timee = timeJson.getString("timee");
                break;
            case 2:
                timeJson = DateUtil.getDifferOneDayTimes(0);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 3:
                timeJson = DateUtil.getDifferOneDayTimes(-1);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("times") + " 23:59:59";
                break;
            case 4:
                timeJson = DateUtil.getDifferOneDayTimes(-3);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 5:
                timeJson = DateUtil.getDifferOneDayTimes(-7);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 6:
                timeJson = DateUtil.getDifferOneDayTimes(-15);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 7:
                timeJson = DateUtil.getDifferOneDayTimes(-30);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 8:
                times = paramJson.getString("times") + " 00:00:00";
                timee = paramJson.getString("timee") + " 23:59:59";
                break;
        }
        paramJson.put("times", times);
        paramJson.put("timee", timee);
        paramJson.put("matchingmode", matchingmode);
        paramJson.put("emotionalIndex", emotionalIndex);
        paramJson.put("searchType", searchType);
        paramJson.put("size", 10);
        
        
        
        

        int precise = paramJson.getIntValue("precise");
        if (precise == 0) {
            // precise == 0 为精准关闭，即不传屏蔽词
            paramJson.put("stopword", "");
        }
        paramJson.remove("projectid");
        paramJson.remove("group_id");
        paramJson.remove("precise");
        paramJson.remove("similar");
        paramJson.remove("groupid");
        paramJson.remove("projectId");
        switch (searchType) {
            case 1:
                searchType = 1;
                break;
            case 2:
                searchType = 0;
                break;
            case 3:
                searchType = 2;
                break;
        }
        paramJson.put("searchType", searchType);
        // 查询es数据
        String url = "";

        if (similar == 1) {  // 合并
            Integer totalCount = 0;
            Integer currentPage = paramJson.getInteger("page");
            String article_public_idStr = paramJson.getString("article_public_idstr");
            if (article_public_idStr == null) {
                article_public_idStr = "";
            }
            if (currentPage == 1) {
                String params = MapUtil.getUrlParamsByMap(paramJson);
                String similarUrl = es_search_url + MonitorConstant.es_api_similar_titlekeyword_search_content;
                String esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);

                if (!esSimilarResponse.equals("")) {
                    List article_public_idList = new ArrayList();
                    JSONArray similarArray = JSON.parseArray(esSimilarResponse);
                    for (int i = 0; i < similarArray.size(); i++) {
                        JSONObject similarJson = (JSONObject) similarArray.get(i);
                        String article_public_id = similarJson.getString("article_public_id");
                        article_public_idList.add(article_public_id);
                        if (i < 10) {
                            article_public_idStr += article_public_id + ",";
                        }
                    }
                    totalCount = article_public_idList.size();
                    dataGroupJson.put("article_public_idList", article_public_idList);
                }
            }
            if (!article_public_idStr.equals("")) {
                paramJson.put("article_public_idstr", article_public_idStr);
                
                String params1 = MapUtil.getUrlParamsByMap(paramJson);
                url = es_search_url + MonitorConstant.es_api_similar_contentlist;
                String articleResponse = MyHttpRequestUtil.sendPostEsSearch(url, params1);
                JSONObject articleResponseJson = JSON.parseObject(articleResponse);
                String code = articleResponseJson.getString("code");
                if (code.equals("200")) {
                    Integer page_count = articleResponseJson.getInteger("page_count");
                    Integer count = articleResponseJson.getInteger("count");
                    Integer page = articleResponseJson.getInteger("page");
                    if (currentPage == 1) {
                        dataGroupJson.put("totalCount", totalCount);
                        if (totalCount % 10 == 0) {
                            page_count = totalCount / 10;
                        } else {
                            page_count = totalCount / 10 + 1;
                        }
                        dataGroupJson.put("totalPage", page_count);
                    } else {
                        Integer totalPage = paramJson.getInteger("totalPage");
                        totalCount = paramJson.getInteger("totalCount");
                        dataGroupJson.put("totalPage", totalPage);
                        dataGroupJson.put("totalCount", totalCount);
                    }
                    dataGroupJson.put("currentPage", page);
                    JSONArray esDataArray = articleResponseJson.getJSONArray("data");
                    JSONArray dataArray = new JSONArray();
                    for (int i = 0; i < esDataArray.size(); i++) {
                        JSONObject dataJson = (JSONObject) esDataArray.get(i);
                        JSONObject highlightJson = dataJson.getJSONObject("highlight"); // 高亮
                        JSONObject _sourceJson = dataJson.getJSONObject("_source");
                        Set<String> relatedWord = new HashSet<>();
                        relatedWord = ProjectWordUtil
                                .projectRelatedWord(_sourceJson.getString("title") + _sourceJson.getString("content"), subject_word, regional_word, character_word, event_word);
                        _sourceJson.put("relatedWord", relatedWord);
                        String title = "";
                        if (highlightJson.containsKey("title")) {
                            title = highlightJson.getJSONArray("title").getString(0);
                            _sourceJson.put("title", title);
                        }
                        String content = "";
                        if (highlightJson.containsKey("content")) {
                            content = highlightJson.getJSONArray("content").getString(0);
                            _sourceJson.put("content", content);
                        }
//                        content = content.replaceAll("\\s*", "");
//                        _sourceJson.put("content", content);
                        title = _sourceJson.getString("title");
                        if (title.contains("_http://") || title.contains("_https://")) {
                            title = title.substring(0, title.indexOf("_"));
                            _sourceJson.put("title", title);
                        }
//                        title = title.replaceAll("\\s*", "");
//                        _sourceJson.put("title", title);

                        if (_sourceJson.containsKey("extend_string_one")) {
                            String extend_string_one = _sourceJson.getString("extend_string_one");
                            if (!extend_string_one.equals("")) {
                                JSONObject extend_string_oneJson = JSON.parseObject(extend_string_one);
                                JSONArray imglist = extend_string_oneJson.getJSONArray("imglist");
                                extend_string_oneJson.put("imglist", imglist);
                                _sourceJson.put("extend_string_one", extend_string_oneJson);
                            }
                        } else {
                            _sourceJson.put("extend_string_one", "");
                        }
                        String key_words = _sourceJson.getString("key_words");
                        if (!key_words.equals("")) {
                            String sb = "";
                            JSONObject key_wordsJson = JSON.parseObject(key_words);
                            Integer index = 0;
                            for (Map.Entry entry : key_wordsJson.entrySet()) {
                                if (index < 5 && index >= 0) {
                                    String keywords = String.valueOf(entry.getKey());
                                    if (index < 4) {
                                        sb += keywords + "，";
                                    } else {
                                        sb += keywords;
                                    }
                                    index++;
                                } else {
                                    break;
                                }
                            }
                            if (sb.endsWith("，")) {
                                sb = sb.substring(0, sb.lastIndexOf("，"));
                            }
                            _sourceJson.put("key_words", sb);
                        }
                        dataArray.add(_sourceJson);
                    }
                    dataGroupJson.put("data", dataArray);
                    response.put("code", 200);
                    response.put("msg", "舆情列表es返回成功");
                    response.put("data", dataGroupJson);
                } else {
                    response.put("code", 500);
                    response.put("msg", "舆情列表es返回错误");
                    response.put("data", dataGroupJson);
                }
            } else {
                dataGroupJson.put("data", new JSONArray());
                dataGroupJson.put("totalPage", 1);
                dataGroupJson.put("totalCount", 0);
                dataGroupJson.put("currentPage", 1);
                response.put("code", 200);
                response.put("msg", "舆情列表es返回成功");
                response.put("data", dataGroupJson);
            }
        } else {
            paramJson.put("esindex", "postal");
            paramJson.put("estype", "infor");
            String params = MapUtil.getUrlParamsByMap(paramJson);
            url = es_search_url + MonitorConstant.es_api_search_list;
            /**
             * 查询ES时间
             */
            System.out.println("ES查询开始时间：" + TimeUtil.getCurrenttime());
            String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
            System.out.println("ES查询结束开始时间：" + TimeUtil.getCurrenttime());
            if (!esResponse.equals("")) {
                JSONObject esResponseJson = JSON.parseObject(esResponse);
                String code = esResponseJson.getString("code");
                if (code.equals("200")) {
                    Integer page_count = esResponseJson.getInteger("page_count");
                    Integer count = esResponseJson.getInteger("count");
                    Integer page = esResponseJson.getInteger("page");
                    JSONArray esDataArray = esResponseJson.getJSONArray("data");
                    dataGroupJson.put("totalPage", page_count);
                    dataGroupJson.put("totalCount", count);
                    dataGroupJson.put("currentPage", page);
                    JSONArray dataArray = new JSONArray();
                    System.out.println("后台计算开始时间：" + TimeUtil.getCurrenttime());

                    for (int i = 0; i < esDataArray.size(); i++) {
                        JSONObject dataJson = (JSONObject) esDataArray.get(i);
                        JSONObject highlightJson = dataJson.getJSONObject("highlight"); // 高亮
                        JSONObject _sourceJson = dataJson.getJSONObject("_source");
                        Set<String> relatedWord = new HashSet<>();
                        relatedWord = ProjectWordUtil
                                .projectRelatedWord(_sourceJson.getString("title") + _sourceJson.getString("content"), subject_word, regional_word, character_word, event_word);
                        _sourceJson.put("relatedWord", relatedWord);
                        String title = "";
                        if (highlightJson.containsKey("title")) {
                            title = highlightJson.getJSONArray("title").getString(0);
                            _sourceJson.put("title", title);
                        }
                        String content = "";
                        if (highlightJson.containsKey("content")) {
                            content = highlightJson.getJSONArray("content").getString(0);
                            _sourceJson.put("content", content);
                        }

                        title = _sourceJson.getString("title");
                        if (title.contains("_http://") || title.contains("_https://")) {
                            title = title.substring(0, title.indexOf("_"));
                            _sourceJson.put("title", title);
                        }
                        _sourceJson.put("title", title);

                        if (_sourceJson.containsKey("extend_string_one")) {
                            String extend_string_one = _sourceJson.getString("extend_string_one");
                            if (!extend_string_one.equals("")) {
                                JSONObject extend_string_oneJson = JSON.parseObject(extend_string_one);
                                JSONArray imglist = extend_string_oneJson.getJSONArray("imglist");
                                extend_string_oneJson.put("imglist", imglist);
                                _sourceJson.put("extend_string_one", extend_string_oneJson);
                            }
                        } else {
                            _sourceJson.put("extend_string_one", "");
                        }
                        String key_words = _sourceJson.getString("key_words");
                        if (!key_words.equals("")) {
                            String sb = "";
                            JSONObject key_wordsJson = JSON.parseObject(key_words);
                            Integer index = 0;
                            for (Map.Entry entry : key_wordsJson.entrySet()) {
                                if (index < 5 && index >= 0) {
                                    String keywords = String.valueOf(entry.getKey());
                                    if (index < 4) {
                                        sb += keywords + "，";
                                    } else {
                                        sb += keywords;
                                    }
                                    index++;
                                } else {
                                    break;
                                }
                            }
                            if (sb.endsWith("，")) {
                                sb = sb.substring(0, sb.lastIndexOf("，"));
                            }
                            _sourceJson.put("key_words", sb);
                        }
                        dataArray.add(_sourceJson);
                    }
                    System.out.println("后台计算结束时间：" + TimeUtil.getCurrenttime());
                    dataGroupJson.put("data", dataArray);
                    response.put("code", 200);
                    response.put("msg", "舆情列表es返回成功");
                    response.put("data", dataGroupJson);
                } else {
                    response.put("code", 500);
                    response.put("msg", "舆情列表es返回错误");
                    response.put("data", dataGroupJson);
                }
            } else {
                response.put("code", 500);
                response.put("msg", "舆情列表es返回错误");
                response.put("data", dataGroupJson);
            }
        }
        JSONObject dataResponseJson = response.getJSONObject("data");
//        if (dataResponseJson.containsKey("totalCount")) {
//            Integer totalCount = dataResponseJson.getInteger("totalCount");
//            if (totalCount > 5000) {
//                totalCount = 5000;
//            }
//            dataResponseJson.put("totalCount", totalCount);
//        }
        if (dataResponseJson.containsKey("totalPage")) {
            Integer totalPage = dataResponseJson.getInteger("totalPage");
            if (totalPage > 500) {
                totalPage = 500;
            }
            dataResponseJson.put("totalPage", totalPage);
        }
        return response;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    

    @Override
    public JSONObject getGroupNameById(JSONObject paramJson) {
        JSONObject responseJson = new JSONObject();
        try {
            Map<String, Object> paramMap = JSON.parseObject(paramJson.toJSONString());
            Map<String, Object> responseMap = projectDao.getGroupNameById(paramMap);
            JSONObject dataJson = new JSONObject(responseMap);
            responseJson.put("code", 200);
            responseJson.put("msg", "获取方案组成功");
            responseJson.put("data", dataJson);
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject dataJson = new JSONObject();
            responseJson.put("code", 500);
            responseJson.put("msg", "获取方案组失败");
            responseJson.put("data", dataJson);
        }
        return responseJson;
    }


    /**
     * @param [paramJson]
     * @return com.alibaba.fastjson.JSONObject
     * @description: 导出文章数据 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/17 19:00 <br>
     * @author: huajiancheng <br>
     */

    @Override
    public void exportArticleList(JSONObject paramJson, HttpServletResponse response, HttpServletRequest request) {
        try {
            String exportType = paramJson.getString("exportType");
            String projectid = paramJson.getString("projectid");

            String esResponse = "";
            String times = paramJson.getString("times") + " 00:00:00";
            String timee = paramJson.getString("timee") + " 00:00:00";
            
            Integer timeType = paramJson.getInteger("timeType");
            JSONObject timeJson = new JSONObject();
            switch (timeType) {
                case 1:
                    timeJson = DateUtil.dateRoll(new Date(), Calendar.HOUR, -24);
                    times = timeJson.getString("times");
                    timee = timeJson.getString("timee");
                    break;
                case 2:
                    timeJson = DateUtil.getDifferOneDayTimes(0);
                    times = timeJson.getString("times") + " 00:00:00";
                    timee = timeJson.getString("timee") + " 23:59:59";
                    break;
                case 3:
                    timeJson = DateUtil.getDifferOneDayTimes(-1);
                    times = timeJson.getString("times") + " 00:00:00";
                    timee = timeJson.getString("times") + " 23:59:59";
                    break;
                case 4:
                    timeJson = DateUtil.getDifferOneDayTimes(-3);
                    times = timeJson.getString("times") + " 00:00:00";
                    timee = timeJson.getString("timee") + " 23:59:59";
                    break;
                case 5:
                    timeJson = DateUtil.getDifferOneDayTimes(-7);
                    times = timeJson.getString("times") + " 00:00:00";
                    timee = timeJson.getString("timee") + " 23:59:59";
                    break;
                case 6:
                    timeJson = DateUtil.getDifferOneDayTimes(-15);
                    times = timeJson.getString("times") + " 00:00:00";
                    timee = timeJson.getString("timee") + " 23:59:59";
                    break;
                case 7:
                    timeJson = DateUtil.getDifferOneDayTimes(-30);
                    times = timeJson.getString("times") + " 00:00:00";
                    timee = timeJson.getString("timee") + " 23:59:59";
                    break;
                case 8:
                    times = paramJson.getString("times") + " 00:00:00";
                    timee = paramJson.getString("timee") + " 23:59:59";
                    break;
            }

            paramJson.put("times", times);
            paramJson.put("timee", timee);
            
            
            JSONArray eventArray = paramJson.getJSONArray("eventIndex");
            String eventlable = StringUtils.join(eventArray, ",");
           
            eventlable = eventlable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
            if(eventlable.endsWith(",")) {
            	eventlable = eventlable.substring(0, eventlable.length()-1);
            }
            paramJson.put("eventlable", eventlable);
            
            paramJson.remove("eventIndex");
            
            
            JSONArray industryArray = paramJson.getJSONArray("industryIndex");
            String industrylable = StringUtils.join(industryArray, ",");
            
            
            
            industrylable = industrylable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
            if(industrylable.endsWith(",")) {
            	industrylable = industrylable.substring(0, industrylable.length()-1);
            }
            paramJson.put("industrylable", industrylable);
            paramJson.remove("industryIndex");
            
            
            
            String province = "";
            JSONArray provinceArray = paramJson.getJSONArray("province");
            province = StringUtils.join(provinceArray, ",");
            if(!StringUtils.isEmpty(province)) {
            	province = province.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
            }else {
            	province = "";
            }
            if(province.endsWith(",")) {
            	province = province.substring(0, province.length()-1);
            }
            
            paramJson.put("province", province);
            
            
            
            JSONArray cityArray = paramJson.getJSONArray("city");
            String city = StringUtils.join(cityArray, ",");
            
            if(!StringUtils.isEmpty(city)) {
            	city = city.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
            }else {
            	city = "";
            }
            if(city.endsWith(",")) {
            	city = city.substring(0, city.length()-1);
            }
            paramJson.put("city", city);
            
            
          //机构类型
            JSONArray orgarray = paramJson.getJSONArray("organizationtype");
            String orgtypelist = StringUtils.join(orgarray, ",");
            if (StringUtils.isBlank(orgtypelist)) {
            	orgtypelist = "0";
            }
            
            paramJson.put("orgtypelist", orgtypelist);
            paramJson.remove("organizationtype");
            
            
            //文章分类
            JSONArray categorylabledataarray = paramJson.getJSONArray("categorylabledata");
            String categorylable = StringUtils.join(categorylabledataarray, ",");
            if (StringUtils.isBlank(categorylable)) {
            	categorylable = "0";
            }
            paramJson.put("categorylable", categorylable);
            paramJson.remove("categorylabledata");
            
            
            //企业类型
            
            JSONArray enterprisetypelistarray = paramJson.getJSONArray("enterprisetypelist");
            String enterprisetypelist = StringUtils.join(enterprisetypelistarray, ",");
            if (StringUtils.isBlank(enterprisetypelist)) {
            	enterprisetypelist = "0";
            }
            paramJson.put("enterprisetypelist", enterprisetypelist);
            
            
            
            //高科技型企业
            JSONArray hightechtypelistarray = paramJson.getJSONArray("hightechtypelist");
            String hightechtypelist = StringUtils.join(hightechtypelistarray, ",");
            if (StringUtils.isBlank(hightechtypelist)) {
            	hightechtypelist = "0";
            }
            paramJson.put("hightechtypelist", hightechtypelist);
            
           //政策
            JSONArray policylableflagarray = paramJson.getJSONArray("policylableflag");
            String policylableflag = StringUtils.join(policylableflagarray, ",");
            if (StringUtils.isBlank(policylableflag)) {
            	policylableflag = "0";
            }
            paramJson.put("policylableflag", policylableflag);

            //数据来源
            JSONArray classify = paramJson.getJSONArray("classify");
            String classifylist= StringUtils.join(classify, ",");
            if (StringUtils.isBlank(classifylist)) {
                classifylist = "0";
            }
            paramJson.put("classify", classifylist);


            //数据品类

            JSONArray datasource_type = paramJson.getJSONArray("datasource_type");
            String datasourceTypeShow= StringUtils.join(datasource_type, ",");
            if (StringUtils.isBlank(datasourceTypeShow)) {
                datasourceTypeShow = "0";
            }
            paramJson.put("datasource_type", datasourceTypeShow);


            JSONArray dataArray = new JSONArray();  // 导出的数据
            if (exportType.equals("1")) {  // 全部导出
                Map<String, Object> projectParamMap = new HashMap<String, Object>();
                projectParamMap.put("project_id", projectid);
                Map<String, Object> projectInfo = projectDao.getProjectInfo(projectParamMap);
                Integer projectType = Integer.parseInt(projectInfo.get("project_type").toString());
                
                String subject_word = String.valueOf(projectInfo.get("subject_word"));
                String regional_word = String.valueOf(projectInfo.get("regional_word"));
                String character_word = String.valueOf(projectInfo.get("character_word"));
                String event_word = String.valueOf(projectInfo.get("event_word"));
                String stop_word = String.valueOf(projectInfo.get("stop_word"));
                String searchkeyword = paramJson.getString("searchkeyword");
                
                
                if (projectType == 2) {
                    paramJson.put("projecttype", "2");
                    paramJson.put("keyword", ProjectWordUtil.highProjectKeyword(subject_word, regional_word, character_word, event_word));
                    paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
                }
                
                if (projectType == 1) {
                    paramJson.put("projecttype", "2");
                    paramJson.put("keyword", ProjectWordUtil.QuickProjectKeyword(subject_word));
                    paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
                }

                Integer similar = paramJson.getInteger("similar");

                Integer matchingmode = paramJson.getInteger("matchingmode") - 1; // 关键词匹配规则
                JSONArray emotionArray = paramJson.getJSONArray("emotionalIndex");
                String emotionalIndex = StringUtils.join(emotionArray, ",");
                Integer searchType = paramJson.getInteger("searchType");
                switch (searchType) {
                    case 1:
                        searchType = 1;
                        break;
                    case 2:
                        searchType = 0;
                        break;
                    case 3:
                        searchType = 2;
                        break;
                }

                paramJson.put("searchType", searchType);
                paramJson.put("matchingmode", matchingmode);
                paramJson.put("emotionalIndex", emotionalIndex);
                paramJson.put("searchType", searchType);
                paramJson.put("searchkeyword", searchkeyword);
                paramJson.remove("projectid");
                paramJson.remove("similar");
                paramJson.remove("groupid");

                if (similar == 1) {  // 合并
                    String params = MapUtil.getUrlParamsByMap(paramJson);
                    String similarUrl = es_search_url + MonitorConstant.es_api_similar_titlekeyword_search_content;
                    String esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
                    if (!esSimilarResponse.equals("")) {
                        List article_public_idList = new ArrayList();
                       JSONObject parseObject = JSONObject.parseObject(esSimilarResponse);
                        JSONArray similarArray = parseObject.getJSONArray("data");
                        String article_public_idStr = "";
                        for (int i = 0; i < similarArray.size(); i++) {
                            JSONObject similarJson = (JSONObject) similarArray.get(i);
                            String article_public_id = similarJson.getString("article_public_id");
                            article_public_idList.add(article_public_id);
                        }
                        String article_public_idstr = StringUtils.join(article_public_idList, ",");
                        paramJson.put("article_public_idstr", article_public_idstr);
                        paramJson.put("size", article_public_idList.size());
                        
                        String params1 = MapUtil.getUrlParamsByMap(paramJson);
                        String url = es_search_url + MonitorConstant.es_api_similar_contentlist;
                        String articleResponse = MyHttpRequestUtil.sendPostEsSearch(url, params1);
                        JSONObject articleResponseJson = JSON.parseObject(articleResponse);
                        String code = articleResponseJson.getString("code");
                        if (code.equals("200")) {
                            dataArray = articleResponseJson.getJSONArray("data");
                        }
                    }
                } else { //  非合并
                    paramJson.put("size", 5000);
                    String url = es_search_url + MonitorConstant.es_api_search_list;
                    String parsms = MapUtil.getUrlParamsByMap(paramJson);
                    esResponse = MyHttpRequestUtil.sendPostEsSearch(url, parsms);
                    JSONObject esResponseJson = JSON.parseObject(esResponse);
                    String code = esResponseJson.getString("code");
                    if (code.equals("200")) {
                        dataArray = esResponseJson.getJSONArray("data");
                    }
                }
            } else { // 部分导出
                JSONArray exportlist = paramJson.getJSONArray("exportlist");
                String article_public_idStr = StringUtils.join(exportlist, ",");
                Map<String, Object> map = new HashMap<>();
                map.put("article_public_idstr", article_public_idStr);
                map.put("jumptype", "1");
                map.put("page", "1");
                map.put("size", exportlist.size());
                map.put("times", times);
                map.put("timee", timee);
                

                String url = es_search_url + MonitorConstant.es_api_exportqbsearchconten;
                String parsms = MapUtil.getUrlParamsByMap(map);
                esResponse = MyHttpRequestUtil.sendPostEsSearch(url, parsms);
                JSONObject esResponseJson = JSON.parseObject(esResponse);
                String code = esResponseJson.getString("code");
                if (code.equals("200")) {
                    dataArray = esResponseJson.getJSONArray("data");
                }
            }

            String header = "序号,标题/微博内容,地址,媒体名称,发布日期,媒体类型,属性,摘要,实体";
            String fileName = projectid + ".xls";
            String sheetName = "数据监测";
            exportUtil.createExcel(header, fileName, sheetName, dataArray, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * @param
     * @return
     * @description: 获取用户是否含有方案 <br>
     * @version: 1.0 <br>
     * @date: 2020/7/13 17:36 <br>
     * @author: huajiancheng <br>
     */
    public boolean boolUserProjectByUserId(HttpServletRequest request) {
        boolean flag = false;
        JSONObject paramJson = new JSONObject();
        User user = userUtil.getuser(request);
        String user_id = String.valueOf(user.getUser_id());
        paramJson.put("del_status", 0);
        paramJson.put("user_id", user_id);
        Integer count = projectDao.getProjectCountById(paramJson);
        if (count <= 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 获取行业标签
     * @author lh
     * @date 2021/6/1 13:54
     * @param paramJson
     * @return com.alibaba.fastjson.JSONObject
     */
	@Override
	public JSONObject getArticleIndustryList(JSONObject paramJson) {
		JSONObject response = new JSONObject();
        JSONObject dataGroupJson = new JSONObject();
        Long projectid = paramJson.getLong("projectid");
        Map<String, Object> projectParamMap = new HashMap<String, Object>();
        projectParamMap.put("project_id", projectid);
        Map<String, Object> projectInfo = projectDao.getProjectInfo(projectParamMap);
        if (projectInfo == null) {
            response.put("code", 200);
            response.put("msg", "行业标签列表成功");
            dataGroupJson.put("data", new ArrayList<>());
            response.put("data", dataGroupJson);
            return response;
        }
        int projectType = (int) projectInfo.get("project_type");
        String subject_word = String.valueOf(projectInfo.get("subject_word"));
        String regional_word = String.valueOf(projectInfo.get("regional_word"));
        String character_word = String.valueOf(projectInfo.get("character_word"));
        String event_word = String.valueOf(projectInfo.get("event_word"));
        String stop_word = String.valueOf(projectInfo.get("stop_word"));
        String searchkeyword = paramJson.getString("searchkeyword");
        
        String user_id = projectInfo.get("user_id").toString();
        
        if (StringUtils.isNotBlank(subject_word)) subject_word = subject_word.trim();
        if (StringUtils.isNotBlank(stop_word)) stop_word = stop_word.trim();
        if (StringUtils.isNotBlank(searchkeyword)) searchkeyword = searchkeyword.trim();
        paramJson.put("keyword", subject_word);
        paramJson.put("stopword", stop_word);
        if (projectType == 2) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.highProjectKeyword(subject_word, regional_word, character_word, event_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        
        if (projectType == 1) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.QuickProjectKeyword(subject_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        Integer similar = paramJson.getInteger("similar");
        Integer matchingmode = paramJson.getInteger("matchingmode") - 1; // 关键词匹配规则
        JSONArray emotionArray = paramJson.getJSONArray("emotionalIndex");
        String emotionalIndex = StringUtils.join(emotionArray, ",");
        if (StringUtils.isBlank(emotionalIndex)) {
            emotionalIndex = "1,2,3";
        }
        Integer searchType = paramJson.getInteger("searchType");
        String times = "";
        String timee = "";
        Integer timeType = paramJson.getInteger("timeType");
        JSONObject timeJson = new JSONObject();
        switch (timeType) {
            case 1:
                timeJson = DateUtil.dateRoll(new Date(), Calendar.HOUR, -24);
                times = timeJson.getString("times");
                timee = timeJson.getString("timee");
                break;
            case 2:
                timeJson = DateUtil.getDifferOneDayTimes(0);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 3:
                timeJson = DateUtil.getDifferOneDayTimes(-1);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("times") + " 23:59:59";
                break;
            case 4:
                timeJson = DateUtil.getDifferOneDayTimes(-3);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 5:
                timeJson = DateUtil.getDifferOneDayTimes(-7);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 6:
                timeJson = DateUtil.getDifferOneDayTimes(-15);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 7:
                timeJson = DateUtil.getDifferOneDayTimes(-30);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 8:
                times = paramJson.getString("times") + " 00:00:00";
                timee = paramJson.getString("timee") + " 23:59:59";
                break;
        }
        paramJson.put("times", times);
        paramJson.put("timee", timee);
        paramJson.put("matchingmode", matchingmode);
        paramJson.put("emotionalIndex", emotionalIndex);
        paramJson.put("searchType", searchType);
        paramJson.put("size", 10);
        
        
        
        
        //事件
        JSONArray eventArray = paramJson.getJSONArray("eventIndex");
        String eventlable = StringUtils.join(eventArray, ",");
       
        eventlable = eventlable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        if(eventlable.endsWith(",")) {
        	eventlable = eventlable.substring(0, eventlable.length()-1);
        }
        paramJson.put("eventlable", eventlable);
        //2021.7.2
        paramJson.put("eventlable", "");
        
        paramJson.remove("eventIndex");
        
        //行业
        JSONArray industryArray = paramJson.getJSONArray("industryIndex");
        String industrylable = StringUtils.join(industryArray, ",");
        
        
        
        industrylable = industrylable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        if(industrylable.endsWith(",")) {
        	industrylable = industrylable.substring(0, industrylable.length()-1);
        }
        paramJson.put("industrylable", industrylable);
        //2021.7.1
        paramJson.put("industrylable", "");
        paramJson.remove("industryIndex");
        
        
        //省份
        String province = "";
        JSONArray provinceArray = paramJson.getJSONArray("province");
        province = StringUtils.join(provinceArray, ",");
        if(!StringUtils.isEmpty(province)) {
        	province = province.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        }else {
        	province = "";
        }
        if(province.endsWith(",")) {
        	province = province.substring(0, province.length()-1);
        }
        
        paramJson.put("province", province);
        //2021.7.2
        paramJson.put("province", "");
        
        
        //城市
        JSONArray cityArray = paramJson.getJSONArray("city");
        String city = StringUtils.join(cityArray, ",");
        
        if(!StringUtils.isEmpty(city)) {
        	city = city.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        }else {
        	city = "";
        }
        if(city.endsWith(",")) {
        	city = city.substring(0, city.length()-1);
        }
        paramJson.put("city", city);
        //2021.7.2
        paramJson.put("city", "");
        
        //机构类型
        JSONArray orgarray = paramJson.getJSONArray("organizationtype");
        String orgtypelist = StringUtils.join(orgarray, ",");
        if (StringUtils.isBlank(orgtypelist)) {
        	orgtypelist = "0";
        }
        
        paramJson.put("orgtypelist", orgtypelist);
        paramJson.remove("organizationtype");
        
        
        //文章分类
        JSONArray categorylabledataarray = paramJson.getJSONArray("categorylabledata");
        String categorylable = StringUtils.join(categorylabledataarray, ",");
        if (StringUtils.isBlank(categorylable)) {
        	categorylable = "0";
        }
        paramJson.put("categorylable", categorylable);
        paramJson.remove("categorylabledata");
        
        
        //企业类型
        
        JSONArray enterprisetypelistarray = paramJson.getJSONArray("enterprisetypelist");
        String enterprisetypelist = StringUtils.join(enterprisetypelistarray, ",");
        if (StringUtils.isBlank(enterprisetypelist)) {
        	enterprisetypelist = "0";
        }
        paramJson.put("enterprisetypelist", enterprisetypelist);
        
        
        
        //高科技型企业
        JSONArray hightechtypelistarray = paramJson.getJSONArray("hightechtypelist");
        String hightechtypelist = StringUtils.join(hightechtypelistarray, ",");
        if (StringUtils.isBlank(hightechtypelist)) {
        	hightechtypelist = "0";
        }
        paramJson.put("hightechtypelist", hightechtypelist);
        
       //政策
        JSONArray policylableflagarray = paramJson.getJSONArray("policylableflag");
        String policylableflag = StringUtils.join(policylableflagarray, ",");
        if (StringUtils.isBlank(policylableflag)) {
        	policylableflag = "0";
        }
        paramJson.put("policylableflag", policylableflag);



        //数据来源
        JSONArray classify = paramJson.getJSONArray("classify");
        String classifylist= StringUtils.join(classify, ",");
        if (StringUtils.isBlank(classifylist)) {
            orgtypelist = "0";
        }
        paramJson.put("classify", classifylist);


        /*数据品类*/
        JSONArray datasource_type = paramJson.getJSONArray("datasource_type");
        String dataCategoryList = StringUtils.join(datasource_type, ",");
        if (StringUtils.isBlank(dataCategoryList)){
            dataCategoryList = "0";
        }
        paramJson.put("datasource_type" , dataCategoryList);
        
        
        
        
        int precise = paramJson.getIntValue("precise");
        if (precise == 0) {
            // precise == 0 为精准关闭，即不传屏蔽词
            paramJson.put("stopword", "");
        }
        paramJson.remove("projectid");
        paramJson.remove("group_id");
        paramJson.remove("precise");
        paramJson.remove("similar");
        paramJson.remove("groupid");
        paramJson.remove("projectId");
        switch (searchType) {
            case 1:
                searchType = 1;
                break;
            case 2:
                searchType = 0;
                break;
            case 3:
                searchType = 2;
                break;
        }
        paramJson.put("searchType", searchType);
        // 查询es数据
        String url = "";
        similar = 0;
        if (similar == 1) {  // 合并
            Integer totalCount = 0;
            Integer currentPage = paramJson.getInteger("page");
            String article_public_idStr = paramJson.getString("article_public_idstr");
            if (article_public_idStr == null) {
                article_public_idStr = "";
            }
                String params = MapUtil.getUrlParamsByMap(paramJson);
                String similarUrl = es_search_url + MonitorConstant.es_api_similar_titlekeyword_search_content;
                //String esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
                
                
                
                String esSimilarResponse = null;
//                String key = redisUtil.getKey(user_id);
//                if(key!=null) {
//                	esSimilarResponse =key ;
//                }else {
//                	esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
//                }                                                                                                                                                                                                                                                                                                                                                                                                                               /*数据品类*/
                String key = projectid.toString()+paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword")+paramJson.getString("datasource_type")+timeType+matchingmode+searchType+emotionalIndex;
                //String key = projectid.toString()+paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword");
                if(redisUtil.existsKey(key)) {
                	esSimilarResponse = redisUtil.getKey(key);
                }else {
                	esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
                    redisUtil.set(key, esSimilarResponse);
                }
                
                

                if (!esSimilarResponse.equals("")) {
                    List article_public_idList = new ArrayList();
                    JSONArray similarArray = JSON.parseArray(esSimilarResponse);
                    for (int i = 0; i < similarArray.size(); i++) {
                        JSONObject similarJson = (JSONObject) similarArray.get(i);
                        String article_public_id = similarJson.getString("article_public_id");
                            article_public_idList.add(article_public_id);
                            article_public_idStr += article_public_id + ",";
                    }
                    totalCount = article_public_idList.size();
                    dataGroupJson.put("article_public_idList", article_public_idList);
            }
            if (!article_public_idStr.equals("")) {
                paramJson.put("article_public_idstr", article_public_idStr);
                paramJson.remove("keyword");
                paramJson.remove("stopword");
                String params1 = MapUtil.getUrlParamsByMap(paramJson);
                url = es_search_url + MonitorConstant.es_api_similar_industry_list;
                String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params1);
                JSONArray jsonArray = JSON.parseObject(esResponse).getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
                int total = Integer.parseInt(JSON.parseObject(esResponse).getJSONObject("hits").get("total").toString());

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", "total");
                jsonObject.put("doc_count", total);
                jsonArray.add(jsonObject);
                response.put("code", 200);
                response.put("msg", "行业标签列表成功");
                dataGroupJson.put("data", jsonArray);
                response.put("data", dataGroupJson);
                } else {
                    response.put("code", 500);
                    response.put("msg", "行业标签列表失败");
                    response.put("data", dataGroupJson);
                    return response;
                }
        }else {
        	paramJson.put("esindex", "postal");
            paramJson.put("estype", "infor");
            String params = MapUtil.getUrlParamsByMap(paramJson);
            url = es_search_url + MonitorConstant.es_api_search_industry_list;
            /**
             * 查询ES时间
             */
            System.out.println("ES查询开始时间：" + TimeUtil.getCurrenttime());
            String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
            System.out.println("ES查询结束开始时间：" + TimeUtil.getCurrenttime());
            if (!esResponse.equals("")) {
                JSONArray jsonArray = JSON.parseObject(esResponse).getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
                int total = Integer.parseInt(JSON.parseObject(esResponse).getJSONObject("hits").get("total").toString());
                
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", "total");
                jsonObject.put("doc_count", total);
                jsonArray.add(jsonObject);
                response.put("code", 200);
                response.put("msg", "行业标签列表成功");
                dataGroupJson.put("data", jsonArray);
                response.put("data", dataGroupJson);
			return response;
			}
        }
       return response;

}

	@Override
	public JSONObject getArticleEventList(JSONObject paramJson) {




		JSONObject response = new JSONObject();
        JSONObject dataGroupJson = new JSONObject();
        Long projectid = paramJson.getLong("projectid");
        Map<String, Object> projectParamMap = new HashMap<String, Object>();
        projectParamMap.put("project_id", projectid);
        Map<String, Object> projectInfo = projectDao.getProjectInfo(projectParamMap);
        if (projectInfo == null) {
            response.put("code", 200);
            response.put("msg", "行业标签列表成功");
            dataGroupJson.put("data", new ArrayList<>());
            response.put("data", dataGroupJson);
            return response;
        }
        int projectType = (int) projectInfo.get("project_type");
        String subject_word = String.valueOf(projectInfo.get("subject_word"));
        String regional_word = String.valueOf(projectInfo.get("regional_word"));
        String character_word = String.valueOf(projectInfo.get("character_word"));
        String event_word = String.valueOf(projectInfo.get("event_word"));
        String stop_word = String.valueOf(projectInfo.get("stop_word"));
        String searchkeyword = paramJson.getString("searchkeyword");
        
        String user_id = projectInfo.get("user_id").toString();
        
        if (StringUtils.isNotBlank(subject_word)) subject_word = subject_word.trim();
        if (StringUtils.isNotBlank(stop_word)) stop_word = stop_word.trim();
        if (StringUtils.isNotBlank(searchkeyword)) searchkeyword = searchkeyword.trim();
        paramJson.put("keyword", subject_word);
        paramJson.put("stopword", stop_word);
        if (projectType == 2) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.highProjectKeyword(subject_word, regional_word, character_word, event_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        
        if (projectType == 1) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.QuickProjectKeyword(subject_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        Integer similar = paramJson.getInteger("similar");
        Integer matchingmode = paramJson.getInteger("matchingmode") - 1; // 关键词匹配规则
        JSONArray emotionArray = paramJson.getJSONArray("emotionalIndex");
        String emotionalIndex = StringUtils.join(emotionArray, ",");
        if (StringUtils.isBlank(emotionalIndex)) {
            emotionalIndex = "1,2,3";
        }
        Integer searchType = paramJson.getInteger("searchType");
        String times = "";
        String timee = "";
        Integer timeType = paramJson.getInteger("timeType");
        JSONObject timeJson = new JSONObject();
        switch (timeType) {
            case 1:
                timeJson = DateUtil.dateRoll(new Date(), Calendar.HOUR, -24);
                times = timeJson.getString("times");
                timee = timeJson.getString("timee");
                break;
            case 2:
                timeJson = DateUtil.getDifferOneDayTimes(0);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 3:
                timeJson = DateUtil.getDifferOneDayTimes(-1);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("times") + " 23:59:59";
                break;
            case 4:
                timeJson = DateUtil.getDifferOneDayTimes(-3);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 5:
                timeJson = DateUtil.getDifferOneDayTimes(-7);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 6:
                timeJson = DateUtil.getDifferOneDayTimes(-15);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 7:
                timeJson = DateUtil.getDifferOneDayTimes(-30);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 8:
                times = paramJson.getString("times") + " 00:00:00";
                timee = paramJson.getString("timee") + " 23:59:59";
                break;
        }
        paramJson.put("times", times);
        paramJson.put("timee", timee);
        paramJson.put("matchingmode", matchingmode);
        paramJson.put("emotionalIndex", emotionalIndex);
        paramJson.put("searchType", searchType);
        paramJson.put("size", 10);
        
        
        JSONArray eventArray = paramJson.getJSONArray("eventIndex");
        String eventlable = StringUtils.join(eventArray, ",");
       
        eventlable = eventlable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        if(eventlable.endsWith(",")) {
        	eventlable = eventlable.substring(0, eventlable.length()-1);
        }
        paramJson.put("eventlable", eventlable);
        //2021.7.1
        paramJson.put("eventlable", "");
        paramJson.remove("eventIndex");
        
        
        JSONArray industryArray = paramJson.getJSONArray("industryIndex");
        String industrylable = StringUtils.join(industryArray, ",");
        
        
        
        industrylable = industrylable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        if(industrylable.endsWith(",")) {
        	industrylable = industrylable.substring(0, industrylable.length()-1);
        }
        paramJson.put("industrylable", industrylable);
        paramJson.remove("industryIndex");
        
        
        
        String province = "";
        JSONArray provinceArray = paramJson.getJSONArray("province");
        province = StringUtils.join(provinceArray, ",");
        if(!StringUtils.isEmpty(province)) {
        	province = province.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        }else {
        	province = "";
        }
        if(province.endsWith(",")) {
        	province = province.substring(0, province.length()-1);
        }
        
        paramJson.put("province", province);
        //2021.7.2
        paramJson.put("province", "");
        
        
        JSONArray cityArray = paramJson.getJSONArray("city");
        String city = StringUtils.join(cityArray, ",");
        
        if(!StringUtils.isEmpty(city)) {
        	city = city.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        }else {
        	city = "";
        }
        if(city.endsWith(",")) {
        	city = city.substring(0, city.length()-1);
        }
        paramJson.put("city", city);
        //2021.7.2
        paramJson.put("city", "");
        
        //机构类型
        JSONArray orgarray = paramJson.getJSONArray("organizationtype");
        String orgtypelist = StringUtils.join(orgarray, ",");
        if (StringUtils.isBlank(orgtypelist)) {
        	orgtypelist = "0";
        }
        
        paramJson.put("orgtypelist", orgtypelist);
        paramJson.remove("organizationtype");


        
        //文章分类
        JSONArray categorylabledataarray = paramJson.getJSONArray("categorylabledata");
        String categorylable = StringUtils.join(categorylabledataarray, ",");
        if (StringUtils.isBlank(categorylable)) {
        	categorylable = "0";
        }
        paramJson.put("categorylable", categorylable);
        paramJson.remove("categorylabledata");
        
        
        //企业类型
        
        JSONArray enterprisetypelistarray = paramJson.getJSONArray("enterprisetypelist");
        String enterprisetypelist = StringUtils.join(enterprisetypelistarray, ",");
        if (StringUtils.isBlank(enterprisetypelist)) {
        	enterprisetypelist = "0";
        }
        paramJson.put("enterprisetypelist", enterprisetypelist);
        
        
        
        //高科技型企业
        JSONArray hightechtypelistarray = paramJson.getJSONArray("hightechtypelist");
        String hightechtypelist = StringUtils.join(hightechtypelistarray, ",");
        if (StringUtils.isBlank(hightechtypelist)) {
        	hightechtypelist = "0";
        }
        paramJson.put("hightechtypelist", hightechtypelist);
        
       //政策
        JSONArray policylableflagarray = paramJson.getJSONArray("policylableflag");
        String policylableflag = StringUtils.join(policylableflagarray, ",");
        if (StringUtils.isBlank(policylableflag)) {
        	policylableflag = "0";
        }
        paramJson.put("policylableflag", policylableflag);

        //数据来源
        JSONArray classify = paramJson.getJSONArray("classify");
        String classifylist= StringUtils.join(classify, ",");
        if (StringUtils.isBlank(classifylist)) {
            orgtypelist = "0";
        }
        paramJson.put("classify", classifylist);

        /*数据品类*/
        JSONArray datasource_type = paramJson.getJSONArray("datasource_type");
        String dataCategoryList = StringUtils.join(datasource_type, ",");
        if (StringUtils.isBlank(dataCategoryList)){
            dataCategoryList = "0";
        }
        paramJson.put("datasource_type" , dataCategoryList);
        
        
        int precise = paramJson.getIntValue("precise");
        if (precise == 0) {
            // precise == 0 为精准关闭，即不传屏蔽词
            paramJson.put("stopword", "");
        }
        paramJson.remove("projectid");
        paramJson.remove("group_id");
        paramJson.remove("precise");
        paramJson.remove("similar");
        paramJson.remove("groupid");
        paramJson.remove("projectId");
        switch (searchType) {
            case 1:
                searchType = 1;
                break;
            case 2:
                searchType = 0;
                break;
            case 3:
                searchType = 2;
                break;
        }
        paramJson.put("searchType", searchType);
        // 查询es数据
        String url = "";
        similar = 0;
        if (similar == 1) {  // 合并
            Integer totalCount = 0;
            Integer currentPage = paramJson.getInteger("page");
            String article_public_idStr = paramJson.getString("article_public_idstr");
            if (article_public_idStr == null) {
                article_public_idStr = "";
            }
                String params = MapUtil.getUrlParamsByMap(paramJson);
                String similarUrl = es_search_url + MonitorConstant.es_api_similar_titlekeyword_search_content;
                //String esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
                
				                
				String esSimilarResponse = null;
                
//                String key = redisUtil.getKey(user_id);
//                if(key!=null) {
//                	esSimilarResponse =key ;
//                }else {
//                	esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
//                }
	            String key = projectid.toString()+paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword")+paramJson.getString("datasource_type")+timeType+matchingmode+searchType+emotionalIndex;
                //String key = projectid.toString()+paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword");
                if(redisUtil.existsKey(key)) {
                	esSimilarResponse = redisUtil.getKey(key);
                }else {
                	esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
                    redisUtil.set(key, esSimilarResponse);
                }
				
                

                if (!esSimilarResponse.equals("")) {
                    List article_public_idList = new ArrayList();
                    JSONArray similarArray = JSON.parseArray(esSimilarResponse);
                    for (int i = 0; i < similarArray.size(); i++) {
                        JSONObject similarJson = (JSONObject) similarArray.get(i);
                        String article_public_id = similarJson.getString("article_public_id");
                        article_public_idList.add(article_public_id);
                            article_public_idStr += article_public_id + ",";
                    }
                    totalCount = article_public_idList.size();
                    dataGroupJson.put("article_public_idList", article_public_idList);
            }
            if (!article_public_idStr.equals("")) {
	                paramJson.put("article_public_idstr", article_public_idStr);
	                paramJson.remove("keyword");
	                paramJson.remove("stopword");
	                String params1 = MapUtil.getUrlParamsByMap(paramJson);
	                url = es_search_url + MonitorConstant.es_api_similar_event_list;
	                String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params1);
	                JSONArray jsonArray = JSON.parseObject(esResponse).getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
	                int total = Integer.parseInt(JSON.parseObject(esResponse).getJSONObject("hits").get("total").toString());
	                JSONObject jsonObject = new JSONObject();
	                jsonObject.put("key", "total");
	                jsonObject.put("doc_count", total);
	                jsonArray.add(jsonObject);
	                response.put("code", 200);
	                response.put("msg", "行业标签列表成功");
	                dataGroupJson.put("data", jsonArray);
	                response.put("data", dataGroupJson);
                    return response;
                } else {
                    response.put("code", 500);
                    response.put("msg", "行业标签列表失败");
                    response.put("data", dataGroupJson);
                    return response;
                }
        }else {
        	paramJson.put("esindex", "postal");
            paramJson.put("estype", "infor");
            String params = MapUtil.getUrlParamsByMap(paramJson);
            url = es_search_url + MonitorConstant.es_api_search_event_list;
            /**
             * 查询ES时间
             */
            System.out.println("ES查询开始时间：" + TimeUtil.getCurrenttime());
            String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
            System.out.println("ES查询结束开始时间：" + TimeUtil.getCurrenttime());
            if (!esResponse.equals("")) {
                JSONArray jsonArray = JSON.parseObject(esResponse).getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
                int total = Integer.parseInt(JSON.parseObject(esResponse).getJSONObject("hits").get("total").toString());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", "total");
                jsonObject.put("doc_count", total);
                jsonArray.add(jsonObject);
                response.put("code", 200);
                response.put("msg", "行业标签列表成功");
                dataGroupJson.put("data", jsonArray);
                response.put("data", dataGroupJson);
				return response;
			}
        }
			return response;
	}

	@Override
	public JSONObject getArticleProvinceList(JSONObject paramJson) {
		

		JSONObject response = new JSONObject();
        JSONObject dataGroupJson = new JSONObject();
        Long projectid = paramJson.getLong("projectid");
        Map<String, Object> projectParamMap = new HashMap<String, Object>();
        projectParamMap.put("project_id", projectid);
        Map<String, Object> projectInfo = projectDao.getProjectInfo(projectParamMap);
        if (projectInfo == null) {
            response.put("code", 200);
            response.put("msg", "行业标签列表成功");
            dataGroupJson.put("data", new ArrayList<>());
            response.put("data", dataGroupJson);
            return response;
        }
        int projectType = (int) projectInfo.get("project_type");
        String subject_word = String.valueOf(projectInfo.get("subject_word"));
        String regional_word = String.valueOf(projectInfo.get("regional_word"));
        String character_word = String.valueOf(projectInfo.get("character_word"));
        String event_word = String.valueOf(projectInfo.get("event_word"));
        String stop_word = String.valueOf(projectInfo.get("stop_word"));
        String searchkeyword = paramJson.getString("searchkeyword");
        if (StringUtils.isNotBlank(subject_word)) subject_word = subject_word.trim();
        if (StringUtils.isNotBlank(stop_word)) stop_word = stop_word.trim();
        if (StringUtils.isNotBlank(searchkeyword)) searchkeyword = searchkeyword.trim();
        paramJson.put("keyword", subject_word);
        paramJson.put("stopword", stop_word);
        String user_id = projectInfo.get("user_id").toString();
        
        if (projectType == 2) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.highProjectKeyword(subject_word, regional_word, character_word, event_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        
        if (projectType == 1) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.QuickProjectKeyword(subject_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        Integer similar = paramJson.getInteger("similar");
        Integer matchingmode = paramJson.getInteger("matchingmode") - 1; // 关键词匹配规则
        JSONArray emotionArray = paramJson.getJSONArray("emotionalIndex");
        String emotionalIndex = StringUtils.join(emotionArray, ",");
        if (StringUtils.isBlank(emotionalIndex)) {
            emotionalIndex = "1,2,3";
        }
        Integer searchType = paramJson.getInteger("searchType");
        String times = "";
        String timee = "";
        Integer timeType = paramJson.getInteger("timeType");
        JSONObject timeJson = new JSONObject();
        switch (timeType) {
            case 1:
                timeJson = DateUtil.dateRoll(new Date(), Calendar.HOUR, -24);
                times = timeJson.getString("times");
                timee = timeJson.getString("timee");
                break;
            case 2:
                timeJson = DateUtil.getDifferOneDayTimes(0);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 3:
                timeJson = DateUtil.getDifferOneDayTimes(-1);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("times") + " 23:59:59";
                break;
            case 4:
                timeJson = DateUtil.getDifferOneDayTimes(-3);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 5:
                timeJson = DateUtil.getDifferOneDayTimes(-7);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 6:
                timeJson = DateUtil.getDifferOneDayTimes(-15);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 7:
                timeJson = DateUtil.getDifferOneDayTimes(-30);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 8:
                times = paramJson.getString("times") + " 00:00:00";
                timee = paramJson.getString("timee") + " 23:59:59";
                break;
        }
        paramJson.put("times", times);
        paramJson.put("timee", timee);
        paramJson.put("matchingmode", matchingmode);
        paramJson.put("emotionalIndex", emotionalIndex);
        paramJson.put("searchType", searchType);
        paramJson.put("size", 10);
        
        
        JSONArray eventArray = paramJson.getJSONArray("eventIndex");
        String eventlable = StringUtils.join(eventArray, ",");
       
        eventlable = eventlable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        if(eventlable.endsWith(",")) {
        	eventlable = eventlable.substring(0, eventlable.length()-1);
        }
        paramJson.put("eventlable", eventlable);
        
        paramJson.remove("eventIndex");
        
        
        JSONArray industryArray = paramJson.getJSONArray("industryIndex");
        String industrylable = StringUtils.join(industryArray, ",");
        
        
        
        industrylable = industrylable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        if(industrylable.endsWith(",")) {
        	industrylable = industrylable.substring(0, industrylable.length()-1);
        }
        paramJson.put("industrylable", industrylable);
        paramJson.remove("industryIndex");
        
        
        
        String province = "";
        JSONArray provinceArray = paramJson.getJSONArray("province");
        province = StringUtils.join(provinceArray, ",");
        if(!StringUtils.isEmpty(province)) {
        	province = province.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        }else {
        	province = "";
        }
        if(province.endsWith(",")) {
        	province = province.substring(0, province.length()-1);
        }
        
        paramJson.put("province", province);
        //2021.7.2
        paramJson.put("province", "");
        
        
        
        JSONArray cityArray = paramJson.getJSONArray("city");
        String city = StringUtils.join(cityArray, ",");
        
        if(!StringUtils.isEmpty(city)) {
        	city = city.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        }else {
        	city = "";
        }
        if(city.endsWith(",")) {
        	city = city.substring(0, city.length()-1);
        }
        paramJson.put("city", city);
        //2021.7.2
        paramJson.put("city", "");
        
        //机构类型
        JSONArray orgarray = paramJson.getJSONArray("organizationtype");
        String orgtypelist = StringUtils.join(orgarray, ",");
        if (StringUtils.isBlank(orgtypelist)) {
        	orgtypelist = "0";
        }
        
        paramJson.put("orgtypelist", orgtypelist);
        paramJson.remove("organizationtype");
        
        
        //文章分类
        JSONArray categorylabledataarray = paramJson.getJSONArray("categorylabledata");
        String categorylable = StringUtils.join(categorylabledataarray, ",");
        if (StringUtils.isBlank(categorylable)) {
        	categorylable = "0";
        }
        paramJson.put("categorylable", categorylable);
        paramJson.remove("categorylabledata");
        
        
        //企业类型
        
        JSONArray enterprisetypelistarray = paramJson.getJSONArray("enterprisetypelist");
        String enterprisetypelist = StringUtils.join(enterprisetypelistarray, ",");
        if (StringUtils.isBlank(enterprisetypelist)) {
        	enterprisetypelist = "0";
        }
        paramJson.put("enterprisetypelist", enterprisetypelist);
        
        
        
        //高科技型企业
        JSONArray hightechtypelistarray = paramJson.getJSONArray("hightechtypelist");
        String hightechtypelist = StringUtils.join(hightechtypelistarray, ",");
        if (StringUtils.isBlank(hightechtypelist)) {
        	hightechtypelist = "0";
        }
        paramJson.put("hightechtypelist", hightechtypelist);
        
       //政策
        JSONArray policylableflagarray = paramJson.getJSONArray("policylableflag");
        String policylableflag = StringUtils.join(policylableflagarray, ",");
        if (StringUtils.isBlank(policylableflag)) {
        	policylableflag = "0";
        }
        paramJson.put("policylableflag", policylableflag);

        //数据来源
        JSONArray classify = paramJson.getJSONArray("classify");
        String classifylist= StringUtils.join(classify, ",");
        if (StringUtils.isBlank(classifylist)) {
            orgtypelist = "0";
        }
        paramJson.put("classify", classifylist);



        /*数据品类*/
        JSONArray datasource_type = paramJson.getJSONArray("datasource_type");
        String dataCategoryList = StringUtils.join(datasource_type, ",");
        if (StringUtils.isBlank(dataCategoryList)){
            dataCategoryList = "0";
        }
        paramJson.put("datasource_type" , dataCategoryList);

        
        int precise = paramJson.getIntValue("precise");
        if (precise == 0) {
            // precise == 0 为精准关闭，即不传屏蔽词
            paramJson.put("stopword", "");
        }
        paramJson.remove("projectid");
        paramJson.remove("group_id");
        paramJson.remove("precise");
        paramJson.remove("similar");
        paramJson.remove("groupid");
        paramJson.remove("projectId");
        switch (searchType) {
            case 1:
                searchType = 1;
                break;
            case 2:
                searchType = 0;
                break;
            case 3:
                searchType = 2;
                break;
        }
        paramJson.put("searchType", searchType);
        // 查询es数据
        String url = "";
        similar = 0;
        if (similar == 1) {  // 合并
            Integer totalCount = 0;
            Integer currentPage = paramJson.getInteger("page");
            String article_public_idStr = paramJson.getString("article_public_idstr");
            if (article_public_idStr == null) {
                article_public_idStr = "";
            }
                String params = MapUtil.getUrlParamsByMap(paramJson);
                String similarUrl = es_search_url + MonitorConstant.es_api_similar_titlekeyword_search_content;
                //String esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
                
                
				String esSimilarResponse = null;
                
//                String key = redisUtil.getKey(user_id);
//                if(key!=null) {
//                	esSimilarResponse =key ;
//                }else {
//                	esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
//                }
                //String key = projectid.toString()+paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword");                                                          数据品类
	            String key = projectid.toString()+paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword")+paramJson.getString("datasource_type")+timeType+matchingmode+searchType+emotionalIndex;
				if(redisUtil.existsKey(key)) {
                	esSimilarResponse = redisUtil.getKey(key);
                }else {
                	esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
                    redisUtil.set(key, esSimilarResponse);
                }
				
                
                

                if (!esSimilarResponse.equals("")) {
                    List article_public_idList = new ArrayList();
                    JSONArray similarArray = JSON.parseArray(esSimilarResponse);
                    for (int i = 0; i < similarArray.size(); i++) {
                        JSONObject similarJson = (JSONObject) similarArray.get(i);
                        String article_public_id = similarJson.getString("article_public_id");
                        article_public_idList.add(article_public_id);
                            article_public_idStr += article_public_id + ",";
                    }
                    totalCount = article_public_idList.size();
                    dataGroupJson.put("article_public_idList", article_public_idList);
            }
            if (!article_public_idStr.equals("")) {
	                paramJson.put("article_public_idstr", article_public_idStr);
	                paramJson.remove("keyword");
	                paramJson.remove("stopword");
	                String params1 = MapUtil.getUrlParamsByMap(paramJson);
	                url = es_search_url + MonitorConstant.es_api_similar_province_list;
	                String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params1);
	                JSONArray jsonArray = JSON.parseObject(esResponse).getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
	                int total = Integer.parseInt(JSON.parseObject(esResponse).getJSONObject("hits").get("total").toString());
	
	                JSONObject jsonObject = new JSONObject();
	                jsonObject.put("key", "total");
	                jsonObject.put("doc_count", total);
	                jsonArray.add(jsonObject);
	                response.put("code", 200);
	                response.put("msg", "行业标签列表成功");
	                dataGroupJson.put("data", jsonArray);
	                response.put("data", dataGroupJson);
                } else {
                    response.put("code", 500);
                    response.put("msg", "行业标签列表失败");
                    response.put("data", dataGroupJson);
                }
        }else {
        	paramJson.put("esindex", "postal");
            paramJson.put("estype", "infor");
            String params = MapUtil.getUrlParamsByMap(paramJson);
            url = es_search_url + MonitorConstant.es_api_search_province_list;
            /**
             * 查询ES时间
             */
            System.out.println("ES查询开始时间：" + TimeUtil.getCurrenttime());
            String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
            System.out.println("ES查询结束开始时间：" + TimeUtil.getCurrenttime());
            if (!esResponse.equals("")) {
                JSONArray jsonArray = JSON.parseObject(esResponse).getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
                int total = Integer.parseInt(JSON.parseObject(esResponse).getJSONObject("hits").get("total").toString());
                
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", "total");
                jsonObject.put("doc_count", total);
                jsonArray.add(jsonObject);
                response.put("code", 200);
                response.put("msg", "行业标签列表成功");
                dataGroupJson.put("data", jsonArray);
                response.put("data", dataGroupJson);
			}
        }
			return response;
		
		
	}

	@Override
	public JSONObject getArticleCityList(JSONObject paramJson) {

		JSONObject response = new JSONObject();
        JSONObject dataGroupJson = new JSONObject();
        Long projectid = paramJson.getLong("projectid");
        Map<String, Object> projectParamMap = new HashMap<String, Object>();
        projectParamMap.put("project_id", projectid);
        Map<String, Object> projectInfo = projectDao.getProjectInfo(projectParamMap);
        if (projectInfo == null) {
            response.put("code", 200);
            response.put("msg", "城市标签列表成功");
            dataGroupJson.put("data", new ArrayList<>());
            response.put("data", dataGroupJson);
            return response;
        }
        int projectType = (int) projectInfo.get("project_type");
        String subject_word = String.valueOf(projectInfo.get("subject_word"));
        String regional_word = String.valueOf(projectInfo.get("regional_word"));
        String character_word = String.valueOf(projectInfo.get("character_word"));
        String event_word = String.valueOf(projectInfo.get("event_word"));
        String stop_word = String.valueOf(projectInfo.get("stop_word"));
        String searchkeyword = paramJson.getString("searchkeyword");
        if (StringUtils.isNotBlank(subject_word)) subject_word = subject_word.trim();
        if (StringUtils.isNotBlank(stop_word)) stop_word = stop_word.trim();
        if (StringUtils.isNotBlank(searchkeyword)) searchkeyword = searchkeyword.trim();
        String user_id = projectInfo.get("user_id").toString();
        paramJson.put("keyword", subject_word);
        paramJson.put("stopword", stop_word);
        if (projectType == 2) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.highProjectKeyword(subject_word, regional_word, character_word, event_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        
        if (projectType == 1) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.QuickProjectKeyword(subject_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        Integer similar = paramJson.getInteger("similar");
        Integer matchingmode = paramJson.getInteger("matchingmode") - 1; // 关键词匹配规则
        JSONArray emotionArray = paramJson.getJSONArray("emotionalIndex");
        
        String emotionalIndex = StringUtils.join(emotionArray, ",");
        if (StringUtils.isBlank(emotionalIndex)) {
            emotionalIndex = "1,2,3";
        }
        Integer searchType = paramJson.getInteger("searchType");
        String times = "";
        String timee = "";
        Integer timeType = paramJson.getInteger("timeType");
        JSONObject timeJson = new JSONObject();
        switch (timeType) {
            case 1:
                timeJson = DateUtil.dateRoll(new Date(), Calendar.HOUR, -24);
                times = timeJson.getString("times");
                timee = timeJson.getString("timee");
                break;
            case 2:
                timeJson = DateUtil.getDifferOneDayTimes(0);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 3:
                timeJson = DateUtil.getDifferOneDayTimes(-1);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("times") + " 23:59:59";
                break;
            case 4:
                timeJson = DateUtil.getDifferOneDayTimes(-3);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 5:
                timeJson = DateUtil.getDifferOneDayTimes(-7);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 6:
                timeJson = DateUtil.getDifferOneDayTimes(-15);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 7:
                timeJson = DateUtil.getDifferOneDayTimes(-30);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 8:
                times = paramJson.getString("times") + " 00:00:00";
                timee = paramJson.getString("timee") + " 23:59:59";
                break;
        }
        paramJson.put("times", times);
        paramJson.put("timee", timee);
        paramJson.put("matchingmode", matchingmode);
        paramJson.put("emotionalIndex", emotionalIndex);
        paramJson.put("searchType", searchType);
        paramJson.put("size", 10);
        
        
        JSONArray eventArray = paramJson.getJSONArray("eventIndex");
        String eventlable = StringUtils.join(eventArray, ",");
       
        eventlable = eventlable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        if(eventlable.endsWith(",")) {
        	eventlable = eventlable.substring(0, eventlable.length()-1);
        }
        paramJson.put("eventlable", eventlable);
        
        paramJson.remove("eventIndex");
        
        
        JSONArray industryArray = paramJson.getJSONArray("industryIndex");
        String industrylable = StringUtils.join(industryArray, ",");
        
        
        
        industrylable = industrylable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        if(industrylable.endsWith(",")) {
        	industrylable = industrylable.substring(0, industrylable.length()-1);
        }
        paramJson.put("industrylable", industrylable);
        paramJson.remove("industryIndex");
        
        
        
        String province = "";
        JSONArray provinceArray = paramJson.getJSONArray("province");
        province = StringUtils.join(provinceArray, ",");
        if(!StringUtils.isEmpty(province)) {
        	province = province.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        }else {
        	province = "";
        }
        if(province.endsWith(",")) {
        	province = province.substring(0, province.length()-1);
        }
        
        paramJson.put("province", province);
        
        
        
        JSONArray cityArray = paramJson.getJSONArray("city");
        String city = StringUtils.join(cityArray, ",");
        
        if(!StringUtils.isEmpty(city)) {
        	city = city.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        }else {
        	city = "";
        }
        if(city.endsWith(",")) {
        	city = city.substring(0, city.length()-1);
        }
        paramJson.put("city", city);
        //2021.7.1
        paramJson.put("city", "");
        //机构类型
        JSONArray orgarray = paramJson.getJSONArray("organizationtype");
        String orgtypelist = StringUtils.join(orgarray, ",");
        if (StringUtils.isBlank(orgtypelist)) {
        	orgtypelist = "0";
        }
        
        paramJson.put("orgtypelist", orgtypelist);
        paramJson.remove("organizationtype");
        
        
        //文章分类
        JSONArray categorylabledataarray = paramJson.getJSONArray("categorylabledata");
        String categorylable = StringUtils.join(categorylabledataarray, ",");
        if (StringUtils.isBlank(categorylable)) {
        	categorylable = "0";
        }
        paramJson.put("categorylable", categorylable);
        paramJson.remove("categorylabledata");
        
        
        //企业类型
        
        JSONArray enterprisetypelistarray = paramJson.getJSONArray("enterprisetypelist");
        String enterprisetypelist = StringUtils.join(enterprisetypelistarray, ",");
        if (StringUtils.isBlank(enterprisetypelist)) {
        	enterprisetypelist = "0";
        }
        paramJson.put("enterprisetypelist", enterprisetypelist);
        
        
        
        //高科技型企业
        JSONArray hightechtypelistarray = paramJson.getJSONArray("hightechtypelist");
        String hightechtypelist = StringUtils.join(hightechtypelistarray, ",");
        if (StringUtils.isBlank(hightechtypelist)) {
        	hightechtypelist = "0";
        }
        paramJson.put("hightechtypelist", hightechtypelist);
        
       //政策
        JSONArray policylableflagarray = paramJson.getJSONArray("policylableflag");
        String policylableflag = StringUtils.join(policylableflagarray, ",");
        if (StringUtils.isBlank(policylableflag)) {
        	policylableflag = "0";
        }
        paramJson.put("policylableflag", policylableflag);

        //数据来源
        JSONArray classify = paramJson.getJSONArray("classify");
        String classifylist= StringUtils.join(classify, ",");
        if (StringUtils.isBlank(classifylist)) {
            orgtypelist = "0";
        }
        paramJson.put("classify", classifylist);


        /*数据品类*/
        JSONArray datasource_type = paramJson.getJSONArray("datasource_type");
        String dataCategoryList = StringUtils.join(datasource_type, ",");
        if (StringUtils.isBlank(dataCategoryList)){
            dataCategoryList = "0";
        }
        paramJson.put("datasource_type" , dataCategoryList);



        int precise = paramJson.getIntValue("precise");
        if (precise == 0) {
            // precise == 0 为精准关闭，即不传屏蔽词
            paramJson.put("stopword", "");
        }
        paramJson.remove("projectid");
        paramJson.remove("group_id");
        paramJson.remove("precise");
        paramJson.remove("similar");
        paramJson.remove("groupid");
        paramJson.remove("projectId");
        switch (searchType) {
            case 1:
                searchType = 1;
                break;
            case 2:
                searchType = 0;
                break;
            case 3:
                searchType = 2;
                break;
        }
        paramJson.put("searchType", searchType);
        // 查询es数据
        String url = "";
        similar = 0;
        if (similar == 1) {  // 合并
            Integer totalCount = 0;
            Integer currentPage = paramJson.getInteger("page");
            String article_public_idStr = paramJson.getString("article_public_idstr");
            if (article_public_idStr == null) {
                article_public_idStr = "";
            }
                String params = MapUtil.getUrlParamsByMap(paramJson);
                String similarUrl = es_search_url + MonitorConstant.es_api_similar_titlekeyword_search_content;
                //String esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
                String esSimilarResponse = null;
                
//                String key = redisUtil.getKey(user_id);
//                if(key!=null) {
//                	esSimilarResponse =key ;
//                }else {
//                	esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
//                }
                
                //String key = projectid.toString()+paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword");
                String key = projectid.toString()+paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword")+timeType+matchingmode+searchType+emotionalIndex;
                if(redisUtil.existsKey(key)) {
                	esSimilarResponse = redisUtil.getKey(key);
                }else {
                	esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
                    redisUtil.set(key, esSimilarResponse);
                }
                
                

                if (!esSimilarResponse.equals("")) {
                    List article_public_idList = new ArrayList();
                    JSONArray similarArray = JSON.parseArray(esSimilarResponse);
                    for (int i = 0; i < similarArray.size(); i++) {
                        JSONObject similarJson = (JSONObject) similarArray.get(i);
                        String article_public_id = similarJson.getString("article_public_id");
                        article_public_idList.add(article_public_id);
                            article_public_idStr += article_public_id + ",";
                    }
                    totalCount = article_public_idList.size();
                    dataGroupJson.put("article_public_idList", article_public_idList);
            }
            if (!article_public_idStr.equals("")) {
	                paramJson.put("article_public_idstr", article_public_idStr);
	                paramJson.remove("keyword");
	                paramJson.remove("stopword");
	                String params1 = MapUtil.getUrlParamsByMap(paramJson);
	                url = es_search_url + MonitorConstant.es_api_similar_city_list;
	                String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params1);
	                JSONArray jsonArray = JSON.parseObject(esResponse).getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
	                int total = Integer.parseInt(JSON.parseObject(esResponse).getJSONObject("hits").get("total").toString());
	
	                JSONObject jsonObject = new JSONObject();
	                jsonObject.put("key", "total");
	                jsonObject.put("doc_count", total);
	                jsonArray.add(jsonObject);
	                response.put("code", 200);
	                response.put("msg", "行业标签列表成功");
	                dataGroupJson.put("data", jsonArray);
	                response.put("data", dataGroupJson);
                    return response;
                } else {
                    response.put("code", 500);
                    response.put("msg", "行业标签列表失败");
                    response.put("data", dataGroupJson);
                    return response;
                }
        }else {
        	paramJson.put("esindex", "postal");
            paramJson.put("estype", "infor");
            String params = MapUtil.getUrlParamsByMap(paramJson);
            url = es_search_url + MonitorConstant.es_api_search_city_list;
            /**
             * 查询ES时间
             */
            System.out.println("ES查询开始时间：" + TimeUtil.getCurrenttime());
            String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
            System.out.println("ES查询结束开始时间：" + TimeUtil.getCurrenttime());
            if (!esResponse.equals("")) {
                JSONArray jsonArray = JSON.parseObject(esResponse).getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
                int total = Integer.parseInt(JSON.parseObject(esResponse).getJSONObject("hits").get("total").toString());
                
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", "total");
                jsonObject.put("doc_count", total);
                jsonArray.add(jsonObject);
                response.put("code", 200);
                response.put("msg", "行业标签列表成功");
                dataGroupJson.put("data", jsonArray);
                response.put("data", dataGroupJson);
			return response;
			}
        }

			return response;
		
		
		
	}
	 /**
     * @param [paramJson]
     * @return com.alibaba.fastjson.JSONObject
     * @description: 获取文章列表数据 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/16 19:38 <br>
     * @author: huajiancheng <br>
     */

    @Override
    public JSONObject getAnalysisArticleList(JSONObject paramJson) {
        JSONObject response = new JSONObject();
        JSONObject dataGroupJson = new JSONObject();
        Long projectid = paramJson.getLong("projectid");
        Map<String, Object> projectParamMap = new HashMap<String, Object>();
        projectParamMap.put("project_id", projectid);
        Map<String, Object> projectInfo = projectDao.getProjectInfo(projectParamMap);
        if (projectInfo == null) {
            response.put("code", 200);
            response.put("msg", "舆情列表es返回成功");
            dataGroupJson.put("data", new ArrayList<>());
            response.put("data", dataGroupJson);
            return response;
        }
        int projectType = (int) projectInfo.get("project_type");
        String subject_word = String.valueOf(projectInfo.get("subject_word"));
        String regional_word = String.valueOf(projectInfo.get("regional_word"));
        String character_word = String.valueOf(projectInfo.get("character_word"));
        String event_word = String.valueOf(projectInfo.get("event_word"));
        String stop_word = String.valueOf(projectInfo.get("stop_word"));
        String searchkeyword = paramJson.getString("searchkeyword");
        String user_id = projectInfo.get("user_id").toString();
        if (StringUtils.isNotBlank(subject_word)) subject_word = subject_word.trim();
        if (StringUtils.isNotBlank(stop_word)) stop_word = stop_word.trim();
        if (StringUtils.isNotBlank(searchkeyword)) searchkeyword = searchkeyword.trim();
//        if (projectid == 1) {
//            paramJson.put("keyword", searchkeyword);
//            paramJson.put("searchkeyword", "");
//        } else {
//            paramJson.put("keyword", subject_word);
//        }
        paramJson.put("keyword", subject_word);
        paramJson.put("stopword", stop_word);
        if (projectType == 2) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.highProjectKeyword(subject_word, regional_word, character_word, event_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        
        if (projectType == 1) {
            paramJson.put("projecttype", "2");
            paramJson.put("keyword", ProjectWordUtil.QuickProjectKeyword(subject_word));
            paramJson.put("stopword", ProjectWordUtil.highProjectStopword(stop_word));
        }
        Integer similar = paramJson.getInteger("similar");
        Integer matchingmode = paramJson.getInteger("matchingmode") - 1; // 关键词匹配规则
        JSONArray emotionArray = paramJson.getJSONArray("emotionalIndex");
        String emotionalIndex = StringUtils.join(emotionArray, ",");
        if (StringUtils.isBlank(emotionalIndex)) {
            emotionalIndex = "1,2,3";
        }
        //机构类型
        JSONArray orgarray = paramJson.getJSONArray("organizationtype");
        String orgtypelist = StringUtils.join(orgarray, ",");
        if (StringUtils.isBlank(orgtypelist)) {
        	orgtypelist = "0";
        }
        
        paramJson.put("orgtypelist", orgtypelist);
        paramJson.remove("organizationtype");
        
        
        //文章分类
        JSONArray categorylabledataarray = paramJson.getJSONArray("categorylabledata");
        String categorylable = StringUtils.join(categorylabledataarray, ",");
        if (StringUtils.isBlank(categorylable)) {
        	categorylable = "0";
        }
        paramJson.put("categorylable", categorylable);
        paramJson.remove("categorylabledata");
        
        
        //企业类型
        
        JSONArray enterprisetypelistarray = paramJson.getJSONArray("enterprisetypelist");
        String enterprisetypelist = StringUtils.join(enterprisetypelistarray, ",");
        if (StringUtils.isBlank(enterprisetypelist)) {
        	enterprisetypelist = "0";
        }
        paramJson.put("enterprisetypelist", enterprisetypelist);
        
        
        
        //高科技型企业
        JSONArray hightechtypelistarray = paramJson.getJSONArray("hightechtypelist");
        String hightechtypelist = StringUtils.join(hightechtypelistarray, ",");
        if (StringUtils.isBlank(hightechtypelist)) {
        	hightechtypelist = "0";
        }
        paramJson.put("hightechtypelist", hightechtypelist);
        
       //政策
        JSONArray policylableflagarray = paramJson.getJSONArray("policylableflag");
        String policylableflag = StringUtils.join(policylableflagarray, ",");
        if (StringUtils.isBlank(policylableflag)) {
        	policylableflag = "0";
        }
        paramJson.put("policylableflag", policylableflag);


        //数据来源
        JSONArray classify = paramJson.getJSONArray("classify");
        String classifylist= StringUtils.join(classify, ",");
        if (StringUtils.isBlank(classifylist)) {
            orgtypelist = "0";
        }
        paramJson.put("classify", classifylist);


        /*数据品类*/
        JSONArray datasource_type = paramJson.getJSONArray("datasource_type");
        String dataCategoryList = StringUtils.join(datasource_type, ",");
        if (StringUtils.isBlank(dataCategoryList)){
            dataCategoryList = "0";
        }
        paramJson.put("datasource_type" , dataCategoryList);


        Integer searchType = paramJson.getInteger("searchType");
        String times = "";
        String timee = "";
        Integer timeType = paramJson.getInteger("timeType");
        JSONObject timeJson = new JSONObject();
        switch (timeType) {
            case 1:
                timeJson = DateUtil.dateRoll(new Date(), Calendar.HOUR, -24);
                times = timeJson.getString("times");
                timee = timeJson.getString("timee");
                break;
            case 2:
                timeJson = DateUtil.getDifferOneDayTimes(0);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 3:
                timeJson = DateUtil.getDifferOneDayTimes(-1);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("times") + " 23:59:59";
                break;
            case 4:
                timeJson = DateUtil.getDifferOneDayTimes(-3);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 5:
                timeJson = DateUtil.getDifferOneDayTimes(-7);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 6:
                timeJson = DateUtil.getDifferOneDayTimes(-15);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 7:
                timeJson = DateUtil.getDifferOneDayTimes(-30);
                times = timeJson.getString("times") + " 00:00:00";
                timee = timeJson.getString("timee") + " 23:59:59";
                break;
            case 8:
                times = paramJson.getString("times") + " 00:00:00";
                timee = paramJson.getString("timee") + " 23:59:59";
                break;
        }
        paramJson.put("times", times);
        paramJson.put("timee", timee);
        paramJson.put("matchingmode", matchingmode);
        paramJson.put("emotionalIndex", emotionalIndex);
        paramJson.put("searchType", searchType);
        
        Integer size = paramJson.getInteger("size");
        
        paramJson.put("size", size);
        
        JSONArray eventArray = paramJson.getJSONArray("eventIndex");
        String eventlable = StringUtils.join(eventArray, ",");
       
        eventlable = eventlable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        if(eventlable.endsWith(",")) {
        	eventlable = eventlable.substring(0, eventlable.length()-1);
        }
        paramJson.put("eventlable", eventlable);
        
        paramJson.remove("eventIndex");
        
        
        JSONArray industryArray = paramJson.getJSONArray("industryIndex");
        String industrylable = StringUtils.join(industryArray, ",");
        
        
        
        industrylable = industrylable.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        if(industrylable.endsWith(",")) {
        	industrylable = industrylable.substring(0, industrylable.length()-1);
        }
        paramJson.put("industrylable", industrylable);
        paramJson.remove("industryIndex");
        
        
        
        String province = "";
        JSONArray provinceArray = paramJson.getJSONArray("province");
        province = StringUtils.join(provinceArray, ",");
        if(!StringUtils.isEmpty(province)) {
        	province = province.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        }else {
        	province = "";
        }
        if(province.endsWith(",")) {
        	province = province.substring(0, province.length()-1);
        }
        
        paramJson.put("province", province);
        
        
        
        JSONArray cityArray = paramJson.getJSONArray("city");
        String city = StringUtils.join(cityArray, ",");
        
        if(!StringUtils.isEmpty(city)) {
        	city = city.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
        }else {
        	city = "";
        }
        if(city.endsWith(",")) {
        	city = city.substring(0, city.length()-1);
        }
        paramJson.put("city", city);
        // 更新偏好设置值
        @SuppressWarnings("unused")
        //Integer opinionCount = opinionConditionService.updateOpinionConditionByMap(paramJson);
        int precise = paramJson.getIntValue("precise");
        if (precise == 0) {
            // precise == 0 为精准关闭，即不传屏蔽词
            paramJson.put("stopword", "");
        }
        paramJson.remove("projectid");
        paramJson.remove("group_id");
        paramJson.remove("precise");
        paramJson.remove("similar");
        paramJson.remove("groupid");
        paramJson.remove("projectId");
        switch (searchType) {
            case 1:
                searchType = 1;
                break;
            case 2:
                searchType = 0;
                break;
            case 3:
                searchType = 2;
                break;
        }
        paramJson.put("searchType", searchType);
        // 查询es数据
        String url = "";

        if (similar == 1) {  // 合并
            Integer totalCount = 0;
            Integer totalNum = 0;
            Integer currentPage = paramJson.getInteger("page");
            String article_public_idStr = paramJson.getString("article_public_idstr");
            if (article_public_idStr == null) {
                article_public_idStr = "";
            }
            /*String key = projectid.toString()+paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword")+timeType+matchingmode+searchType+emotionalIndex+searchkeyword;*/
            /*2021.6.25修改*/                                                                                                                                                                                                                                                                                                                                                                                                                                                /*数据品类*/
            String key = projectid.toString()+paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword")+ paramJson.getString("classify") + paramJson.getString("datasource_type")+paramJson.getString("sourceWebsite") +paramJson.getString("author")+timeType+matchingmode+searchType+emotionalIndex+searchkeyword;

            /*2021.6.25修改*/
            if (currentPage == 1) {
                String params = MapUtil.getUrlParamsByMap(paramJson);
                String similarUrl = es_search_url + MonitorConstant.es_api_similar_titlekeyword_search_content_by_num_five;
                String esSimilarResponse = null;
                if(redisUtil.existsKey(key)) {
                	esSimilarResponse = redisUtil.getKey(key);
                }else {
                	esSimilarResponse = MyHttpRequestUtil.sendPostEsSearch(similarUrl, params);
                    redisUtil.filteritemset(key, esSimilarResponse);
                }
                if (!esSimilarResponse.equals("")) {
                    List article_public_idList = new ArrayList();
                    JSONObject parseObject = JSONObject.parseObject(esSimilarResponse);
                    //JSONArray similarArray = JSON.parseArray(esSimilarResponse);
                    JSONArray similarArray = parseObject.getJSONArray("data");
                    for (int i = 0; i < similarArray.size(); i++) {
                        JSONObject similarJson = (JSONObject) similarArray.get(i);
                        String article_public_id = similarJson.getString("article_public_id");
                        article_public_idList.add(article_public_id);
                        if (i < 30) {
                            article_public_idStr += article_public_id + ",";
                        }
                    }
                    totalCount = article_public_idList.size();
                    
                    totalNum = parseObject.getInteger("totalnum");
                    dataGroupJson.put("article_public_idList", article_public_idList);
                }
            }
            if (!article_public_idStr.equals("")) {
                paramJson.put("article_public_idstr", article_public_idStr);
                paramJson.remove("stopword");
                String params1 = MapUtil.getUrlParamsByMap(paramJson);
                url = es_search_url + MonitorConstant.es_api_similar_contentlist;
                String articleResponse = MyHttpRequestUtil.sendPostEsSearch(url, params1);
                JSONObject articleResponseJson = JSON.parseObject(articleResponse);
                String code = articleResponseJson.getString("code");
                if (code.equals("200")) {
                    Integer page_count = articleResponseJson.getInteger("page_count");
                    Integer count = articleResponseJson.getInteger("count");
                    Integer page = articleResponseJson.getInteger("page");
                    if (currentPage == 1) {
                        dataGroupJson.put("totalCount", totalCount);
                        dataGroupJson.put("totalNum", JSONObject.parseObject(redisUtil.getKey(key)).getString("totalnum"));
                        if (totalCount % 5 == 0) {
                            page_count = totalCount / 5;
                        } else {
                            page_count = totalCount / 5 + 1;
                        }
                        dataGroupJson.put("totalPage", page_count);
                    } else {
                        Integer totalPage = paramJson.getInteger("totalPage");
                        totalCount = paramJson.getInteger("totalCount");
                        dataGroupJson.put("totalPage", totalPage);
                        dataGroupJson.put("totalCount", totalCount);
                        dataGroupJson.put("totalNum", JSONObject.parseObject(redisUtil.getKey(key)).getString("totalnum"));
                    }
                    dataGroupJson.put("currentPage", page);
                    JSONArray esDataArray = articleResponseJson.getJSONArray("data");
                    JSONArray dataArray = new JSONArray();
                    for (int i = 0; i < esDataArray.size(); i++) {
                        JSONObject dataJson = (JSONObject) esDataArray.get(i);
                        JSONObject highlightJson = dataJson.getJSONObject("highlight"); // 高亮
                        JSONObject _sourceJson = dataJson.getJSONObject("_source");
                        
                        String _score = dataJson.getString("_score");//分数
                        DecimalFormat df = new DecimalFormat("#.00");
                        _sourceJson.put("_score", df.format(Double.parseDouble(_score)));
                        
                        Set<String> relatedWord = new HashSet<>();
                        if(projectType==2) {
                        	relatedWord = ProjectWordUtil
                                    .projectRelatedWord(_sourceJson.getString("title") + _sourceJson.getString("content"), subject_word, regional_word, character_word, event_word);
                            _sourceJson.put("relatedWord", relatedWord);
                        }else if(projectType==1) {
                        	relatedWord = ProjectWordUtil
                                    .CommononprojectRelatedWord(_sourceJson.getString("title") + _sourceJson.getString("content"), subject_word, regional_word, character_word, event_word);
                            _sourceJson.put("relatedWord", relatedWord);
                        }
                        
                        String title = "";
                        if (highlightJson.containsKey("title")) {
                            title = highlightJson.getJSONArray("title").getString(0);
                            
                            
                            //title = ProjectWordUtil.highlightcontent(title, subject_word, regional_word, character_word, event_word);
                            _sourceJson.put("title", Jsoup.parse(title).text());
                            
                           // _sourceJson.put("title", title);
                        }
                        String content = "";
                        if (highlightJson.containsKey("content")) {
                            content = highlightJson.getJSONArray("content").getString(0);
                           // content = ProjectWordUtil.highlightcontent(content, subject_word, regional_word, character_word, event_word);
                            _sourceJson.put("content", Jsoup.parse(content).text());
                           // _sourceJson.put("content", content);
                        }
//                        content = content.replaceAll("\\s*", "");
//                        _sourceJson.put("content", content);
                        title = _sourceJson.getString("title");
                        if (title.contains("_http://") || title.contains("_https://")) {
                            title = title.substring(0, title.indexOf("_"));
                            _sourceJson.put("title", title);
                        }
//                        title = title.replaceAll("\\s*", "");
//                        _sourceJson.put("title", title);

                        if (_sourceJson.containsKey("extend_string_one")) {
                            String extend_string_one = _sourceJson.getString("extend_string_one");
                            if (!extend_string_one.equals("")) {
                                JSONObject extend_string_oneJson = JSON.parseObject(extend_string_one);
                                JSONArray imglist = extend_string_oneJson.getJSONArray("imglist");
                                extend_string_oneJson.put("imglist", imglist);
                                _sourceJson.put("extend_string_one", extend_string_oneJson);
                            }
                        } else {
                            _sourceJson.put("extend_string_one", "");
                        }
                      //事件标签
                        if (_sourceJson.containsKey("eventlable")) {
                        	_sourceJson.put("eventlable", _sourceJson.get("eventlable").toString());
                        }else {
                        	 _sourceJson.put("eventlable", "");
                        }
                        //行业标签
                        if (_sourceJson.containsKey("industrylable")) {
                        	_sourceJson.put("industrylable", _sourceJson.get("industrylable").toString());
                        }else {
                        	 _sourceJson.put("industrylable", "");
                        }
                        //文章分类
                        if (_sourceJson.containsKey("article_category")) {
                        	_sourceJson.put("article_category", _sourceJson.get("article_category").toString());
                        }else {
                        	 _sourceJson.put("article_category", "");
                        }
                        //相似文章数量
                        _sourceJson.put("num", 1);
                        JSONArray similarArray = JSON.parseArray(JSONObject.parseObject(redisUtil.getKey(key)).getString("data"));
                        for (Object object : similarArray) {
                        	JSONObject parseObject = JSONObject.parseObject(object.toString());
                        	if(parseObject.get("article_public_id").equals(_sourceJson.getString("article_public_id"))) {
                        		_sourceJson.put("num", Integer.parseInt(parseObject.getString("num")));
                        	}
						}
                        String key_words = _sourceJson.getString("key_words");
                        if (!key_words.equals("")) {
                            String sb = "";
                            JSONObject key_wordsJson = JSON.parseObject(key_words);
                            Integer index = 0;
                            for (Map.Entry entry : key_wordsJson.entrySet()) {
                                if (index < 5 && index >= 0) {
                                    String keywords = String.valueOf(entry.getKey());
                                    if (index < 4) {
                                        sb += keywords + "，";
                                    } else {
                                        sb += keywords;
                                    }
                                    index++;
                                } else {
                                    break;
                                }
                            }
                            if (sb.endsWith("，")) {
                                sb = sb.substring(0, sb.lastIndexOf("，"));
                            }
                            _sourceJson.put("key_words", sb);
                        }
                        dataArray.add(_sourceJson);
                    }
                    dataGroupJson.put("data", dataArray);
                    response.put("code", 200);
                    response.put("msg", "舆情列表es返回成功");
                    response.put("data", dataGroupJson);
                } else {
                    response.put("code", 500);
                    response.put("msg", "舆情列表es返回错误");
                    response.put("data", dataGroupJson);
                }
            } else {
                dataGroupJson.put("data", new JSONArray());
                dataGroupJson.put("totalPage", 1);
                dataGroupJson.put("totalCount", 0);
                dataGroupJson.put("currentPage", 1);
                response.put("code", 200);
                response.put("msg", "舆情列表es返回成功");
                response.put("data", dataGroupJson);
            }
        } else {
        	redisUtil.deleteKey(user_id);
            paramJson.put("esindex", "postal");
            paramJson.put("estype", "infor");
            String params = MapUtil.getUrlParamsByMap(paramJson);
            url = es_search_url + MonitorConstant.es_api_search_list;
            /**
             * 查询ES时间
             */
            System.out.println("ES查询开始时间：" + TimeUtil.getCurrenttime());
            String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
            System.out.println("ES查询结束开始时间：" + TimeUtil.getCurrenttime());
            if (!esResponse.equals("")) {
                JSONObject esResponseJson = JSON.parseObject(esResponse);
                String code = esResponseJson.getString("code");
                if (code.equals("200")) {
                    Integer page_count = esResponseJson.getInteger("page_count");
                    Integer count = esResponseJson.getInteger("count");
                    Integer page = esResponseJson.getInteger("page");
                    JSONArray esDataArray = esResponseJson.getJSONArray("data");
                    dataGroupJson.put("totalPage", page_count);
                    dataGroupJson.put("totalCount", count);
                    dataGroupJson.put("currentPage", page);
                    JSONArray dataArray = new JSONArray();
                    System.out.println("后台计算开始时间：" + TimeUtil.getCurrenttime());

                    for (int i = 0; i < esDataArray.size(); i++) {
                        JSONObject dataJson = (JSONObject) esDataArray.get(i);
                        JSONObject highlightJson = dataJson.getJSONObject("highlight"); // 高亮
                        JSONObject _sourceJson = dataJson.getJSONObject("_source");
                        String _score = dataJson.getString("_score");//分数
                        DecimalFormat df = new DecimalFormat("#.00");
                        _sourceJson.put("_score", df.format(Double.parseDouble(_score)));
                        
                        
                        Set<String> relatedWord = new HashSet<>();
                        if(projectType==2) {
                        	relatedWord = ProjectWordUtil
                                    .projectRelatedWord(_sourceJson.getString("title") + _sourceJson.getString("content"), subject_word, regional_word, character_word, event_word);
                            _sourceJson.put("relatedWord", relatedWord);
                        }else if(projectType==1) {
                        	relatedWord = ProjectWordUtil
                                    .CommononprojectRelatedWord(_sourceJson.getString("title") + _sourceJson.getString("content"), subject_word, regional_word, character_word, event_word);
                            _sourceJson.put("relatedWord", relatedWord);
                        }
                        String title = "";
                        if (highlightJson.containsKey("title")) {
                            title = highlightJson.getJSONArray("title").getString(0);
                            _sourceJson.put("title", title);
                        }
                        String content = "";
                        if (highlightJson.containsKey("content")) {
                            content = highlightJson.getJSONArray("content").getString(0);
                            _sourceJson.put("content", content);
                        }
                        String ner = "";
                        if(_sourceJson.containsKey("ner")) {
                        	ner = _sourceJson.getString("ner");
                        }

                        title = _sourceJson.getString("title");
                        if (title.contains("_http://") || title.contains("_https://")) {
                            title = title.substring(0, title.indexOf("_"));
                            _sourceJson.put("title", title);
                        }
                        _sourceJson.put("title", title);

                        if (_sourceJson.containsKey("extend_string_one")) {
                            String extend_string_one = _sourceJson.getString("extend_string_one");
                            if (!extend_string_one.equals("")) {
                                JSONObject extend_string_oneJson = JSON.parseObject(extend_string_one);
                                JSONArray imglist = extend_string_oneJson.getJSONArray("imglist");
                                extend_string_oneJson.put("imglist", imglist);
                                _sourceJson.put("extend_string_one", extend_string_oneJson);
                            }
                        } else {
                            _sourceJson.put("extend_string_one", "");
                        }
                        //事件标签
                        if (_sourceJson.containsKey("eventlable")) {
                        	_sourceJson.put("eventlable", _sourceJson.get("eventlable").toString());
                        }else {
                        	 _sourceJson.put("eventlable", "");
                        }
                        //行业标签
                        if (_sourceJson.containsKey("industrylable")) {
                        	_sourceJson.put("industrylable", _sourceJson.get("industrylable").toString());
                        }else {
                        	 _sourceJson.put("industrylable", "");
                        }
                        
                        _sourceJson.put("title", Jsoup.parse(_sourceJson.get("title").toString()).text());
                        
                        _sourceJson.put("content", Jsoup.parse(_sourceJson.get("content").toString()).text());
                        
                        String key_words = _sourceJson.getString("key_words");
                        if (!key_words.equals("")) {
                            String sb = "";
                            JSONObject key_wordsJson = JSON.parseObject(key_words);
                            Integer index = 0;
                            for (Map.Entry entry : key_wordsJson.entrySet()) {
                                if (index < 5 && index >= 0) {
                                    String keywords = String.valueOf(entry.getKey());
                                    if (index < 4) {
                                        sb += keywords + "，";
                                    } else {
                                        sb += keywords;
                                    }
                                    index++;
                                } else {
                                    break;
                                }
                            }
                            if (sb.endsWith("，")) {
                                sb = sb.substring(0, sb.lastIndexOf("，"));
                            }
                            _sourceJson.put("key_words", sb);
                        }
                        dataArray.add(_sourceJson);
                    }
                    System.out.println("后台计算结束时间：" + TimeUtil.getCurrenttime());
                    dataGroupJson.put("data", dataArray);
                    response.put("code", 200);
                    response.put("msg", "舆情列表es返回成功");
                    response.put("data", dataGroupJson);
                } else {
                    response.put("code", 500);
                    response.put("msg", "舆情列表es返回错误");
                    response.put("data", dataGroupJson);
                }
            } else {
                response.put("code", 500);
                response.put("msg", "舆情列表es返回错误");
                response.put("data", dataGroupJson);
            }
        }
        JSONObject dataResponseJson = response.getJSONObject("data");
//        if (dataResponseJson.containsKey("totalCount")) {
//            Integer totalCount = dataResponseJson.getInteger("totalCount");
//            if (totalCount > 5000) {
//                totalCount = 5000;
//            }
//            dataResponseJson.put("totalCount", totalCount);
//        }
        if (dataResponseJson.containsKey("totalPage")) {
            Integer totalPage = dataResponseJson.getInteger("totalPage");
            if (totalPage > 500) {
                totalPage = 500;
            }
            dataResponseJson.put("totalPage", totalPage);
        }
        return response;
    }
	
	
	
	}