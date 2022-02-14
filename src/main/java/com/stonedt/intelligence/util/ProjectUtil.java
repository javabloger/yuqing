package com.stonedt.intelligence.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.stonedt.intelligence.entity.Project;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.ProjectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description: 方案相关的工具类 <br>
 * date: 2020/4/14 15:17 <br>
 * author: huajiancheng <br>
 * version: 1.0 <br>
 */
@Component
public class ProjectUtil {

    @Autowired
    UserUtil userUtil;
    @Autowired
    ProjectService projectService;

    /**
     * @param [request]
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @description: 获取方案组 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/14 15:41 <br>
     * @author: huajiancheng <br>
     */

    public List<Map<String, Object>> getGroupInfoByUserId(HttpServletRequest request) {
        User user = userUtil.getuser(request);
        Long userId = user.getUser_id();
        List<Map<String, Object>> groupList = projectService.getGroupInfoByUserId(userId);
        return groupList;
    }

    /**
     * @param request,
     * @param projectsearch 搜索的方案关键词
     * @param group_id      方案组id
     * @param page          第几页
     * @param pageSize      每页条数
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @description: 根据方案id和用户id, 搜索的关键词来获取方案列表 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/14 16:00 <br>
     * @author: huajiancheng <br>
     */

    public JSONObject getProjectInfoByGroupIdAndUserId(HttpServletRequest request,
                                                       String projectsearch, Long group_id,
                                                       Integer page, Integer pageSize) {
        JSONObject response = new JSONObject();
        List<Map<String, Object>> projectList = new ArrayList<Map<String, Object>>();
        try {
            User user = userUtil.getuser(request);
            Long user_id = user.getUser_id();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("project_name", projectsearch);
            paramMap.put("del_status", 0);
            paramMap.put("group_id", group_id);
            paramMap.put("user_id", user_id);

            PageHelper.startPage(page, pageSize);
            projectList = projectService.getProjectInfoByGroupIdAndUserId(paramMap);
            PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(projectList);
            Integer totalPage = pageInfo.getPages();
            Long totalData = pageInfo.getTotal();
            response.put("totalData", totalData);
            response.put("totalPage", totalPage);
            response.put("page", page);
            response.put("data", projectList);
            response.put("code", 200);
        } catch (Exception e) {
            response.put("totalData", 0);
            response.put("totalPage", 1);
            response.put("page", 1);
            response.put("data", projectList);
            response.put("code", 500);
            e.printStackTrace();
        }

        return response;
    }

    /**
     * @param [request]
     * @return 方案组和方案数据
     * @description: 首页获取所有的方案组和方案列表 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/14 16:22 <br>
     * @author: huajiancheng <br>
     */

    public JSONObject getProjectAndGroupInfoByUserId(HttpServletRequest request) {
        JSONObject response = new JSONObject();
        try {
            boolean projectFlag = false;
            User user = userUtil.getuser(request);
            Long user_id = user.getUser_id();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("user_id", user_id);
            paramMap.put("group_id", "");
            List<Map<String, Object>> list = projectService.getProjectInfoByGroupIdAndUserId(paramMap);
            List<Map<String, Object>> groupList = projectService.getGroupInfoByUserId(user_id);

            if (list.size() <= 0) {
                projectFlag = true;
            }
            JSONArray responseArray = new JSONArray();
            if (groupList.size() > 0) {
                for (int i = 0; i < groupList.size(); i++) {
                    Map<String, Object> groupMap = groupList.get(i);
                    String group_id = String.valueOf(groupMap.get("group_id"));
                    String group_name = String.valueOf(groupMap.get("group_name"));
                    JSONArray groupArray = new JSONArray();
                    JSONObject groupJson = new JSONObject();
                    for (int j = 0; j < list.size(); j++) {
                        Map<String, Object> listMap = list.get(j);
                        String group_id_list = String.valueOf(listMap.get("group_id"));
                        if (group_id.equals(group_id_list)) {
                            groupArray.add(listMap);
                        }
                    }

                    String key = group_id + "-" + group_name;
                    groupJson.put(key, groupArray);
                    responseArray.add(groupJson);
                }
                response.put("code", 200);
                response.put("msg", "用户方案和方案组返回成功");
                response.put("data", responseArray);
                response.put("flag", projectFlag);
            } else {
                projectFlag = true;
                response.put("code", 200);
                response.put("msg", "用户暂无方案和方案组");
                response.put("data", responseArray);
                response.put("flag", projectFlag);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("msg", "获取用户方案和方案组失败");
            response.put("data", new JSONArray());
        }
        return response;
    }

    /**
     * 获取所有用户的所有方案信息
     */
    public List<Project> listAllProject() {
        try {
            List<Project> listProject = projectService.listProjects();
            return listProject;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Project>();
        }
    }

    /**
     * @param [keyword]
     * @return java.lang.String
     * @description: 处理词 <br>
     * @version: 1.0 <br>
     * @date: 2020/5/6 19:38 <br>
     * @author: huajiancheng <br>
     */

    public static String dealProjectWords(String keyword) {
        String response = "";
        try {
            if (keyword != null) {
                if (keyword.contains("，")) {
                    keyword = keyword.replaceAll("，", ",");
                }
                String keywords[] = keyword.split(",");
                List<String> keywordList = new ArrayList<String>();
                for (int i = 0; i < keywords.length; i++) {
                    String currentKeyword = keywords[i];

                    if (currentKeyword.contains("\n") || currentKeyword.contains("\t") || currentKeyword.contains("\r")) {
                        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                        Matcher m = p.matcher(currentKeyword);
                        currentKeyword = m.replaceAll("");
                    }

                    if (!currentKeyword.equals("") && currentKeyword != "") {
                        keywordList.add(currentKeyword);
                    }
                }
                response = StringUtils.join(keywordList, ",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
