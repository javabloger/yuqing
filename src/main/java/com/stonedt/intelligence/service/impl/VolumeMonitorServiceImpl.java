package com.stonedt.intelligence.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.dao.VolumeMonitorDao;
import com.stonedt.intelligence.entity.VolumeMonitor;
import com.stonedt.intelligence.service.VolumeMonitorService;

/** 
* @author 作者 ZouFangHao: 
*/
@Service
public class VolumeMonitorServiceImpl implements VolumeMonitorService {
	
	@Autowired
	VolumeMonitorDao volumeMonitorDao;

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String,Object> getproject(Map<String, Object> map) {
		Map<String,Object> hashMap = new HashMap<String, Object>();
		Map<String,Object> keywordsMood = new HashMap<String, Object>();//keywordsMood  
		Map<String,Object> biaoge = new HashMap<String, Object>();//表格 
		VolumeMonitor getproject = volumeMonitorDao.getproject(map);
		if (getproject == null) {
			return hashMap;
		}
		String keyword_emotion_statistical = getproject.getKeyword_emotion_statistical();
		if (!StringUtils.isEmpty(keyword_emotion_statistical)) {
			JSONObject parseObject = JSONObject.parseObject(keyword_emotion_statistical);
			JSONArray positive = parseObject.getJSONArray("positive");
			JSONArray negative = parseObject.getJSONArray("negative");
			Integer keyword_count = parseObject.getInteger("keyword_count");
			String chinaString = "";
//			if (keyword_count > 1) {
//				JSONObject object = JSONObject.parseObject(String.valueOf(positive.get(0)));
//				String keyword = object.getString("keyword");//正面占比最高关键词
//				String rate = object.getString("rate");//占比
//				
//				JSONObject object2 = JSONObject.parseObject(String.valueOf(positive.get(1)));
//				String keyword2 = object2.getString("keyword");//其次是正面关键词
//				String rate2 = object2.getString("rate");//占比
//				chinaString = "您一共设置了 "+keyword_count+"个关键词，正面占比最高的是【"+keyword+"】到达"+rate+"，其次是【"+keyword2+"】到达"+rate2+"。负面占比最高的是【"+negative.getJSONObject(0).getString("keyword")+"】到达"+negative.getJSONObject(0).getString("rate")+"，其次是【"+negative.getJSONObject(1).getString("keyword")+"】到达"+negative.getJSONObject(1).getString("rate")+"。";
//			}else if (keyword_count==1) {
//				chinaString = "您一共设置了 "+keyword_count+"个关键词，正面占比最高的是【"+positive.getJSONObject(0).getString("keyword")+"】到达"+positive.getJSONObject(0).getString("rate")+"。负面占比最高的是【"+negative.getJSONObject(0).getString("keyword")+"】到达"+negative.getJSONObject(0).getString("rate")+"。";
//			}
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("您一共设置了").append(keyword_count).append("个关键词。");
			if (positive.size() > 0) {
				JSONObject object = JSONObject.parseObject(String.valueOf(positive.get(0)));
				String keyword = object.getString("keyword");//正面占比最高关键词
				String rate = object.getString("rate");//占比
				stringBuilder.append("正面占比最高的是【"+keyword+"】到达"+rate+"。");
			}
			if (positive.size() > 1) {
				JSONObject object = JSONObject.parseObject(String.valueOf(positive.get(1)));
				String keyword = object.getString("keyword");//正面占比最高关键词
				String rate = object.getString("rate");//占比
				stringBuilder.append("其次是【"+keyword+"】到达"+rate+"。");
			}
			if (negative.size() > 0) {
				JSONObject object = JSONObject.parseObject(String.valueOf(negative.get(0)));
				String keyword = object.getString("keyword");//正面占比最高关键词
				String rate = object.getString("rate");//占比
				stringBuilder.append("负面占比最高的是【"+keyword+"】到达"+rate+"。");
			}
			if (negative.size() > 1) {
				JSONObject object = JSONObject.parseObject(String.valueOf(negative.get(1)));
				String keyword = object.getString("keyword");//正面占比最高关键词
				String rate = object.getString("rate");//占比
				stringBuilder.append("其次是【"+keyword+"】到达"+rate+"。");
			}
			chinaString = stringBuilder.toString();
			keywordsMood.put("china", chinaString);
			JSONArray parseArray = JSONArray.parseArray(parseObject.getString("chart"));
			List<Object> arrayList = new ArrayList<Object>();
			for (int i = 0; i < parseArray.size(); i++) {
				List<Object> list = new ArrayList<Object>();
				JSONObject jsonObject = JSONObject.parseObject(String.valueOf(parseArray.get(i)));
				String keyword = jsonObject.getString("keyword");
				Integer positive_num = jsonObject.getInteger("positive_num");
				Integer negative_num = jsonObject.getInteger("negative_num");
				list.add(keyword);
				list.add(positive_num);
				list.add(negative_num);
				arrayList.add(list);
			}
			keywordsMood.put("list", arrayList);
		}
		hashMap.put("keywordsMood", keywordsMood);
		
		String keyword_source_distribution = getproject.getKeyword_source_distribution();//关键词数据来源分布
		if (!StringUtils.isEmpty(keyword_source_distribution)) {
			JSONObject parseObject = JSONObject.parseObject(keyword_source_distribution);
			Integer keyword_count = parseObject.getInteger("keyword_count");
			JSONArray chart = JSONObject.parseArray(parseObject.getString("chart"));
			JSONArray text = parseObject.getJSONArray("text");
			String chinaString = "";
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("您一共设置了").append(keyword_count).append("个关键词。");
			if (text.size() > 0) {
				stringBuilder.append("【"+text.getJSONObject(0).getString("keyword")+"】数据量占比最高到达"+text.getJSONObject(0).getString("rate")+"，【"+text.getJSONObject(0).getString("key")+"】信息来源最多。");
			}
			if (text.size() > 1) {
				stringBuilder.append("其次是【"+text.getJSONObject(1).getString("keyword")+"】数据量占比最高到达"+text.getJSONObject(1).getString("rate")+"，【"+text.getJSONObject(1).getString("key")+"】信息来源最多。");
			}
			chinaString = stringBuilder.toString();
//			try {
//				if (keyword_count > 1) {
//					chinaString = "您一共设置了 "+keyword_count+"个关键词，【"+text.getJSONObject(0).getString("keyword")+"】数据量占比最高到达"+text.getJSONObject(0).getString("rate")+"，【"+text.getJSONObject(0).getString("key")+"】信息来源最多，其次是【"+text.getJSONObject(0).getString("keyword")+"】数据量占比最高到达"+text.getJSONObject(0).getString("rate")+"，【"+text.getJSONObject(0).getString("key")+"】信息来源最多。";
//				}else if (keyword_count==1) {
//					chinaString = "您一共设置了 "+keyword_count+"个关键词，【"+text.getJSONObject(0).getString("keyword")+"】数据量占比最高到达"+text.getJSONObject(0).getString("rate")+"，【"+text.getJSONObject(0).getString("key")+"】信息来源最多。";
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				if (keyword_count>1) {
//					chinaString = "您一共设置了 "+keyword_count+"个关键词。";
//				}else if (keyword_count==1) {
//					chinaString = "您一共设置了 "+keyword_count+"个关键词。";
//				}
//			}
			biaoge.put("china", chinaString);
			biaoge.put("list", chart);
		}
		hashMap.put("biaoge", biaoge);
		
		String keyword_emotion_trend = getproject.getKeyword_emotion_trend();
		List<Object> arrayList = new ArrayList<Object>();
		if (!StringUtils.isEmpty(keyword_emotion_trend)) {	//关键词情感分析数据走势
			JSONArray parseArray = JSONObject.parseArray(keyword_emotion_trend);
			for (int i = 0; i < parseArray.size(); i++) {
				List<Object> list = new ArrayList<Object>();
				JSONObject jsonObject = parseArray.getJSONObject(i);
				String time = jsonObject.getString("time"); //时间
				String positive_num = jsonObject.getString("positive_num");
				String negative_num = jsonObject.getString("negative_num");
				if (StringUtils.isEmpty(negative_num)) {
					negative_num="0";
				}
				if (StringUtils.isEmpty(positive_num)) {
					positive_num="0";
				}
				list.add(time);
				list.add(positive_num);
				list.add(negative_num);
				arrayList.add(list);
			}
		}
		hashMap.put("keywordsLine", arrayList);
		
		String keyword_news_rank = getproject.getKeyword_news_rank();//关键词资讯数量排名
		JSONArray keyword_news_rankArray = JSONObject.parseArray(keyword_news_rank);
		hashMap.put("keyword_news_rank", keyword_news_rankArray);
		
		String highword_cloud = getproject.getHighword_cloud(); //关键词高频分布统计
		JSONArray highword_cloudArray = JSONObject.parseArray(highword_cloud);
		List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < highword_cloudArray.size(); i++) {
			Map<String,Object> hashMap2 = new HashMap<String, Object>();
			String jsonObject = highword_cloudArray.getString(i);
			Map parseObject = JSONObject.parseObject(jsonObject, Map.class);
			Object count = parseObject.get("value");
			Object keyword = parseObject.get("keyword");
			hashMap2.put("x", keyword);
			hashMap2.put("value", count);
			list.add(hashMap2);
		}
		hashMap.put("highword_cloud", list);
		
		
		String keyword_exposure_rank = getproject.getKeyword_exposure_rank();//关键词曝光度环比排行
		List<Object> ranklist = new ArrayList<Object>();
		if (!keyword_exposure_rank.isEmpty()) {
			JSONArray parseObject = JSONObject.parseArray(keyword_exposure_rank);
			for (int i = 0; i < parseObject.size(); i++) {
				Map<String,Object> map2 = new HashMap<String, Object>();
				JSONObject jsonObject = parseObject.getJSONObject(i);
				Integer total = jsonObject.getInteger("total");
				Integer trend = jsonObject.getInteger("trend");
				String keyword = jsonObject.getString("keyword");
				String chain_growth = jsonObject.getString("chain_growth").replaceAll("%", "");
				String positive_rate = jsonObject.getString("positive_rate");
				String negative_rate = jsonObject.getString("negative_rate");
				map2.put("total", total);
				map2.put("trend", trend);
				map2.put("keyword", keyword);
				map2.put("chain_growth", chain_growth);
				map2.put("positive_rate", positive_rate);
				map2.put("negative_rate", negative_rate);
				ranklist.add(map2);
			}
		}
		hashMap.put("keyword_exposure_rank", ranklist);
		
		String media_user_volume_rank = getproject.getMedia_user_volume_rank();//自媒体用户声量排名
		JSONArray parseArray = JSONObject.parseArray(media_user_volume_rank);
		hashMap.put("media_user_volume_rank", parseArray);
		
		System.err.println(hashMap);
		return hashMap;
	}

	@Override
	public Map<String, Object> getprojectName(Map<String, Object> map) {
		return volumeMonitorDao.getprojectName(map);
	}
}
