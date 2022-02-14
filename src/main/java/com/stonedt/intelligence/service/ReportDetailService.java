package com.stonedt.intelligence.service;

import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.ReportDetail;

/**
 *
 */
public interface ReportDetailService {
	
	int saveReportDetail(ReportDetail reportDetail);
	
	ReportDetail getReportDetail(@Param("reportId")Long reportId);

}
