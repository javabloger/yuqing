
package com.stonedt.intelligence.service;

import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import com.stonedt.intelligence.entity.WarningSetting;
import com.stonedt.intelligence.util.ResultUtil;

/**
* <p></p>
* <p>Title: SystemService</p>  
* <p>Description: </p>  
* @author Mapeng 
* @date Apr 16, 2020  
*/

public interface SystemService {
	
	Map<String, Object> listWarning(Integer pageNum,Long uesrId, Long group_id);
	
	boolean updateWarningStatusById(Integer warning_status, Long project_id);

	boolean getWarningWordById( Long project_id);

	WarningSetting getWarningByProjectId(Long project_id);
	
	ResultUtil updateWarning(WarningSetting warningSetting);

	Integer addWarning(Map<String,Object> map);
	
	ResponseEntity<InputStreamResource> uploadProductManual();
}
