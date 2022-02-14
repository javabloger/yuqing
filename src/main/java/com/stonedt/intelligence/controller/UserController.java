package com.stonedt.intelligence.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.stonedt.intelligence.aop.SystemControllerLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.UserService;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.MD5Util;
import com.stonedt.intelligence.util.ResultUtil;
import com.stonedt.intelligence.util.SnowFlake;

import cn.hutool.core.lang.Snowflake;

/**
 * description: UserController <br>
 * date: 2020/4/13 10:52 <br>
 * author: xiaomi <br>
 * version: 1.0 <br>
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    private SnowFlake snowFlake = new SnowFlake();

    /**
     * description: 账号管理跳转,userid为用户的id<br>
     * version: 1.0 <br>
     * date: 2020/4/13 11:19 <br>
     * author: objcat <br>
     *
     * @return
     */
    @SystemControllerLog(module = "系统设置", submodule = "系统设置-账号管理", type = "查询", operation = "")
    @GetMapping(value = "/{userid}")
    public ModelAndView skipUserManage(@PathVariable("userid") String userid, ModelAndView mv) {
        mv.setViewName("setting/userCenter");
        mv.addObject("userid", userid);
        mv.addObject("settingLeft", "user");
        return mv;
    }

    /**
     * description: 查询个人信息<br>
     * version: 1.0 <br>
     * date: 2020/4/13 11:15 <br>
     * author: objcat <br>
     *
     * @return
     */
   /* @SystemControllerLog(module = "系统设置", submodule = "系统设置-账号管理", type = "查询", operation = "detail")*/
    @PostMapping(value = "/detail")
    public @ResponseBody
    ResultUtil detail(HttpSession session) {
        User user = (User) session.getAttribute("User");
        Map<String, String> userObj = userService.getUserById(user.getUser_id());
        return ResultUtil.build(200, "", userObj);
    }


    /**
     * description: 修改个人密码 <br>
     * version: 1.0 <br>
     * date: 2020/4/13 11:25 <br>
     * author: objcat <br>
     *
     * @return
     */
    @SystemControllerLog(module = "系统设置", submodule = "系统设置-账号管理", type = "修改密码", operation = "detail")
    @PostMapping(value = "/edit")
    public @ResponseBody
    ResultUtil editUserInfo(@RequestParam("oldPassword") String oldPassword,
                            @RequestParam("newPassword") String newPassword, HttpSession session) {
        User user = (User) session.getAttribute("User");
        if (MD5Util.getMD5(oldPassword).equals(user.getPassword())) {
            boolean updateUserPwdById = userService.updateUserPwdById(user.getUser_id(), MD5Util.getMD5(newPassword));
            if (updateUserPwdById) {
                return ResultUtil.build(200, "密码修改成功！");
            } else {
                return ResultUtil.build(201, "密码修改失败！");
            }
        } else {
            return ResultUtil.build(203, "旧密码输入错误！");
        }
    }

    /**
     * postman测试添加账号 暂未拦截
     */
    @SystemControllerLog(module = "系统设置", submodule = "系统设置-账号管理", type = "新增", operation = "save")
    @PostMapping(value = "/save")
    @ResponseBody
    public String save(User user) {
        long id = snowFlake.getId();
        user.setCreate_time(DateUtil.nowTime());
        user.setUser_id(id);
//    	user.setTelephone(telephone);
        user.setPassword(MD5Util.getMD5(user.getPassword()));
//    	user.setEmail(email);
        user.setEnd_login_time(DateUtil.nowTime());
        user.setStatus(1);
//    	user.setUsername(username);
//    	user.setWechat_number(wechat_number);
//    	user.setOpenid(openid);
        user.setLogin_count(0);
        user.setIdentity(1);
//    	user.setOrganization_id(organization_id);
        Boolean saveUser = userService.saveUser(user);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("state", saveUser);
        map.put("message", "");
        return JSON.toJSONString(map);
    }
    @SystemControllerLog(module = "获取微信二维码", submodule = "", type = "查询", operation = "")
    @GetMapping(value = "/getwechatqrcode")
    public @ResponseBody
    ResultUtil wechatqrcode(HttpSession session) {
        User user = (User) session.getAttribute("User");
        Map<String, String> userObj = userService.getqrcode(user.getTelephone());
        return ResultUtil.build(200, "", userObj);
    }
    
    
}
