package com.stonedt.intelligence.util;

import com.stonedt.intelligence.entity.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * description 用户操作工具 <br>
 * date: 2020/4/14 15:01 <br>
 * author: huajiancheng <br>
 * version: 1.0 <br>
 */
@Component
public class UserUtil {
	
    public User getuser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("User");
        return user;
    }
    
    public long getUserId(HttpServletRequest request) {
    	User user = (User) request.getSession().getAttribute("User");
    	return user.getUser_id();
	}
}
