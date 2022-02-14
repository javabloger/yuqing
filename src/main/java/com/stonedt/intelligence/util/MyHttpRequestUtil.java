package com.stonedt.intelligence.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MyHttpRequestUtil {
	
	public static void main(String[] args) {
		String sendPostEsSearch = sendPostEsSearch("http://dx2.stonedt.com:7121/yqsearch/countwebsitenum","&?times=2021-03-24 14:06:20&timee=2021-04-08 14:06:20&projecttype=1&website_id=1378");
		
		System.out.println(sendPostEsSearch);
		
	}
	

	/**
	 * 发送post请求
	 */
	public static String sendPostEsSearch(String url, String params) {
		System.err.println(url + "?" + params);
		try {
			PrintWriter out = null;
			BufferedReader in = null;
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print(params);
			out.flush();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * @description: 发送卡夫卡请求 <br>
	 * @version: 1.0 <br>
	 * @date: 2020/5/6 16:49 <br>
	 * @param topicName 队列名称
	 * @param message   发送的数据
	 * @param kafkaUrl  卡夫卡地址
	 * @return java.lang.String
	 */

	public static String doPostKafka(String topicName, String message, String kafkaUrl) {
		String resultJson = "";
		// 1、创建一个httpClient客户端对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			// 2、创建一个HttpPost请求
			HttpPost httpPost = new HttpPost(kafkaUrl);
			// 设置请求头
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded"); // 设置传输的数据格式
			// 携带普通的参数params的方式
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("topicName", topicName));
			params.add(new BasicNameValuePair("message", message));
			// 放参数进post请求里面 从名字可以知道 这个类是专门处理x-www-form-urlencoded 添加参数的
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			// 7、执行post请求操作，并拿到结果
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			try {
				// 获取结果实体
				HttpEntity entity = httpResponse.getEntity();
				if (entity != null) {
					// 进行输出操作 这里就简单的使用EntityUtils工具类的toString()方法
					resultJson = EntityUtils.toString(entity, "UTF-8");
				} else
					EntityUtils.consume(entity);
			} finally {
				httpResponse.close();
			}
			// 最后释放资源之类的
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultJson;
	}

	public static String HttpGet(String url) throws ParseException, IOException {

		CloseableHttpClient httpclient = HttpClients.createDefault();

		String string = null;
		HttpGet httpget = new HttpGet(url);
		RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).build();

		httpget.setConfig(config);
		httpget.setHeader("user-agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");

		System.out.println("executing request " + httpget.getURI());
		CloseableHttpResponse response = httpclient.execute(httpget);
		// 获取响应实体
		HttpEntity entity = response.getEntity();
		System.out.println("--------------------------------------");
		// 打印响应状态
		System.out.println(response.getStatusLine());
		if (entity != null) {
			// 打印响应内容长度
			System.out.println("Response content length: " + entity.getContentLength());
			// 打印响应内容
			string = EntityUtils.toString(entity);
		}
		response.close();
		return string;
	}

	public static String sendPostRaw(String url, String params, String encoding) throws IOException {
		String body = "";
		try {
			// 创建httpclient对象
			CloseableHttpClient client = HttpClients.createDefault();
			// 创建post方式请求对象
			HttpPost httpPost = new HttpPost(url);

			// 装填参数
			StringEntity s = new StringEntity(params, "utf-8");
			s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
			// 设置参数到请求对象中
			httpPost.setEntity(s);
			System.out.println("请求地址：" + url);
//              System.out.println("请求参数："+nvps.toString());

			// 设置header信息
			// 指定报文头【Content-type】、【User-Agent】
//               httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
			httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
			httpPost.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");

			// 执行请求操作，并拿到结果（同步阻塞）
			CloseableHttpResponse response = client.execute(httpPost);
			// 获取结果实体
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// 按指定编码转换结果实体为String类型
				body = EntityUtils.toString(entity, encoding);
			}
			EntityUtils.consume(entity);
			// 释放链接
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body;
	}

	public static String getJsonHtml(String url, JSONObject jsonObject) throws Exception {
		System.err.println(url + jsonObject.toString());
		String result = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		StringEntity postingString = new StringEntity(jsonObject.toString());// json传递
		post.setEntity(postingString);
		post.setHeader("Referer",
				"http://hotel.qunar.com/cn/shanghai_city/?fromDate=2020-02-13&toDate=2020-02-14&cityName=%E4%B8%8A%E6%B5%B7");
		post.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(post);
		String content = EntityUtils.toString(response.getEntity());
		result = content;
		return result;
	}
//	public static void main(String[] args) {
//		for (int i = 0; i < 10000; i++) {
//			MyHttpRequestUtil.doPostKafka("ikHotWords", "习近平李克强", "http://dx2.stonedt.com:7189/stonedt-etl/setKafkaMsg");
//		}
//		 
//	}
	
	

}
