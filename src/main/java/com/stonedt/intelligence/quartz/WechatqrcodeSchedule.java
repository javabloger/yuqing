package com.stonedt.intelligence.quartz;

import java.io.IOException;
import java.util.List;

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
 * 微信定时生成二维码
 */
@SuppressWarnings({"deprecation", "unused"})
@Component
public class WechatqrcodeSchedule {

    // 定时任务开关
    @Value("${schedule.wechatqrcode.open}")
    private Integer schedule_wechatqrcode_open;
    @Autowired
    private UserDao userDao;
    /**
     * 生成二维码
     * @throws IOException 
     * @throws ParseException 
     */
    @Scheduled(cron = "0 0 23 * * ?")
    public void popularInformation() throws ParseException, IOException {
    	if(schedule_wechatqrcode_open==1) {
    		String accesstokenresult = WechatConstant.api_wechat_template;
    		String accesstoken = MyHttpRequestUtil.HttpGet(accesstokenresult);
    		List<User> list = userDao.getAllUser();
    		for (User user : list) {
    			String telephone = user.getTelephone();
					try {
						JSONObject accesstokenjson = JSONObject.parseObject(accesstoken);
	    				if(accesstokenjson.containsKey("access_token")) 
	    				{
	    					String access_tokenstr = accesstokenjson.get("access_token").toString();
	    					//生成临时二维码
	    					
	    					String url = WechatConstant.api_wechat_temporaryqrcode+"?access_token="+access_tokenstr;
	    					getqrcode(telephone,url);
	    					
	    					
	    					
	    				}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				
    			
    			
    			
    			
			}
    		
    		
    		
    		
    		
    		
    		
    		
    		
    	}
    	
    	
    	
    	
    	
    	
    	
    	
       
    }
    private void getqrcode(String telephone, String url) {
    	JSONObject datajson = new JSONObject();
    	datajson.put("expire_seconds", 604800);
    	datajson.put("action_name", "QR_STR_SCENE");
    	JSONObject scenestrjson = new JSONObject();
    	scenestrjson.put("scene_str", telephone);
    	JSONObject scenejson = new JSONObject();
    	scenejson.put("scene", scenestrjson);
    	datajson.put("action_info", scenejson);
    	
    	
		try {
			String sendPostRaw = "";
			sendPostRaw = MyHttpRequestUtil.sendPostRaw(url,datajson.toJSONString(),"utf-8");
			
			JSONObject parseObject = JSONObject.parseObject(sendPostRaw);
			
			String ticket = parseObject.get("ticket").toString();
			ticket = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+ticket;
			userDao.addticket(telephone,ticket);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
	}
	
    	
    	
    
    
    public static String sendPostRaw(String url, String params, String encoding) throws IOException {
        String body = "";
        try {
            //创建httpclient对象
            CloseableHttpClient client = HttpClients.createDefault();
            //创建post方式请求对象
            HttpPost httpPost = new HttpPost(url);

            //装填参数
            StringEntity s = new StringEntity(params, "utf-8");
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
            //设置参数到请求对象中
            httpPost.setEntity(s);
            System.out.println("请求地址：" + url);
//          System.out.println("请求参数："+nvps.toString());

            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
//           httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");

            //执行请求操作，并拿到结果（同步阻塞）
            CloseableHttpResponse response = client.execute(httpPost);
            //获取结果实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //按指定编码转换结果实体为String类型
                body = EntityUtils.toString(entity, encoding);
            }
            EntityUtils.consume(entity);
            //释放链接
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }
    
    
    
    
    
    
    

   

}
