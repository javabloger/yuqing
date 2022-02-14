package com.stonedt.intelligence.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.OpinionCondition;

import java.util.Map;

/**
 *
 */
@Mapper
public interface OpinionConditionDao {
	
	OpinionCondition getOpinionConditionByProjectId(@Param("projectId")Long projectId);
	
	int updateOpinionCondition(OpinionCondition opinionCondition);

	int addOpinionConditionById(@Param("map") Map<String,Object> map);

	Integer updateOpinionConditionByMap(@Param("map") Map<String,Object> map);

	Integer updateOpinionConditionById(@Param("map") Map<String,Object> map);

	Map<String,Object> getOpinionConditionById(@Param("map") Map<String,Object> map);



}
