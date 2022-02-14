package com.stonedt.intelligence.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.constant.MonitorConstant;
import com.stonedt.intelligence.entity.DatafavoriteEntity;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.DatafavoriteService;
import com.stonedt.intelligence.service.EarlyWarningService;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.MyHttpRequestUtil;
import com.stonedt.intelligence.util.SnowFlake;
import com.stonedt.intelligence.aop.SystemControllerLog;

import lombok.extern.slf4j.Slf4j;

/**
 * 收藏
 * 
 * @author wangyi  datamonitor/updateemtion
 *
 */
@Controller
@RequestMapping("/datamonitor")
@Slf4j
public class DatafavoriteContoller {
	
	public SnowFlake snowFlake = new SnowFlake();

	@Autowired
	public DatafavoriteService datafavoriteService;
	@Autowired
    private EarlyWarningService earlyWarningService;

	// es搜索地址
    @Value("${es.search.url}")
    private String es_search_url;
	/**
	 * 情感标记
	 * @param request
	 * @param mv
	 * @param session
	 * @param id
	 * @param projectid
	 * @param groupid
	 * @return
	 */
    @SystemControllerLog(module = "数据监测", submodule = "情感标记", type = "情感标记", operation = "")
	@PostMapping(value = "/updateemtion")
	@ResponseBody
	public String updateemtion(HttpServletRequest request, ModelAndView mv, HttpSession session,
			@RequestParam(value = "id", required = false, defaultValue = "") String id,
			@RequestParam(value = "publish_time", required = false, defaultValue = "") String publish_time,
			@RequestParam(value = "flag", required = false, defaultValue = "") int flag) {
		
        datafavoriteService.updateemtion(id,flag,es_search_url,publish_time);
		User user = (User) session.getAttribute("User");
		 Map<String,Object> map =new HashMap<String,Object>();
		 map.put("status", 200);
		 map.put("result", "success");
		return JSON.toJSONString(map);

	}
    
	/**
	 * 添加收藏
	 * @param request
	 * @param mv
	 * @param session
	 * @param id
	 * @param projectid
	 * @param groupid
	 * @return
	 */
    @SystemControllerLog(module = "数据监测", submodule = "收藏", type = "添加收藏", operation = "")
	@PostMapping(value = "/addfavoritedata")
	@ResponseBody
	public String displayboardlist(HttpServletRequest request, ModelAndView mv, HttpSession session,
			@RequestParam(value = "id", required = false, defaultValue = "") String id,
			@RequestParam(value = "projectid", required = false, defaultValue = "") Long projectid,
			@RequestParam(value = "groupid", required = false, defaultValue = "") Long groupid) {
		 User user = (User) session.getAttribute("User");
		 String url = es_search_url + MonitorConstant.es_api_article_newdetail;
         String params = "article_public_id=" + id + "&esindex=postal&estype=infor";
         String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
		JSONObject parseObject = JSONObject.parseObject(sendPostEsSearch);
		String title = parseObject.get("title").toString();
		String source_name = parseObject.get("sourcewebsitename").toString();
		String emotionalIndex = parseObject.get("emotionalIndex").toString();
		String publish_time = parseObject.get("publish_time").toString();
		DatafavoriteEntity favorite = datafavoriteService.selectdata(user.getUser_id(),id);
//		System.err.println(favorite1);
		String result = "";
		if(favorite==null) {
		    result = datafavoriteService.adddata(user.getUser_id(),id,projectid,groupid,title,source_name,emotionalIndex,publish_time);
		}else{
			result = datafavoriteService.updatedata(user.getUser_id(),id,projectid,groupid,title,source_name,emotionalIndex,publish_time);
		}
		return result;

	}
	/**
	 * 已读、未读标记
	 * @param request
	 * @param mv
	 * @param session
	 * @param id
	 * @param flag
	 * @return
	 */
    @SystemControllerLog(module = "数据监测", submodule = "已读、未读标记", type = "已读、未读标记", operation = "")
	@PostMapping(value = "/isread")
	@ResponseBody
	public String isread(HttpServletRequest request, ModelAndView mv, HttpSession session,
			@RequestParam(value = "id", required = false, defaultValue = "") String id,
			@RequestParam(value = "flag", required = false, defaultValue = "") int flag) {
		
		String result = "";
		 User user = (User) session.getAttribute("User");
		 Map<String, Object> readsign = new HashMap<>();
			 readsign.put("create_time", DateUtil.nowTime());
			 readsign.put("user_id", user.getUser_id());
			 readsign.put("article_id", id);
	        // 入库
		if(flag==1) {
			try {
	            boolean bool = earlyWarningService.readSign(readsign);
	            if (bool) {
	            	Map<String, Object> map = new HashMap<String, Object>();
	        		map.put("status", 200);
	        		map.put("result", "success");
	        		
	        		result = JSON.toJSONString(map);
	            } else {
	            	Map<String, Object> map = new HashMap<String, Object>();
	        		map.put("status", 500);
	        		map.put("result", "fail");
	        		
	        		result = JSON.toJSONString(map);
	            }
	        } catch (Exception e) {
//	            e.printStackTrace();
	            Map<String, Object> map = new HashMap<String, Object>();
        		map.put("status", 500);
        		map.put("result", "fail");
        		
        		result = JSON.toJSONString(map);
	        }	 
		}else if(flag==2) {
			try {
	           earlyWarningService.delReadSign(readsign);
	            Map<String, Object> map = new HashMap<String, Object>();
	        	map.put("status", 300);
	        	map.put("result", "success");
	        	result = JSON.toJSONString(map);
	        } catch (Exception e) {
//	            e.printStackTrace();
	            Map<String, Object> map = new HashMap<String, Object>();
        		map.put("status", 400);
        		map.put("result", "fail");
        		result = JSON.toJSONString(map);
	        }	
		}
	        
		return result;

	}
	
	/**
	 * 删除信息来源
	 * @param request
	 * @param mv
	 * @param session
	 * @param id
	 * @param flag
	 * @return
	 */
    @SystemControllerLog(module = "数据监测", submodule = "删除信息来源", type = "删除", operation = "deletedata")
	@PostMapping(value = "/deletedata")
	@ResponseBody
	public String deletedata(HttpServletRequest request, ModelAndView mv, HttpSession session,
			@RequestParam(value = "id", required = false, defaultValue = "") String id,
			@RequestParam(value = "flag", required = false, defaultValue = "")int flag,
			@RequestParam(value = "publish_time", required = false, defaultValue = "")String publish_time ) {
		datafavoriteService.deletedata(id,flag,es_search_url,publish_time);
		User user = (User) session.getAttribute("User");
		 Map<String,Object> map =new HashMap<String,Object>();
		 map.put("status", 200);
		 map.put("result", "success");
		return JSON.toJSONString(map);

	}
	/**
	 * 一键复制
	 * @param request
	 * @param mv
	 * @param id
	 * @return
	 */
    @SystemControllerLog(module = "数据监测", submodule = "一键复制", type = "复制", operation = "")
	@PostMapping(value = "/copytext")
	@ResponseBody
	public String copytext(HttpServletRequest request, ModelAndView mv, HttpSession session,
			@RequestParam(value = "id", required = false, defaultValue = "") String id) {
		String result = "";
		String url = es_search_url + MonitorConstant.es_api_article_newdetail;
        String params = "article_public_id=" + id + "&esindex=postal&estype=infor";
        try {
        	String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
    		JSONObject parseObject = JSONObject.parseObject(sendPostEsSearch);
    		String title = parseObject.get("title").toString();
    		String content = parseObject.get("content").toString();
    		String	result1 = "标题："+title+" 内容："+content;
//    		result = "标题："+title+" 内容："+content;
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("status", 200);
    		map.put("result", result1);
    		
    		result = JSON.toJSONString(map);
		} catch (Exception e) {
			// TODO: handle exception
			Map<String, Object> map = new HashMap<String, Object>();
    		map.put("status", 500);
    		map.put("result", "");
    		
    		result = JSON.toJSONString(map);
		}
        
		return result;

	}
	
	/**
	 * 发送、移动
	 * @param request
	 * @param mv
	 * @param session
	 * @param id
	 * @param projectid
	 * @param groupid
	 * @return
	 */
    @SystemControllerLog(module = "数据监测", submodule = "发送移动", type = "发送", operation = "sending")
	@PostMapping(value = "/sending")
	@ResponseBody
	public String sending(HttpServletRequest request, ModelAndView mv, HttpSession session,
			@RequestParam(value = "id", required = false, defaultValue = "") String id,
			@RequestParam(value = "projectid", required = false, defaultValue = "") Long projectid,
			@RequestParam(value = "groupid", required = false, defaultValue = "") Long groupid) {
		String result = "";
		User user = (User) session.getAttribute("User");
		String url = es_search_url + MonitorConstant.es_api_article_newdetail;
        String params = "article_public_id=" + id + "&esindex=postal&estype=infor";
        String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
		JSONObject parseObject = JSONObject.parseObject(sendPostEsSearch);
		String title = parseObject.get("title").toString();
		String source_name = parseObject.get("sourcewebsitename").toString();
		String emotionalIndex = parseObject.get("emotionalIndex").toString();
		String publish_time = parseObject.get("publish_time").toString();
		String content = parseObject.get("content").toString();
		if (content.length() > 255) {
            content = content.substring(0, 254);
        }
		
		Map<String, Object> warning_popup = new HashMap<>();
        warning_popup.put("create_time", DateUtil.nowTime());
        warning_popup.put("warning_article_id", snowFlake.getId());
        warning_popup.put("user_id", user.getUser_id());
        warning_popup.put("popup_id", snowFlake.getId());
        warning_popup.put("popup_content", content);
        warning_popup.put("popup_time", DateUtil.nowTime());
        warning_popup.put("article_id", id);
        warning_popup.put("article_time", publish_time);
        warning_popup.put("article_title", title);
        warning_popup.put("article_emotion", emotionalIndex);
        warning_popup.put("project_id", projectid);
        warning_popup.put("status", 0);
        warning_popup.put("type", 0);
        warning_popup.put("read_status", 0);
        Map<String, Object> article_detail = new HashMap<>();
        article_detail.put("sourcewebsitename", source_name);
        warning_popup.put("article_detail", JSON.toJSONString(article_detail));
        // 入库
        try {
            boolean warning_popupresult = earlyWarningService.saveWarningPopup(warning_popup);
            if (warning_popupresult) {
            	Map<String, Object> map = new HashMap<String, Object>();
        		map.put("status", 200);
        		map.put("result", "1");
        		
        		result = JSON.toJSONString(map);
            } else {
            	Map<String, Object> map = new HashMap<String, Object>();
        		map.put("status", 500);
        		map.put("result", "1");
        		
        		result = JSON.toJSONString(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return result;

	}
	
	
	/**
	 * 读取：已读未读标记
	 * @param request
	 * @param mv
	 * @param session
	 * @param id
	 * @param flag
	 * @return
	 */
    @SystemControllerLog(module = "数据监测", submodule = "读取标记", type = "读取标记", operation = "selectreadsign")
	@PostMapping(value = "/selectreadsign")
	@ResponseBody
	public String selectreadsign(HttpServletRequest request, ModelAndView mv, HttpSession session,
			@RequestParam(value = "id", required = false, defaultValue = "") String id) {
		String result = "";
		User user = (User) session.getAttribute("User");
		Map<String, Object> selectreadsign = new HashMap<>();
		selectreadsign.put("article_id", id);
		selectreadsign.put("user_id", user.getUser_id());
		Map<String, Object> resMap = earlyWarningService.selectReadSign(selectreadsign);
		System.err.println(id);
		if(resMap!=null) {
			 Map<String,Object> map =new HashMap<String,Object>();
			 map.put("status", 200);
			 map.put("result", "success");
			 result = JSON.toJSONString(map);
		}else {
			Map<String,Object> map =new HashMap<String,Object>();
			 map.put("status", 500);
			 map.put("result", "err");
			 result = JSON.toJSONString(map);
		}
		System.err.println(result);
		return result;

	}
    
//    /**
//     * @param [mv]
//     * @return org.springframework.web.servlet.ModelAndView
//     * @description: 添加关键词预警设置 <br>
//     * @version: 1.0 <br>
//     * @date: 2020/4/13 15:44 <br>
//     * @author: wangziqiu <br>
//     */
//    @SystemControllerLog(module = "系统设置", submodule = "系统设置-预警设置-添加关键词预警", type = "新增", operation = "toaddwordwarning")
//    @GetMapping(value = "/createtrack")
//    public ModelAndView toaddwordwarning(HttpServletRequest request, ModelAndView mv, String id, String keyword) {
////        String id = request.getParameter("id");
//        String page = request.getParameter("page");
//
//        WordWarningSetting warning = new WordWarningSetting();
//        warning.setWarning_classify("1,2,3,4,5,6,7,8,9,10,11,");
//        warning.setWarning_content(0);
//        warning.setWarning_similar(0);
//        warning.setWarning_match(2);
//        warning.setWarning_deduplication(0);
//        warning.setWarning_source("{\"type\":\"1\",\"email\":\"\"}");
//        warning.setWarning_receive_time("{\"start\":\"00:00\",\"end\":\"23:00\"}");
//        warning.setWeekend_warning(1);
//        warning.setWarning_interval("{\"type\":\"1\",\"time\":\"1\"}");
//        System.out.println(warning);
//        mv.addObject("warning", warning);
//        mv.addObject("warningStr", JSON.toJSONString(warning));
//        mv.addObject("page", page);
//        mv.addObject("article_id", id);
//        mv.addObject("keyword", keyword);
//        mv.addObject("settingLeft", "wordswarning");
//        mv.setViewName("setting/to_add_wordwarningcopy");
//        return mv;
//    }
    
}
