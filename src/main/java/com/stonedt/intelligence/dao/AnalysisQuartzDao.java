package com.stonedt.intelligence.dao;

import org.apache.ibatis.annotations.Mapper;

import com.stonedt.intelligence.entity.AnalysisQuartzDo;

@Mapper
public interface AnalysisQuartzDao {

	Boolean updateAnalysisPopularInformation(AnalysisQuartzDo analysisQuartzDo);
	
	Boolean updateAnalysisExceptPopularInformation(AnalysisQuartzDo analysisQuartzDo);
}
