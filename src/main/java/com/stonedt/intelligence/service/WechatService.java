package com.stonedt.intelligence.service;

import com.stonedt.intelligence.entity.User;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface WechatService {

	void dealevent(HttpServletRequest request, HttpServletResponse response);
    
}
