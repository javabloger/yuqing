package com.stonedt.intelligence.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.constant.MonitorConstant;
import com.stonedt.intelligence.dao.DatafavoriteDao;
import com.stonedt.intelligence.entity.DatafavoriteEntity;
import com.stonedt.intelligence.service.DatafavoriteService;
import com.stonedt.intelligence.util.MyHttpRequestUtil;

@Service
public class DatafavoriteServiceImpl implements DatafavoriteService{
	
	
	
	@Autowired
	private DatafavoriteDao datafavoriteDao;

	@Override
	public String adddata(Long user_id, String id, Long projectid, Long groupid, String title, String source_name,
			String emotionalIndex, String publish_time) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 200);
		map.put("result", "success");
		try {
			DatafavoriteEntity favorite = new DatafavoriteEntity();
			favorite.setUser_id(user_id);
			favorite.setArticle_public_id(id);
			favorite.setProjectid(projectid);
			favorite.setGroupid(groupid);
			favorite.setTitle(title);
			favorite.setSource_name(source_name);
			favorite.setEmotionalIndex(Integer.parseInt(emotionalIndex));
			favorite.setPublish_time(publish_time);
			datafavoriteDao.adddata(favorite);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", 500);
			map.put("result", "fail");
		}
		return JSON.toJSONString(map);
	}

	@Override
	public void updateemtion(String id, int flag, String es_search_url,String publish_time) {
		// TODO 自动生成的方法存根
		String url = es_search_url + MonitorConstant.es_api_updateemtion;
        String params = "article_public_id=" + id + "&esindex=postal&estype=infor&publish_time="+publish_time+"&emotiontype="+flag;
        MyHttpRequestUtil.sendPostEsSearch(url, params);
	}

	@Override
	public void deletedata(String id, int flag, String es_search_url,String publish_time) {
		// TODO 自动生成的方法存根
		String url = es_search_url + MonitorConstant.es_api_deletedata;
        String params = "article_public_id=" + id + "&esindex=postal&estype=infor&publish_time="+publish_time;
        MyHttpRequestUtil.sendPostEsSearch(url, params);
	}

	@Override
	public String updatedata(Long user_id, String id, Long projectid, Long groupid, String title, String source_name,
			String emotionalIndex, String publish_time) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 200);
		map.put("result", "success");
		try {
			DatafavoriteEntity favorite = new DatafavoriteEntity();
			favorite.setUser_id(user_id);
			favorite.setArticle_public_id(id);
			favorite.setProjectid(projectid);
			favorite.setGroupid(groupid);
			favorite.setTitle(title);
			favorite.setSource_name(source_name);
			favorite.setEmotionalIndex(Integer.parseInt(emotionalIndex));
			favorite.setPublish_time(publish_time);
			datafavoriteDao.updatedata(favorite);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", 500);
			map.put("result", "fail");
		}
		return JSON.toJSONString(map);
	}

	@Override
	public DatafavoriteEntity selectdata(Long user_id, String id) {
		// TODO 自动生成的方法存根
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_id", user_id);
		map.put("public_id", id);
		DatafavoriteEntity res = datafavoriteDao.selectdata(map);
		return res;
	}

}
