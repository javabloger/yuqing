package com.stonedt.intelligence.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stonedt.intelligence.dao.ReportDetailDao;
import com.stonedt.intelligence.entity.ReportDetail;
import com.stonedt.intelligence.service.ReportDetailService;

/**
 *
 */
@Service
public class ReportDetailServiceImpl implements ReportDetailService{
	
	@Autowired
	private ReportDetailDao reportDetailDao;

	@Override
	public int saveReportDetail(ReportDetail reportDetail) {
		int saveReportDetail = reportDetailDao.saveReportDetail(reportDetail);
		return saveReportDetail;
	}

	@Override
	public ReportDetail getReportDetail(Long reportId) {
		try {
			ReportDetail reportDetail = reportDetailDao.getReportDetail(reportId);
			if (null == reportDetail) {
				return new ReportDetail();
			}
			return reportDetail;
		} catch (Exception e) {
			e.printStackTrace();
			return new ReportDetail();
		}
	}

}
