package com.stonedt.intelligence.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.entity.PublicoptionDetailEntity;
import com.stonedt.intelligence.entity.PublicoptionEntity;

public interface PublicOptionService {
	
	List<PublicoptionEntity> getlist(Long user_id, String searchkeyword);

	PublicoptionEntity getdatabyid(Map<String, Object> mapParam);
	
	PublicoptionEntity getdatabyid2(Integer id);

	String updatabyid(Integer id, String eventname, String eventkeywords, String eventstarttime, String eventendtime,
			String eventstopwords);

	String addpublicoptiondata(Long user_id,String eventname, String eventkeywords, String eventstarttime, String eventendtime,
			String eventstopwords);

	String DeleteupinfoByIds(String ids, HttpServletRequest request);

	PublicoptionDetailEntity getdetail(Map<String, Object> mapParam);

	List<PublicoptionEntity> getpublicoptionreportlist(Long user_id, String searchkeyword);

	String getBackAnalysisById(Integer id);

	String getEventContextById(Integer id);

	String getEventTraceById(Integer id);

	String getHotAnalysisById(Integer id);

	String getNetizensAnalysisById(Integer id);

	String getStatisticsById(Integer id);

	String getPropagationAnalysisById(Integer id);

	String getThematicAnalysisById(Integer id);

	String getUnscrambleContentById(Integer id);

	JSONObject loadInformation(PublicoptionEntity publicoptionEntity);
	
}
