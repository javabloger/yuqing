package com.stonedt.intelligence.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stonedt.intelligence.aop.SystemControllerLog;
import com.stonedt.intelligence.dao.ProjectDao;
import com.stonedt.intelligence.dao.ProjectTaskDao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.entity.Analysis;
import com.stonedt.intelligence.service.AnalysisService;

/**
 * 监测分析
 */
@Controller
@RequestMapping(value = "/analysis")
public class AnalysisController {

	@Autowired
	private AnalysisService analysisService;
	
	@Autowired
	private ProjectTaskDao projectTaskDao;
	
	
	
	/**
	 * 获取监测分析数据
	 */
	/*@SystemControllerLog(module = "监测分析", submodule = "监测分析", type = "数据获取", operation = "")*/
	@PostMapping(value = "/getAanlysisByProjectidAndTimeperiod")
	@ResponseBody
	public String getAanlysisByProjectidAndTimeperiod(Long projectId, Integer timePeriod) {
		Analysis anlysisByProjectidAndTimeperiod = analysisService.getAanlysisByProjectidAndTimeperiod(projectId, timePeriod);
		return JSON.toJSONString(anlysisByProjectidAndTimeperiod);
	}

	/**
	 * 跳转监测分析页面
	 */
	@SystemControllerLog(module = "监测分析", submodule = "监测分析页面", type = "查询", operation = "")
	@GetMapping(value = "")
	public ModelAndView analysis(HttpServletRequest request, ModelAndView mv) {
		String groupId = request.getParameter("groupid");
		String projectId = request.getParameter("projectid");
		if (StringUtils.isBlank(groupId))
			groupId = "";
		if (StringUtils.isBlank(projectId))
			projectId = "";
		mv.addObject("groupId", groupId);
		mv.addObject("projectId", projectId);
		mv.addObject("groupid", groupId);
		mv.addObject("menu", "analysis");
		mv.addObject("projectid", projectId);
		mv.setViewName("monitor/overview");
		return mv;
	}
	
	
	

// 历史代码
	/**
	 * 获取监测分析数据
	 */
	/*@SystemControllerLog(module = "监测分析", submodule = "监测分析", type = "查询", operation = "getAnalysisMonitorProjectid")*/
	@PostMapping(value = "/getAnalysisMonitorProjectid")
	@ResponseBody
	public String getAnalysisMonitorProjectid(Long projectId, Integer timePeriod) {
		Analysis analysisMonitorProjectid = analysisService.getAnalysisMonitorProjectid(projectId, timePeriod);
		return JSON.toJSONString(analysisMonitorProjectid);
	}

	/**
	 * 获取相关资讯数据
	 */
	/*@SystemControllerLog(module = "监测分析", submodule = "监测分析", type = "最新资讯", operation = "latestnews")*/
	@PostMapping(value = "/latestnews")
	@ResponseBody
	public String latestnews(@RequestParam("projectid") Long projectid, Integer timePeriod) {
		List<Map<String, Object>> latestnews = analysisService.latestnews(projectid, timePeriod);
		return JSON.toJSONString(latestnews);
	}

	/**
	 * @param [projectid 方案id]
	 * @description: 获取情感占比 <br>
	 * @author: huajiancheng <br>
	 */
	/*@SystemControllerLog(module = "监测分析", submodule = "监测分析", type = "查询", operation = "emotionalproportion")*/
	@PostMapping(value = "/emotionalproportion")
	@ResponseBody
	public String emotionalProportion(@RequestParam("projectid") Long projectid, Integer timePeriod) {
		JSONObject json = new JSONObject();
		// 根据projectid获取信息
		Analysis a = analysisService.getInfoByProjectid(projectid, timePeriod);
		if (a == null) {
			return "{}";
		}
		if (a.getEmotionalProportion() != null && !"".equals(a.getEmotionalProportion())) {
			json = JSONObject.parseObject(a.getEmotionalProportion());
		}
		return json.toJSONString();
	}

	/**
	 * @param [projectid 方案id]
	 * @description: 方案词命中 <br>
	 * @author: huajiancheng <br>
	 */
	/*@SystemControllerLog(module = "监测分析", submodule = "监测分析", type = "查询", operation = "planwordhit")*/
	@PostMapping(value = "/planwordhit")
	@ResponseBody
	public String planwordhit(@RequestParam("projectid") Long projectid, Integer timePeriod) {
		JSONArray jsona = new JSONArray();
		Analysis a = analysisService.getInfoByProjectid(projectid, timePeriod);
		if (a == null) {
			return "[]";
		}
		if (a.getPlanWordHit() != null && !"".equals(a.getPlanWordHit())) {
			jsona = JSONArray.parseArray(a.getPlanWordHit());
		}
		return jsona.toJSONString();
	}

	/**
	 * @param [projectid 方案id]
	 * @return java.lang.String
	 * @description: 热门资讯 <br>
	 * @version: 1.0 <br>
	 * @date: 2020/4/13 14:10 <br>
	 * @author: huajiancheng <br>
	 */
	/*@SystemControllerLog(module = "监测分析", submodule = "监测分析", type = "查询", operation = "popularinformation")*/
	@PostMapping(value = "/popularinformation")
	@ResponseBody
	public String popularInformation(@RequestParam("projectid") Long projectid, Integer timePeriod) {
		JSONArray json = new JSONArray();
		Analysis a = analysisService.getInfoByProjectid(projectid, timePeriod);
		if (a == null) {
			return "[]";
		}
		if (a.getPopularInformation() != null && !"".equals(a.getPopularInformation())) {
			json = JSONArray.parseArray(a.getPopularInformation());
		}
		return json.toJSONString();
	}

	/**
	 * 获取关键词情感分析数据走势
	 */
	/*@SystemControllerLog(module = "监测分析", submodule = "监测分析", type = "关键词情感分析数据走势", operation = "emotioncategory")*/
	@PostMapping(value = "/emotioncategory")
	@ResponseBody
	public String emotioncategory(@RequestParam("projectid") Long projectid, Integer timePeriod) {

		@SuppressWarnings("unused")
		JSONArray json = new JSONArray();
		JSONObject objectdata = new JSONObject();
		Analysis a = analysisService.getInfoByProjectid(projectid, timePeriod);
		if (a == null) {
			return "[]";
		}
//    	if(a.getKeyword_emotion_trend()!=null&&!"".equals(a.getKeyword_emotion_trend())) {
//    		json=JSONArray.parseArray(a.getKeyword_emotion_trend());
//    	}
		String chinaString = "";
		String keyword_emotion_statistical = a.getKeyword_emotion_statistical();
		JSONObject parseObject = JSONObject.parseObject(keyword_emotion_statistical);
		Integer keyword_count = parseObject.getInteger("keyword_count");
		JSONArray positive = parseObject.getJSONArray("positive");
		JSONArray negative = parseObject.getJSONArray("negative");
		if (keyword_count > 1) {
			JSONObject object = JSONObject.parseObject(String.valueOf(positive.get(0)));
			String keyword = object.getString("keyword");// 正面占比最高关键词
			String rate = object.getString("rate");// 占比

			JSONObject object2 = JSONObject.parseObject(String.valueOf(positive.get(1)));
			String keyword2 = object2.getString("keyword");// 其次是正面关键词
			String rate2 = object2.getString("rate");// 占比
			chinaString = "您一共设置了 " + keyword_count + "个关键词，正面占比最高的是【" + keyword + "】到达" + rate + "，其次是【" + keyword2
					+ "】到达" + rate2 + "。负面占比最高的是【" + negative.getJSONObject(0).getString("keyword") + "】到达"
					+ negative.getJSONObject(0).getString("rate") + "，其次是【"
					+ negative.getJSONObject(1).getString("keyword") + "】到达"
					+ negative.getJSONObject(1).getString("rate") + "。";
		} else if (keyword_count == 1) {
			chinaString = "您一共设置了 " + keyword_count + "个关键词，正面占比最高的是【" + positive.getJSONObject(0).getString("keyword")
					+ "】到达" + positive.getJSONObject(0).getString("rate") + "。负面占比最高的是【"
					+ negative.getJSONObject(0).getString("keyword") + "】到达"
					+ negative.getJSONObject(0).getString("rate") + "。";
		}
		// json.put("china", chinaString);

		objectdata.put("china", chinaString);
		objectdata.put("data", a);

		return objectdata.toJSONString();
	}

	/**
	 * @param [projectid 方案id]
	 * @description: 高频词指数 <br>
	 * @author: huajiancheng <br>
	 */
	/*@SystemControllerLog(module = "监测分析", submodule = "监测分析", type = "高频词指数", operation = "keywordindex")*/
	@PostMapping(value = "/keywordindex")
	@ResponseBody
	public String keywordindex(@RequestParam("projectid") Long projectid, HttpServletRequest request,
			Integer timePeriod) {
//    	User user = userUtil.getuser(request);
//        Long user_id = user.getUser_id();
		JSONArray json = new JSONArray();
//        Map<String, Object> queryUserid = projectService.queryUserid(user_id);
//        if (MapUtils.isEmpty(queryUserid)) {
//        	return json.toJSONString();
//		}

		// 根据projectid获取信息
		Analysis a = analysisService.getInfoByProjectid(projectid, timePeriod);
		if (a == null) {
			return "[]";
		}
		if (a.getKeywordIndex() != null && !"".equals(a.getKeywordIndex())) {
			json = JSONArray.parseArray(a.getKeywordIndex());
		}
		return json.toJSONString();
	}

	/*@SystemControllerLog(module = "监测分析", submodule = "监测分析", type = "查询", operation = "popularkeyword")*/
	@PostMapping("/popularkeyword")
	@ResponseBody
	public String popularKeyword(@RequestParam("projectid") Long projectid, HttpServletRequest request,
			Integer timePeriod) {
		JSONObject jsonObject = new JSONObject();
//    	User user = userUtil.getuser(request);
//        Long user_id = user.getUser_id();
//        Map<String, Object> queryUserid = projectService.queryUserid(user_id);
//        if (MapUtils.isEmpty(queryUserid)) {
//        	return jsonObject.toJSONString();
//		}
		Analysis analysis = analysisService.getInfoByProjectid(projectid, timePeriod);
		if (analysis == null) {
			jsonObject.put("updateTime", "");
			jsonObject.put("hotCompany", JSONArray.parseArray("[]"));
			jsonObject.put("hotPeople", JSONArray.parseArray("[]"));
			jsonObject.put("hotSpot", JSONArray.parseArray("[]"));
			return jsonObject.toJSONString();
		}
		Date createTime = analysis.getCreateTime();
		if (null != createTime) {
			try {
				Instant instant = createTime.toInstant();
				ZoneId zoneId = ZoneId.systemDefault();
				LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				jsonObject.put("updateTime", localDateTime.format(dateTimeFormatter));
			} catch (Exception e) {
				e.printStackTrace();
				jsonObject.put("updateTime", "");
			}
		} else {
			jsonObject.put("updateTime", "");
		}
		if (analysis.getHotCompany() != null && !"".equals(analysis.getHotCompany())) {
			String hotCompany = analysis.getHotCompany();
			jsonObject.put("hotCompany", JSONArray.parseArray(hotCompany));
		} else {
			jsonObject.put("hotCompany", JSONArray.parseArray("[]"));
		}

		if (analysis.getHotPeople() != null && !"".equals(analysis.getHotPeople())) {
			String hotPeople = analysis.getHotPeople();
			jsonObject.put("hotPeople", JSONArray.parseArray(hotPeople));
		} else {
			jsonObject.put("hotPeople", JSONArray.parseArray("[]"));
		}

		if (analysis.getHotSpot() != null && !"".equals(analysis.getHotSpot())) {
			String hotSpot = analysis.getHotSpot();
			jsonObject.put("hotSpot", JSONArray.parseArray(hotSpot));
		} else {
			jsonObject.put("hotSpot", JSONArray.parseArray("[]"));
		}
		return jsonObject.toJSONString();
	}
	
	/**
	 * 刷新监测分析结果
	 */
	@SystemControllerLog(module = "监测分析", submodule = "更新监测分析", type = "更新", operation = "")
	@GetMapping(value = "/updateanalysisdata")
	@ResponseBody
	public String updateanalysis(HttpServletRequest request, ModelAndView mv) {
		Map map = new HashMap<String, Object>();
		map.put("status", 500);
		map.put("result", "fail");
		String groupId = request.getParameter("groupid");
		String projectId = request.getParameter("projectid");
		if (StringUtils.isBlank(groupId))groupId = "";
		if (StringUtils.isBlank(projectId))projectId = "";
		try {
			Boolean flag = projectTaskDao.updateProjectTaskAnalysisToUnDealFlag(Long.parseLong(projectId));
			if(flag==true) {
				 map.put("status", 200);
				 map.put("result", "success");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONObject.toJSONString(map);
	}
	
	
	
	
	

}
