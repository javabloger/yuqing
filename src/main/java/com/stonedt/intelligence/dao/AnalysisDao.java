package com.stonedt.intelligence.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.Analysis;
@Mapper
public interface AnalysisDao {
	
	Analysis getAanlysisByProjectidAndTimeperiod(@Param("projectId") Long projectId,
			@Param("timePeriod")Integer timePeriod);
	

	
	
// 历史代码	
	int insert(Analysis a);

	Analysis getInfoByProjectid(@Param("projectid") Long projectid,
			@Param("timePeriod")Integer timePeriod);
	
	Analysis getAnalysisMonitorProjectid(@Param("projectId")Long projectId,
			@Param("timePeriod")Integer timePeriod);

}
