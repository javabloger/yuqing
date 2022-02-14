package com.stonedt.intelligence.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.stonedt.intelligence.dao.ReportCustomDao;
import com.stonedt.intelligence.entity.ReportCustom;
import com.stonedt.intelligence.service.ReportCustomService;

/**
 *
 */
@Service
public class ReportCustomServiceImpl implements ReportCustomService{
	
	@Autowired
	private ReportCustomDao reportCustomDao;

	@Override
	public int saveReportCustom(ReportCustom reportCustom) {
		int saveReportCustom = reportCustomDao.saveReportCustom(reportCustom);
		return saveReportCustom;
	}

	@Override
	public List<ReportCustom> listReportCustomByStatus(Integer report_status) {
		return reportCustomDao.listReportCustomByStatus(report_status);
	}

	@Override
	public int updateReportCustomStatus(ReportCustom reportCustom) {
		int updateReportCustomStatus = reportCustomDao.updateReportCustomStatus(reportCustom);
		return updateReportCustomStatus;
	}

	@Override
	public Map<String, Object> listReportCustom(Integer pageNum, Integer reportType, Long projectId, String nameSearch) {
		Map<String, Object> result = new HashMap<String, Object>();
		PageHelper.startPage(pageNum, 10);
		List<ReportCustom> listReportCustom = reportCustomDao.listReportCustom(reportType, projectId, nameSearch);
		PageInfo<ReportCustom> pageInfo = new PageInfo<>(listReportCustom);
		result.put("list", listReportCustom);
		result.put("pageCount", pageInfo.getPages());
		result.put("dataCount", pageInfo.getTotal());
		return result;
	}

	@Override
	public Map<String, Object> batchUpdateReportCustom(Long userId, String list) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(list)) {
				result.put("state", true);
				result.put("message", "删除报告成功！");
				return result;
			}
			List<Long> parseArray = JSON.parseArray(list, Long.class);
			if (parseArray.isEmpty()) {
				result.put("state", true);
				result.put("message", "删除报告成功！");
				return result;
			}
			int batchUpdateProject = reportCustomDao.batchUpdateReportCustom(userId, parseArray);
			if (batchUpdateProject > 0) {
				result.put("state", true);
				result.put("message", "删除报告成功！");
			}else {
				result.put("state", false);
				result.put("message", "删除报告失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", false);
			result.put("message", "删除报告失败！");
		}
		return result;
	}

	@Override
	public Map<String, Object> batchUpdateReportCustomStatus(long userId, String reportId) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			int batchUpdateProject = reportCustomDao.batchUpdateReportCustomStatus(userId, reportId);
			result.put("state", true);
			result.put("message", "修改任务成功！");
		} catch (Exception e) {
			result.put("state", true);
			result.put("message", "修改任务失败！");
			// TODO: handle exception
		}
		return result;
		
	}

}
