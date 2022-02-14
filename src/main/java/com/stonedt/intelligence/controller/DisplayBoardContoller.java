package com.stonedt.intelligence.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.aop.SystemControllerLog;
import com.stonedt.intelligence.entity.DatafavoriteEntity;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.DisplayBoardService;
import com.stonedt.intelligence.service.UserService;
import com.stonedt.intelligence.util.TextUtil;


@Controller
@RequestMapping("/displayboard")
public class DisplayBoardContoller {
	@Autowired
	DisplayBoardService displayBoardService;
	@Autowired
	UserService userService;
	
	@SystemControllerLog(module = "综合看板", submodule = "综合看板", type = "查询", operation = "displayboardlist")
	@GetMapping(value = "")
	public ModelAndView displayboardlist(HttpSession session,HttpServletRequest request,ModelAndView mv) {
		User user =  (User)session.getAttribute("User");
		User u = userService.selectUserByTelephone(user.getTelephone());
		session.setAttribute("User", u);
		List<Map<String, Object>> list = displayBoardService.searchDisplayBiardByUser(u);
		String groupId = request.getParameter("groupid");
		String projectId = request.getParameter("projectid");
		if (StringUtils.isBlank(groupId))
			groupId = "";
		if (StringUtils.isBlank(projectId))
			projectId = "";
		if(list.size() > 0){
			Map<String, Object> map = list.get(0);
			Set<String> keySet = map.keySet();
			for (String string : keySet) {
//			if(string)
				String string2 = TextUtil.processQuotationMarks(map.get(string).toString());
				mv.addObject(string,JSONObject.parse(string2));
			}
		}
		JSONObject parseObject2 = JSONObject.parseObject(JSON.toJSONString(u) );
		mv.addObject("user",parseObject2);
		mv.addObject("groupId", groupId);
		mv.addObject("projectId", projectId);
		mv.addObject("groupid", groupId);
		mv.addObject("menu", "displayboard");
		mv.addObject("projectid", projectId);
		mv.setViewName("displayboard/displayboard");
		return mv;
	}
	
	/**
     * @return java.lang.String
     * @description: 获取左侧的方案组数据 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 13:47 <br>
     * @author: huajiancheng <br>
     * @params []
     */
	/*@SystemControllerLog(module = "综合看板", submodule = "获取左侧的方案组数据", type = "查询", operation = "getprojectType")*/
    @PostMapping(value = "/collection")
    @ResponseBody
    public String getprojectType(@RequestParam(value="user_id") Long user_id,HttpServletRequest request) {
        JSONObject response = new JSONObject();
        List<DatafavoriteEntity> result = displayBoardService.getCollectionByuser(user_id);
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        try {
//            list = projectUtil.getprojectType();
//            response.put("code", 200);
//            response.put("msg", "方案组数据返回成功");
//            response.put("data", list);
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.put("code", 500);
//            response.put("msg", "方案组数据返回失败");
//            response.put("data", list);
//        }
        response.put("user_id", user_id);
        response.put("data", result);
        return JSON.toJSONString(response);
    }
    
    /**
     * @return java.lang.String
     * @description: 获取左侧的方案组数据 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 13:47 <br>
     * @author: huajiancheng <br>
     * @params []
     */
	/*@SystemControllerLog(module = "综合看板", submodule = "获取左侧的方案组数据", type = "查询", operation = "collection2")*/
	@PostMapping(value = "/collection2")
    @ResponseBody
    public String getprojectType2(@RequestParam(value="user_id") Long user_id,HttpServletRequest request) {
        JSONObject response = new JSONObject();
        List<DatafavoriteEntity> result = displayBoardService.getCollectionByuser(user_id);
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        try {
//            list = projectUtil.getprojectType();
//            response.put("code", 200);
//            response.put("msg", "方案组数据返回成功");
//            response.put("data", list);
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.put("code", 500);
//            response.put("msg", "方案组数据返回失败");
//            response.put("data", list);
//        }
        response.put("user_id", user_id);
        response.put("data", result);
        return JSON.toJSONString(response);
    }
	

}
