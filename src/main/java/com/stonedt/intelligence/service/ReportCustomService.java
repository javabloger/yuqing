package com.stonedt.intelligence.service;

import java.util.List;
import java.util.Map;

import com.stonedt.intelligence.entity.ReportCustom;

/**
 *
 */
public interface ReportCustomService {
	
	Map<String, Object> listReportCustom(Integer pageNum, Integer reportType, Long projectId, String nameSearch);

	int saveReportCustom(ReportCustom reportCustom);
	
	List<ReportCustom> listReportCustomByStatus(Integer report_status);
	
	int updateReportCustomStatus(ReportCustom reportCustom);
	
	Map<String, Object> batchUpdateReportCustom(Long userId, String list);

	Map<String, Object> batchUpdateReportCustomStatus(long userId, String reportId);
}
