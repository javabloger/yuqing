package com.stonedt.intelligence.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.stonedt.intelligence.aop.SystemControllerLog;
import com.stonedt.intelligence.service.*;
import com.stonedt.intelligence.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.dao.ProjectTaskDao;
import com.stonedt.intelligence.entity.Project;
import com.stonedt.intelligence.entity.ProjectTask;
import com.stonedt.intelligence.entity.SolutionGroup;
import com.stonedt.intelligence.entity.User;

/**
 * description: 监测管理 <br>
 * date: 2020/4/13 10:53 <br>
 * author: xiaomi <br>
 * version: 1.0 <br>
 */
@Controller
@RequestMapping(value = "/project")
public class ProjectController {

    @Autowired
    private SolutionGroupService solutionGroupService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private ProjectUtil projectUtil;
    @Autowired
    private OpinionConditionService opinionConditionService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private ProjectTaskDao projectTaskDao;
    @Autowired
    MonitorService monitorService;
    @Value("${kafuka.url}")
    private String kafuka_url;

    @Value("${insertnewwords.url}")
    private String insert_new_words_url;

//    @Autowired
//    private RestTemplate template;

    /**
     * 删除方案组
     */
    @SystemControllerLog(module = "监测管理", submodule = "监测管理-删除方案组", type = "删除", operation = "updateSolutionGroupStatus")
    @PostMapping(value = "/updateSolutionGroupStatus")
    @ResponseBody
    public String updateSolutionGroupStatus(Long groupId) {
        Map<String, Object> updateSolutionGroupStatus = solutionGroupService.updateSolutionGroupStatus(groupId);
        return JSON.toJSONString(updateSolutionGroupStatus);
    }

    /**
     * 获取方案组下方案的数量
     */
    /*@SystemControllerLog(module = "监测管理", submodule = "监测管理-查询方案组", type = "查询", operation = "getProjectCountByGroupId")*/
    @PostMapping(value = "/getProjectCountByGroupId")
    @ResponseBody
    public String getProjectCountByGroupId(Long groupId) {
        Map<String, Object> projectCountByGroupId = projectService.getProjectCountByGroupId(groupId);
        return JSON.toJSONString(projectCountByGroupId);
    }

    /**
     * 获取方案组和方案的名称
     */
    /*@SystemControllerLog(module = "监测管理", submodule = "监测管理-查询方案组", type = "查询", operation = "names")*/
    @PostMapping(value = "/names")
    @ResponseBody
    public String names(Long projectId, Long groupId) {
        Map<String, String> result = new HashMap<String, String>();
        if (null != projectId) {
            String projectName = projectService.getProjectName(projectId);
            result.put("projectName", projectName);
        }
        if (null != groupId) {
            String groupName = solutionGroupService.getGroupName(groupId);
            result.put("groupName", groupName);
        }
        return JSON.toJSONString(result);
    }

    /***
     * @description: 跳转监测管理页面 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 13:37 <br>
     * @author: huajiancheng <br>
     * @params [groupid, projectsearch, page, menu, mv]
     * @return org.springframework.web.servlet.ModelAndView
     * */
    @SystemControllerLog(module = "监测管理", submodule = "监测管理-列表", type = "查询", operation = "")
    @GetMapping(value = "")
    public ModelAndView project(@RequestParam(value = "groupid", required = false) Long groupid,
                                @RequestParam(value = "projectsearch", required = false) String projectsearch,
                                @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                @RequestParam(value = "menu", required = false) String menu,
                                ModelAndView mv, HttpServletRequest request) {
        boolean projectFlag = monitorService.boolUserProjectByUserId(request);
        mv.addObject("groupid", groupid);
        mv.addObject("currentPage", page);
        mv.addObject("projectsearch", projectsearch);
        mv.addObject("menu", "project");
        mv.addObject("projectFlag", projectFlag);
        mv.setViewName("projectCenter/projectlist");
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
    /*@SystemControllerLog(module = "监测管理", submodule = "监测管理-查询方案组", type = "查询", operation = "groupandproject")*/
    @PostMapping(value = "/groupandproject")
    @ResponseBody
    public String groupandproject(HttpServletRequest request) {
        JSONObject response = new JSONObject();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = projectUtil.getGroupInfoByUserId(request);
            response.put("code", 200);
            response.put("msg", "方案组数据返回成功");
            response.put("data", list);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("msg", "方案组数据返回失败");
            response.put("data", list);
        }
        return JSON.toJSONString(response);
    }

    /***
     * @description: 获取对应方案组的方案列表数据 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 13:50 <br>
     * @author: huajiancheng <br>
     * @param groupid  方案组id
     * @param projectsearch 方案搜索关键词
     * @return java.lang.String
     * */
    @SystemControllerLog(module = "监测管理", submodule = "方案组监测列表", type = "查询", operation = "listproject")
    @PostMapping(value = "/listproject")
    @ResponseBody
    public JSONObject listproject(@RequestParam(value = "groupid") Long groupid,
                                  @RequestParam(value = "projectsearch", required = false, defaultValue = "") String projectsearch,
                                  @RequestParam(value = "page", defaultValue = "1") Integer page,
                                  HttpServletRequest request) {
        JSONObject response = projectUtil.getProjectInfoByGroupIdAndUserId(request, projectsearch, groupid, page, 10);
        return response;
    }


    /**
     * @param group_name 方案组名字,
     * @param userid     用户id
     * @return java.lang.String
     * @description: 创建方案组 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 13:54 <br>
     * @author: kanshicheng <br>
     */
    @SystemControllerLog(module = "监测管理", submodule = "监测管理-新建方案组", type = "新增", operation = "mkdirgroup")
    @PostMapping(value = "/mkdirgroup")
    @ResponseBody
    public String mkdirgroup(@RequestParam("group_name") String group_name, HttpSession session, HttpServletRequest request) {
        SolutionGroup sg = new SolutionGroup();
        sg.setGroupName(group_name);
        sg.setGroupId(SnowflakeUtil.getId());
        User u = userUtil.getuser(request);
        if (u.getUser_id() != null) {
            sg.setUserId(u.getUser_id());
        }
        //新增方案组数据
        int i = solutionGroupService.addSolutionGroup(sg);
        if (i > 0) {
            return "success";
        } else {
            return "fail";
        }
    }

    /**
     * @param group_name 方案组名字,
     * @param userid     用户id
     * @return java.lang.String
     * @description: 修改方案组 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 13:54 <br>
     * @author: kanshicheng <br>
     */
    @SystemControllerLog(module = "监测管理", submodule = "监测管理-修改方案组", type = "修改", operation = "editgroup")
    @PostMapping(value = "/editgroup")
    @ResponseBody
    public JSONObject editgroup(@RequestParam("group_name") String group_name,
                                @RequestParam("group_id") Long group_id, HttpServletRequest request) {
        SolutionGroup sg = new SolutionGroup();
        sg.setGroupName(group_name);
        sg.setGroupId(group_id);
        User user = userUtil.getuser(request);
        if (user.getUser_id() != null) {
            sg.setUserId(user.getUser_id());
        }
        JSONObject response = solutionGroupService.editSolutionGroup(sg);
        return response;
    }

    /**
     * @param [mv]
     * @return org.springframework.web.servlet.ModelAndView
     * @description: 跳转新增方案页面 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 13:57 <br>
     * @author: huajiancheng <br>
     */
    @SystemControllerLog(module = "监测管理", submodule = "监测管理-新增方案", type = "新增", operation = "addproject")
    @GetMapping(value = "/addproject")
    public ModelAndView addproject(ModelAndView mv, HttpServletRequest request,
                                   @RequestParam("groupid") Long groupid) {
        String groupId = request.getParameter("groupid");
        mv.setViewName("projectCenter/createProject");
        mv.addObject("menu", "project");
        mv.addObject("groupId", groupId);
        mv.addObject("groupid", groupid);
        return mv;
    }

    /**
     * @param [request]
     * @return com.alibaba.fastjson.JSONObject
     * @description: 新增方案前检查方案组数量 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/15 15:47 <br>
     * @author: huajiancheng <br>
     */

    /*@SystemControllerLog(module = "监测管理", submodule = "监测管理-查询方案组", type = "查询", operation = "verifygroup")*/
    @PostMapping(value = "/verifygroup")
    @ResponseBody
    public JSONObject verifygroup(HttpServletRequest request) {
        JSONObject response = new JSONObject();
        List<Map<String, Object>> list = projectUtil.getGroupInfoByUserId(request);
        if (list.size() <= 0) {
            response.put("code", 500);
        } else {
            response.put("code", 200);
        }
        return response;
    }


    /**
     * @param [mv]
     * @return org.springframework.web.servlet.ModelAndView
     * @description: 跳转方案详情页面 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 13:57 <br>
     * @author: huajiancheng <br>
     */
    @SystemControllerLog(module = "监测管理", submodule = "监测管理-方案详情", type = "查询", operation = "detail")
    @GetMapping(value = "/detail")
    public ModelAndView detailproject(@RequestParam("groupid") Long groupid, ModelAndView mv, @RequestParam("projectid") Long projectid) {
        mv.addObject("groupid", String.valueOf(groupid));
        mv.addObject("projectid", String.valueOf(projectid));
        mv.setViewName("projectCenter/projectlistDetail");
        Map<String, Object> map = projectService.getProjectByProId(projectid);
        mv.addObject("projectDetail", map);
        mv.addObject("menu", "project");
        mv.addObject("detail_projectid", projectid);
        return mv;
    }

    /*@SystemControllerLog(module = "监测管理", submodule = "监测管理-方案详情", type = "查询", operation = "detail")*/
    @PostMapping(value = "/detail")
    @ResponseBody
    public String detail(Long projectid) {
        Map<String, Object> map = projectService.getProjectByProId(projectid);
        return JSON.toJSONString(map);
    }


    /**
     * @param paramJson 用户输入的方案数据
     * @return java.lang.String
     * @description: 提交新增的方案数据 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 13:59 <br>
     * @author: kanshicheng <br>
     */
    @SystemControllerLog(module = "监测管理", submodule = "监测管理-新增方案", type = "提交新增", operation = "commitproject")
    @PostMapping(value = "/commitproject")
    @ResponseBody
    public JSONObject commitproject(@RequestBody JSONObject paramJson, HttpServletRequest request) {
        JSONObject response = new JSONObject();
        JSONObject kafukaJson = new JSONObject();
        kafukaJson = paramJson;
        
        JSONObject CommonJson = new JSONObject();
        CommonJson = paramJson;
        
        JSONObject idJson = new JSONObject();
        User user = userUtil.getuser(request);
        Long user_id = user.getUser_id();
        String project_name = paramJson.getString("project_name");
        Long group_id = paramJson.getLong("group_id");
        Map<String, Object> verifyMap = new HashMap<String, Object>();
        verifyMap.put("user_id", user_id);
        verifyMap.put("del_status", 0);
        verifyMap.put("project_name", project_name);
        verifyMap.put("group_id", group_id);
        Integer existCount = projectService.getProjectCount(verifyMap);
        if (existCount <= 0) {
            Project p = new Project();
            //公共id
            String stop_word = paramJson.getString("stop_word");
            String regional_word = paramJson.getString("regional_word");
            String event_word = paramJson.getString("event_word");
            String character_word = paramJson.getString("character_word");
            String subject_word = paramJson.getString("subject_word");

            stop_word = projectUtil.dealProjectWords(stop_word);
            regional_word = projectUtil.dealProjectWords(regional_word);
            event_word = projectUtil.dealProjectWords(event_word);
            character_word = projectUtil.dealProjectWords(character_word);
            subject_word = projectUtil.dealProjectWords(subject_word);

            Long projectid = SnowflakeUtil.getId();
            p.setProjectId(projectid);
            p.setProjectName(paramJson.getString("project_name"));
            p.setProjectType(paramJson.getInteger("project_type"));
            p.setProjectDescription(paramJson.getString("project_description"));
            p.setSubjectWord(subject_word);
            p.setCharacterWord(character_word);
            p.setEventWord(event_word);
            p.setRegionalWord(regional_word);
            p.setStopWord(stop_word);
            p.setGroupId(paramJson.getLong("group_id"));
            p.setUserId(userUtil.getuser(request).getUser_id());
            //新增方案
            int i = projectService.insertProject(p);
            if (i > 0) {
                Map<String, Object> paramOpinionMap = new HashMap<String, Object>();
                String create_time = DateUtil.getNowTime();
                Long opinion_condition_id = SnowflakeUtil.getId();
                paramOpinionMap.put("create_time", create_time);
                paramOpinionMap.put("opinion_condition_id", opinion_condition_id);
                paramOpinionMap.put("project_id", projectid);
                paramOpinionMap.put("time", 4);
                if (stop_word.equals("")) {
                    paramOpinionMap.put("precise", 0);
                } else {
                    paramOpinionMap.put("precise", 1);
                }
                paramOpinionMap.put("emotion", "[1,2,3]");
                paramOpinionMap.put("similar", 0);
                paramOpinionMap.put("sort", 1);
                paramOpinionMap.put("matchs", 1);
                Integer opinionConditionCount = opinionConditionService.addOpinionConditionById(paramOpinionMap);

                Map<String, Object> warningMap = new HashMap<String, Object>();
                Long warning_setting_id = SnowflakeUtil.getId();
                warningMap.put("create_time", create_time);
                warningMap.put("project_id", projectid);
                warningMap.put("warning_setting_id", warning_setting_id);
                warningMap.put("warning_status", 0);
                warningMap.put("warning_name", "预警");
                warningMap.put("warning_word", "");
                warningMap.put("warning_classify", "1,2,3,4,5,6,7,8,9,10,11");
                warningMap.put("warning_content", 0);
                warningMap.put("warning_similar", 0);
                warningMap.put("warning_match", 2);
                warningMap.put("warning_deduplication", 0);
                warningMap.put("weekend_warning", 1);
                warningMap.put("warning_interval", "{\"type\":\"1\",\"time\":\"1\"}");
                warningMap.put("warning_source", "{\"type\":\"1\",\"email\":\"\"}");
                warningMap.put("warning_receive_time", "{\"start\":\"00:00\",\"end\":\"23:00\"}");
                Integer warningCount = systemService.addWarning(warningMap);

                idJson.put("project_id", projectid);
                idJson.put("group_id", group_id);

                if (opinionConditionCount > 0 && warningCount > 0) {
                    response.put("code", 200);
                    response.put("msg", "方案新增成功");
                    response.put("data", idJson);
                } else {
                    response.put("code", 500);
                    response.put("msg", "方案新增失败");
                    response.put("data", idJson);
                }

                ProjectTask projectTask = new ProjectTask();
                projectTask.setAnalysis_flag(0);
//                projectTask.setCharacter_word(paramJson.getString("character_word"));
//                projectTask.setEvent_word(paramJson.getString("event_word"));
//                projectTask.setProject_id(projectid);
//                projectTask.setProject_type(paramJson.getInteger("project_type"));
//                projectTask.setRegional_word(paramJson.getString("regional_word"));
//                projectTask.setStop_word(paramJson.getString("stop_word"));
//                projectTask.setSubject_word(paramJson.getString("subject_word"));
                projectTask.setCharacter_word(character_word);
                projectTask.setEvent_word(event_word);
                projectTask.setProject_id(projectid);
                projectTask.setProject_type(paramJson.getInteger("project_type"));
                projectTask.setRegional_word(regional_word);
                projectTask.setStop_word(stop_word);
                projectTask.setSubject_word(subject_word);
                projectTask.setVolume_flag(0);
                projectTaskDao.saveProjectTask(projectTask);


                String message = "";
                
                if(CommonJson.getIntValue("project_type") == 1){
                	message = ProjectWordUtil.CommononprojectKeyWord(kafukaJson.getString("subject_word"));
                }else {
                	
                	if(kafukaJson.getString("subject_word").indexOf("\\|")!=-1||kafukaJson.getString("subject_word").indexOf("+")!=-1) {
                		message = ProjectWordUtil.CommononprojectKeyWord(kafukaJson.getString("subject_word"));
                	}else {
                		kafukaJson.remove("project_type");
                        kafukaJson.remove("group_id");
                        kafukaJson.remove("project_name");
                        for (Map.Entry entry : kafukaJson.entrySet()) {
                            String value = String.valueOf(entry.getValue());
                            if (!value.equals("")) {
                                if (value.endsWith(",")) {
                                    value = value.substring(0, value.lastIndexOf(","));
                                }

                                if (!message.equals("")) {
                                    message = message + "," + value;
                                } else {
                                    message = value;
                                }

                                if (message.endsWith(",")) {
                                    message = message.substring(0, message.lastIndexOf(","));
                                }
                            }
                        }
                   }
                	}
                	
                String kafukaResponse = MyHttpRequestUtil.doPostKafka("ikHotWords", message, kafuka_url);
                RestTemplate template = new RestTemplate();
                MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
                paramMap.add("text", message);
                String result = template.postForObject(insert_new_words_url, paramMap, String.class);
                System.out.println("result========================="+result);
            } else {
                response.put("code", 500);
                response.put("msg", "方案新增失败");
            }
        } else {
            response.put("code", 500);
            response.put("msg", "方案名已存在");
        }
        return response;
    }

    /**
     * @param [mv, projectid]
     * @return org.springframework.web.servlet.ModelAndView
     * @description: 修改方案，跳转修改方案页面 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/15 15:15 <br>
     * @author: huajiancheng <br>
     */

    @SystemControllerLog(module = "监测管理", submodule = "监测管理-修改方案", type = "修改", operation = "editproject")
    @GetMapping(value = "/editproject")
    public ModelAndView editproject(ModelAndView mv, @RequestParam("projectid") Long projectid,
                                    @RequestParam("groupid") Long groupid,
                                    HttpServletRequest request) {
        String groupId = request.getParameter("groupid");
        if (StringUtils.isBlank(groupId)) groupId = "";
        String projectId = request.getParameter("projectid");
        if (StringUtils.isBlank(projectId)) projectId = "";

        mv.addObject("groupId", groupId);
        mv.addObject("groupid", groupid);
        mv.addObject("projectid", projectId);
        mv.addObject("edit_groupid", groupid);
        mv.setViewName("projectCenter/editProject");
        mv.addObject("menu", "project");
        mv.addObject("edit_projectid", projectid);
        return mv;
    }


    /**
     * @param [mv, projectid]
     * @return org.springframework.web.servlet.ModelAndView
     * @description: 修改方案，跳转修改方案页面 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/15 15:15 <br>
     * @author: huajiancheng <br>
     */

    /*@SystemControllerLog(module = "监测管理", submodule = "监测管理-修改方案", type = "修改", operation = "getedit")*/
    @GetMapping(value = "/getedit")
    @ResponseBody
    public String getedit(@RequestParam("projectid") Long projectid) {
        JSONObject response = new JSONObject();
        Map<String, Object> project = projectService.getProjectByProId(projectid);
        Map<String, Object> opinionParamMap = new HashMap<String, Object>();
        opinionParamMap.put("projectid", projectid);
        Map<String, Object> opinionResponseMap = projectService.getOpinionConditionById(opinionParamMap);
        String precise = String.valueOf(opinionResponseMap.get("precise"));
        project.put("precise", precise);

        if (project != null) {
            response.put("code", 200);
            response.put("msg", "获取方案信息成功");
            response.put("data", project);
        } else {
            response.put("code", 500);
            response.put("msg", "获取方案信息失败");
            response.put("data", project);
        }
        return JSON.toJSONString(response);
    }


    /***
     * @description: 提交用户修改的方案数据 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 14:02 <br>
     * @author: huajiancheng <br>
     * @param [paramJson 用户修改的方案数据 ]
     * @return java.lang.String
     * */
    @SystemControllerLog(module = "监测管理", submodule = "监测管理-修改方案", type = "提交修改", operation = "commiteditproject")
    @PostMapping(value = "/commiteditproject")
    @ResponseBody
    public JSONObject commiteditproject(@RequestBody JSONObject paramJson, HttpServletRequest request) {
        JSONObject response = new JSONObject();
        if (paramJson.getIntValue("project_type") == 1) {
            paramJson.put("character_word", "");
            paramJson.put("event_word", "");
            paramJson.put("regional_word", "");
        } else {
            String regional_word = paramJson.getString("regional_word");
            String event_word = paramJson.getString("event_word");
            String character_word = paramJson.getString("character_word");
            regional_word = projectUtil.dealProjectWords(regional_word);
            event_word = projectUtil.dealProjectWords(event_word);
            character_word = projectUtil.dealProjectWords(character_word);
            paramJson.put("regional_word", regional_word);
            paramJson.put("event_word", event_word);
            paramJson.put("character_word", character_word);
        }

        String stop_word = paramJson.getString("stop_word");
        String subject_word = paramJson.getString("subject_word");
        stop_word = projectUtil.dealProjectWords(stop_word);
        subject_word = projectUtil.dealProjectWords(subject_word);
        paramJson.put("stop_word", stop_word);
        paramJson.put("subject_word", subject_word);

        User user = userUtil.getuser(request);
        Long user_id = user.getUser_id();
        Map<String, Object> editParam = paramJson;
        JSONObject kafukaJson = new JSONObject();
        kafukaJson = paramJson;
        
        JSONObject CommonJson = new JSONObject();
        CommonJson = paramJson;
        
        String update_time = DateUtil.nowTime();
        editParam.put("user_id", user_id);
        editParam.put("update_time", update_time);

        String project_id = paramJson.getString("project_id");
        String precise = paramJson.getString("precise");
        Map<String, Object> opinionConditionParam = new HashMap<String, Object>();
        opinionConditionParam.put("projectid", project_id);
        if (precise.equals("0")) {
            opinionConditionParam.put("precise", 0);
        } else {
            opinionConditionParam.put("precise", 1);
        }
        @SuppressWarnings("unused")
        Integer opinronCount = projectService.updateOpinionConditionById(opinionConditionParam);
        Integer count = projectService.editProjectInfo(editParam);
        if (count > 0) {
           
            String message = "";
            
            
            if(CommonJson.getIntValue("project_type") == 1){
            	message = ProjectWordUtil.CommononprojectKeyWord(kafukaJson.getString("subject_word"));
            }else {
            	
            	if(kafukaJson.getString("subject_word").indexOf("\\|")!=-1||kafukaJson.getString("subject_word").indexOf("+")!=-1) {
            		message = ProjectWordUtil.CommononprojectKeyWord(kafukaJson.getString("subject_word"));
            	}else {

               	 // 将词发给卡夫卡
                   kafukaJson.remove("project_type");
                   kafukaJson.remove("group_id");
                   kafukaJson.remove("project_id");
                   kafukaJson.remove("project_name");
                   kafukaJson.remove("update_time");
                   kafukaJson.remove("user_id");
               	for (Map.Entry entry : kafukaJson.entrySet()) {
                       String value = String.valueOf(entry.getValue());
                       if (!value.equals("")) {
                           if (value.endsWith(",")) {
                               value = value.substring(0, value.lastIndexOf(","));
                           }

                           if (!message.equals("")) {
                               message = message + "," + value;
                           } else {
                               message = value;
                           }
                           if (message.endsWith(",")) {
                               message = message.substring(0, message.lastIndexOf(","));
                           }
                       }
                   }
            	}
            	
            	
            	
            }
            
            String kafukaResponse = MyHttpRequestUtil.doPostKafka("ikHotWords", message, kafuka_url);
            RestTemplate template = new RestTemplate();
            MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
            paramMap.add("text", message);
            try {
            	String result = template.postForObject(insert_new_words_url, paramMap, String.class);
            	 System.out.println("result========================="+result);
			} catch (Exception e) {
				// TODO: handle exception
			}
            

            response.put("code", 200);
            response.put("msg", "方案信息修改成功！");
        } else {
            response.put("code", 500);
            response.put("msg", "方案信息修改失败！");
        }
        return response;
    }


    /**
     * @param []
     * @return com.alibaba.fastjson.JSONObject
     * @description: 获取方案组和方案列表数据 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/14 17:52 <br>
     * @author: huajiancheng <br>
     */
    /*@SystemControllerLog(module = "监测管理", submodule = "监测管理-查询方案组", type = "查询", operation = "getGroupAndProject")*/
    @PostMapping(value = "getGroupAndProject")
    @ResponseBody
    public String getGroupAndProject(HttpServletRequest request) {
        JSONObject response = projectUtil.getProjectAndGroupInfoByUserId(request);
        return JSON.toJSONString(response);
    }


    /**
     * @param [request]
     * @return java.lang.String
     * @description: 删除方案 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/16 10:54 <br>
     * @author: huajiancheng <br>
     */
    @SystemControllerLog(module = "监测管理", submodule = "监测管理-删除方案", type = "删除", operation = "delProject")
    @PostMapping(value = "delProject")
    @ResponseBody
    public String delProject(@RequestParam(value = "groupid", required = true) Long groupid,
                             @RequestParam(value = "projectid", required = true) String projectid,
                             HttpServletRequest request) {
        List<String> projectids = new ArrayList<String>();
        JSONObject response = new JSONObject();
        if (projectid.contains(",")) {
            projectids = Arrays.asList(projectid.split(","));
        } else {
            projectids = Arrays.asList(projectid.split("，"));
        }
        User user = userUtil.getuser(request);
        Long user_id = user.getUser_id();

        for (int i = 0; i < projectids.size(); i++) {
            String id = projectids.get(i);
            Map<String, Object> delParam = new HashMap<String, Object>();
            delParam.put("user_id", user_id);
            delParam.put("del_status", 1);
            delParam.put("project_id", id);
            Integer count = projectService.delProject(delParam);
            if (count > 0) {
                response.put("delstatus", 200);
                response.put("msg", "方案删除成功！");
            } else {
                response.put("delstatus", 500);
                response.put("msg", "方案删除失败！");
            }
        }

        response = projectUtil.getProjectInfoByGroupIdAndUserId(request, "", groupid, 1, 10);

        return JSON.toJSONString(response);
    }

    @SystemControllerLog(module = "监测管理", submodule = "监测管理-删除方案", type = "删除", operation = "delProjectDetail")
    @PostMapping(value = "/delProjectDetail")
    @ResponseBody
    public String delProjectDetail(String projectid, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        long user_id = userUtil.getUserId(request);
        Map<String, Object> delParam = new HashMap<String, Object>();
        delParam.put("user_id", user_id);
        delParam.put("del_status", 1);
        delParam.put("project_id", projectid);
        Integer count = projectService.delProject(delParam);
        if (count > 0) {
            result.put("delstatus", 200);
            result.put("msg", "方案删除成功！");
        } else {
            result.put("delstatus", 500);
            result.put("msg", "方案删除失败！");
        }
        return JSON.toJSONString(result);
    }

    /**
     * 批量删除方案
     */
    @SystemControllerLog(module = "监测管理", submodule = "监测管理-删除方案", type = "删除", operation = "batchUpdateProject")
    @PostMapping(value = "/batchUpdateProject")
    @ResponseBody
    public String batchUpdateProject(String projectIds, HttpServletRequest request) {
        long userId = userUtil.getUserId(request);
        Map<String, Object> batchUpdateProject = projectService.batchUpdateProject(userId, projectIds);
        return JSON.toJSONString(batchUpdateProject);
    }

    @PostMapping(value = "/keywords")
    @ResponseBody
    public String getAllKeywords(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                 @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        JSONObject response = projectService.getAllKeywords();
        return response.toJSONString();
    }

}


