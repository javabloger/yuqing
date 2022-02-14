package com.stonedt.intelligence.quartz;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.MD5Util;
import com.stonedt.intelligence.util.RedisUtil;
import com.stonedt.intelligence.vo.FullSearchParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.constant.WechatConstant;
import com.stonedt.intelligence.dao.UserDao;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.util.MyHttpRequestUtil;

/**
 * 监测分析定时任务
 */
@SuppressWarnings({"deprecation", "unused"})
@Component
public class WechatSchedule {

    // 定时任务开关
    @Value("${schedule.wechat.open}")
    private Integer schedule_wechat_open;
    @Autowired
    private UserDao userDao;
	@Autowired
	private RedisUtil redisUtil;
    /**
     * 模板消息
     */

    @Scheduled(cron = "0 0 18 * * ?")
	//@Scheduled(cron = "0 0/1 * * * ?")
    public void popularInformation() {
    	if(schedule_wechat_open==1) {
    		//获取accesstoken
			System.out.println("开始推送");
    		String accesstokenresult = WechatConstant.api_wechat_template;
    		try {
				String accesstoken = MyHttpRequestUtil.HttpGet(accesstokenresult);
				JSONObject accesstokenjson = JSONObject.parseObject(accesstoken);
				if(accesstokenjson.containsKey("access_token")) {
					String access_tokenstr = accesstokenjson.get("access_token").toString();
					//推送消息
					//List<User> list = userDao.getUserByWechatUser();
					String wechat_user = MyHttpRequestUtil.HttpGet(WechatConstant.api_wechat_user+"access_token="+access_tokenstr);
					JSONArray jsonArray = JSONObject.parseObject(wechat_user).getJSONObject("data").getJSONArray("openid");
					for (Object object : jsonArray) {
						String openid = object.toString();//用户openid
						System.out.println(openid);
						sendtemplateinfo(openid,WechatConstant.api_wechat_template_id,access_tokenstr);
					}
					
					
//					for (User user : list) {
//						String openid = user.getOpenid();//用户openid
//						
//						sendtemplateinfo(openid,WechatConstant.api_wechat_template_id,access_tokenstr);
//					}
				}
				System.out.println("accesstoken:"+accesstoken);
				System.out.println("完成");
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}

    }
    public void sendtemplateinfo(String openid,String template_id,String access_token) {
    	
    	String url = WechatConstant.api_wechat_templatepush+"?access_token="+access_token;
    	
    	try {
			JSONObject datajson = new JSONObject();
			datajson.put("touser", openid);
			datajson.put("template_id", template_id);
			datajson.put("url", "http://app.stonedt.com/hot/hotpage");
			JSONObject templatedata = new JSONObject();

			JSONObject firstdata = new JSONObject();
			firstdata.put("value", DateUtil.getDateday() + "全网热门汇总");
			firstdata.put("color", "#173177");
			templatedata.put("first", firstdata);

			JSONObject reasondata = new JSONObject();
			FullSearchParam searchParam = new FullSearchParam();
			searchParam.setPageNum(1);
			searchParam.setClassify("4");
			String keyString = MD5Util.getMD5( searchParam.getPageNum()+ searchParam.getClassify() + "百度" + "");
			System.err.println("redis key" + keyString);
			String key = redisUtil.getKey(keyString);
			JSONObject jsonObject = JSONObject.parseObject(key);
			JSONArray data = jsonObject.getJSONArray("data");
			StringBuffer dataString = new StringBuffer();
			for (int i = 0; i < 10; i++) {
				JSONObject jsonObject1 = data.getJSONObject(i);
				JSONObject source = jsonObject1.getJSONObject("_source");
				String topic = source.getString("topic");
				dataString.append((i+1)+".");
				dataString.append(topic);
				dataString.append("\n");
			}
			reasondata.put("value", dataString);
			reasondata.put("color", "#173177");
			templatedata.put("keyword2", reasondata);


			JSONObject timedata = new JSONObject();
			timedata.put("value", "查看更多全网热点");
			timedata.put("color", "#173177");
			templatedata.put("keyword3", timedata);

			JSONObject remarkdata = new JSONObject();
			remarkdata.put("value", DateUtil.getDateday());
			remarkdata.put("color", "#173177");
			templatedata.put("keyword1", remarkdata);

			datajson.put("data", templatedata);
			String sendPostRaw = MyHttpRequestUtil.sendPostRaw(url,datajson.toJSONString(),"utf-8");
			System.out.println(sendPostRaw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    
    
    
    
    
    
    
    
    

   

}
