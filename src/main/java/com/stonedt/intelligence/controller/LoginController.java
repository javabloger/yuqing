package com.stonedt.intelligence.controller;

import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.aop.SystemControllerLog;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.UserService;
import com.stonedt.intelligence.util.Base64;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.MD5Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * description: 登录控制器 <br>
 * date: 2020/4/13 10:51 <br>
 * author: xiaomi <br>
 * version: 1.0 <br>
 */
@Controller
@RequestMapping(value = "/")
public class LoginController {

    @Autowired
    UserService userService;

    /**
     * description: 登录页面跳转<br>
     * version: 1.0 <br>
     * date: 2020/4/13 11:06 <br>
     * author: objcat <br>
     * <p>
     * No such property: code for class: Script1
     *
     * @return ModelAndView
     */
  //  @SystemControllerLog(module = "用户登录",submodule="用户登录", type = "查询",operation = "login")
    @GetMapping(value = "/login")
    public ModelAndView login(ModelAndView mv) {
        mv.setViewName("user/login");
        return mv;
    }

    /**
     * description: 退出 <br>
     * version: 1.0 <br>
     * date: 2020/4/13 11:08 <br>
     * author: objcat <br>
     *
     * @return ModelAndView
     */
    @SystemControllerLog(module = "用户登出", submodule = "用户登出", type = "登出", operation = "logout")
    @GetMapping(value = "/logout")
    public void logout(HttpServletResponse response, HttpServletRequest request) {
        try {
            try {
                User user = (User)request.getSession().getAttribute("User");
                try {
                    //如果Vector中有用户==》移除==》记录==>这样如果切换到别的浏览器同一账号登录且之前账号没有退出就不准确了
                    //如果Vector中没用户==》不记录
                        userService.updateEndLoginTime(user.getUser_id());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            request.getSession().removeAttribute("User");
            response.sendRedirect(request.getContextPath() + "user/login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * description: 登录处理<br>
     * version: 1.0 <br>
     * date: 2020/4/13 11:09 <br>
     * author: objcat <br>
     *
     * @param telephone(登录手机号)、password(密码)
     * @return ModelAndView
     */
    @SystemControllerLog(module = "用户登录", submodule = "用户登录", type = "登录", operation = "login")
    @PostMapping(value = "/login")
    @ResponseBody
    public JSONObject login(@RequestParam(value = "telephone") String telephone,
                            @RequestParam(value = "password") String password,
                            HttpSession session) {
        JSONObject response = new JSONObject();
        User user = userService.selectUserByTelephone(telephone);
        if (null != user) {
            if (user.getStatus() == 0) {
                response.put("code", 3);
                response.put("msg", "用户禁止登录");
            } else {
                if (MD5Util.getMD5(password).equals(user.getPassword())) {
                    Integer status = user.getStatus();
                    if (status == 2) {
                        response.put("code", 4);
                        response.put("msg", "账户已被注销");
                    } else {
                        session.setAttribute("User", user);
                        response.put("code", 1);
                        response.put("msg", "用户登录成功");
                        Integer login_count = user.getLogin_count() + 1;
                        String end_login_time = DateUtil.getNowTime();
                        Map<String, Object> paramMap = new HashMap<String, Object>();
                        paramMap.put("telephone", telephone);
                        paramMap.put("end_login_time", end_login_time);
                        paramMap.put("login_count", login_count);
                        userService.updateUserLoginCountByPhone(paramMap);
                    }
                } else {
                    response.put("code", 2);
                    response.put("msg", "登录密码错误");
                }
            }
        } else {
            response.put("code", -1);
            response.put("msg", "用户不存在");
        }
        return response;
    }


    /**
     * description: 跳转忘记密码页面 <br>
     * version: 1.0 <br>
     * date: 2020/4/13 11:08 <br>
     * author: objcat <br>
     *
     * @return ModelAndView
     */
    @SystemControllerLog(module = "用户登录", submodule = "用户登录-忘记密码", type = "忘记密码", operation = "forgotpwd")
    @GetMapping(value = "/forgotpwd")
    public ModelAndView forgotpwd(ModelAndView mv) {
        mv.setViewName("user/forgotPassword");
        return mv;
    }

    @SystemControllerLog(module = "用户登录", submodule = "用户登录", type = "查询", operation = "jumpLogin")
    @GetMapping(value = "/jumpLogin")
    public String login(String b64, HttpSession session) {
        System.err.println("=====b64-encode:" + b64 + "====================================================");
        b64 = Base64.decode(b64);
        System.err.println("=====b64-decode:" + b64 + "====================================================");
        b64 = b64.substring(4, 15);
        System.err.println("=====phone:" + b64 + "========================================================");
        User user = userService.selectUserByTelephone(b64);
        if (null != user) {
            if (user.getStatus() == 0) {
                return "user/login";
            } else {
                Integer status = user.getStatus();
                if (status == 2) {
                    return "user/login";
                } else {
                    session.setAttribute("User", user);
                    Integer login_count = user.getLogin_count() + 1;
                    String end_login_time = DateUtil.getNowTime();
                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    paramMap.put("telephone", b64);
                    paramMap.put("end_login_time", end_login_time);
                    paramMap.put("login_count", login_count);
                    userService.updateUserLoginCountByPhone(paramMap);
                }
            }
        } else {
            return "user/login";
        }
        return "redirect:/monitor";
    }

    public static void main(String[] args) {
        System.err.println(Base64.encode("$$$#13813866138===1553241639885#$$$"));
    }

    /**
     * description: 登录处理<br>
     * version: 1.0 <br>
     * date: 2020/4/13 11:09 <br>
     * author: objcat <br>
     *
     * @param
     * @return ModelAndView
     */
//    @SystemControllerLog(module = "统计用户在线数量", submodule = "用户在线数量统计", type = "查询", operation = "onlinestatistical")
    @PostMapping(value = "/onlinestatistical")
    @ResponseBody
    public JSONObject onlinestatistical( HttpSession session) {
        JSONObject response = new JSONObject();
        try {
            User user = (User)session.getAttribute("User");
            Map<String, Object>result = userService.onlinestatistical(user);
            response.put("onlinedata", result);
            response.put("code",1);
            return response;
        } catch (Exception e) {
            // TODO: handle exception
            response.put("code",-1 );
            response.put("msg","查询失败");
            return response;
        }
    }

}
