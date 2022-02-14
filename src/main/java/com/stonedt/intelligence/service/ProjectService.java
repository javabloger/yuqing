package com.stonedt.intelligence.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.Project;

public interface ProjectService {
	
	Map<String, Object> batchUpdateProject(Long userId, String list);
	
	String getProjectName(@Param("projectId") Long projectId);

	Map<String,Object> getProjectByProId(@Param("projectId") Long projectId);

	List<Map<String,Object>> getGroupInfoByUserId(Long userId);

	List<Map<String,Object>> getProjectInfoByGroupIdAndUserId(Map<String,Object> map);

	List<Project> getInfoByGroupId(Long groupId);

	int insertProject(Project p);
	
	List<Project> listProjects();
	
	int timingProject(Map<String, Object> map);//声量监测

	Map<String,Object> getProjectByProjectId(Map<String,Object> map);

	Integer delProject(Map<String,Object> map);

	Integer editProjectInfo(Map<String,Object> map);

	Integer getProjectCount(Map<String,Object> map);

	Integer updateOpinionConditionById(Map<String,Object> map);

	Map<String,Object> getOpinionConditionById(Map<String,Object> map);

	List<Project> listProjectByGroupId(@Param("groupId")Long groupId);
	
	Map<String,Object> queryUserid(Long user_id);
	
	Map<String, Object> getProjectCountByGroupId(@Param("groupId")Long groupId);

//	JSONObject getAllKeywords(Integer page,Integer pageSize);
	JSONObject getAllKeywords();

	Map<String,Object> getProjectInfoById(Map<String,Object> map);
}
