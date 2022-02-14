
package com.stonedt.intelligence.dao;
/**
* <p></p>
* <p>Title: SystemDao</p>  
* <p>Description: </p>  
* @author Mapeng 
* @date Apr 16, 2020  
*/

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.WarningSetting;


@Mapper
public interface SystemDao {
	
	WarningSetting getWarningByProjectid(@Param("projectId")Long projectId);

	List<WarningSetting> listWarning(@Param("userId")Long userId, @Param("group_id")Long group_id);
	
	List<WarningSetting> listWarningMsg();
	
	boolean updateWarningStatusById(@Param(value = "warning_status")Integer warning_status, @Param(value = "project_id")Long project_id);

	WarningSetting getWarningWord( @Param(value = "project_id")Long project_id);

	WarningSetting getWarningByProjectId(@Param(value = "project_id")Long project_id);
	
	boolean updateWarning(WarningSetting warningSetting);
	
	boolean saveWarningPopup(Map<String, Object> warning_popup);
	
	List<Map<String, Object>> getWarningArticle(@Param("user_id")Long user_id, @Param("project_id")Long project_id,
			@Param("openFlag")Integer openFlag);
	
	boolean updateWarningArticle(@Param("article_id")String article_id,@Param("user_id")Long user_id);

	Integer addWarning(@Param("map") Map<String,Object> map);
	
	
	

	boolean updateWordWarningStatusById(Integer warning_status, Long id);




	int deleteWordWarning(@Param("id")Long id);

	boolean readSign(Map<String, Object> readsign);

	void delReadSign(Map<String, Object> readsign);

	Map<String, Object> selectReadSign(Map<String, Object> selectreadsign);


	void deletekeyAttention(Integer id);



	void deleteguideAssess(Integer id);

	
	
	
	
	
	
}



