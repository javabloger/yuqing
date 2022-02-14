package com.stonedt.intelligence.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lh
 * @date 2021-05-31 18:13
 */
public interface LSearchService {

    JSONObject getArticleSynthesizeList(JSONObject paramJson);

    JSONObject getArticleIndustryList(JSONObject paramJson);

    JSONObject getArticleEventList(JSONObject paramJson);

    JSONObject getArticleProvinceList(JSONObject paramJson);

    JSONObject getArticleCityList(JSONObject paramJson);

    JSONObject getAnalysisArticleList(JSONObject paramJson);

}
