package com.stonedt.intelligence.dao;

import org.apache.ibatis.annotations.Mapper;
import com.stonedt.intelligence.entity.Project;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProjectDao {
	
	Project getProject(@Param("projectId") Long projectId);
	
	int batchUpdateProject(@Param("userId")Long userId, @Param("list")List<Long> list);
	
	String getProjectName(@Param("projectId") Long projectId);

	Map<String,Object> getProjectByProId(@Param("projectId") Long projectId);

    List<Map<String, Object>> getGroupInfoByUserId(@Param("user_id") Long user_id, @Param("del_status") Integer del_status);


    List<Map<String,Object>> getProjectInfoByGroupIdAndUserId(@Param("map") Map<String,Object> map);

	List<Project> getInfoByGroupId(Long groupId);

	int insertProject(Project p);

    List<Map<String,Object>> getProjectAndGroupInfoByUserId(@Param("map") Map<String,Object> map);
    
    List<Project> listProjects();

    Map<String,Object> getProjectByProjectId(@Param("map") Map<String,Object> map);

    int timingProject(Map<String, Object> map);//声量监测

    Integer delProject(@Param("map") Map<String,Object> map);

    Integer editProjectInfo(@Param("map") Map<String,Object> map);

    Integer getProjectCount(@Param("map") Map<String,Object> map);
    
    List<Project> listProjectByGroupId(@Param("groupId")Long groupId);

    Map<String,Object> getProjectInfo(@Param("map") Map<String,Object> map);
    
    Map<String,Object> queryUserid(Long user_id);

    Map<String,Object> getGroupNameById(@Param("map") Map<String,Object> map);
    
    Integer getProjectCountByGroupId(@Param("groupId")Long groupId);

    List<Map<String,Object>> getAllKeywords();

    Map<String,Object> getProjectInfoById(@Param("map") Map<String,Object> map);

    Integer getProjectCountById(@Param("map") Map<String,Object> map);


	List<Map<String, Object>> getprojectByUser2(@Param("user_id")Long user_id, @Param("type")Integer type);

	List<String> getKeywordsByUser2(@Param("user_id")Long user_id, @Param("type")Integer type);
}
