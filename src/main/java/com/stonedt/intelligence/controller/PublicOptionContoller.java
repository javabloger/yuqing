package com.stonedt.intelligence.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.stonedt.intelligence.entity.PublicoptionDetailEntity;
import com.stonedt.intelligence.entity.PublicoptionEntity;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.PublicOptionService;
import com.stonedt.intelligence.util.TextUtil;
import com.stonedt.intelligence.aop.SystemControllerLog;
/**
 * 事件分析
 * @author wangyi
 *
 */
@Controller
@RequestMapping("/publicoption")
public class PublicOptionContoller {
	@Autowired
	private PublicOptionService publicOptionService;
	 
	
	
	
	/**
	 * 舆情研判分析详情任务列表
	 * @param request
	 * @param mv
	 * @return
	 */
	@SystemControllerLog(module = "事件分析", submodule = "事件分析页面", type = "查询", operation = "")
	@GetMapping(value = "")
	public ModelAndView displayboardlist(HttpServletRequest request,ModelAndView mv) {
		mv.addObject("menu", "public_option");
		mv.addObject("publicoptionleft_menu", "public_optionlist");
		mv.setViewName("publicoption/eventAnalysisList");
		return mv;
		
		
	}
	/**
	 * 展示舆情研判列表
	 * @param request
	 * @param mv
	 * @param session
	 * @param pagenum
	 * @param searchkeyword
	 * @return
	 */
	@SystemControllerLog(module = "事件分析", submodule = "展示事件列表", type = "查询", operation = "")
	@PostMapping(value = "/list")
	@ResponseBody
	public String list(HttpServletRequest request, ModelAndView mv, HttpSession session,
			@RequestParam(value = "pagenum", required = false, defaultValue = "1") Integer pagenum,
			@RequestParam(value = "searchkeyword", required = false, defaultValue = "") String searchkeyword) {
		Map<String, Object> result = new HashMap<String, Object>();
		User user = (User) session.getAttribute("User");
		PageHelper.startPage(pagenum, 10);
		List<PublicoptionEntity> datalist = publicOptionService.getlist(user.getUser_id(), searchkeyword);
		PageInfo<PublicoptionEntity> pageInfo = new PageInfo<>(datalist);
		result.put("list", datalist);
		result.put("pageCount", pageInfo.getPages());
		result.put("dataCount", pageInfo.getTotal());
		return JSON.toJSONString(result);

	}
	
	/**
	 * 根据id查询基本数据
	 * @param request
	 * @param mv
	 * @param session
	 * @param id
	 * @return
	 */
	@SystemControllerLog(module = "事件分析", submodule = "查询基本数据", type = "查询", operation = "")
	@PostMapping(value = "/getdatabyid")
	@ResponseBody
	public String getbyid(HttpServletRequest request, ModelAndView mv, HttpSession session,
			@RequestParam(value = "id", required = false, defaultValue = "1") Integer id) {
		Map<String, Object> result = new HashMap<String, Object>();
		PublicoptionEntity publicoption =publicOptionService.getdatabyid2(id);
		result.put("publicoption", publicoption);
		return JSON.toJSONString(result);

	}
	
	
	
	
	/**
	 * 任务报告列表
	 * @param request
	 * @param mv
	 * @return
	 */
	@SystemControllerLog(module = "事件分析", submodule = "任务报告列表", type = "查询", operation = "")
	@GetMapping(value = "reportlist")
	public ModelAndView reportlist(HttpServletRequest request,ModelAndView mv) {
		mv.addObject("menu", "public_option");
		mv.addObject("publicoptionleft_menu", "reportlist");
		mv.setViewName("publicoption/eventAnalysisReport");
		return mv;
	}
	
	/**
	 * 研判分析详情
	 * @param request
	 * @param mv
	 * @param id
	 * @return
	 */
	@SystemControllerLog(module = "事件分析", submodule = "任务报告详情", type = "查询", operation = "")
	@GetMapping(value = "reportdetail/{id}")
	public ModelAndView reportdetail(HttpServletRequest request,ModelAndView mv,
			@PathVariable(required = false) Integer id,HttpSession session) {
		Map<String, Object> mapParam=new HashMap<String, Object>();
		User user=(User)session.getAttribute("User");
		mapParam.put("userId", user.getUser_id());
		mapParam.put("id", id);
		PublicoptionDetailEntity dcd= publicOptionService.getdetail(mapParam);
		mv.addObject("publicoptionleft_menu", "public_optionlist");
		mv.addObject("menu", "public_option");
		mapParam.put("reportId", id);
		PublicoptionEntity publicoption =publicOptionService.getdatabyid(mapParam);
		mv.addObject("publicoption", publicoption);
		if(dcd!=null) {
			JSONObject parseObject = JSONObject.parseObject(JSON.toJSONString(dcd));
			Set<String> keySet = parseObject.keySet();
			for (String string : keySet) {
				String string2 = TextUtil.processQuotationMarks(parseObject.get(string).toString());
				mv.addObject(string,JSONObject.parse(string2));
			}
		}
		mv.setViewName("publicoption/eventAnalysisDetail");
		return mv;
		
		
	}
	
	/**
	 * 更新舆情研判分析数据
	 * @param request
	 * @param mv
	 * @param session
	 * @param id
	 * @param eventname
	 * @param eventkeywords
	 * @param eventstarttime
	 * @param eventendtime
	 * @param eventstopwords
	 * @return
	 */
	@SystemControllerLog(module = "事件分析", submodule = "更新事件分析数据", type = "更新", operation = "")
	@PostMapping(value = "/updatedatabyid")
	@ResponseBody
	public String updatedatabyid(HttpServletRequest request, ModelAndView mv, HttpSession session,
			@RequestParam(value = "id", required = false, defaultValue = "1") Integer id,
			@RequestParam(value = "eventname", required = false, defaultValue = "") String eventname,
			@RequestParam(value = "eventkeywords", required = false, defaultValue = "") String eventkeywords,
			@RequestParam(value = "eventstarttime", required = false, defaultValue = "") String eventstarttime,
			@RequestParam(value = "eventendtime", required = false, defaultValue = "") String eventendtime,
			@RequestParam(value = "eventstopwords", required = false, defaultValue = "") String eventstopwords
			) {
		String result =publicOptionService.updatabyid(id,eventname,eventkeywords,eventstarttime,eventendtime,eventstopwords);
		return result;

	}
	
	/**
	 * 创建舆情研判数据
	 * @param request
	 * @param mv
	 * @param session
	 * @param eventname
	 * @param eventkeywords
	 * @param eventstarttime
	 * @param eventendtime
	 * @param eventstopwords
	 * @return
	 */
	@SystemControllerLog(module = "事件分析", submodule = "创建事件数据", type = "创建", operation = "")
	@PostMapping(value = "/addpublicoptiondata")
	@ResponseBody
	public String addpublicoptiondata(HttpServletRequest request, ModelAndView mv, HttpSession session,
			@RequestParam(value = "eventname", required = false, defaultValue = "") String eventname,
			@RequestParam(value = "eventkeywords", required = false, defaultValue = "") String eventkeywords,
			@RequestParam(value = "eventstarttime", required = false, defaultValue = "") String eventstarttime,
			@RequestParam(value = "eventendtime", required = false, defaultValue = "") String eventendtime,
			@RequestParam(value = "eventstopwords", required = false, defaultValue = "") String eventstopwords
			) {
		User user = (User) session.getAttribute("User");
		
		
		String result =publicOptionService.addpublicoptiondata(user.getUser_id(),eventname,eventkeywords,eventstarttime,eventendtime,eventstopwords);
		
		return result;

	}




	@SystemControllerLog(module = "事件分析", submodule = "删除事件数据", type = "删除", operation = "")
	@PostMapping(value = "/deletepublicoptioninfo")
	@ResponseBody
	public String deletepublicoptioninfo(HttpServletRequest request, ModelAndView mv, HttpSession session,
			@RequestParam(value = "Ids", required = false, defaultValue = "") String Ids
			) {
		User user = (User) session.getAttribute("User");
		Long user_id = user.getUser_id();
		String result = publicOptionService.DeleteupinfoByIds(Ids,request);
		return result;

	}
	/**
	 * 
	 * @param request
	 * @param mv
	 * @param session
	 * @param pagenum
	 * @param searchkeyword
	 * @return
	 */
	@PostMapping(value = "/publicoptionreportlist")
	@ResponseBody
	public String publicoptionreportlist(HttpServletRequest request, ModelAndView mv, HttpSession session,
			@RequestParam(value = "pagenum", required = false, defaultValue = "1") Integer pagenum,
			@RequestParam(value = "searchkeyword", required = false, defaultValue = "") String searchkeyword) {
		Map<String, Object> result = new HashMap<String, Object>();
		User user = (User) session.getAttribute("User");
		PageHelper.startPage(pagenum, 10);
		List<PublicoptionEntity> datalist = publicOptionService.getpublicoptionreportlist(user.getUser_id(), searchkeyword);
		PageInfo<PublicoptionEntity> pageInfo = new PageInfo<>(datalist);
		result.put("list", datalist);
		result.put("pageCount", pageInfo.getPages());
		result.put("dataCount", pageInfo.getTotal());
		return JSON.toJSONString(result);

	}
	
	/**
	 * 溯源分析页面
	 * @param session
	 * @param mv
	 * @return
	 */
	@GetMapping(value = "/backanalysis")
	public ModelAndView backanalysis(HttpSession session, ModelAndView mv) {
		JSONArray data=new JSONArray();
		User user=(User)session.getAttribute("User");
		List<PublicoptionEntity> list = publicOptionService.getpublicoptionreportlist(user.getUser_id(), "");
		for (PublicoptionEntity publicoption : list) {
			String jsonString = JSON.toJSONString(publicoption);
			JSONObject obj = JSON.parseObject(jsonString);
			String backAnalysisStr = publicOptionService.getBackAnalysisById(publicoption.getId());
			if(backAnalysisStr==null || "".equals(backAnalysisStr)) {
				backAnalysisStr="{}";
			}
			obj.put("backAnalysis", JSON.parseObject(backAnalysisStr));
			data.add(obj);
		}
		mv.addObject("data", data.toJSONString());
		mv.addObject("publicoptionleft_menu", "reportlist");
		mv.addObject("menu", "public_option");
		mv.addObject("childmenu", "backanalysis");
		mv.setViewName("publicoption/backanalysis");
		return mv;
	}
	
	/**
	 * 获取报告的资讯列表
	 * @param publicoptionEntity
	 * @return
	 */
	@PostMapping(value = "/loadInformation")
	@ResponseBody
	public ResponseEntity<JSONObject> loadInformation(PublicoptionEntity publicoptionEntity){
		JSONObject data=publicOptionService.loadInformation(publicoptionEntity);
		return new ResponseEntity<JSONObject>(data, HttpStatus.OK);
	}
	
	/**
	 * 事件脉络页面
	 * @param session
	 * @param mv
	 * @return
	 */
	@GetMapping(value = "/eventContext")
	public ModelAndView eventContext(HttpSession session, ModelAndView mv) {
		JSONArray data=new JSONArray();
		User user=(User)session.getAttribute("User");
		List<PublicoptionEntity> list = publicOptionService.getpublicoptionreportlist(user.getUser_id(), "");
		for (PublicoptionEntity publicoption : list) {
			String jsonString = JSON.toJSONString(publicoption);
			JSONObject obj = JSON.parseObject(jsonString);
			String backAnalysisStr = publicOptionService.getEventContextById(publicoption.getId());
			if(backAnalysisStr==null || "".equals(backAnalysisStr)) {
				backAnalysisStr="[]";
			}
			obj.put("eventContext", JSON.parseArray(backAnalysisStr));
			data.add(obj);
		}
		mv.addObject("data", data.toJSONString());
		mv.addObject("publicoptionleft_menu", "reportlist");
		mv.addObject("menu", "public_option");
		mv.addObject("childmenu", "eventContext");
		mv.setViewName("publicoption/eventContext");
		return mv;
	}
	
	/**
	  * 事件跟踪页面
	  * @param session
	  * @param mv
	  * @return
	  */
	 @GetMapping(value = "/eventTrace")
	 public ModelAndView eventTrace(HttpSession session, ModelAndView mv) {
	  JSONArray data=new JSONArray();
	  User user=(User)session.getAttribute("User");
	  List<PublicoptionEntity> list = publicOptionService.getpublicoptionreportlist(user.getUser_id(), "");
	  for (PublicoptionEntity publicoption : list) {
	   String jsonString = JSON.toJSONString(publicoption);
	   JSONObject obj = JSON.parseObject(jsonString);
	   String backAnalysisStr = publicOptionService.getEventTraceById(publicoption.getId());
	   if(backAnalysisStr==null || "".equals(backAnalysisStr)) {
	    backAnalysisStr="{}";
	   }
	   obj.put("eventTrace", JSON.parseObject(backAnalysisStr));
	   
	   
	   String backAnalysisStraa = publicOptionService.getBackAnalysisById(publicoption.getId());
	   if(backAnalysisStraa==null || "".equals(backAnalysisStraa)) {
	    backAnalysisStraa="{}";
	   }
	   obj.put("backAnalysis", JSON.parseObject(backAnalysisStraa));
	   
	   
	   
	   
	   
	   
	   data.add(obj);
	  }
	  mv.addObject("data", data.toJSONString());
	  mv.addObject("publicoptionleft_menu", "reportlist");
	  mv.addObject("menu", "public_option");
	  mv.addObject("childmenu", "eventTrace");
	  mv.setViewName("publicoption/eventTrace");
	  return mv;
	 }

	/**
	 * 热点分析页面
	 * @param session
	 * @param mv
	 * @return
	 */
	@GetMapping(value = "/hotAnalysis")
	public ModelAndView hotAnalysis(HttpSession session, ModelAndView mv) {
		JSONArray data=new JSONArray();
		User user=(User)session.getAttribute("User");
		List<PublicoptionEntity> list = publicOptionService.getpublicoptionreportlist(user.getUser_id(), "");
		for (PublicoptionEntity publicoption : list) {
			String jsonString = JSON.toJSONString(publicoption);
			JSONObject obj = JSON.parseObject(jsonString);
			String backAnalysisStr = publicOptionService.getHotAnalysisById(publicoption.getId());
			if(backAnalysisStr==null || "".equals(backAnalysisStr)) {
				backAnalysisStr="[]";
			}
			obj.put("hotAnalysis", JSON.parseArray(backAnalysisStr));
			data.add(obj);
		}
		mv.addObject("data", data.toJSONString());
		mv.addObject("publicoptionleft_menu", "reportlist");
		mv.addObject("menu", "public_option");
		mv.addObject("childmenu", "hotAnalysis");
		mv.setViewName("publicoption/hotAnalysis");
		return mv;
	}
	
	/**
	 * 重点网民分析页面
	 * @param session
	 * @param mv
	 * @return
	 */
	@GetMapping(value = "/netizensAnalysis")
	public ModelAndView netizensAnalysis(HttpSession session, ModelAndView mv) {
		JSONArray data=new JSONArray();
		User user=(User)session.getAttribute("User");
		List<PublicoptionEntity> list = publicOptionService.getpublicoptionreportlist(user.getUser_id(), "");
		for (PublicoptionEntity publicoption : list) {
			String jsonString = JSON.toJSONString(publicoption);
			JSONObject obj = JSON.parseObject(jsonString);
			String backAnalysisStr = publicOptionService.getNetizensAnalysisById(publicoption.getId());
			if(backAnalysisStr==null || "".equals(backAnalysisStr)) {
				backAnalysisStr="{}";
			}
			obj.put("netizensAnalysis", JSON.parseObject(backAnalysisStr));
			data.add(obj);
		}
		mv.addObject("data", data.toJSONString());
		mv.addObject("publicoptionleft_menu", "reportlist");
		mv.addObject("menu", "public_option");
		mv.addObject("childmenu", "netizensAnalysis");
		mv.setViewName("publicoption/netizensAnalysis");
		return mv;
	}
	
	/**
	 * 统计页面
	 * @param session
	 * @param mv
	 * @return
	 */
	@GetMapping(value = "/statistics")
	public ModelAndView statistics(HttpSession session, ModelAndView mv) {
		JSONArray data=new JSONArray();
		User user=(User)session.getAttribute("User");
		List<PublicoptionEntity> list = publicOptionService.getpublicoptionreportlist(user.getUser_id(), "");
		for (PublicoptionEntity publicoption : list) {
			String jsonString = JSON.toJSONString(publicoption);
			JSONObject obj = JSON.parseObject(jsonString);
			String backAnalysisStr = publicOptionService.getStatisticsById(publicoption.getId());
			if(backAnalysisStr==null || "".equals(backAnalysisStr)) {
				backAnalysisStr="{}";
			}
			obj.put("statistics", JSON.parseObject(backAnalysisStr));
			data.add(obj);
		}
		mv.addObject("data", data.toJSONString());
		mv.addObject("publicoptionleft_menu", "reportlist");
		mv.addObject("menu", "public_option");
		mv.addObject("childmenu", "statistics");
		mv.setViewName("publicoption/statistics");
		return mv;
	}
	
	/**
	 * 传播分析页面
	 * @param session
	 * @param mv
	 * @return
	 */
	@GetMapping(value = "/propagationAnalysis")
	public ModelAndView propagationAnalysis(HttpSession session, ModelAndView mv) {
		JSONArray data=new JSONArray();
		User user=(User)session.getAttribute("User");
		List<PublicoptionEntity> list = publicOptionService.getpublicoptionreportlist(user.getUser_id(), "");
		for (PublicoptionEntity publicoption : list) {
			String jsonString = JSON.toJSONString(publicoption);
			JSONObject obj = JSON.parseObject(jsonString);
			String backAnalysisStr = publicOptionService.getPropagationAnalysisById(publicoption.getId());
			if(backAnalysisStr==null || "".equals(backAnalysisStr)) {
				backAnalysisStr="{}";
			}
			obj.put("propagationAnalysis", JSON.parseObject(backAnalysisStr));
			data.add(obj);
		}
		mv.addObject("data", data.toJSONString());
		mv.addObject("publicoptionleft_menu", "reportlist");
		mv.addObject("menu", "public_option");
		mv.addObject("childmenu", "propagationAnalysis");
		mv.setViewName("publicoption/propagationAnalysis");
		return mv;
	}
	
	/**
	 * 专题分析页面
	 * @param session
	 * @param mv
	 * @return
	 */
	@GetMapping(value = "/thematicAnalysis")
	public ModelAndView thematicAnalysis(HttpSession session, ModelAndView mv) {
		JSONArray data=new JSONArray();
		User user=(User)session.getAttribute("User");
		List<PublicoptionEntity> list = publicOptionService.getpublicoptionreportlist(user.getUser_id(), "");
		for (PublicoptionEntity publicoption : list) {
			String jsonString = JSON.toJSONString(publicoption);
			JSONObject obj = JSON.parseObject(jsonString);
			String backAnalysisStr = publicOptionService.getThematicAnalysisById(publicoption.getId());
			if(backAnalysisStr==null || "".equals(backAnalysisStr)) {
				backAnalysisStr="{}";
			}
			obj.put("thematicAnalysis", JSON.parseObject(backAnalysisStr));
			data.add(obj);
		}
		mv.addObject("data", data.toJSONString());
		mv.addObject("publicoptionleft_menu", "reportlist");
		mv.addObject("menu", "public_option");
		mv.addObject("childmenu", "thematicAnalysis");
		mv.setViewName("publicoption/thematicAnalysis");
		return mv;
	}
	
	/**
	 * 内容解读页面
	 * @param session
	 * @param mv
	 * @return
	 */
	@GetMapping(value = "/unscrambleContent")
	public ModelAndView unscrambleContent(HttpSession session, ModelAndView mv) {
		JSONArray data=new JSONArray();
		User user=(User)session.getAttribute("User");
		List<PublicoptionEntity> list = publicOptionService.getpublicoptionreportlist(user.getUser_id(), "");
		for (PublicoptionEntity publicoption : list) {
			String jsonString = JSON.toJSONString(publicoption);
			JSONObject obj = JSON.parseObject(jsonString);
			String backAnalysisStr = publicOptionService.getUnscrambleContentById(publicoption.getId());
			if(backAnalysisStr==null || "".equals(backAnalysisStr)) {
				backAnalysisStr="{}";
			}
			obj.put("unscrambleContent", JSON.parseObject(backAnalysisStr));
			data.add(obj);
		}
		mv.addObject("data", data.toJSONString());
		mv.addObject("publicoptionleft_menu", "reportlist");
		mv.addObject("menu", "public_option");
		mv.addObject("childmenu", "unscrambleContent");
		mv.setViewName("publicoption/unscrambleContent");
		return mv;
	}
	
	/**
	 * 司法舆情分析研判对象页面
	 * @param session
	 * @param mv
	 * @return
	 */
	@GetMapping(value = "/popular_feelings_analys")
	public ModelAndView popular_feelings_analys(HttpSession session, ModelAndView mv) {
		JSONArray data=new JSONArray();
		User user=(User)session.getAttribute("User");
		List<PublicoptionEntity> list = publicOptionService.getpublicoptionreportlist(user.getUser_id(), "");
		for (PublicoptionEntity publicoption : list) {
			String jsonString = JSON.toJSONString(publicoption);
			JSONObject obj = JSON.parseObject(jsonString);
			data.add(obj);
		}
		mv.addObject("data", data.toJSONString());
		mv.addObject("publicoptionleft_menu", "reportlist");
		mv.addObject("menu", "public_option");
		mv.addObject("childmenu", "popular_feelings_analys");
		mv.setViewName("publicoption/popular_feelings_analys");
		return mv;
	}
	
}
