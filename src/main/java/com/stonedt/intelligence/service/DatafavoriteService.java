package com.stonedt.intelligence.service;

import com.stonedt.intelligence.entity.DatafavoriteEntity;

public interface DatafavoriteService {

	String adddata(Long user_id, String id, Long projectid, Long groupid, String title, String source_name,
			String emotionalIndex, String publish_time);

	void updateemtion(String id, int flag, String es_search_url,String publish_time);

	void deletedata(String id, int flag, String es_search_url,String publish_time);

	String updatedata(Long user_id, String id, Long projectid, Long groupid, String title, String source_name,
			String emotionalIndex, String publish_time);

	DatafavoriteEntity selectdata(Long user_id, String id);

}
