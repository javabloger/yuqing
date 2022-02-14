package com.stonedt.intelligence.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.SolutionGroup;
@Mapper
public interface SolutionGroupDao {
	
	String getGroupName(@Param("groupId") Long groupId);

	Integer getGroupCount(@Param("sg") SolutionGroup sg);

	int addSolutionGroup(SolutionGroup sg);

	Integer editSolutionGroup(@Param("sg") SolutionGroup sg);

	SolutionGroup getSolutionByGroupId(String group_id);

	int updateSolutionGroupById(SolutionGroup sg);

	int deleteById(int id);

	List<SolutionGroup> getAllInfo();

	SolutionGroup getFirstInfo();
	
	List<SolutionGroup> listSolutionGroupByUserId(@Param("userId")Long userId);
	
	Boolean updateSolutionGroupStatus(@Param("groupId") Long groupId);

	Map<String, Object> getGroupNameByprojectId(@Param("projectid") String projectid);

}
