package com.stonedt.intelligence.controller;

import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.service.WechatService;
import com.stonedt.intelligence.util.MyHttpRequestUtil;
import com.stonedt.intelligence.util.RedisUtil;
import org.springframework.stereotype.Controller;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;


@Controller
@RequestMapping(value = "/hot")
public class HotNewsController {
	
	
	@Autowired
	private WechatService wechatService;
	@Resource
	private RedisUtil redisUtil;
	@RequestMapping("/hotpage")
    public ModelAndView weChat(HttpServletRequest request,
            ModelAndView mv,Integer page,Integer one_type,String two_type) throws IOException {
		mv.addObject("page", page);
		mv.addObject("one_type", one_type);
		mv.addObject("two_type", two_type);
        mv.setViewName("hot/hotpage");
        return mv;
		
    }
	
	
	@RequestMapping("/hotlist")
    public String weChat(HttpServletRequest request,@RequestParam(value = "page", required = false, defaultValue = "1") String page) throws IOException {
		String sendPostEsSearch = "";
		   if(redisUtil.existsKey(page)) {
			   sendPostEsSearch = redisUtil.getKey(page);
			   BASE64Decoder decoder = new BASE64Decoder();
		    	byte[] bytes = decoder.decodeBuffer(sendPostEsSearch);
			   sendPostEsSearch = new String(bytes);
		   }else {
			   String url = "http://192.168.71.63:6304/hotnews/hotnewslist";
			   sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, "?searchparam=rank&searchType=1&source_name=微博&page="+page); 
			   BASE64Encoder encoder = new BASE64Encoder();
				String data = encoder.encode(sendPostEsSearch.getBytes());
			   redisUtil.set(page, data);
			   
		   }
		return sendPostEsSearch;
		
    }
	
	
	

}
