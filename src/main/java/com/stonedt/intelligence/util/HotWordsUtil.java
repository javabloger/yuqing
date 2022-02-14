package com.stonedt.intelligence.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class HotWordsUtil {
	
	public static void main(String[] args) {
		System.out.println(search2());
	}
	
	
	public static String search() {
		//实时热点
		String realtimehotspotsurl = "http://top.baidu.com/buzz?b=1&c=513&fr=topbuzz_b341_c513";
		//今日热点
		String todayhotspotsurl = "http://top.baidu.com/buzz?b=341&c=513&fr=topbuzz_b1_c513";
		//热门搜索
		String hotsearchurl = "http://top.baidu.com/buzz?b=2";
		
		String[] arr ={realtimehotspotsurl,todayhotspotsurl,hotsearchurl};
		Map<String,Object> map =new HashMap<String,Object>();
		for (int m = 0; m < arr.length; m++) {
			String html = get(arr[m], "gb2312");
			JSONArray list= new JSONArray();
			Document parse = Jsoup.parse(html);
			try {
				Elements tobody = parse.select("#main > div.mainBody > div > table > tbody >tr");
				for(int i = 1;i<tobody.size();i++) {
					Elements select = tobody.get(i).select("td");
						String rank = select.select("td.first").text();
						String source_url = select.select("td.keyword > a.list-title").attr("href");
						String topic = select.select("td.keyword > a.list-title").text();
						String original_weight = select.select("td.last >span").text();
						if(StringUtils.isBlank(topic)) {
							continue;
						}
						map.put(topic, original_weight);
						//判断selectOffset是否获取到值了，未获取到值则说明是新的界面，获取到值则说明是老界面
				}
			} catch (Exception e) {
			}
			
		}
		JSONArray list = new JSONArray();
		for(String key:map.keySet()){
			JSONObject js = new JSONObject();
			js.put("x", key);
			js.put("value", map.get(key).toString());
			list.add(js);
		       System.out.println("key:"+key+" "+"Value:"+map.get(key));
		     }
		return list.toJSONString();
	}
	public static String search2() {
		String url = "https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&wd=%E7%83%AD%E7%82%B9";
		String html = HotWordsUtil.get(url, "gb2312");
		Document parse = Jsoup.parse(html);
		Element element = parse.getElementsByClass("FYB_RD").get(0);
		Elements elementsByClass = element.getElementsByClass("toplist1-tr_4kE4D");
	    JSONArray jsonArray = new JSONArray();
		for (Element element2 : elementsByClass) {
			JSONObject jsonObject = new JSONObject();
			Element element3 = element2.getElementsByTag("a").get(0);
			String title = element3.attr("title").toString();
			jsonObject.put("topic", title);
			String href = "https://www.baidu.com"+element3.attr("href").toString();
			String text = element2.getElementsByClass("toplist1-right-num_3FteC").get(0).text();
			int parseInt = Integer.parseInt(text.replaceAll("万", ""));
			jsonObject.put("original_weight", parseInt*10000);
			jsonObject.put("source_url", href);
			jsonObject.put("id", MD5Util.MD5(href));
			jsonObject.put("source_name", "百度风云榜");
			jsonArray.add(jsonObject);
		}
		
		return jsonArray.toJSONString();
		
	}
	
	/**
	 * 默认get请求
	 * @param url
	 * @param entityType
	 * @return
	 */
	public static String get(String url,String entityType) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		System.out.println("get请求开始------");
		String string = null;
		try {
			// 创建httpget.
			HttpGet httpget = new HttpGet(url);
			
	        RequestConfig config = null;
	        config = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).build();
			// 设置httpclient端口IP
			httpget.setConfig(config);
			httpget.setHeader("User-Agent",getRandomAgent());
			System.out.println("executing request " + httpget.getURI());
			// 执行get请求.141.196.71.217:8080
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				// 打印响应状态
				System.out.println("响应状态: " + response.getStatusLine());
				if (entity != null) {
					// 打印响应内容长度
					System.out.println("Response content length:" + entity.getContentLength());
					// 打印响应内容
					string = EntityUtils.toString(entity, entityType);
					
				}
			} finally {
				response.close();
			}
		} catch (HttpHostConnectException e) {
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			System.out.println("get请求结束------");
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return string;
	}
	
	
	/**
	 * 随机一个Agent
	 * @return
	 */
	public static String getRandomAgent()
	{
		List<String> RandomAgent = new ArrayList<String>();
		RandomAgent.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
		RandomAgent.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
		RandomAgent.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
		RandomAgent.add("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
		RandomAgent.add("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.133 Safari/534.16");
		int sign = (int)(0+Math.random()*(5));
		return RandomAgent.get(sign);
	}
	

}
