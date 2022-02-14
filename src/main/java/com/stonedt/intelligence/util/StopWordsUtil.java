package com.stonedt.intelligence.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.constant.ReportConstant;

public class StopWordsUtil {

//	public static void main(String[] args) {
//
//		//boolean lockWordsMonster = isLockWordsMonster("分别为121");
//		//System.out.println(lockWordsMonster);
//
//		testmain();
//	}

	public static void testmain() {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		JSONObject resultjson = new JSONObject();

		String times = "2021-04-13 00:00:00";
		String timee = "2021-04-14 00:00:00";
		//String highKeyword = "(诚迈科技 OR 第九公司 OR 微软 OR 腾讯控股 OR 宁德时代 OR 三安电光 OR 工商银行 OR 怪兽充电 OR 沙钢 OR 中国电信 OR 中国移动 OR 建设银行 OR 联合丽格 OR 苏州贝康医疗器械有限公司 OR 北京云鸟科技有限公司 OR 北京易泊安科技有限公司 OR 北京伍方兴业科技有限公司)";
		//String highKeyword = "(北京 OR 南京 OR 上海)";
		String highKeyword = "(制药 OR 临床试验 OR 药物 OR 药厂 OR 医药公司 OR 医药集团 OR 药品 OR 药效 OR 药业集团 OR 药品监督 OR 制药行业 OR 制药厂 OR 药剂 OR 药膏 OR 药价 OR 药监局 OR 医药股份 OR 医药有限公司 OR 医药控股 OR 药房 OR 药店 OR 注射剂 OR 西药 OR 医药 OR 国药 OR 国家药品 OR 药品流通 OR 药品监督管理局 OR 临床药学 OR 药学科学 OR 药学 OR 制剂 OR 特效药 OR 万灵药 OR 靶向药 OR 抗癌药 OR 避孕药 OR 胶囊 OR 口服液 OR 药丸 OR 处方药 OR 麻醉剂 OR 感冒药 OR 医药机构 OR 药津贴 OR 生物药 OR 抗生素 OR 创新药 OR 停药 OR 药师 OR 药业 OR 鼻炎药 OR 试剂 OR 消炎药 OR 药学 OR 用药 OR FDA OR 新药 OR 靶向 OR 药监机构 OR cde OR pmda OR 输液 OR 药品质量 OR 药历 OR 口服药 OR 中成药 OR 生物制剂 OR 麻醉药 OR 精神药 OR 毒性药 OR 特殊药 OR 药品生产 OR 医药贸易 OR 连锁药店 OR 药性 OR 静脉输液 OR 中药 OR 药草 OR 药方 OR 药材 OR 中药文化 OR 药源基地 OR 药材基地 OR 药材品种 OR 入药 OR 中药行业)";
		String stopword = "";
		Integer projectType = 2;
		int pagesize = 500;

		while (true) {
			String url = "http://192.168.71.81:8123" + ReportConstant.es_api_search_list;
			try {
				// 全部情感
				String params = "times=" + times + "&timee=" + timee + "&keyword=" + highKeyword + "&stopword="
						+ stopword + "&emotionalIndex=" + "&projecttype=" + projectType + "&searchType=1&size="+pagesize;
				System.out.println("params:"+params);
				String sendPostEsSearch = MyHttpRequestUtil.sendPostEsSearch(url, params);
				JSONArray jsonArray = JSONObject.parseObject(sendPostEsSearch).getJSONArray("data");

				for (int i = 0; i < jsonArray.size(); i++) {

					JSONObject contentObject = JSONObject.parseObject(jsonArray.get(i).toString());
					JSONObject jsonsourceObject = contentObject.getJSONObject("_source");
					// 标题
					String title = jsonsourceObject.getString("title");

					// 正文
					String content = jsonsourceObject.getString("content");

					String result = HttpPost("http://s1.stonedt.com:8499/v1/seg", title + content);

					JSONObject jsonObject = JSONObject.parseObject(result).getJSONObject("data");

					for (Map.Entry entry : jsonObject.entrySet()) {
						String key = entry.getKey().toString();

						if (key.length() > 2) {
							String value = entry.getValue().toString();
							JSONObject parseObject = JSONObject.parseObject(value);
							Integer num = Integer.parseInt(parseObject.getString("num"));
							// System.out.println("num:"+num);
							String chara = parseObject.getString("chara");
							if (chara != null && !"".equals(chara)) {
								//System.out.print("key:" + key + "--------------------");
								//System.out.println("chara:" + chara);
								boolean lockNessMonster = isLockNessMonster(chara);
								boolean lockWordsMonster = isLockWordsMonster(key);
								if (lockNessMonster == true&&lockWordsMonster==false) {
									if (resultjson.containsKey(key)) {
										resultjson.put(key, (Integer.parseInt(resultjson.get(key).toString()) + num));
									} else {
										resultjson.put(key, num);
									}
								}
							}
						}
					}
				}
				if (jsonArray.size() == pagesize) {
					JSONObject contentObject = JSONObject.parseObject(jsonArray.get(pagesize-1).toString());
					JSONObject jsonsourceObject = contentObject.getJSONObject("_source");
					timee = jsonsourceObject.getString("publish_time").toString();
					if(timee.equals(times)) {
						break;
					}
					
					
				} else {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (Map.Entry entry : resultjson.entrySet()) {
			Map<String, Object> map = new HashMap<String, Object>();
			String key = entry.getKey().toString();
			map.put("key", key);
			String value = entry.getValue().toString();
			map.put("value", value);
			list.add(map);
		}
		list = list.stream().sorted(
				(i, j) -> Integer.parseInt(j.get("value").toString()) - Integer.parseInt(i.get("value").toString()))
				.limit(500).collect(Collectors.toList());
		System.out.println(list);

	}

	// post字符串请求
	public static String HttpPost(String url, String rawBody) {

		HttpURLConnection conn = null;
		PrintWriter pw = null;
		BufferedReader rd = null;
		StringBuilder sb = new StringBuilder();
		String line = null;
		String response = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(20000);
			conn.setConnectTimeout(20000);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.connect();
			pw = new PrintWriter(conn.getOutputStream());
			pw.print(rawBody);
			pw.flush();
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			response = sb.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pw != null) {
					pw.close();
				}
				if (rd != null) {
					rd.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static boolean isLockNessMonster(String s) {
		return Pattern.matches("(n|s|f|NT|ORG|gi|vn|j).*", s);
	}
	
	public static boolean isLockWordsMonster(String words) {
		return Pattern.matches("(南京|北京).*", words);
	}
	
	
	
	

	public static void main(String[] args) {
		boolean lockNessMonster = isLockWordsMonster("1南京阿撒撒是撒");
		System.out.println("lockNessMonster:" + lockNessMonster);
	}

}
