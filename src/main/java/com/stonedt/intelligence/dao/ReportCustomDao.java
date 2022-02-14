package com.stonedt.intelligence.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.ReportCustom;

/**
 *
 */
@Mapper
public interface ReportCustomDao {
	
	List<ReportCustom> listReportCustom(@Param("reportType")Integer reportType,
			@Param("projectId")Long projectId, @Param("nameSearch")String nameSearch);
	
	int saveReportCustom(ReportCustom reportCustom);
	
	List<ReportCustom> listReportCustomByStatus(@Param("report_status")Integer report_status);
	
	int updateReportCustomStatus(ReportCustom reportCustom);
	
	int batchUpdateReportCustom(@Param("userId")Long userId, @Param("list")List<Long> list);

	int batchUpdateReportCustomStatus(@Param("userId")long userId, @Param("reportId")String reportId);

	List<Map<String, Object>> searchReportByUserAndType2(@Param("user_id")Long user_id, @Param("report_type")Integer report_type);
	
}
