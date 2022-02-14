
package com.stonedt.intelligence.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.stonedt.intelligence.dao.SolutionGroupDao;
import com.stonedt.intelligence.dao.SystemDao;
import com.stonedt.intelligence.service.EarlyWarningService;

/**
* <p></p>
* <p>Title: EarlyWarningServiceImpl</p>  
* <p>Description: </p>  
* @author Mapeng 
* @date Apr 18, 2020  
*/
@Service
public class EarlyWarningServiceImpl implements EarlyWarningService{

	@Autowired
	private SystemDao systemDao;
	@Autowired
	private SolutionGroupDao solutionGroupDao;
	
	@Override
	public boolean saveWarningPopup(Map<String, Object> warning_popup) {
		return systemDao.saveWarningPopup(warning_popup);
	}

	@Override
	public Map<String, Object> getWarningArticle(Integer pageNum, Long user_id,Long project_id, Integer openFlag) {
		Map<String, Object> resMap = new HashedMap<String, Object>();
		PageHelper.startPage(pageNum, 10);
		List<Map<String, Object>> warningArticle = systemDao.getWarningArticle(user_id,project_id,openFlag);
		for (int i = 0; i < warningArticle.size(); i++) {
			Long group_id = Long.valueOf(String.valueOf(warningArticle.get(i).get("group_id")));
			String groupName = solutionGroupDao.getGroupName(group_id);
			warningArticle.get(i).put("groupName", groupName);
		}
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String,Object>>(warningArticle);
		resMap.put("warningArticle", warningArticle);
		resMap.put("pageInfo", pageInfo);
		return resMap;
	}

	@Override
	public boolean updateWarningArticle(String article_id,Long user_id) {
		return systemDao.updateWarningArticle(article_id, user_id);
	}

	@Override
	public boolean readSign(Map<String, Object> readsign) {
		// TODO 自动生成的方法存根
		return systemDao.readSign(readsign);
	}

	@Override
	public void delReadSign(Map<String, Object> readsign) {
		// TODO 自动生成的方法存根
		systemDao.delReadSign(readsign);
	}

	@Override
	public Map<String, Object> selectReadSign(Map<String, Object> selectreadsign) {
		// TODO 自动生成的方法存根
		return systemDao.selectReadSign(selectreadsign);
	}
}
