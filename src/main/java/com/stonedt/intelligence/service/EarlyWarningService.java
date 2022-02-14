package com.stonedt.intelligence.service;

import java.util.Map;

/**
* <p>Title: EarlyWarningService</p>  
* @author Mapeng 
*/
public interface EarlyWarningService {

	boolean saveWarningPopup(Map<String, Object> warning_popup);
	
	Map<String, Object> getWarningArticle(Integer pageNum, Long user_id,Long project_id, Integer openFlag);
	
	boolean updateWarningArticle(String article_id,Long user_id);
	
	boolean readSign(Map<String, Object> readsign);

	void delReadSign(Map<String, Object> readsign);

	Map<String, Object> selectReadSign(Map<String, Object> selectreadsign);
}
