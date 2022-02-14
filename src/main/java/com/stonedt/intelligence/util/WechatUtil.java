package com.stonedt.intelligence.util;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.ParseException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Constant;
import com.stonedt.intelligence.constant.WechatConstant;


public class WechatUtil {
	 
	public static Map<String, String> xmlToMap(HttpServletRequest request) throws Exception{
		HashMap<String, String> map = new HashMap<String,String>();
		SAXReader reader = new SAXReader();
 
		InputStream ins = request.getInputStream();
		Document doc = reader.read(ins);
 
		Element root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> list = (List<Element>)root.elements();
 
		for(Element e:list){
			map.put(e.getName(), e.getText());
		}
		ins.close();
		return map;
	}

    /**
     * 构建授权跳转URL
     * @param redirectUrl 授权后的跳转URL(我方服务器URL)
     * @param quiet 是否静默: true: 仅获取openId，false: 获取openId和个人信息(需用户手动确认)
     * @return 微信授权跳转URL
     */
    public static String authUrl(String redirectUrl) {
    	String resulturl = null;
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "utf-8");
            
            resulturl = WechatConstant.AUTH_URL +
            "appid=" + WechatConstant.AppID +
            "&redirect_uri=" + redirectUrl + "&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect";
            
        } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
        }
		return resulturl;
    }

	public static String getUserInfo(String openid) throws ParseException, IOException {
		
		String accessToken = getAccessToken();
		
		
		String authInfo = getAuthInfo(accessToken,openid);
		return authInfo;
		
		
	}
	/**
	 * 获取accesstoken
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static String getAccessToken() throws ParseException, IOException {
		
		String accesstokenurl = WechatConstant.api_wechat_template;
		String accesstoken = MyHttpRequestUtil.HttpGet(accesstokenurl);
		String string = JSONObject.parseObject(accesstoken).get("access_token").toString();
		
		return string;
		
	}
	/**
	 * 根据access_token、openid获取用户的基本信息
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static String getAuthInfo(String accessToken,String openid) throws ParseException, IOException {
		
		String url = WechatConstant.AUTH_Basic_Info +"access_token="+accessToken+"&openid="+openid+"&lang=zh_CN";
		String userbasicinfo = MyHttpRequestUtil.HttpGet(url);
		
		return userbasicinfo;
		
	}
	

}
