package com.stonedt.intelligence.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stonedt.intelligence.aop.SystemControllerLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.stonedt.intelligence.entity.ReportDetail;
import com.stonedt.intelligence.service.ReportCustomService;
import com.stonedt.intelligence.service.ReportDetailService;
import com.stonedt.intelligence.util.UserUtil;

/**
 * description: 数据报告控制器 <br>
 * date: 2020/4/13 10:53 <br>
 * author: xiaomi <br>
 * version: 1.0 <br>
 */
@Controller
@RequestMapping(value = "/report")
public class ReportController {
	
	@Autowired
	private UserUtil userUtil;
	@Autowired
	private ReportCustomService reportCustomService;
	@Autowired
	private ReportDetailService reportDetailService;
	
	/**
	 * 	获取当前用户的数据报告列表
	 * 	pageNum 页数，reportType 报告类型(1日报 2周报 3月报)， nameSearch 搜索词
	 */
	/*@SystemControllerLog(module = "分析报告", submodule = "分析报告-列表", type = "查询", operation = "listReportCustom")*/
	@PostMapping(value = "/listReportCustom")
	@ResponseBody
	public String name(Integer pageNum, Integer reportType, String nameSearch, Long projectId) {
		Map<String, Object> listReportCustom = reportCustomService.listReportCustom(pageNum, reportType, projectId, nameSearch);
		return JSON.toJSONString(listReportCustom);
	}
	
    /**
     * @param [mv]
     * @return org.springframework.web.servlet.ModelAndView
     * @description: 跳转报告列表页面 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 15:54 <br>
     * @author: huajiancheng <br>
     */
	@SystemControllerLog(module = "分析报告", submodule = "分析报告-列表", type = "查询", operation = "")
	@GetMapping(value = "")
    public ModelAndView reportlist(ModelAndView mv, HttpServletRequest request) {
    	String groupid = request.getParameter("groupid");
    	String projectid = request.getParameter("projectid");
    	String search = request.getParameter("search");
    	String type = request.getParameter("type");
    	String page = request.getParameter("page");
    	mv.addObject("groupid", groupid);
    	mv.addObject("projectid", projectid);
    	mv.addObject("search", search);
    	mv.addObject("type", type);
    	mv.addObject("page", page);
    	mv.addObject("menu", "report");
        mv.setViewName("report/reportList");
        return mv;
    }

    /**
     * 	数据报告详情页面
     */
	@SystemControllerLog(module = "分析报告", submodule = "分析报告-详情", type = "查询报告详情", operation = "")
	@GetMapping(value = "/{id}")
    public ModelAndView reportdetail(@PathVariable()String id, ModelAndView mv, HttpServletRequest request) {
    	String groupid = request.getParameter("groupid");
    	String projectid = request.getParameter("projectid");
    	mv.addObject("id", id);
    	mv.addObject("groupid", groupid);
    	mv.addObject("projectid", projectid);
    	mv.addObject("menu", "report");
        mv.setViewName("report/report");
        return mv;
    }
    
    /**
     * 	报告详情数据
     */
	/*@SystemControllerLog(module = "分析报告", submodule = "分析报告-详情", type = "查询", operation = "reportDetail")*/
	@PostMapping(value = "/reportDetail")
    @ResponseBody
    public String reportDetail(Long reportId) {
    	ReportDetail reportDetail = reportDetailService.getReportDetail(reportId);
    	return JSON.toJSONString(reportDetail);
	}
    
    /**
     * 批量删除报告
     */
	@SystemControllerLog(module = "分析报告", submodule = "分析报告-列表", type = "删除报告", operation = "batchUpdateReportCustom")
	@PostMapping(value = "/batchUpdateReportCustom")
    @ResponseBody
    public String batchUpdateReportCustom(String reportIds, HttpServletRequest request) {
        long userId = userUtil.getUserId(request);
        Map<String, Object> batchUpdateProject = reportCustomService.batchUpdateReportCustom(userId, reportIds);
        return JSON.toJSONString(batchUpdateProject);
    }
	/**
     * 报告编制状态修改
     */
	@SystemControllerLog(module = "分析报告", submodule = "分析报告-列表", type = "编制分析报告", operation = "batchUpdateReportCustom")
	@PostMapping(value = "/batchUpdateReportCustomStatus")
    @ResponseBody
    public String batchUpdateReportCustomStatus(String reportIds, HttpServletRequest request) {
        long userId = userUtil.getUserId(request);
        Map<String, Object> batchUpdateProject = reportCustomService.batchUpdateReportCustomStatus(userId, reportIds);
        return JSON.toJSONString(batchUpdateProject);
    }
	
	
	
    
}
