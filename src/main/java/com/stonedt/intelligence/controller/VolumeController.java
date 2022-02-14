package com.stonedt.intelligence.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.stonedt.intelligence.service.VolumeMonitorService;

/**
 * description: 声量监测控制器 <br>
 * date: 2020/4/13 15:55 <br>
 * author: xiaomi <br>
 * version: 1.0 <br>
 */
@Controller
@RequestMapping(value = "/volume")
public class VolumeController {
    /**
     * @param [mv]
     * @return org.springframework.web.servlet.ModelAndView
     * @description: 跳转声量监测页面 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 15:58 <br>
     * @author: huajiancheng <br>
     */
	@Autowired
	private VolumeMonitorService volumeMonitorService;

//	@Autowired
//    private UserUtil userUtil;
//	
//	@Autowired
//	private ProjectService projectService;

    @GetMapping(value = "")
    public ModelAndView tovolume(ModelAndView mv, HttpServletRequest request) {
    	String groupId = request.getParameter("groupid");
    	String projectId = request.getParameter("projectid");
    	if (StringUtils.isBlank(groupId)) groupId = "";
    	if (StringUtils.isBlank(projectId)) projectId = "";
    	mv.addObject("groupId", groupId);
    	mv.addObject("projectId", projectId);
    	mv.addObject("groupid", groupId);
    	mv.addObject("projectid", projectId);
        mv.setViewName("volume/volumeMonitor");
        mv.addObject("menu", "volume");
        return mv;
    }

    @PostMapping("/getproject")
    @ResponseBody
    public Map<String, Object> getproject(@RequestParam("projectid") String projectid,@RequestParam("time_period") Integer time_period,HttpServletRequest request) {
    	Map<String,Object> map = new HashMap<String, Object>();
    	map.put("project_id", projectid);
    	map.put("time_period", time_period);

//    	User user = userUtil.getuser(request);
//        Long user_id = user.getUser_id();
//        Map<String, Object> jsona = new HashMap<String, Object>();
//        Map<String, Object> queryUserid = projectService.queryUserid(user_id);
//        if (MapUtils.isEmpty(queryUserid)) {
//        	System.err.println(jsona+"---");
//        	return jsona;
//		}

    	Map<String, Object> getproject = volumeMonitorService.getproject(map);
		return getproject;
	}

    @PostMapping("/projectname")
    @ResponseBody
    public Map<String, Object> getprojectname(@RequestParam("projectid") String projectid,@RequestParam("groupId") String groupId){
    	Map<String,Object> hashMap = new HashMap<String, Object>();
    	hashMap.put("project_id", projectid);
    	hashMap.put("group_id", groupId);
    	Map<String, Object> getprojectName = volumeMonitorService.getprojectName(hashMap);
    	if (getprojectName != null) {
    		return getprojectName;
		}
    	return new HashMap<String, Object>();
    }
}
