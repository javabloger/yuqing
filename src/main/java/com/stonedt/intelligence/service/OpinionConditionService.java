package com.stonedt.intelligence.service;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.OpinionCondition;

/**
 *
 */
public interface OpinionConditionService {
	
	OpinionCondition getOpinionConditionByProjectId(@Param("projectId")Long projectId);
	
	Map<String, Object> updateOpinionCondition(OpinionCondition opinionCondition);

	/**
	 * @description: 新增偏好设置 <br>
	 * @version: 1.0 <br>
	 * @date: 2020/4/18 19:56 <br>
	 * @author: huajiancheng <br>
	 * @param [map]
	 * @return java.lang.Integer 
	 * */ 
	
	Integer addOpinionConditionById(Map<String,Object> map);

	/**
	 * @description: 更新偏好设置 <br>
	 * @version: 1.0 <br>
	 * @date: 2020/4/18 19:56 <br>
	 * @author: huajiancheng <br>
	 * @param [map]
	 * @return java.lang.Integer
	 * */
	Integer updateOpinionConditionByMap(Map<String,Object> map);

	/**
	 * @description: 根据id更新精准打开 <br>
	 * @version: 1.0 <br>
	 * @date: 2020/5/6 10:45 <br>
	 * @author: huajiancheng <br>
	 * @param [map]
	 * @return java.lang.Integer 
	 * */ 
	
	Integer updateOpinionConditionById(Map<String,Object> map);
}
