package com.stonedt.intelligence.service.impl;



import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stonedt.intelligence.dao.UserDao;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.WechatService;
import com.stonedt.intelligence.util.CheckoutUtil;
import com.stonedt.intelligence.util.WechatUtil;

@Service
public class WechatServiceImpl implements WechatService {
	
	@Autowired
	private UserDao userDao;

	@Override
	public void dealevent(HttpServletRequest request, HttpServletResponse response) {
		boolean isGet = request.getMethod().toLowerCase().equals("get");
        /*System.out.println("isGet:"+isGet);*/
        if (isGet) {
            // 微信加密签名
            String signature = request.getParameter("signature");
            // 时间戳
            String timestamp = request.getParameter("timestamp");
            // 随机数
            String nonce = request.getParameter("nonce");
            // 随机字符串
            String echostr = request.getParameter("echostr");
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (signature != null && CheckoutUtil.checkSignature(signature, timestamp, nonce)) {
                try {
                    PrintWriter print = response.getWriter();
                    print.write(echostr);
                    print.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
        	 try {
             	Map<String, String> wxdata = WechatUtil.xmlToMap(request);
             	       if(wxdata.get("MsgType")!=null){
             	            if("event".equals(wxdata.get("MsgType"))){
             	            	System.out.println("2.1解析消息内容为：事件推送");
             	                 if("subscribe".equals(wxdata.get("Event"))){
             	                	String EventKey = wxdata.get("EventKey").toString();
             	                	String telephone = EventKey.split("_")[1];//获取手机号码
             	                	String openid = wxdata.get("FromUserName").toString();//获取openid
             	                   User user = userDao.selectUserByTelephone(telephone);
             	                   System.out.println("2.2用户第一次关注 返回true哦");
             	                   if (null != user) {
             	                	   //更新用户绑定微信的状态
             	                	  boolean updateUseropenidById = userDao.updateUseropenidById(user.getUser_id(),openid);
             	                   }
             	                }
             	                 //取消关注
             	                if("unsubscribe".equals(wxdata.get("Event"))){
             	                	System.out.println("用户id:"+wxdata.get("FromUserName"));
             	                	//解绑用户状态
             	                	boolean updateUseropenidById = userDao.updateUseropenidstatusById(wxdata.get("FromUserName"));
            	                	 System.out.println("取消关注");
            	                }
             	              }
             	        }
             	
             } catch (Exception e) {
                 e.printStackTrace();
             }
        }
		
	}

    

}
