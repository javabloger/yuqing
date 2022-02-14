package com.stonedt.intelligence.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.ReportDetail;

/**
 *
 */
@Mapper
public interface ReportDetailDao {
	
	int saveReportDetail(ReportDetail reportDetail);
	
	ReportDetail getReportDetail(@Param("reportId")Long reportId);

}
