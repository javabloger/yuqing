package com.stonedt.intelligence.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.stonedt.intelligence.constant.MonitorConstant;
import com.stonedt.intelligence.constant.SearchConstant;
import com.stonedt.intelligence.dao.IFullSearchDao;
import com.stonedt.intelligence.entity.FullPolymerization;
import com.stonedt.intelligence.entity.FullType;
import com.stonedt.intelligence.entity.FullWord;
import com.stonedt.intelligence.service.FullSearchService;
import com.stonedt.intelligence.util.*;
import com.stonedt.intelligence.vo.FullSearchParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

/**
 *	全文搜索
 * @date  2020年4月17日 下午4:46:30
 */
@Service
public class FullSearchServiceImpl implements FullSearchService{
	
	// es搜索地址
	@Value("${es.search.url}")
	private String es_search_url;
	// es热点地址
	@Value("${es.hot.search.url}")
	private String es_hot_search_url;
	
	
	@Autowired
	private IFullSearchDao fullSearchDao;
	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 全国律师列表
	 */
	@Override
	public JSONObject lawyerList(FullSearchParam param) {
		if(param.getSearchWord()=="") {
			param.setSearchWord(null);
		}
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("size", param.getPageSize());
		map.put("index", "stonedt_law");
		map.put("type", "infor");
		map.put("page", param.getPageNum());
		map.put("timefield", "qualifitime");
		if(param.getTimes()!=null) {
			map.put("times", param.getTimes());
		}
		if(param.getTimee()!=null) {
			map.put("timee", param.getTimee());
		}
		List<ParamUtil> list=new ArrayList<>();
		if(param.getMatchingmode()!=null || param.getKinds()!=null || null != param.getSearchWord()) {
			if(param.getMatchingmode()!=null) {
				switch (param.getMatchingmode()) {
				case "姓名":
					list.add(new ParamUtil("name", param.getSearchWord()));
					break;
				case "律所名称":
					list.add(new ParamUtil("lawfirm", param.getSearchWord()));
					break;
				case "擅长领域":
					list.add(new ParamUtil("goods", param.getSearchWord()));
					break;
				case "城市":
					list.add(new ParamUtil("city", param.getSearchWord()));
					break;
				}
			}else if(null != param.getSearchWord()){
				list.add(new ParamUtil("name", param.getSearchWord()));
			}
			if(param.getKinds()!=null) {
				list.add(new ParamUtil("kinds", param.getKinds()));
			}
			map.put("and", list);
		}
//		String urlParam=MapUtil.getUrlParamsByMap(map);
//		System.out.println(urlParam);
		JSONObject j = new JSONObject(map);
		String url=es_search_url+SearchConstant.ES_API_COMMON_SEARCH;
		String response = "";
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();
		try {
			response = MyHttpRequestUtil.sendPostRaw(url, j.toJSONString(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", list2);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			String totalData = parseObject.getString("count");
			String totalPage = parseObject.getString("page_count");
			String currentPage=parseObject.getString("page");
			String code=parseObject.getString("code");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				params.put("content", params.get("abstract"));
				list2.add(params);
			}
			if (Integer.parseInt(totalData) > 5000) {
				totalPage = "500";
			}
			if (StringUtils.isBlank(totalPage)) {
				totalPage = "1";
			}else if ("0".equals(totalPage)) {
				totalPage = "1";
			}
			result.put("list", list2);
			result.put("totalData", totalData);
			result.put("totalPage", totalPage);
			result.put("code", code);
			result.put("currentPage", currentPage);
		}else {
			result.put("list", list2);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		return new JSONObject(result);
	}
	
	/**
	 * 律师详情
	 */
	@Override
	public JSONObject lawyerDetailData(String article_public_id) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("timefield", "qualifitime");
		map.put("size", "1");
		map.put("index", "stonedt_law");
		map.put("page", "1");
		map.put("type", "infor");
		map.put("times", "");
		map.put("timee", "");
		List<ParamUtil> paramList=new ArrayList<ParamUtil>();
		paramList.add(new ParamUtil("article_public_id", article_public_id));
		map.put("and", paramList);
		JSONObject j=new JSONObject(map);
		String response = "";
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<>();
		try {
			String url=es_search_url+SearchConstant.ES_API_COMMON_SEARCH;
//			System.out.println(url);
			response = MyHttpRequestUtil.sendPostRaw(url, j.toJSONString(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			String totalData = parseObject.getString("count");
			String totalPage = parseObject.getString("page_count");
			String currentPage=parseObject.getString("page");
			String code=parseObject.getString("code");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				params.put("content", params.get("abstract"));
				list.add(params);
			}
			if (Integer.parseInt(totalData) > 5000) {
				totalPage = "500";
			}
			if (StringUtils.isBlank(totalPage)) {
				totalPage = "1";
			}else if ("0".equals(totalPage)) {
				totalPage = "1";
			}
			result.put("list", list);
			result.put("totalData", totalData);
			result.put("totalPage", totalPage);
			result.put("code", code);
			result.put("currentPage", currentPage);
		}else {
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		return new JSONObject(result);
	}
	
	/**
	 * 被执行人
	 */
	@Override
	public JSONObject executionPersonList(FullSearchParam param) {
		if(null != param.getSearchWord() && "".equals(param.getSearchWord().trim())) {
			param.setSearchWord(null);
		}
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("size", param.getPageSize());
		map.put("index", "stonedt_executionperson");
		map.put("type", "infor");
		map.put("page", param.getPageNum());
		map.put("timefield", "publishDate");
		if(param.getTimes()!=null) {
			map.put("times", param.getTimes());
		}
		if(param.getTimee()!=null) {
			map.put("timee", param.getTimee());
		}
		if(param.getMatchingmode()!=null || param.getKinds()!=null || null != param.getSearchWord()) {
			List<ParamUtil> not=new ArrayList<>();
			List<ParamUtil> or=new ArrayList<ParamUtil>();
			List<ParamUtil> and=new ArrayList<ParamUtil>();
			if(param.getMatchingmode()!=null) {
				not.add(new ParamUtil("age", "0"));
				map.put("not", not);
				switch (param.getMatchingmode()) {
				case "地区":
					or.add(new ParamUtil("courtName", param.getSearchWord()));
					or.add(new ParamUtil("areaNameNew", param.getSearchWord()));
					map.put("or", or);
					break;
				case "名称":
					and.add(new ParamUtil("iname", param.getSearchWord()));
					map.put("and", and);
					break;
				}
			}else if(null != param.getSearchWord()) {
				not.add(new ParamUtil("age", "0"));
				map.put("not", not);
				or.add(new ParamUtil("courtName", param.getSearchWord()));
				or.add(new ParamUtil("areaNameNew", param.getSearchWord()));
				map.put("or", or);
			}
			if(param.getKinds()!=null) {
				switch (param.getKinds()) {
				case "企业":
					and.add(new ParamUtil("age", "0"));
					map.put("and", and);
					break;
				case "个人":
					not.add(new ParamUtil("age", "0"));
					map.put("not", not);
					break;
				}
			}
		}
		JSONObject j = new JSONObject(map);
		String url=es_search_url+SearchConstant.ES_API_COMMON_SEARCH;
		String response = "";
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();
		try {
			response = MyHttpRequestUtil.sendPostRaw(url, j.toJSONString(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", list2);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			String totalData = parseObject.getString("count");
			String totalPage = parseObject.getString("page_count");
			String currentPage=parseObject.getString("page");
			String code=parseObject.getString("code");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				params.put("content", params.get("abstract"));
				list2.add(params);
			}
			if (Integer.parseInt(totalData) > 5000) {
				totalPage = "500";
			}
			if (StringUtils.isBlank(totalPage)) {
				totalPage = "1";
			}else if ("0".equals(totalPage)) {
				totalPage = "1";
			}
			result.put("list", list2);
			result.put("totalData", totalData);
			result.put("totalPage", totalPage);
			result.put("code", code);
			result.put("currentPage", currentPage);
		}else {
			result.put("list", list2);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		return new JSONObject(result);
	}
	
	/**
	 * 被执行人详情
	 */
	@Override
	public JSONObject executionPersonDetailData(String article_public_id) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("timefield", "publishDate");
		map.put("size", "1");
		map.put("index", "stonedt_executionperson");
		map.put("page", "1");
		map.put("type", "infor");
		map.put("times", "");
		map.put("timee", "");
		List<ParamUtil> paramList=new ArrayList<ParamUtil>();
		paramList.add(new ParamUtil("article_public_id", article_public_id));
		map.put("and", paramList);
		JSONObject j=new JSONObject(map);
		String response = "";
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<>();
		try {
			String url=es_search_url+SearchConstant.ES_API_COMMON_SEARCH;
//			System.out.println(url);
			response = MyHttpRequestUtil.sendPostRaw(url, j.toJSONString(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			String totalData = parseObject.getString("count");
			String totalPage = parseObject.getString("page_count");
			String currentPage=parseObject.getString("page");
			String code=parseObject.getString("code");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				params.put("content", params.get("abstract"));
				list.add(params);
			}
			if (Integer.parseInt(totalData) > 5000) {
				totalPage = "500";
			}
			if (StringUtils.isBlank(totalPage)) {
				totalPage = "1";
			}else if ("0".equals(totalPage)) {
				totalPage = "1";
			}
			result.put("list", list);
			result.put("totalData", totalData);
			result.put("totalPage", totalPage);
			result.put("code", code);
			result.put("currentPage", currentPage);
		}else {
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		return new JSONObject(result);
	}
	
	/**
	 * 专家人才列表
	 */
	@Override
	public JSONObject professorList(FullSearchParam professorListParam) {
		if(professorListParam != null && "".equals(professorListParam.getSearchWord())) {
			professorListParam.setSearchWord(null);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("timefield", "spider_time");
		map.put("size", professorListParam.getPageSize());
		map.put("index", "stonedt_baidutalentdatabase");
		map.put("type", "infor");
		map.put("times", professorListParam.getTimes());
		map.put("timee", professorListParam.getTimee());
		map.put("page", professorListParam.getPageNum());
		if(professorListParam.getMatchingmode()!=null || null != professorListParam.getSearchWord()) {
			List<ParamUtil> and=new ArrayList<ParamUtil>();
			if(professorListParam.getMatchingmode()!=null) {
				switch (professorListParam.getMatchingmode()) {
				case "姓名":
					and.add(new ParamUtil("title", professorListParam.getSearchWord()));
					break;
				case "研究领域":
					and.add(new ParamUtil("field", professorListParam.getSearchWord()));
					break;
				case "机构":
					and.add(new ParamUtil("institution", professorListParam.getSearchWord()));
					break;
				}
			}else if(null != professorListParam.getSearchWord()) {
				and.add(new ParamUtil("title", professorListParam.getSearchWord()));
			}
			map.put("and", and);
		}
		JSONObject j=new JSONObject(map);
		String response = "";
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<>();
		try {
			String url=es_search_url+SearchConstant.ES_API_COMMON_SEARCH;
//			System.out.println(url);
			response = MyHttpRequestUtil.sendPostRaw(url, j.toJSONString(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			String totalData = parseObject.getString("count");
			String totalPage = parseObject.getString("page_count");
			String currentPage=parseObject.getString("page");
			String code=parseObject.getString("code");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				params.put("content", params.get("abstract"));
				list.add(params);
			}
			if (Integer.parseInt(totalData) > 5000) {
				totalPage = "500";
			}
			if (StringUtils.isBlank(totalPage)) {
				totalPage = "1";
			}else if ("0".equals(totalPage)) {
				totalPage = "1";
			}
			result.put("list", list);
			result.put("totalData", totalData);
			result.put("totalPage", totalPage);
			result.put("code", code);
			result.put("currentPage", currentPage);
		}else {
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		return new JSONObject(result);
	}
	
	/**
	 * 专家人才详情
	 */
	@Override
	public JSONObject professorDetailData(String article_public_id) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("timefield", "spider_time");
		map.put("size", "1");
		map.put("index", "stonedt_baidutalentdatabase");
		map.put("page", "1");
		map.put("type", "infor");
		map.put("times", "");
		map.put("timee", "");
		List<ParamUtil> paramList=new ArrayList<ParamUtil>();
		paramList.add(new ParamUtil("article_public_id", article_public_id));
		map.put("and", paramList);
		JSONObject j=new JSONObject(map);
		String response = "";
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<>();
		try {
			String url=es_search_url+SearchConstant.ES_API_COMMON_SEARCH;
//			System.out.println(url);
			response = MyHttpRequestUtil.sendPostRaw(url, j.toJSONString(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			String totalData = parseObject.getString("count");
			String totalPage = parseObject.getString("page_count");
			String currentPage=parseObject.getString("page");
			String code=parseObject.getString("code");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				params.put("content", params.get("abstract"));
				list.add(params);
			}
			if (Integer.parseInt(totalData) > 5000) {
				totalPage = "500";
			}
			if (StringUtils.isBlank(totalPage)) {
				totalPage = "1";
			}else if ("0".equals(totalPage)) {
				totalPage = "1";
			}
			result.put("list", list);
			result.put("totalData", totalData);
			result.put("totalPage", totalPage);
			result.put("code", code);
			result.put("currentPage", currentPage);
		}else {
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		return new JSONObject(result);
	}
	
	/**
	 * 医生列表
	 */
	@Override
	public JSONObject doctorList(FullSearchParam doctorListParam) {
		if(doctorListParam != null && "".equals(doctorListParam.getSearchWord())) {
			doctorListParam.setSearchWord(null);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("timefield", "spider_time");
		map.put("size", doctorListParam.getPageSize());
		map.put("times", "");
		map.put("timee", "");
		map.put("page", doctorListParam.getPageNum());
		map.put("index", "stonedt_doctor");
		map.put("type", "infor");
		if(doctorListParam.getMatchingmode()!=null || doctorListParam.getSearchWord() != null) {
			List<ParamUtil> paramList=new ArrayList<ParamUtil>();
			if(doctorListParam.getMatchingmode()!=null) {
				switch (doctorListParam.getMatchingmode()) {
				case "姓名":
					paramList.add(new ParamUtil("name", doctorListParam.getSearchWord()));
					break;
				case "医院":
					paramList.add(new ParamUtil("hospital", doctorListParam.getSearchWord()));
					break;
				case "擅长领域":
					paramList.add(new ParamUtil("adept", doctorListParam.getSearchWord()));
					break;
				case "所属科室":
					paramList.add(new ParamUtil("department", doctorListParam.getSearchWord()));
					break;
				}
			}else if(doctorListParam.getSearchWord() != null) {
				paramList.add(new ParamUtil("name", doctorListParam.getSearchWord()));
			}
			map.put("and", paramList);
		}
		JSONObject j=new JSONObject(map);
		String response = "";
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<>();
		try {
			String url=es_search_url+SearchConstant.ES_API_COMMON_SEARCH;
//			System.out.println(url);
			response = MyHttpRequestUtil.sendPostRaw(url, j.toJSONString(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			String totalData = parseObject.getString("count");
			String totalPage = parseObject.getString("page_count");
			String currentPage=parseObject.getString("page");
			String code=parseObject.getString("code");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				params.put("content", params.get("abstract"));
				list.add(params);
			}
			if (Integer.parseInt(totalData) > 5000) {
				totalPage = "500";
			}
			if (StringUtils.isBlank(totalPage)) {
				totalPage = "1";
			}else if ("0".equals(totalPage)) {
				totalPage = "1";
			}
			result.put("list", list);
			result.put("totalData", totalData);
			result.put("totalPage", totalPage);
			result.put("code", code);
			result.put("currentPage", currentPage);
		}else {
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		return new JSONObject(result);
	}
	
	/**
	 * 医生详情
	 */
	@Override
	public JSONObject doctorDetailData(String article_public_id) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("timefield", "spider_time");
		map.put("size", "1");
		map.put("index", "stonedt_doctor");
		map.put("page", "1");
		map.put("type", "infor");
		map.put("times", "");
		map.put("timee", "");
		List<ParamUtil> paramList=new ArrayList<ParamUtil>();
		paramList.add(new ParamUtil("article_public_id", article_public_id));
		map.put("and", paramList);
		JSONObject j=new JSONObject(map);
		String response = "";
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<>();
		try {
			String url=es_search_url+SearchConstant.ES_API_COMMON_SEARCH;
//			System.out.println(url);
			response = MyHttpRequestUtil.sendPostRaw(url, j.toJSONString(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			String totalData = parseObject.getString("count");
			String totalPage = parseObject.getString("page_count");
			String currentPage=parseObject.getString("page");
			String code=parseObject.getString("code");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				params.put("content", params.get("abstract"));
				list.add(params);
			}
			if (Integer.parseInt(totalData) > 5000) {
				totalPage = "500";
			}
			if (StringUtils.isBlank(totalPage)) {
				totalPage = "1";
			}else if ("0".equals(totalPage)) {
				totalPage = "1";
			}
			result.put("list", list);
			result.put("totalData", totalData);
			result.put("totalPage", totalPage);
			result.put("code", code);
			result.put("currentPage", currentPage);
		}else {
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		return new JSONObject(result);
	}
	
	/**
	 * 	资讯数据列表
	 */
	@Override
	public JSONObject informationList(FullSearchParam param) {

		JSONObject response = new JSONObject();
        JSONObject dataGroupJson = new JSONObject();
        JSONObject paramJson = new JSONObject();
       
        String searchkeyword = param.getSearchWord();
        searchkeyword = searchkeyword.replace(" ", ",");
        paramJson.put("keyword", searchkeyword);
        if (StringUtils.isNotBlank(searchkeyword)) searchkeyword = searchkeyword.trim();
        Integer similar = param.getMergeType();
        Integer matchingmode = param.getMatchType() - 1; // 关键词匹配规则
        String emotionalIndex = param.getEmotions();
        String classify = param.getClassify();
        if (StringUtils.isBlank(emotionalIndex)) {
            emotionalIndex = "1,2,3";
        }

        if (StringUtils.isBlank(classify)) {
        	classify = "3,6,7,8";
        }

        Integer searchType = param.getSortType();
        String times = "";
        String timee = "";
        Integer timeType = param.getTimeType();
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
                times = param.getStartTime() + " 00:00:00";
                timee = param.getEndTime() + " 23:59:59";
                break;
        }
        paramJson.put("times", times);
        paramJson.put("timee", timee);
        paramJson.put("matchingmode", matchingmode);
        paramJson.put("emotionalIndex", emotionalIndex);
        paramJson.put("searchType", searchType);
        paramJson.put("size", param.getPageSize());
        paramJson.put("page", param.getPageNum());
        paramJson.put("totalPage", param.getTotalPage());
        paramJson.put("totalCount", param.getTotalCount());

        paramJson.put("classify", classify);
        
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
            Integer currentPage = param.getPageNum();
            String article_public_idStr = "";
            if (currentPage == 1) {
                String params = MapUtil.getUrlParamsByMap(paramJson);
                String similarUrl = es_search_url + MonitorConstant.es_api_similarsearch_content;
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
            if (!article_public_idStr.equals("") || currentPage>1) {
            	if(currentPage==1) {
            		paramJson.put("article_public_idstr", article_public_idStr);
            	}else {
            		paramJson.put("article_public_idstr", param.getArticle_public_idstr());
            	}
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
                    }else {
                        Integer totalPage = Integer.parseInt(paramJson.getString("totalPage"));
                        totalCount = Integer.parseInt(paramJson.getString("totalCount"));
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
            System.out.println("ES查询开始时间："+TimeUtil.getCurrenttime());
            String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
            System.out.println("ES查询结束开始时间："+TimeUtil.getCurrenttime());
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
                    System.out.println("后台计算开始时间："+TimeUtil.getCurrenttime());

                    for (int i = 0; i < esDataArray.size(); i++) {
                        JSONObject dataJson = (JSONObject) esDataArray.get(i);
                        JSONObject highlightJson = dataJson.getJSONObject("highlight"); // 高亮
                        JSONObject _sourceJson = dataJson.getJSONObject("_source");
                        Set<String> relatedWord = new HashSet<>();
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
                    System.out.println("后台计算结束时间："+TimeUtil.getCurrenttime());
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
	 * 筛选查找
	 * @author lh
	 * @date 2021/6/2 19:00
	 * @param
	 * @return com.alibaba.fastjson.JSONObject
	 */
	public JSONObject informationListSearch(FullSearchParam param) {

		JSONObject response = new JSONObject();
		JSONObject dataGroupJson = new JSONObject();
		JSONObject paramJson = new JSONObject();

		String searchkeyword = param.getSearchWord();
		searchkeyword = searchkeyword.replace(" ", ",");
		paramJson.put("keyword", searchkeyword);
		if (StringUtils.isNotBlank(searchkeyword)) searchkeyword = searchkeyword.trim();
//		Integer similar = param.getMergeType();
		Integer similar = param.getSimilar();
		Integer matchingmode =Integer.parseInt( param.getMatchingmode()) - 1; // 关键词匹配规则
		String emotionArray = param.getEmotionalIndex();
		String classify = param.getClassify();
		String emotionalIndex = StringUtils.join(emotionArray, ",");
		emotionalIndex = emotionalIndex.replaceAll("\"", "").replaceAll("0", "").replaceAll("\\[", "").replaceAll("\\]", "");
		if(emotionalIndex.endsWith(",")) {
			emotionalIndex = emotionalIndex.substring(0, emotionalIndex.length()-1);
		}
		if (StringUtils.isBlank(emotionalIndex)) {
			emotionalIndex = "1,2,3";
		}

        if (StringUtils.isBlank(classify)) {
		classify = "3,6,7,8";
        }

		Integer searchType = param.getSearchType();
		String times = "";
		String timee = "";
		Integer timeType = param.getTimeType();
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
				times = param.getStartTime() + " 00:00:00";
				timee = param.getEndTime() + " 23:59:59";
				break;
		}
		paramJson.put("times", times);
		paramJson.put("timee", timee);
		paramJson.put("matchingmode", matchingmode);
		paramJson.put("emotionalIndex", emotionalIndex);
		paramJson.put("searchType", searchType);
		paramJson.put("size", param.getPageSize());
		paramJson.put("page", param.getPage());
		paramJson.put("totalPage", param.getTotalPage());
		paramJson.put("totalCount", param.getTotalCount());
		paramJson.put("classify", classify);
		paramJson.put("city", param.getCity());
		paramJson.put("eventIndex", param.getEventIndex());
		paramJson.put("industryIndex", param.getIndustryIndex());
		paramJson.put("province", param.getProvince());


		paramJson.remove("projectid");
		paramJson.remove("group_id");
		paramJson.remove("precise");
//		paramJson.remove("similar");
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
		JSONArray classify1 = paramJson.getJSONArray("classify");
		String classifylist= StringUtils.join(classify1, ",");
		if (StringUtils.isBlank(classifylist)) {
			classifylist = "0";
		}
		paramJson.put("classify", classifylist);


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
			String key = paramJson.getString("city")+paramJson.getString("province")+paramJson.getString("eventlable")+paramJson.getString("industrylable")+paramJson.getString("hightechtypelist")+paramJson.getString("policylableflag")+paramJson.getShortValue("orgtypelist")+paramJson.getString("categorylable")+paramJson.getString("searchkeyword")+timeType+matchingmode+searchType+emotionalIndex+searchkeyword;

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

						String title = "";
						if (highlightJson.containsKey("title")) {
							title = highlightJson.getJSONArray("title").getString(0);


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
			paramJson.put("esindex", "postal");
			paramJson.put("estype", "infor");
			String params = MapUtil.getUrlParamsByMap(paramJson);
			url = es_search_url + MonitorConstant.es_api_search_list;
			/**
			 * 查询ES时间
			 */
			System.out.println("ES查询开始时间："+TimeUtil.getCurrenttime());
			String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
			System.out.println("ES查询结束开始时间："+TimeUtil.getCurrenttime());
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
					System.out.println("后台计算开始时间："+TimeUtil.getCurrenttime());

					for (int i = 0; i < esDataArray.size(); i++) {
						JSONObject dataJson = (JSONObject) esDataArray.get(i);
						JSONObject highlightJson = dataJson.getJSONObject("highlight"); // 高亮
						JSONObject _sourceJson = dataJson.getJSONObject("_source");
						Set<String> relatedWord = new HashSet<>();
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
					System.out.println("后台计算结束时间："+TimeUtil.getCurrenttime());
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
	public JSONObject hotList(FullSearchParam searchParam) throws UnsupportedEncodingException {
		String keyString = MD5Util.getMD5(searchParam.getPageNum() + searchParam.getClassify() + searchParam.getSource_name() + searchParam.getSearchWord());
		System.err.println("redis key" + keyString);
		String key = redisUtil.getKey(keyString);
//		if(StringUtils.isNotBlank(key)) {
//			System.err.println("当前查询存在redis中");
//			return JSONObject.parseObject(key);
//		}
		System.err.println("当前查询不存在redis中");
		String times = "", timee = ""; 
		String timetype = searchParam.getTimetype(); 
		timetype = "spider_time";
		String searchparam = "spider_time";
		JSONObject timeJson = new JSONObject();
		switch (searchParam.getTimeType()) {
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
	            times = searchParam.getStartTime() + " 00:00:00";
	            timee = searchParam.getEndTime() + " 23:59:59";
	            break;
	    }
		if(StringUtils.equals(searchParam.getSource_name(), "全部")) {
			searchParam.setSource_name("");
		}
		String params = "page="+searchParam.getPageNum()+ "&topic="+searchParam.getSearchWord() + 
				"&searchparam=" + searchparam + "&searchType=0" +
				"&source_name="+searchParam.getSource_name() +
				"&classify=" + searchParam.getClassify() + "&size=" + searchParam.getPageSize() +
				"&times="+times+
				"&timee="+timee+
				"&timetype="+timetype;
		String url = es_hot_search_url + SearchConstant.ES_API_HOT;
		String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
		return JSONObject.parseObject(esResponse);
	}
	
	@Override
	public JSONObject complaintList(FullSearchParam searchParam) {
		JSONObject json=new JSONObject();
		try {
			String sourceName="";
			String param="";
			if(searchParam.getClassify().equals("h")) {
				sourceName="黑猫投诉";
			}else if(searchParam.getClassify().equals("j")) {
				sourceName="聚投诉";
			}else if(searchParam.getClassify().equals("x")) {
				sourceName="消费宝";
			}
			param="times=&timee=&page="+searchParam.getPageNum()+"&size="+searchParam.getPageSize()+"&sourceName="+sourceName
					+"&keyword="+searchParam.getSearchWord()+"&matchingmode="+searchParam.getMatchType();
			Integer count =0;
			Integer page_count =0;
			Integer size =0;
			String url = es_search_url + SearchConstant.ES_API_MAYOR_MAIL_BOX;
			if(!searchParam.getClassify().equals("s")) {
				param = param + "&searchType="+searchParam.getSortType();
				url = es_search_url + SearchConstant.ES_API_COMPLAIN;
			}
			String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, param);
			System.err.println(esResponse);
			json = JSONObject.parseObject(esResponse);
			JSONArray news=json.getJSONArray("data");
			count=Integer.valueOf(json.get("count").toString());
			page_count=Integer.valueOf(json.get("page_count").toString());
			size=Integer.valueOf(json.get("size").toString());
			json.put("news",news);
			json.put("count",count);
			json.put("page_count",page_count);
			json.put("page",searchParam.getPageNum());
			json.put("size",size);
			json.put("classify",searchParam.getClassify());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	
	@Override
	public JSONObject announcementList(FullSearchParam searchParam) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("timefield", "reportDate");
		map.put("index", "stonedt_announcement");
		map.put("page", searchParam.getPageNum());
		map.put("size", searchParam.getPageSize());
		map.put("type", "infor");
		map.put("times", "");
		map.put("timee", "");
		map.put("searchType", searchParam.getSortType()==1 ? 0 : 1);
		if(!searchParam.getSearchWord().equals("")) {
			List<ParamUtil> orParam=new ArrayList<>();
			switch (searchParam.getMatchMethod()) {
				case "机构名称":
					orParam.add(new ParamUtil("codename", searchParam.getSearchWord()));
					break;
				case "标题":
					orParam.add(new ParamUtil("title", searchParam.getSearchWord()));
					break;
				case "股票代码":
					orParam.add(new ParamUtil("code", searchParam.getSearchWord()));
					break;
				case "内容":
					orParam.add(new ParamUtil("content", searchParam.getSearchWord()));
					break;
				case "全部匹配":
					orParam.add(new ParamUtil("code", searchParam.getSearchWord()));
					orParam.add(new ParamUtil("title", searchParam.getSearchWord()));
					orParam.add(new ParamUtil("codename", searchParam.getSearchWord()));
					orParam.add(new ParamUtil("content", searchParam.getSearchWord()));
					break;
			}
			map.put("or", orParam);
		}
		if(!searchParam.getRtype().equals("全部") && !searchParam.getRtype().equals("")) {
			List<ParamUtil> andParam=new ArrayList<>();
			andParam.add(new ParamUtil("rtype", searchParam.getRtype()));
			map.put("and", andParam);
		}
		JSONObject j=new JSONObject(map);
		String response = "";
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<>();
		try {
			String url=es_hot_search_url+SearchConstant.ES_API_COMMON_SEARCH;
			response = MyHttpRequestUtil.sendPostRaw(url, j.toJSONString(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			String totalData = parseObject.getString("count");
			String totalPage = parseObject.getString("page_count");
			String currentPage=parseObject.getString("page");
			String code=parseObject.getString("code");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				params.put("content", params.get("abstract"));
				list.add(params);
			}
			if (Integer.parseInt(totalData) > 5000) {
				totalPage = "500";
			}
			if (StringUtils.isBlank(totalPage)) {
				totalPage = "1";
			}else if ("0".equals(totalPage)) {
				totalPage = "1";
			}
			result.put("list", list);
			result.put("totalData", totalData);
			result.put("totalPage", totalPage);
			result.put("code", code);
			result.put("currentPage", currentPage);
		}else {
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		return new JSONObject(result);
	}
	
	@Override
	public JSONArray announcementRtype(FullSearchParam searchParam) {
        String param = "keyword="+searchParam.getSearchWord()+"&matchingmode="+searchParam.getMatchType();
        String response = "";
        try {
        	String url = es_hot_search_url + SearchConstant.ES_API_ANNOUNCEMENT_RTYPE;
            response = MyHttpRequestUtil.sendPostEsSearch(url, param);
        } catch (Exception e) {
        }
        JSONObject parseObject = JSONObject.parseObject(response);
        JSONObject jsonObject = parseObject.getJSONObject("aggregations");
        JSONObject jsonObject2 = jsonObject.getJSONObject("group_by_tags");
        JSONArray jsonArray = jsonObject2.getJSONArray("buckets");
        JSONArray resJson = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
        	JSONObject jsonObject3 = jsonArray.getJSONObject(i);
        	String string = jsonObject3.getString("key");
        	JSONObject json = new JSONObject();
        	json.put("name", string);
        	json.put("value", string);
        	resJson.add(json);
		}
    	return resJson;
	}
	
	@Override
	public JSONObject reportList(FullSearchParam searchParam) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("timefield", "reportDate");
		map.put("index", "stonedt_researchreport");
		map.put("page", searchParam.getPageNum());
		map.put("size", searchParam.getPageSize());
		map.put("type", "infor");
		switch (searchParam.getPublish()) {
			case 0:
				map.put("times", "");
				map.put("timee", "");
				break;
			case 2020:
				map.put("times", "2020-01-01 00:00:00");
				map.put("timee", "2020-12-31 00:00:00");
				break;
			case 2019:
				map.put("times", "2019-01-01 00:00:00");
				map.put("timee", "2019-12-31 00:00:00");
				break;
			case 2018:
				map.put("times", "2018-01-01 00:00:00");
				map.put("timee", "2018-12-31 00:00:00");
				break;
			case 2017:
				map.put("times", "2017-01-01 00:00:00");
				map.put("timee", "2017-12-31 00:00:00");
				break;
			case 2016:
				map.put("times", "2016-01-01 00:00:00");
				map.put("timee", "2016-12-31 00:00:00");
				break;
			case 1:
				map.put("times", "1900-01-01 00:00:00");
				map.put("timee", "2015-12-31 00:00:00");
				break;
		}
		map.put("searchType", searchParam.getSortType()==1 ? 0 : 1);
		if(!searchParam.getSearchWord().equals("")) {
			List<ParamUtil> orParam=new ArrayList<>();
			switch (searchParam.getMatchMethod()) {
				case "机构名称":
					orParam.add(new ParamUtil("codename", searchParam.getSearchWord()));
					break;
				case "标题":
					orParam.add(new ParamUtil("title", searchParam.getSearchWord()));
					break;
				case "股票代码":
					orParam.add(new ParamUtil("code", searchParam.getSearchWord()));
					break;
				case "内容":
					orParam.add(new ParamUtil("content", searchParam.getSearchWord()));
					break;
				case "全部匹配":
					orParam.add(new ParamUtil("code", searchParam.getSearchWord()));
					orParam.add(new ParamUtil("title", searchParam.getSearchWord()));
					orParam.add(new ParamUtil("codename", searchParam.getSearchWord()));
					orParam.add(new ParamUtil("content", searchParam.getSearchWord()));
					break;
			}
			map.put("or", orParam);
		}
		if(!searchParam.getRtype().equals("全部") && !searchParam.getRtype().equals("")) {
			List<ParamUtil> andParam=new ArrayList<>();
			andParam.add(new ParamUtil("rtype", searchParam.getRtype()));
			map.put("and", andParam);
		}
		JSONObject j=new JSONObject(map);
		String response = "";
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<>();
		try {
			String url=es_hot_search_url+SearchConstant.ES_API_COMMON_SEARCH;
			response = MyHttpRequestUtil.sendPostRaw(url, j.toJSONString(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			String totalData = parseObject.getString("count");
			String totalPage = parseObject.getString("page_count");
			String currentPage=parseObject.getString("page");
			String code=parseObject.getString("code");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				params.put("content", params.get("abstract"));
				list.add(params);
			}
			if (Integer.parseInt(totalData) > 5000) {
				totalPage = "500";
			}
			if (StringUtils.isBlank(totalPage)) {
				totalPage = "1";
			}else if ("0".equals(totalPage)) {
				totalPage = "1";
			}
			result.put("list", list);
			result.put("totalData", totalData);
			result.put("totalPage", totalPage);
			result.put("code", code);
			result.put("currentPage", currentPage);
		}else {
			result.put("list", list);
			result.put("totalData", 0);
			result.put("totalPage", 1);
			result.put("code", 500);
			result.put("currentPage", 1);
		}
		return new JSONObject(result);
	}
	
	@Override
	public JSONArray reportIndustry(FullSearchParam searchParam) {
		String param = "keyword="+searchParam.getSearchWord();
		String response = "";
		try {

        	String url = es_hot_search_url + SearchConstant.ES_API_RESEARCH_REPORT_INDUSTRY;
            response = MyHttpRequestUtil.sendPostEsSearch(url, param);
        } catch (Exception e) {
        }
		JSONObject parseObject = JSONObject.parseObject(response);
        JSONObject jsonObject = parseObject.getJSONObject("aggregations");
        JSONObject jsonObject2 = jsonObject.getJSONObject("group_by_tags");
        JSONArray jsonArray = jsonObject2.getJSONArray("buckets");
        JSONArray resJson = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
        	JSONObject jsonObject3 = jsonArray.getJSONObject(i);
        	String string = jsonObject3.getString("key");
        	JSONObject json = new JSONObject();
        	json.put("name", string);
        	json.put("value", string);
        	resJson.add(json);
		}
    	return resJson;
	}
	
	@Override
	public String getReportDetail(String type, String article_public_id) {
		JSONObject json=new JSONObject();
		if(type.equals("report")) {
			json.put("index", "stonedt_researchreport");
			
		}else if(type.equals("announcement")) {
			json.put("index", "stonedt_announcement");
		}
		json.put("type", "infor");
		json.put("fieldname", "article_public_id");
		json.put("fielddata", article_public_id);
		String data="";
		try {
			String url = es_search_url + SearchConstant.ES_API_ANNOUNCEMENT_DETAIL;
			data = MyHttpRequestUtil.getJsonHtml(url, json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	@Override
	public JSONObject biddingList(FullSearchParam searchParam) {
		JSONObject returnObj = new JSONObject();
        try {
            String page = searchParam.getPageNum().toString();
            String size = searchParam.getPageSize().toString();
            String website_id = searchParam.getWebsite_id();
            System.err.println(website_id);
            Integer matchType = searchParam.getMatchType();
            String keyword = searchParam.getSearchWord();
            String searchkeywrord = "";
    		if(matchType == 1){
    			searchkeywrord = "title,content_text";
    		}else if(matchType == 2){
    			searchkeywrord = "title";
    		}else if(matchType == 3){
    			searchkeywrord = "content_text";
    		}else{
    			searchkeywrord = "title,content_text";
    		}
    		String timee = "",times = "";
    		JSONObject timeJson = new JSONObject();
			switch (searchParam.getPublish()) {
				case 0: break;
				case 1: 
					timeJson = DateUtil.getDifferOneDayTimes(-3);
					times = timeJson.getString("times") + " 00:00:00";
					timee = timeJson.getString("timee") + " 23:59:59";
					break;
				case 2: 
					timeJson = DateUtil.getDifferOneDayTimes(-7);
					times = timeJson.getString("times") + " 00:00:00";
					timee = timeJson.getString("timee") + " 23:59:59";
					break;
				case 3: 
					timeJson = DateUtil.getDifferOneDayTimes(-30);
					times = timeJson.getString("times") + " 00:00:00";
					timee = timeJson.getString("timee") + " 23:59:59";
					break;
				case 4: 
					timeJson = DateUtil.getDifferOneDayTimes(-90);
					times = timeJson.getString("times") + " 00:00:00";
					timee = timeJson.getString("timee") + " 23:59:59";
					break;
				case 5: 
					timeJson = DateUtil.getDifferOneDayTimes(-180);
					times = timeJson.getString("times") + " 00:00:00";
					timee = timeJson.getString("timee") + " 23:59:59";
					break;
				case 6: 
					timeJson = DateUtil.getDifferOneDayTimes(-365);
					times = timeJson.getString("times") + " 00:00:00";
					timee = timeJson.getString("timee") + " 23:59:59";
					break;
				default: break;
			}
			
    		JSONObject end_json=new JSONObject();
            JSONArray  array=new JSONArray();
            JSONArray  searchArray=new JSONArray();
            if (website_id.length()>4) {
            	String[] all_id = website_id.split(",");
            	for (int i = 0; i < all_id.length; i++) {
            		JSONObject json=new JSONObject();
            		json.put("field", "website_id");
                    json.put("keyword", all_id[i]);
                    array.add(json);
				}
                end_json.put("timefield", "publish_time");
                end_json.put("times", times);
                end_json.put("timee", timee);
                end_json.put("index", "stonedt_biao");
                end_json.put("type", "infor");
                end_json.put("page", page);
                end_json.put("size", size);
                end_json.put("or", array);
                if(!keyword.isEmpty()) {
                    JSONObject jsonEnd=new JSONObject();
                    jsonEnd.put("field", searchkeywrord);
                    jsonEnd.put("keyword",keyword);
                    searchArray.add(jsonEnd);
                    end_json.put("or", searchArray);
                }
			}else {
				JSONObject json=new JSONObject();
				json.put("field", "website_id");
	            json.put("keyword", website_id);
	            array.add(json);
            	if(!keyword.isEmpty()) {
            		JSONObject jsonEnd=new JSONObject();
            		jsonEnd.put("field", searchkeywrord);
            		jsonEnd.put("keyword",keyword);
            		searchArray.add(jsonEnd);
    	            end_json.put("or", searchArray);
            	}
	            end_json.put("timefield", "publish_time");
	            end_json.put("times", times);
	            end_json.put("timee", timee);
	            end_json.put("index", "stonedt_biao");
	            end_json.put("type", "infor");
	            end_json.put("page", page);
	            end_json.put("size", size);
	            end_json.put("and", array);
			}
            String url = es_search_url + SearchConstant.ES_API_COMMON_SEARCH;
            String responseData = MyHttpRequestUtil.sendPostRaw(url, end_json.toJSONString(), "utf-8");
            JSONArray biddingList = new JSONArray();
            JSONObject responseJson = JSON.parseObject(responseData);
            int code = responseJson.getIntValue("code");
            Integer totalCount = responseJson.getInteger("count"); // 总条数
            Integer pageCount = responseJson.getInteger("page_count"); // 总页数
            Integer pagenum = responseJson.getInteger("page"); // 当前页数
            JSONArray dataArray = responseJson.getJSONArray("data"); // 数据列表
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject dataObj = (JSONObject) dataArray.get(i);
                JSONObject _sourceObj = dataObj.getJSONObject("_source");
                biddingList.add(_sourceObj);
            }
            returnObj.put("code", code);
            returnObj.put("list", biddingList);
            returnObj.put("totalCount", totalCount);
            returnObj.put("page", pagenum);
            returnObj.put("totalPage", pageCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnObj;
	}
	
	@Override
	public JSONObject biddingDetail(String article_public_id) {
		JSONObject returnObj = new JSONObject();
        try {
            String url = es_search_url + SearchConstant.ES_API_BIDDING_DETAIL;
            String param = "article_public_id=" + article_public_id;
            String responseData = MyHttpRequestUtil.sendPostEsSearch(url, param);
            returnObj = JSON.parseObject(responseData);
            if(!returnObj.containsKey("numberid")) {
            	returnObj.put("numberid", "");
            }else if(!returnObj.containsKey("industry")){
            	returnObj.put("industry", "");
            }
            if(!returnObj.containsKey("detail_url")) {
            	returnObj.put("detail_url", returnObj.get("detailurl"));
            }
        } catch (Exception e) {
        }
        return returnObj;
	}
	
	@Override
	public JSONObject inviteList(FullSearchParam searchParam) {
        Integer page = searchParam.getPageNum(); // 当前页数
        String jobsorigin = searchParam.getClassify();
        String searchkeywrord= searchParam.getSearchWord();
        int wrord = searchParam.getMatchType(); 
        Integer searchType = 0;
        if(searchParam.getSortType() == 2) {
        	searchType = 1;
        }
    	String param = "";
		try {
			param = "&size="+searchParam.getPageSize()+"&invite_province=&invite_city=&page=" + page + "&jobsorigin=" + URLEncoder.encode(jobsorigin, "utf-8")
			        + "&searchtype=" + wrord +"&company_name=&searchkeywrord="+searchkeywrord + "&searchType=" + searchType;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	String url = es_search_url + SearchConstant.ES_API_JOBS_LIST;
        String response = MyHttpRequestUtil.sendPostEsSearch(url, param);
        JSONObject jsonObject = JSONObject.parseObject(response);
        Integer count = jsonObject.getInteger("count");
        Integer page_count = jsonObject.getInteger("page_count");
        Integer code = jsonObject.getIntValue("code");
        JSONArray dataArray = jsonObject.getJSONArray("data");
        JSONObject returnObj = new JSONObject();
        JSONArray list = new JSONArray();
        if(dataArray == null) return returnObj;
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject dataObj = (JSONObject) dataArray.get(i);
            JSONObject _sourceObj = dataObj.getJSONObject("_source");
            list.add(_sourceObj);
        }
        returnObj.put("code", code);
        returnObj.put("data", list);
        returnObj.put("totalPage", page_count);
        returnObj.put("page", page);
        returnObj.put("totalCount", count);
        return returnObj;
	}
	
	@Override
	public JSONObject getInviteDetail(String record_id) {
		JSONObject responseDataJson = new JSONObject();
        try {
            String param = "record_id=" + record_id;
            String url = es_search_url + SearchConstant.ES_API_JOBS_DETAIL;
            String response = MyHttpRequestUtil.sendPostEsSearch(url, param);
            responseDataJson = JSON.parseObject(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseDataJson;
	}
	
	@Override
	public JSONObject companyList(FullSearchParam searchParam) {
		String response = "";
		JSONObject result = new JSONObject();
		List<Map<String, String>> list = new ArrayList<>();
		try {
			if(StringUtils.equals("全部", searchParam.getRtype())) {
				searchParam.setRtype("");
			}
			Integer establish = searchParam.getEstablish();
			String timee = "",times = "";
			JSONObject timesJson = new JSONObject();
			Date dateByYear = null;
			switch (establish) {
			case 1:
		    	timesJson = DateUtil.dateRoll2(new Date(), Calendar.YEAR, -1);
				timee = timesJson.getString("timee");
				times = timesJson.getString("times");
				break;
			case 2:
				dateByYear = DateUtil.getDateByYear(-1);
		    	timesJson = DateUtil.dateRoll2(dateByYear, Calendar.YEAR, -3);
		    	timee = timesJson.getString("timee");
				times = timesJson.getString("times");
				break;
			case 3:
				dateByYear = DateUtil.getDateByYear(-3);
		    	timesJson = DateUtil.dateRoll2(dateByYear, Calendar.YEAR, -5);
		    	timee = timesJson.getString("timee");
				times = timesJson.getString("times");
				break;
			case 4:
				dateByYear = DateUtil.getDateByYear(-5);
		    	timesJson = DateUtil.dateRoll2(dateByYear, Calendar.YEAR, -10);
		    	timee = timesJson.getString("timee");
				times = timesJson.getString("times");
				break;
			case 5:
				dateByYear = DateUtil.getDateByYear(-10);
		    	timesJson = DateUtil.dateRoll2(dateByYear, Calendar.YEAR, -20);
		    	timee = timesJson.getString("timee");
				times = timesJson.getString("times");
				break;
			case 6:
				dateByYear = DateUtil.getDateByYear(-20);
		    	timesJson = DateUtil.dateRoll2(dateByYear, Calendar.YEAR, -50);
		    	timee = timesJson.getString("timee");
				times = timesJson.getString("times");
				break;
			default:
				break;
			}
			String statusStr = "";
			switch (searchParam.getStatus()) {
				case 0: statusStr = ""; break;
				case 1: statusStr = "在业"; break;
				case 2: statusStr = "吊销"; break;
				case 3: statusStr = "注销"; break;
				case 4: statusStr = "迁出"; break;
				case 5: statusStr = "存续"; break;
				case 6: statusStr = "暂无"; break;
				default: break;
			}
			String url = es_hot_search_url + SearchConstant.ES_API_COMPANY_LIST;
			String param = "size="+searchParam.getPageSize()+"&matchingmode=" + searchParam.getMatchType()
					+ "&page=" + searchParam.getPageNum() + "&keyword=" + searchParam.getSearchWord() + "&companystatus=" + URLEncoder.encode(statusStr, "utf-8")
					+ "&industry_involved=" + searchParam.getRtype() + "&label=&times=" + times + "&timee=" + timee;
			if(searchParam.getSortType() != null) {
				param = param + "&searchType=" + searchParam.getSortType();
			}
			response = MyHttpRequestUtil.sendPostEsSearch(url, param);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", list);
			result.put("totalCount", 0);
			result.put("totalPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			Integer code = parseObject.getInteger("code");
			Integer totalData = parseObject.getInteger("count");
			Integer totalPage = parseObject.getInteger("page_count");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");

				BigDecimal divide=new BigDecimal(0);
				if (!"-".equals(jsonObject2.getString("registered_capital"))){
					BigDecimal registered_capital = jsonObject2.getBigDecimal("registered_capital");
					BigDecimal bigDecimal = new BigDecimal(10000);
					divide = registered_capital.divide(bigDecimal).setScale(0, BigDecimal.ROUND_HALF_UP);
				}

				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});

				params.put("registered_capital",divide+"");
				list.add(params);
			}
			if (totalData > 5000) {
				totalPage = 500;
			}
			if (totalPage == 0) {
				totalPage = 1;
			}
			result.put("code", code);
			result.put("list", list);
			result.put("page", searchParam.getPageNum());
			result.put("totalCount", totalData);
			result.put("totalPage", totalPage);
		}else {
			result.put("code", 500);
			result.put("list", list);
			result.put("totalCount", 0);
			result.put("totalPage", 1);
		}
		return result;
	}

	@Override
	public JSONArray companyIndustry(FullSearchParam searchParam) {
		String url = es_search_url + SearchConstant.ES_API_COMPANY_INDUSTRY;
		String param = "matchingmode=1&keyword="+searchParam.getSearchWord();
		JSONArray resJson = new JSONArray();
		String response = MyHttpRequestUtil.sendPostEsSearch(url, param);
		JSONObject parseObject = JSON.parseObject(response);
		if (parseObject.containsKey("aggregations")) {
			JSONObject jsonObject = parseObject.getJSONObject("aggregations");
			if (jsonObject.containsKey("group_by_tags")) {
				JSONObject jsonObject2 = jsonObject.getJSONObject("group_by_tags");
				JSONArray jsonArray = jsonObject2.getJSONArray("buckets");
		        for (int i = 0; i < jsonArray.size(); i++) {
		        	JSONObject jsonObject3 = jsonArray.getJSONObject(i);
		        	String string = jsonObject3.getString("key");
		        	JSONObject json = new JSONObject();
		        	json.put("name", string);
		        	json.put("value", string);
		        	resJson.add(json);
				}
			}
		}
		return resJson;
	}
	
	@Override
	public JSONObject companyDetails(String article_public_id) {
		String url = es_search_url + SearchConstant.ES_API_COMPANY_DETAIL;
		String param = "article_public_id=" + article_public_id;
		JSONObject parseObject = new JSONObject();
		String response = MyHttpRequestUtil.sendPostEsSearch(url, param);
		parseObject = JSON.parseObject(response);
		return parseObject;
	}
	
	@Override
	public JSONObject judgmentList(FullSearchParam searchParam) {
		String response = "";
		JSONObject result = new JSONObject();
		List<Map<String, String>> list = new ArrayList<>();
		try {
			if(StringUtils.equals("全部", searchParam.getRtype())) {
				searchParam.setRtype("");
			}
			String timee = "",times = "";
			switch (searchParam.getPublish()) {
				case 0: break;
				case 2020: timee = "2020-12-30 23:59:59"; times = "2020-01-01 00:00:00"; break;
				case 2019: timee = "2019-12-30 23:59:59"; times = "2019-01-01 00:00:00"; break;
				case 2018: timee = "2018-12-30 23:59:59"; times = "2018-01-01 00:00:00"; break;
				case 2017: timee = "2017-12-30 23:59:59"; times = "2017-01-01 00:00:00"; break;
				case 2016: timee = "2016-12-30 23:59:59"; times = "2016-01-01 00:00:00"; break;
				case 1: timee = "2015-12-30 23:59:59"; times = ""; break;
				default: break;
			}
			int filterType=0;
			if(searchParam.getMatchingmode()!=null) {
				switch (searchParam.getMatchingmode()) {
				case "标题":
					filterType=1;
					break;
				case "正文":
					filterType=2;
					break;
				case "当事人":
					filterType=3;
					break;
				case "法院":
					filterType=4;
					break;
				case "地区":
					filterType=5;
					break;
				}
			}
			
			String url = es_search_url + SearchConstant.ES_API_JUDGMENT_LIST;
			String param = "size="+searchParam.getPageSize()+"&matchingmode=" + filterType
					+ "&page=" + searchParam.getPageNum() + "&keyword=" + searchParam.getSearchWord()
					+ "&caseType=" + searchParam.getRtype() + "&times=" + times + "&timee=" + timee;
			if(searchParam.getSortType() != null) {
				param = param + "&searchType=" + searchParam.getSortType();
			}
			response = MyHttpRequestUtil.sendPostEsSearch(url, param);
			//System.out.println(response);//控制台输出数据
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", list);
			result.put("totalCount", 0);
			result.put("totalPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			Integer code = parseObject.getInteger("code");
			Integer totalData = parseObject.getInteger("count");
			Integer totalPage = parseObject.getInteger("page_count");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				list.add(params);
			}
			if (totalData > 5000) {
				totalPage = 500;
			}
			if (totalPage == 0) {
				totalPage = 1;
			}
			result.put("code", code);
			result.put("list", list);
			result.put("page", searchParam.getPageNum());
			result.put("totalCount", totalData);
			result.put("totalPage", totalPage);
		}else {
			result.put("code", 500);
			result.put("list", list);
			result.put("totalCount", 0);
			result.put("totalPage", 1);
		}
		return result;
	}
	
	@Override
	public JSONArray judgmentCaseType(FullSearchParam searchParam) {
		String url = es_search_url + SearchConstant.ES_API_JUDGMENT_CASE_TYPE;
		String param = "matchingmode=1&keyword="+searchParam.getSearchWord();
		JSONArray resJson = new JSONArray();
		String response = MyHttpRequestUtil.sendPostEsSearch(url, param);
		JSONObject parseObject = JSON.parseObject(response);
		if (parseObject.containsKey("aggregations")) {
			JSONObject jsonObject = parseObject.getJSONObject("aggregations");
			if (jsonObject.containsKey("group_by_tags")) {
				JSONObject jsonObject2 = jsonObject.getJSONObject("group_by_tags");
				JSONArray jsonArray = jsonObject2.getJSONArray("buckets");
		        for (int i = 0; i < jsonArray.size(); i++) {
		        	JSONObject jsonObject3 = jsonArray.getJSONObject(i);
		        	String string = jsonObject3.getString("key");
		        	JSONObject json = new JSONObject();
		        	json.put("name", string);
		        	json.put("value", string);
		        	resJson.add(json);
				}
			}
		}
		return resJson;
	}
	
	@Override
	public JSONObject judgmentDetail(String articleid) {
		 JSONObject post_json=new JSONObject();
         post_json.put("index","judgment");
         post_json.put("type","infor");
         post_json.put("fieldname","article_public_id");
         post_json.put("fielddata",articleid);
	     String responseData="";
		try {
			String url = es_search_url + SearchConstant.ES_API_COMMON_DATA_DETAIL;
			responseData = MyHttpRequestUtil.getJsonHtml(url, post_json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject returnObj = JSON.parseObject(responseData);
	     String content="";
	     for(int i=1;i<12;i++) {
	     	String estend="extend"+i;
	     	content+=returnObj.getString(estend);
	     	returnObj.remove(estend);
	     }
	     returnObj.put("content", content);
	     return returnObj;
	}
	
	@Override
	public JSONObject knowLedgeList(FullSearchParam searchParam) {
		JSONObject retuenObj = new JSONObject();
        try {
        	if(StringUtils.equals("全部", searchParam.getRtype())) {
				searchParam.setRtype("");
			}
        	String timee = "",times = "";
			switch (searchParam.getPublish()) {
				case 0: break;
				case 2020: timee = "2020-12-30 23:59:59"; times = "2020-01-01 00:00:00"; break;
				case 2019: timee = "2019-12-30 23:59:59"; times = "2019-01-01 00:00:00"; break;
				case 2018: timee = "2018-12-30 23:59:59"; times = "2018-01-01 00:00:00"; break;
				case 2017: timee = "2017-12-30 23:59:59"; times = "2017-01-01 00:00:00"; break;
				case 2016: timee = "2016-12-30 23:59:59"; times = "2016-01-01 00:00:00"; break;
				case 1: timee = "2015-12-30 23:59:59"; times = ""; break;
				default: break;
			}
            String param = "times=" + times + "&timee=" + timee + "&size="+searchParam.getPageSize()+"&page="+searchParam.getPageNum()
            	+"&searchtype="+2+"&keyword="+searchParam.getSearchWord()+"&type="+searchParam.getRtype();
            String url = es_search_url + SearchConstant.ES_API_PATENT_LIST;
            String response = MyHttpRequestUtil.sendPostEsSearch(url, param);
            JSONObject responseJson = JSON.parseObject(response);
            Integer code = responseJson.getInteger("code");
            Integer totalCount = responseJson.getInteger("count");
            Integer page_count = responseJson.getInteger("page_count");
            Integer pageNum = responseJson.getInteger("page");
            retuenObj.put("code", code);
            retuenObj.put("totalCount", totalCount);
            retuenObj.put("totalPage", page_count);
            retuenObj.put("page", pageNum);

            JSONArray dataArray = responseJson.getJSONArray("data");
            JSONArray retuenArray = new JSONArray();
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject dataObj = (JSONObject) dataArray.get(i);
                JSONObject _sourceObj = dataObj.getJSONObject("_source");
                retuenArray.add(_sourceObj);
            }
            retuenObj.put("list", retuenArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retuenObj;
	}
	
	@Override
	public JSONArray knowLedgeCaseType(FullSearchParam searchParam) {
		String timee = "",times = "";
		switch (searchParam.getPublish()) {
			case 0: break;
			case 2020: timee = "2020-12-30 23:59:59"; times = "2020-01-01 00:00:00"; break;
			case 2019: timee = "2019-12-30 23:59:59"; times = "2019-01-01 00:00:00"; break;
			case 2018: timee = "2018-12-30 23:59:59"; times = "2018-01-01 00:00:00"; break;
			case 2017: timee = "2017-12-30 23:59:59"; times = "2017-01-01 00:00:00"; break;
			case 2016: timee = "2016-12-30 23:59:59"; times = "2016-01-01 00:00:00"; break;
			case 1: timee = "2015-12-30 23:59:59"; times = ""; break;
			default: break;
		}
		String param="searchtype=2&keyword="+searchParam.getSearchWord()+"&times="+times+"&timee="+timee;	
		String url = es_search_url + SearchConstant.ES_API_PATENT_TYPE;
		String response = MyHttpRequestUtil.sendPostEsSearch(url, param);
        JSONObject parseObject = JSONObject.parseObject(response);
        JSONObject jsonObject = parseObject.getJSONObject("aggregations").getJSONObject("group_by_tags");
        JSONArray jsonArray = jsonObject.getJSONArray("buckets");
        JSONArray resJson = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
        	JSONObject jsonObject3 = jsonArray.getJSONObject(i);
        	String string = jsonObject3.getString("key");
        	JSONObject json = new JSONObject();
        	json.put("name", string);
        	json.put("value", string);
        	resJson.add(json);
		}
        return resJson;
	}
	
	@Override
	public JSONObject knowLedgeDetail(String articleid) {
		 JSONObject returnObj = new JSONObject();
	        try {
	            String param = "article_public_id=" + articleid;
	            String url = es_search_url + SearchConstant.ES_API_PATENT_DETAIL;
	            String response = MyHttpRequestUtil.sendPostEsSearch(url, param);
	            JSONObject responseObj = JSON.parseObject(response);
	            String title = responseObj.getString("title");
	            String applyDay = responseObj.getString("applyDay");
	            String applyMark = responseObj.getString("applyMark");
	            String imgUrl = responseObj.getString("imgUrl");
	            String content_html = responseObj.getString("content_html");
	            returnObj.put("title", title);
	            returnObj.put("applyDay", applyDay);
	            returnObj.put("applyMark", applyMark);
	            returnObj.put("imgUrl", imgUrl);
	            returnObj.put("content_html", content_html);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return returnObj;
	}
	
	@Override
	public JSONObject investmentList(FullSearchParam searchParam) {
//		判断选择的类型是正文还是标题还是全部
		int matchingmode = 0;
		if(("全部").equals(searchParam.getMatchType())) {
			matchingmode = 0;
		}else if(("标题").equals(searchParam.getMatchType())) {
			matchingmode = 1;
		}else if(("正文").equals(searchParam.getMatchType())) {
			matchingmode = 2;
		}
		if(StringUtils.equals("全部", searchParam.getRtype())) {
			searchParam.setRtype("");
		}
    	String timee = "",times = "";
		switch (searchParam.getPublish()) {
			case 0: break;
			case 2020: timee = "2020-12-30 23:59:59"; times = "2020-01-01 00:00:00"; break;
			case 2019: timee = "2019-12-30 23:59:59"; times = "2019-01-01 00:00:00"; break;
			case 2018: timee = "2018-12-30 23:59:59"; times = "2018-01-01 00:00:00"; break;
			case 2017: timee = "2017-12-30 23:59:59"; times = "2017-01-01 00:00:00"; break;
			case 2016: timee = "2016-12-30 23:59:59"; times = "2016-01-01 00:00:00"; break;
			case 1: timee = "2015-12-30 23:59:59"; times = ""; break;
			default: break;
		}
		String response = "";
		JSONObject result = new JSONObject();
		List<Map<String, String>> list = new ArrayList<>();
		try {
			String param = "size="+searchParam.getPageSize()+"&matchingmode=" + matchingmode + "&searchType=0" 
					+ "&times=" + times + "&timee=" + timee + "&page=" + searchParam.getPageNum() + "&keyword=" 
					+ searchParam.getSearchWord() + "&rounds="+searchParam.getRtype();
			String url = es_search_url + SearchConstant.ES_API_INVESTMENT_LIST;
            response = MyHttpRequestUtil.sendPostEsSearch(url, param);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("code", 500);
			result.put("list", list);
			result.put("totalCount", 0);
			result.put("totalPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			Integer code = parseObject.getInteger("code");
			Integer totalData = parseObject.getInteger("count");
			Integer totalPage = parseObject.getInteger("page_count");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				params.put("content", params.get("abstract"));
				list.add(params);
			}
			if (totalPage == 0) {
				totalPage = 1;
			}
			result.put("code", code);
			result.put("list", list);
			result.put("totalCount", totalData);
			result.put("totalPage", totalPage);
		}else {
			result.put("code", 500);
			result.put("list", list);
			result.put("totalCount", 0);
			result.put("totalPage", 1);
		}
		return result;
	}
	
	@Override
	public JSONArray investmentType(FullSearchParam searchParam) {
		JSONObject json=new JSONObject();
		JSONArray resJson = new JSONArray();
		JSONObject jsonObject = new JSONObject();
//		判断选择的类型是正文还是标题还是全部
		int matchingmode = 0;
		if(("全部").equals(searchParam.getMatchType())) {
			matchingmode = 0;
		}else if(("标题").equals(searchParam.getMatchType())) {
			matchingmode = 1;
		}else if(("正文").equals(searchParam.getMatchType())) {
			matchingmode = 2;
		}
		String timee = "",times = "";
		switch (searchParam.getPublish()) {
			case 0: break;
			case 2020: timee = "2020-12-30 23:59:59"; times = "2020-01-01 00:00:00"; break;
			case 2019: timee = "2019-12-30 23:59:59"; times = "2019-01-01 00:00:00"; break;
			case 2018: timee = "2018-12-30 23:59:59"; times = "2018-01-01 00:00:00"; break;
			case 2017: timee = "2017-12-30 23:59:59"; times = "2017-01-01 00:00:00"; break;
			case 2016: timee = "2016-12-30 23:59:59"; times = "2016-01-01 00:00:00"; break;
			case 1: timee = "2015-12-30 23:59:59"; times = ""; break;
			default: break;
		}
		try {
				String param = "matchingmode="+matchingmode+"&keyword="+searchParam.getSearchWord()+"&times="+times+"&timee="+timee;	
				String url = es_search_url + SearchConstant.ES_API_INVESTMENT_TYPE;
				String response = MyHttpRequestUtil.sendPostEsSearch(url, param);
	            json = JSONObject.parseObject(response);
	            jsonObject = json.getJSONObject("aggregations").getJSONObject("group_by_tags");
		        JSONArray jsonArray =jsonObject.getJSONArray("buckets");
		        for (int i = 0; i < jsonArray.size(); i++) {
		        	JSONObject jsonObject3 = jsonArray.getJSONObject(i);
		        	String string = jsonObject3.getString("key");
		        	JSONObject jsons = new JSONObject();
		        	jsons.put("name", string);
		        	jsons.put("value", string);
		        	resJson.add(jsons);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resJson;
	}
	
	@Override
	public JSONObject investmentDetail(String articleid) {
		String param = "article_public_id=" + articleid;
		JSONObject jsonObject = new JSONObject();
		String url = es_search_url + SearchConstant.ES_API_INVESTMENT_DETAIL;
		String response = MyHttpRequestUtil.sendPostEsSearch(url, param);
		jsonObject = JSON.parseObject(response);
		return jsonObject;
	}
	
	@Override
	public JSONObject baiduKnowsList(FullSearchParam searchParam) {
		String response = "";
		JSONObject result = new JSONObject();
		List<Map<String, String>> list = new ArrayList<>();
		try {
			String times = "", timee = "";
			if(searchParam.getSortType() == 1) searchParam.setSortType(0);
			if(searchParam.getSortType() == 2) searchParam.setSortType(1);
			String url = es_search_url + SearchConstant.ES_API_BAIDU_KNOWS_LIST;
			String param = "size="+searchParam.getPageSize()+"&times=" + times + "&timee=" + timee + "&page=" 
					+ searchParam.getPageNum() + "&keyword=" + searchParam.getSearchWord() 
					+ "&matchingmode=" + searchParam.getMatchType() + "&searchType=" + searchParam.getSortType();
			response = MyHttpRequestUtil.sendPostEsSearch(url, param);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("code", 500);
			result.put("page", searchParam.getPageNum());
			result.put("list", list);
			result.put("totalCount", 0);
			result.put("totalPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			Integer code = parseObject.getInteger("code");
			Integer totalData = parseObject.getInteger("count");
			Integer page = parseObject.getInteger("page");
			Integer totalPage = parseObject.getInteger("page_count");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				list.add(params);
			}
			if (totalPage == 0) totalPage = 1;
			result.put("code", code);
			result.put("list", list);
			result.put("page", page);
			result.put("totalCount", totalData);
			result.put("totalPage", totalPage);
		}else {
			result.put("code", 500);
			result.put("list", list);
			result.put("totalCount", 0);
			result.put("totalPage", 1);
		}
		return result;
	}
	
	@Override
	public JSONObject thesisnList(FullSearchParam searchParam) {
		String times = "", timee = "";
		Integer matchingmode = 0;
		if(searchParam.getMatchType() == 1) {
			matchingmode = 0;
		}else if(searchParam.getMatchType() == 2) {
			matchingmode = 1;
		}else if(searchParam.getMatchType() == 3) {
			matchingmode = 2;
		}
		if(searchParam.getSortType() == 1) searchParam.setSortType(0);
		if(searchParam.getSortType() == 2) searchParam.setSortType(1);
		String response = "";
		JSONObject result = new JSONObject();
		List<Map<String, String>> list = new ArrayList<>();
		try {
			String param = "size="+searchParam.getPageSize()+"&matchingmode=" + matchingmode + "&times=" + times + "&timee=" 
					+ timee + "&page=" + searchParam.getPageNum() + "&keyword=" + searchParam.getSearchWord() + "&searchType=" + searchParam.getSortType();
			String url = es_search_url + SearchConstant.ES_API_THESISN_LIST;
			response = MyHttpRequestUtil.sendPostEsSearch(url, param);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("code", 500);
			result.put("list", list);
			result.put("totalCount", 0);
			result.put("totalPage", 1);
		}
		if (StringUtils.isNotBlank(response)) {
			JSONObject parseObject = JSON.parseObject(response);
			Integer code = parseObject.getInteger("code");
			Integer totalData = parseObject.getInteger("count");
			Integer totalPage = parseObject.getInteger("page_count");
			Integer page = parseObject.getInteger("page");
			JSONArray jsonArray = parseObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
				Map<String, String> params = JSONObject.parseObject(jsonObject2.toJSONString(), new TypeReference<Map<String, String>>(){});
				params.put("jianjie", params.get("abstract"));
				list.add(params);
			}
			
			if (totalPage == 0) totalPage = 1;
			result.put("code", code);
			result.put("list", list);
			result.put("page", page);
			result.put("totalCount", totalData);
			result.put("totalPage", totalPage);
		}else {
			result.put("list", list);
			result.put("totalCount", 0);
			result.put("totalPage", 1);
		}
		return result;
	}
	
	@Override
	public JSONObject thesisnDetail(String articleid) {
		String param = "record_id=" + articleid;
		String url = es_search_url + SearchConstant.ES_API_THESISN_DETAIL;
		String response = MyHttpRequestUtil.sendPostEsSearch(url, param);
		JSONObject result = JSON.parseObject(response);
		return result;
	}

	@Override
	public List<FullType> listFullTypeByFirst() {
		return fullSearchDao.listFullTypeOne();
	}

	@Override
	public List<FullType> listFullTypeBysecond(Integer type_one_id) {
		return fullSearchDao.listFullTypeBysecond(type_one_id);
	}

	@Override
	public List<FullType> listFullTypeBythird(Integer type_two_id) {
		return fullSearchDao.listFullTypeBythird(type_two_id);
	}

	@Override
	public List<FullPolymerization> listFullPolymerization() {
		return fullSearchDao.listFullPolymerization();
	}

	@Override
	public List<FullType> listFullTypeOneByIdList(List<Integer> idList) {
		return fullSearchDao.listFullTypeOneByIdList(idList);
	}

	@Override
	public JSONObject getBreadCrumbs(Integer menuStyle, Integer fulltype, Integer onlyid, Integer polyid) {
		JSONObject jsonObject = new JSONObject();
		if(menuStyle == 0) {
			String breadCrumbsByOnlyId = fullSearchDao.getBreadCrumbsByOnlyId(onlyid);
			String breadCrumbsByPolyId = fullSearchDao.getBreadCrumbsByPolyId(polyid);
			jsonObject.put("onlyIdName", breadCrumbsByOnlyId);
			jsonObject.put("polyIdName", breadCrumbsByPolyId);
		}if(menuStyle == 1) {
			String breadCrumbsByFullType = fullSearchDao.getBreadCrumbsByFullType(fulltype);
			jsonObject.put("fullTypeName", breadCrumbsByFullType);
		}
		return jsonObject;
	}

    @Override
    public Boolean saveFullWord(FullWord fullWord) {
        return fullSearchDao.saveFullWord(fullWord);
    }

    @Override
    public JSONObject getSearchWordById(JSONObject paramJson) {
	    JSONObject response = new JSONObject();
	    response.put("code",500);
	    response.put("msg","获取搜索词失败");
	    try {
            List<Map<String, Object>> list = fullSearchDao.getSearchWordById(paramJson);
            response.put("code",200);
            response.put("msg","获取搜索词成功");
            response.put("data",list);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    @Override
	public String hotBaiduList() {
    	
		return HotWordsUtil.search2();
		
	}
	

    /**
	 *  12小时热点
	 */
//	@Override
//	public List<Map<String, Object>> twelveHotArticle() {
//		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
//		try {
//			String url = es_search_url + SearchConstant.es_api_hot_rank;
//			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//			LocalDateTime now = LocalDateTime.now();
//			String timee = now.format(dateTimeFormatter);
//			String times = now.minusHours(12).format(dateTimeFormatter);
//			String params = "times="+times+"&timee="+timee+"&interval=2h&emotionalIndex=1,2,3";
//			String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
//			if (StringUtils.isNotBlank(sendPostEsSearch)) {
//				JSONObject parseObject = JSON.parseObject(sendPostEsSearch);
//				JSONArray jsonArray = parseObject.getJSONObject("aggregations").getJSONObject("top_score")
//						.getJSONArray("buckets");
//				for (int i = 0; i < jsonArray.size(); i++) {
//					JSONArray jsonArray2 = jsonArray.getJSONObject(i).getJSONObject("top_score_hits").getJSONObject("hits")
//							.getJSONArray("hits");
//					if (!jsonArray2.isEmpty()) {
//						JSONObject jsonObject = jsonArray2.getJSONObject(0);
//				         Map<String, Object> data = 
//			                		JSON.parseObject(jsonObject.getString("_source"), new TypeReference<Map<String, Object>>(){});
//				         result.add(data);
//					}
//				}
//				Collections.reverse(result);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
}
