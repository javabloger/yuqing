package com.stonedt.intelligence.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.SolutionGroup;

public interface SolutionGroupService {
	
	String getGroupName(@Param("groupId") Long groupId);

	int addSolutionGroup(SolutionGroup sg);

	JSONObject editSolutionGroup(SolutionGroup sg);
	
	List<SolutionGroup> listSolutionGroupByUserId(@Param("userId")Long userId);
	
	Map<String, Object> updateSolutionGroupStatus(@Param("groupId") Long groupId);

}
