package com.stonedt.intelligence.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.stonedt.intelligence.aop.SystemControllerLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.stonedt.intelligence.entity.OpinionCondition;
import com.stonedt.intelligence.entity.Project;
import com.stonedt.intelligence.entity.SolutionGroup;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.entity.WarningSetting;
import com.stonedt.intelligence.service.EarlyWarningService;
import com.stonedt.intelligence.service.OpinionConditionService;
import com.stonedt.intelligence.service.ProjectService;
import com.stonedt.intelligence.service.SolutionGroupService;
import com.stonedt.intelligence.service.SystemService;
import com.stonedt.intelligence.service.UserService;
import com.stonedt.intelligence.util.ProjectUtil;
import com.stonedt.intelligence.util.ResultUtil;
import com.stonedt.intelligence.util.UserUtil;

/**
 * description: 系统设置、系统消息控制器 <br>
 */
@Controller
@RequestMapping(value = "/system")
@PropertySource(value = "file:./config/config.properties", encoding = "UTF-8")
public class SystemController {

    @Autowired
    private UserUtil userUtil;
    @Autowired
    private ProjectUtil projectUtil;
    @Autowired
    private SystemService systemService;
    @Autowired
    private OpinionConditionService opinionConditionService;
    @Autowired
    private SolutionGroupService solutionGroupService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private EarlyWarningService earlyWarningService;

    @Value("${product.manual.path}")
    private String productManualPath;
    
    @Autowired
    private UserService userService;
    

    /**
     * 在线查看产品使用手册
     */
    @SystemControllerLog(module = "系统设置", submodule = "系统设置-产品手册", type = "查询", operation = "productmanual/online")
    @GetMapping(value = "/productmanual/online")
    public String onlineProductManual(Model model) {
        model.addAttribute("pdfPath", "/pdf/" + productManualPath);
        return "setting/pdf_online";
    }

    /**
     * 下载产品使用手册
     */
    @SystemControllerLog(module = "系统设置", submodule = "系统设置-产品手册", type = "下载", operation = "uploadProductManual")
    @GetMapping(value = "/uploadProductManual")
    public ResponseEntity<InputStreamResource> uploadProductManual() {
        ResponseEntity<InputStreamResource> uploadProductManual = systemService.uploadProductManual();
        return uploadProductManual;
    }

    /**
     * 根据用户id获取所有的方案组
     */
//    @SystemControllerLog(module = "系统设置", submodule = "系统设置-产品手册", type = "查询", operation = "listSolutionGroupByUserId")
    @PostMapping(value = "/listSolutionGroupByUserId")
    @ResponseBody
    public String listSolutionGroupByUserId(HttpServletRequest request) {
        long userId = userUtil.getUserId(request);
        List<SolutionGroup> listSolutionGroupByUserId = solutionGroupService.listSolutionGroupByUserId(userId);
        return JSON.toJSONString(listSolutionGroupByUserId);
    }

    /**
     * 根据方案组id获取所有的方案
     */
    @PostMapping(value = "/listProjectByGroupId")
    @ResponseBody
    public String listProjectByGroupId(Long groupId) {
        List<Project> listProjectByGroupId = projectService.listProjectByGroupId(groupId);
        return JSON.toJSONString(listProjectByGroupId);
    }

    /**
     * @param [mv]
     * @return org.springframework.web.servlet.ModelAndView
     * @description: 跳转预警配置页面 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 15:44 <br>
     * @author: huajiancheng <br>
     */
    @SystemControllerLog(module = "系统设置", submodule = "系统设置-预警设置-配置列表", type = "查询", operation = "warning")
    @GetMapping(value = "/warning")
    public ModelAndView warning(HttpServletRequest request, ModelAndView mv) {
        String groupid = request.getParameter("groupid");
        String page = request.getParameter("page");
        List<Map<String, Object>> groupInfo = projectUtil.getGroupInfoByUserId(request);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("group_id", "");
        map.put("group_name", "全部方案组");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list.add(map);
        for (int i = 0; i < groupInfo.size(); i++) {
            list.add(groupInfo.get(i));
        }
        mv.addObject("groupInfoList", list);
        mv.addObject("groupid", groupid);
        mv.addObject("page", page);
        mv.addObject("settingLeft", "warning");
        mv.setViewName("setting/warning");
        return mv;
    }


    /**
     * 预警列表
     *
     * @param request
     * @param pageNum
     * @param warning_project_name
     * @return
     */
    /*@SystemControllerLog(module = "系统设置", submodule = "系统设置-预警设置-配置列表", type = "查询", operation = "listWarning")*/
    @PostMapping(value = "/listWarning")
    public @ResponseBody
    ResultUtil getWarningList(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                              @RequestParam(value = "groupId", required = false) String groupId) {
        Long id = null;
        if (StringUtils.isNotBlank(groupId)) {
            id = Long.valueOf(groupId);
        }
        long userId = userUtil.getUserId(request);
        Map<String, Object> listWarning = systemService.listWarning(page, userId, id);
        return ResultUtil.build(200, "", JSON.toJSONString(listWarning));
    }

    /**
     * 修改预警开关
     *
     * @param warning_status
     * @param project_id
     * @return
     */
    @SystemControllerLog(module = "系统设置", submodule = "系统设置-预警设置-配置列表", type = "修改开关", operation = "updateWarningStatusById")
    @PostMapping(value = "/updateWarningStatusById")
    public @ResponseBody
    ResultUtil updateWarningStatusById(@RequestParam(value = "warning_status", defaultValue = "1", required = false) Integer warning_status,
                                       @RequestParam(value = "project_id", required = false) String project_id) {
        boolean updateWarning = systemService.updateWarningStatusById(warning_status, Long.valueOf(project_id));
        if (updateWarning) {
            return ResultUtil.build(200, "");
        }
        return ResultUtil.build(500, "");
    }


    /*@SystemControllerLog(module = "系统设置", submodule = "系统设置-预警设置-配置列表", type = "查询", operation = "updateWarningStatusById")*/
    @PostMapping(value = "/getwords")
    public @ResponseBody
    ResultUtil getWarningWordsId(@RequestParam(value = "project_id", required = false) String project_id) {

        boolean wordFlag = systemService.getWarningWordById(Long.valueOf(project_id));  // 词为空
        if (wordFlag) {
            return ResultUtil.build(500, "预警词为空不能打开预警开关！");
        }
        return ResultUtil.build(200, "");
    }

    /**
     * @param [mv]
     * @return org.springframework.web.servlet.ModelAndView
     * @description: 跳转预警消息列表页面 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 15:44 <br>
     * @author: huajiancheng <br>
     */
    @SystemControllerLog(module = "系统设置", submodule = "系统设置-预警设置-消息列表", type = "查询", operation = "warningmsg")
    @GetMapping(value = "/warningmsg")
    public ModelAndView warningMessage(HttpServletRequest request, ModelAndView mv) {
        String groupId = request.getParameter("groupid");
        String projectId = request.getParameter("projectid");
        mv.addObject("groupid", groupId);
        mv.addObject("projectid", projectId);
        mv.addObject("settingLeft", "warningmsg");
        mv.setViewName("setting/warningMessage");
        return mv;
    }


    /**
     * @param [mv]
     * @return org.springframework.web.servlet.ModelAndView
     * @description: 跳转预警修改 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 15:44 <br>
     * @author: huajiancheng <br>
     */
    @SystemControllerLog(module = "系统设置", submodule = "系统设置-预警设置-配置列表", type = "修改", operation = "warningedit")
    @GetMapping(value = "/warningedit")
    public ModelAndView warningedit(HttpServletRequest request, ModelAndView mv) {
        String groupid = request.getParameter("groupid");
        String page = request.getParameter("page");
        String project_id = request.getParameter("project_id");

        Map<String, Object> projectMap = new HashMap<String, Object>();
        projectMap.put("del_status", 0);
        projectMap.put("project_id", project_id);
        projectMap.put("group_id", groupid);
        Map<String, Object> projectInfo = projectService.getProjectInfoById(projectMap);
        String warningWord = "";
        if (projectInfo != null) {
            String subject_word = String.valueOf(projectInfo.get("subject_word"));
            String subject_words[] = subject_word.split(",");
            for (int i = 0; i < subject_words.length; i++) {
                if (i > 2) {
                    break;
                }
                warningWord += subject_words[i] + ",";
            }

            if (warningWord.endsWith(",")) {
                warningWord = warningWord.substring(0, warningWord.lastIndexOf(","));
            }
        }

        WarningSetting warning = systemService.getWarningByProjectId(Long.valueOf(project_id));
        warning.setWarning_word(warningWord);
        mv.addObject("warning", warning);
        mv.addObject("warningStr", JSON.toJSONString(warning));
        mv.addObject("groupid", groupid);
        mv.addObject("project_id", project_id);
        mv.addObject("page", page);
        mv.addObject("settingLeft", "warning");
        mv.setViewName("setting/warningEdit");
        return mv;
    }

    /**
     * 编辑预警信息
     *
     * @param warningSetting
     * @return
     */
    @SystemControllerLog(module = "系统设置", submodule = "系统设置-预警设置-配置列表", type = "提交修改", operation = "updateWarning")
    @PostMapping("/updateWarning")
    public @ResponseBody
    ResultUtil saveWarningEdit(WarningSetting warningSetting) {
        ResultUtil updateWarning = systemService.updateWarning(warningSetting);
        return updateWarning;
    }

    /**
     * 预警弹框
     */
    //@SystemControllerLog(module = "系统设置", submodule = "系统设置-预警设置-消息列表", type = "窗口查询", operation = "getWarningArticle")
    @PostMapping("/getWarningArticle")
    public @ResponseBody
    ResultUtil getWarningArticle(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                 Integer openFlag, HttpServletRequest request) {
        String project_id = request.getParameter("project_id");
        long userId = userUtil.getUserId(request);
        Map<String, Object> warningArticle = null;
        if (project_id != null && !project_id.equals("")) {
            Long valueOf = Long.valueOf(project_id);
            warningArticle = earlyWarningService.getWarningArticle(pageNum, userId, valueOf, openFlag);
        } else {
            warningArticle = earlyWarningService.getWarningArticle(pageNum, userId, null, openFlag);
        }
        return ResultUtil.build(200, "", warningArticle);
    }


    /**
     * @param [mv]
     * @return org.springframework.web.servlet.ModelAndView
     * @description: 跳转偏好页面 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 15:44 <br>
     * @author: huajiancheng <br>
     */
    @SystemControllerLog(module = "系统设置", submodule = "系统设置-偏好设置", type = "查询", operation = "preference")
    @GetMapping(value = "/preference")
    public ModelAndView preference(ModelAndView mv, HttpServletRequest request) {
        String groupId = request.getParameter("groupid");
        String projectId = request.getParameter("projectid");
        mv.setViewName("setting/preference");
        mv.addObject("groupId", groupId);
        mv.addObject("projectId", projectId);
        mv.addObject("settingLeft", "preference");
        return mv;
    }

    /**
     * 根据方案id获取偏好设置信息
     */
    /*@SystemControllerLog(module = "系统设置", submodule = "系统设置-偏好设置", type = "查询", operation = "getOpinionConditionByProjectId")*/
    @PostMapping(value = "/getOpinionConditionByProjectId")
    @ResponseBody
    public String getOpinionConditionByProjectId(Long projectId) {
        OpinionCondition opinionConditionByProjectId = opinionConditionService.getOpinionConditionByProjectId(projectId);
        return JSON.toJSONString(opinionConditionByProjectId);
    }

    /**
     * 保存偏好设置
     */
    @SystemControllerLog(module = "系统设置", submodule = "系统设置-偏好设置", type = "修改", operation = "updateOpinionCondition")
    @PostMapping(value = "/updateOpinionCondition")
    @ResponseBody
    public String updateOpinionCondition(@RequestBody OpinionCondition opinionCondition) {
        Map<String, Object> updateOpinionCondition = opinionConditionService.updateOpinionCondition(opinionCondition);
        return JSON.toJSONString(updateOpinionCondition);
    }


    /**
     * @param [mv]
     * @return org.springframework.web.servlet.ModelAndView
     * @description: 跳转反馈建议页面 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 15:44 <br>
     * @author: huajiancheng <br>
     */
    @SystemControllerLog(module = "系统设置", submodule = "系统设置-反馈建议", type = "查询", operation = "feedback")
    @GetMapping(value = "/feedback")
    public ModelAndView feedback(ModelAndView mv) {
        mv.setViewName("setting/feedback");
        mv.addObject("settingLeft", "feedback");
        return mv;
    }
    
    /**
     * 
     * @param pageNum
     * @param openFlag
     * @param request
     * @return
     */
    @PostMapping("/getSystemTitle")
    public @ResponseBody
    ResultUtil getSystemTitle(HttpServletRequest request,HttpSession session) {
    	
    	 User user = (User) session.getAttribute("User");
         Map<String, String> userObj = userService.getUserById(user.getUser_id());
        return ResultUtil.build(200, "", userObj);
    }
    
    
    
    
    

}
