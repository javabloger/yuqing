package com.stonedt.intelligence.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.Analysis;


public interface AnalysisService {
	
	Analysis getAanlysisByProjectidAndTimeperiod(Long projectId, Integer timePeriod);
	
	
	
// 历史代码
	int insert(Analysis a);

	Analysis getInfoByProjectid(@Param("projectid") Long projectid,
			@Param("timePeriod")Integer timePeriod);
	
	Analysis getAnalysisMonitorProjectid(@Param("projectId")Long projectId,
			@Param("timePeriod")Integer timePeriod);
	
	List<Map<String, Object>> latestnews(Long projectid, Integer timePeriod);


}
