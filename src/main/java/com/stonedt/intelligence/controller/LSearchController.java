package com.stonedt.intelligence.controller;

import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.service.LSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lh
 * @date 2021-05-31 18:21
 */
@RestController
public class LSearchController {

    @Autowired
    private LSearchService lSearchService;

    @PostMapping("/industry")
    @ResponseBody
    public JSONObject getIndustry(@RequestBody JSONObject paramJson) {
        JSONObject articleIndustryList = lSearchService.getArticleIndustryList(paramJson);
        return articleIndustryList;
    }


    @PostMapping("/getevent")
    @ResponseBody
    public JSONObject getEvent(@RequestBody JSONObject paramJson) {
        JSONObject articleEventList = lSearchService.getArticleEventList(paramJson);
        return articleEventList;
    }


    @PostMapping("/getProvinceList")
    @ResponseBody
    public JSONObject getArticleProvinceList(@RequestBody JSONObject paramJson) {

        JSONObject articleProvinceList = lSearchService.getArticleProvinceList(paramJson);
        return articleProvinceList;
    }


    @PostMapping("/getArticleCityList")
    @ResponseBody
    public JSONObject getArticleCityList(@RequestBody JSONObject paramJson) {
        JSONObject articleCityList = lSearchService.getArticleCityList(paramJson);
        return articleCityList;
    }



}
