package com.stonedt.intelligence.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.constant.MonitorConstant;
import com.stonedt.intelligence.dao.ProjectDao;
import com.stonedt.intelligence.service.LSearchService;
import com.stonedt.intelligence.service.OpinionConditionService;
import com.stonedt.intelligence.service.ProjectService;
import com.stonedt.intelligence.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lh
 * @date 2021-05-31 18:14
 */
@Service
public class LSearchServiceImpl implements LSearchService {

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


    @Override
    public JSONObject getArticleSynthesizeList(JSONObject paramJson) {
        return null;
    }

    /**
     * 根据行业查询
     * @author lh
     * @date 2021/5/31 18:16
     * @param paramJson
     * @return com.alibaba.fastjson.JSONObject
     */
    @Override
    public JSONObject getArticleIndustryList(JSONObject paramJson) {

        JSONObject response = new JSONObject();
        JSONObject dataGroupJson = new JSONObject();

        String searchword = paramJson.getString("searchword");
        if (StringUtils.isNotBlank(searchword)) searchword = searchword.trim();
        paramJson.put("keyword", searchword);

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
                timeJson = DateUtil.getDifferOneDayTimes(-365);
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
                timee =  paramJson.getString("timee")+ " 23:59:59";
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
        /*System.out.println(paramJson.toString());*/

        //数据来源
        JSONArray classify = paramJson.getJSONArray("classify");
        String classifylist= StringUtils.join(classify, ",");
        if (StringUtils.isBlank(classifylist)) {
            classifylist = "0";
        }
        paramJson.put("classify", classifylist);



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
        paramJson.remove("status");
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
            String key =paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("keyword")+timeType+matchingmode+searchType+emotionalIndex;
//            String key = projectid.toString()+paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword");
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

        String searchword = paramJson.getString("searchword");
        if (StringUtils.isNotBlank(searchword)) searchword = searchword.trim();
        paramJson.put("keyword", searchword);

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
                timeJson = DateUtil.getDifferOneDayTimes(-365);
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
                timee =  paramJson.getString("timee")+ " 23:59:59";
                break;
        }
        paramJson.put("times", times);
        paramJson.put("timee", timee);
        paramJson.put("matchingmode", matchingmode);
        paramJson.put("emotionalIndex", emotionalIndex);
        paramJson.put("searchType", searchType);
        paramJson.put("size", 10);


//        JSONObject eventIndex = paramJson.getJSONObject("eventIndex");
//        String industryIndex = paramJson.getString("industryIndex");
//        String province1 = paramJson.getString("province");
//
//
//        if (eventIndex== null) {
//            JSONArray objects = new JSONArray();
//            paramJson.put("eventIndex", objects);
//        }
//
//        if (industryIndex == null) {
//            JSONArray objects = new JSONArray();
//            paramJson.put("industryIndex", objects);
//        }
//
//        if (province1 == null) {
//            JSONArray objects = new JSONArray();
//            paramJson.put("province", objects);
//        }



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


        JSONArray classify = paramJson.getJSONArray("classify");
        String classifylist= StringUtils.join(classify, ",");
        if (StringUtils.isBlank(classifylist)) {
            classifylist = "0";
        }
        paramJson.put("classify", classifylist);




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
        paramJson.remove("status");
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
        /*System.out.println(paramJson.toString());*/
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
            String key =paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("keyword")+timeType+matchingmode+searchType+emotionalIndex;
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

        String searchword = paramJson.getString("searchword");
        if (StringUtils.isNotBlank(searchword)) searchword = searchword.trim();
        paramJson.put("keyword", searchword);

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
                timeJson = DateUtil.getDifferOneDayTimes(-365);
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
                timee =  paramJson.getString("timee")+ " 23:59:59";
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
        Object classify1 = paramJson.get("classify");
        if (!classify1.toString().substring(0,1).equals("[")) {
            String[] classifies = paramJson.getString("classify").split(",");
            paramJson.put("classify", classifies);
        }
        JSONArray classify = paramJson.getJSONArray("classify");
        String classifylist= StringUtils.join(classify, ",");
        if (StringUtils.isBlank(classifylist)) {
            orgtypelist = "0";
        }
        paramJson.put("classify", classifylist);


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
        paramJson.remove("status");
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
            String key =paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("keyword")+timeType+matchingmode+searchType+emotionalIndex;
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

//        paramJson.put("similar", "0");
//        paramJson.put("searchType", 1);
//        paramJson.put("matchingmode", 1);
//        paramJson.put("timeType", 6);


        JSONObject response = new JSONObject();
        JSONObject dataGroupJson = new JSONObject();


        String searchword = paramJson.getString("searchword");
        if (StringUtils.isNotBlank(searchword)) searchword = searchword.trim();
        paramJson.put("keyword", searchword);

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
                timeJson = DateUtil.getDifferOneDayTimes(-365);
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
                timee =  paramJson.getString("timee")+ " 23:59:59";
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
        Object classify1 = paramJson.get("classify");
        if (!classify1.toString().substring(0,1).equals("[")) {
            String[] classifies = paramJson.getString("classify").split(",");
            paramJson.put("classify", classifies);
        }


        JSONArray classify = paramJson.getJSONArray("classify");
        String classifylist= StringUtils.join(classify, ",");
        if (StringUtils.isBlank(classifylist)) {
            orgtypelist = "0";
        }
        paramJson.put("classify", classifylist);

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
        paramJson.remove("status");
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
            String key =paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("keyword")+timeType+matchingmode+searchType+emotionalIndex;
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

    @Override
    public JSONObject getAnalysisArticleList(JSONObject paramJson) {
        return null;
    }
}
