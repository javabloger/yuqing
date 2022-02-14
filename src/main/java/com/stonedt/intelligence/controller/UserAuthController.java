package com.stonedt.intelligence.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.constant.WechatConstant;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.UserService;
import com.stonedt.intelligence.util.MyHttpRequestUtil;
import com.stonedt.intelligence.util.WechatUtil;

@RestController
@RequestMapping(value = "/dist")
public class UserAuthController {
	@Autowired
	private UserService userService;
	
	
	@RequestMapping("/getdata")
    public ModelAndView weChat(HttpServletRequest request,HttpSession session,
    		@RequestParam(value = "code", required = false, defaultValue = "") String code,
    		@RequestParam(value = "state", required = false, defaultValue = "") String state) throws IOException {
		/*System.out.println("code:"+code);*/
		
		ModelAndView mv = new ModelAndView();
//		String url = WechatConstant.AUTH_TOKEN + "appid="+WechatConstant.AppID+"&secret="+WechatConstant.AppSecret+"&code="+code+"&grant_type=authorization_code";
//		String access_tokenjsonstr = MyHttpRequestUtil.HttpGet(url);
//		System.out.println("access_tokenjsonstr:"+access_tokenjsonstr);
//		//String access_token = JSONObject.parseObject(access_tokenjsonstr).get("access_token").toString();
//		JSONObject parseObject = JSONObject.parseObject(access_tokenjsonstr);
//		String openid = parseObject.get("openid").toString();
		String openid = "o_IsB0wG396kjZOk6vp9Nncy2p2k";
		User user = userService.selectUserByopenid(openid);
		//根据open获取用户信息
		String userInfo = WechatUtil.getUserInfo(openid);
		JSONObject parseObject2 = JSONObject.parseObject(userInfo);
		//用户关注了微信公众号且绑定了账号
		if(parseObject2.containsKey("nickname")&&user!=null) {
			session.setAttribute("User", user);
			//4.开设了账号，绑定系统的二维码,跳转到数据监测页面
			mv =new ModelAndView(new RedirectView("yqmontitor"));
			return mv;
		}else {
			Map<String,Object> map = userService.selectUserApplyByopenid(openid);
			if(map!=null) {
				
				mv.setViewName("userapply/success");
			}else {
				mv.addObject("openid", openid);
			    mv.setViewName("userapply/apply");
			}
		}
		//1.用户无账号，关注微信公众号
		//2.用户存在账号，未关注微信公众号
		//3.用户有账号，已关注了微信公众号，未绑定
		//关注了微信，但是未开通账号
//		if(!parseObject2.containsKey("nickname")&&user==null) {
//			//跳转到绑定号码提交页面
//			mv.addObject("openid", openid);
//		    mv.setViewName("userapply/apply");
//		}
		return mv;
    }

	/**
	 * 手机端跳转到数据监测页面
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/monitor")
    public ModelAndView view(HttpServletRequest request) throws IOException {
		String authUrl = WechatUtil.authUrl("http://app.stonedt.com/dist/getdata");
		System.out.println(authUrl);
		return new ModelAndView(new RedirectView(authUrl));
    }
	    
	@RequestMapping("/yqmontitor")
    public ModelAndView yqmontitor(HttpServletRequest request) throws IOException {
		ModelAndView mv =  new ModelAndView();
		mv.setViewName("monitor/monitor");
		return mv;
    }
	  

	/**
	 * 跳转到申请页面
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/apply")
    public ModelAndView yqapply(HttpServletRequest request) throws IOException {
		
		String authUrl = WechatUtil.authUrl("http://app.stonedt.com/dist/yqapply");
		System.out.println(authUrl);
		return new ModelAndView(new RedirectView(authUrl));
    }
	
	    
	
	@RequestMapping("/yqapply")
    public ModelAndView yqapply(HttpServletRequest request,HttpSession session,
    		@RequestParam(value = "code", required = false, defaultValue = "") String code,
    		@RequestParam(value = "state", required = false, defaultValue = "") String state) throws IOException {
		ModelAndView mv = new ModelAndView();
		String url = WechatConstant.AUTH_TOKEN + "appid="+WechatConstant.AppID+"&secret="+WechatConstant.AppSecret+"&code="+code+"&grant_type=authorization_code";
		String access_tokenjsonstr = MyHttpRequestUtil.HttpGet(url);
		System.out.println("access_tokenjsonstr:"+access_tokenjsonstr);
		String access_token = JSONObject.parseObject(access_tokenjsonstr).get("access_token").toString();
		JSONObject parseObject = JSONObject.parseObject(access_tokenjsonstr);
		String openid = parseObject.get("openid").toString();
		//String openid = "o_IsB0wG396kjZOk6vp9Nncy2p2k";
		User user = userService.selectUserByopenid(openid);
		//根据open获取用户信息
		String userInfo = WechatUtil.getUserInfo(openid);
		JSONObject parseObject2 = JSONObject.parseObject(userInfo);
		//用户关注了微信公众号且绑定了账号
		if(parseObject2.containsKey("nickname")&&user!=null) {
			//审核通过，提醒用户，是否跳转到舆情监测列表页面
			
			//4.开设了账号，绑定系统的二维码,跳转到数据监测页面
			mv =new ModelAndView(new RedirectView("yqmontitor"));
			return mv;
		}
		Map<String,Object> map = userService.selectUserApplyByopenid(openid);
		if(map!=null) {
			
			mv.setViewName("userapply/success");
		}else {
			mv.addObject("openid", openid);
		    mv.setViewName("userapply/apply");
		}
		
		//1.用户无账号，关注微信公众号
		//2.用户存在账号，未关注微信公众号
		//3.用户有账号，已关注了微信公众号，未绑定
		//关注了微信，但是未开通账号
//		if(parseObject2.containsKey("nickname")&&user==null) {
//			//跳转到绑定号码提交页面
//			mv.addObject("openid", openid);
//		    mv.setViewName("userapply/apply");
//		}
		return mv;
		
		
    }
	
	
	
	
	@RequestMapping("/applydatainfo")
	@ResponseBody
	public String applydatainfo(HttpServletRequest request,@RequestParam(value = "openid", required = false, defaultValue = "")String openid
			,@RequestParam(value = "name", required = false, defaultValue = "")String name
			,@RequestParam(value = "telephone", required = false, defaultValue = "")String telephone
			,@RequestParam(value = "industry", required = false, defaultValue = "")String industry
			,@RequestParam(value = "company", required = false, defaultValue = "")String company) throws IOException {
		String code = "{\"code\":200}";
		try {
			openid = openid.substring(1, openid.length()-1);
			userService.addapply(openid,name,telephone,industry,company);
		} catch (Exception e) {
			code = "{\"code\":500}";
		}
		return code;
	}
	
	/**
	 * 跳转到热点数据页面
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/hotdata")
    public ModelAndView hotdata(HttpServletRequest request) throws IOException {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("hot/hotpage");
		return mv;
    }
	
	

}
