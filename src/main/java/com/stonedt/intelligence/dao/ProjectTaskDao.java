package com.stonedt.intelligence.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.ProjectTask;

/**
 *
 * @date  2020年4月30日 上午11:41:58
 */
@Mapper
public interface ProjectTaskDao {
	
	List<ProjectTask> listProjectTaskByAnalysisFlag();
	
	Boolean updateProjectTaskAnalysisFlag(@Param("projectId")Long projectId);
	
	List<ProjectTask> listProjectTaskByVolumeFlag();
	
	Boolean updateProjectTaskVolumeFlag(@Param("projectId")Long projectId);
	
	Boolean saveProjectTask(ProjectTask projectTask);

	Boolean updateProjectTaskAnalysisToUnDealFlag(@Param("projectId") long projectId);

}
