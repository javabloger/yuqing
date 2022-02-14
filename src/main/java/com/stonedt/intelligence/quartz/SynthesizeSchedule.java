package com.stonedt.intelligence.quartz;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.HotWordsUtil;
import com.stonedt.intelligence.util.MD5Util;
import com.stonedt.intelligence.util.RedisUtil;
import com.stonedt.intelligence.vo.FullSearchParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.constant.MonitorConstant;
import com.stonedt.intelligence.constant.WechatConstant;
import com.stonedt.intelligence.dao.ProjectDao;
import com.stonedt.intelligence.dao.ReportCustomDao;
import com.stonedt.intelligence.dao.SynthesizeDao;
import com.stonedt.intelligence.dao.UserDao;
import com.stonedt.intelligence.dao.WarningarticleDao;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.FullSearchService;
import com.stonedt.intelligence.service.MonitorService;
import com.stonedt.intelligence.util.MyHttpRequestUtil;

/**
 * 综合看板定时任务
 */
@Component
public class SynthesizeSchedule {

    // 定时任务开关
    @Value("${schedule.synthesize.open}")
    private Integer schedule_synthesize_open;
    @Value("${es.search.url}")
    private String es_search_url;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ReportCustomDao reportCustomDao;
    @Autowired
    private FullSearchService fullSearchService;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private WarningarticleDao warningarticleDao;
	@Autowired
	private MonitorService monitorService;
	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private SynthesizeDao synthesizeDao;
	
    /**
     * 模板消息
     */

//	@PostConstruct
 //   @Scheduled(cron = "0 30 4 * * ?")
   	//@Scheduled(fixedDelay = 1000*60*2)
 	//
	
	//@Scheduled(cron = "0 0/2 * * * ?")
	@Scheduled(cron = "0 0/30 * * * ?")
    public void popularInformation() {
    	if(schedule_synthesize_open==1) {
    		//获取accesstoken
			System.out.println("开始生成综合看板");
			String hot_all = "";
			String hot_weibo = "";
			String hot_wechat = "";
			String hot_search_terms = "";
			String hot_douyin = "";
			String hot_bilibili = "";
			String hot_tecentvedio = "";
			String hot_policydata = "";
			String hot_finaceData = "";
			String hot_36kr ="";
			try {
				FullSearchParam searchParam = new FullSearchParam();
				searchParam.setPageNum(1);
				searchParam.setPageSize(50);
				searchParam.setSearchWord("");
				searchParam.setClassify("4");
				searchParam.setTimeType(1);
				
				//热点事件
				searchParam.setSource_name("百度风云榜");
				//JSONObject hotList = fullSearchService.hotList(searchParam);
				hot_all = fullSearchService.hotBaiduList();
				
				//热门微博
				searchParam.setSource_name("微博");
				JSONObject hotList2 = fullSearchService.hotList(searchParam);
				hot_weibo =conversionHotList(hotList2);
				//热门微信
				searchParam.setSource_name("微信");
				
				JSONObject hotListWechat = fullSearchService.hotList(searchParam);
				hot_wechat =conversionHotList(hotListWechat);
				searchParam.setPageSize(10);
				searchParam.setClassify("1");
				//热门科技
				searchParam.setSource_name("36kr");
				
				JSONObject hotList36kr = fullSearchService.hotList(searchParam);
				hot_36kr =conversionHotList(hotList36kr);
				
				searchParam.setClassify("2");
				searchParam.setTimeType(2);
				searchParam.setPageSize(50);
				//热门抖音
				searchParam.setSource_name("抖音");
				
				JSONObject hotListDouyin = fullSearchService.hotList(searchParam);
				hot_douyin =conversionHotList(hotListDouyin);
				
				//热门哔哩哔哩
				searchParam.setSource_name("哔哩哔哩");
				
				JSONObject hotListBiLiBiLi = fullSearchService.hotList(searchParam);
				hot_bilibili =conversionHotList(hotListBiLiBiLi);
				
				//热门腾讯视频
				searchParam.setSource_name("腾讯视频");
				
				JSONObject hotListTecentVedio = fullSearchService.hotList(searchParam);
				hot_tecentvedio =conversionHotList(hotListTecentVedio);
				hot_search_terms = HotWordsUtil.search();
				
				//政策--------国务院 > 首页 > 政策 > 最新    http://www.gov.cn/zhengce/zuixin.htm
				
				hot_policydata = getPolicyData();
				
				
				
				
				//经济--------东方财富网(国内经济首页 > 财经频道 > 焦点 > 国内经济) http://finance.eastmoney.com/a/cgnjj.html
				
				hot_finaceData = getFinaceData();
				
				
				
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("hot_all", hot_all);
					map.put("user_id", "1");
					map.put("hot_weibo", hot_weibo);
					map.put("hot_wechat", hot_wechat);
					map.put("hot_douyin", hot_douyin);
					map.put("hot_bilibili", hot_bilibili);
					map.put("hot_tecentvedio", hot_tecentvedio);
					map.put("hot_search_terms", hot_search_terms);
					map.put("hot_policydata", hot_policydata);
					map.put("hot_finaceData", hot_finaceData);
					map.put("hot_36kr", hot_36kr);
					synthesizeDao.insertSynthesize(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}

			
    }
	private String getreprint(User user) {
		String keywords = getkeywords(user,0);
		
		JSONObject timeJson = new JSONObject();
        timeJson = DateUtil.dateRoll(new Date(), Calendar.HOUR, -24);
        String times = timeJson.getString("times");
        String timee = timeJson.getString("timee");
		String params = "matchingmode=0&searchType=4&timeType=1&stopword=&esindex=postal&timee="+timee+"&estype=infor&times="+times+"&emotionalIndex=1,2,3&size=10&page=1&keyword="+keywords;
		String url = es_search_url + MonitorConstant.es_api_search_list;
		String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
		JSONObject parseObject = JSONObject.parseObject(esResponse);
		JSONArray jsonArray = parseObject.getJSONArray("data");
		JSONArray list = new JSONArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("_source");
			JSONObject ob = new JSONObject();
			ob.put("id", jsonObject.get("article_public_id"));
			String source_name = jsonObject.get("source_name").toString();
			if("微博".equals(source_name)){
				ob.put("title", jsonObject.get("extend_string_two"));
			}else{
				ob.put("title", jsonObject.get("title"));
			}
			ob.put("source_url", jsonObject.get("source_url"));
			ob.put("emotionalIndex", jsonObject.get("emotionalIndex"));
			ob.put("publish_time", jsonObject.get("publish_time"));
			ob.put("source_name", source_name);
			ob.put("forwardingvolume", jsonObject.get("forwardingvolume"));
			list.add(ob);
		}
		return list.toJSONString();
	}
	private String getleaders(User user) {
		String keywords = getkeywords(user,1);
		
		JSONObject timeJson = new JSONObject();
        timeJson = DateUtil.dateRoll(new Date(), Calendar.HOUR, -24);
        String times = timeJson.getString("times");
        String timee = timeJson.getString("timee");
		String params = "matchingmode=0&searchType=1&timeType=1&stopword=&esindex=postal&timee="+timee+"&estype=infor&times="+times+"&emotionalIndex=1,2,3&size=5&page=1&keyword="+keywords;
		String url = es_search_url + MonitorConstant.es_api_search_list;
		System.err.println("============leaders_PO=================");
		System.out.println("url:"+url);
		System.out.println("params:"+params);
		System.err.println("=============leaders_PO=================");
		String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
		JSONObject parseObject = JSONObject.parseObject(esResponse);
		JSONArray jsonArray = parseObject.getJSONArray("data");
		JSONArray list = new JSONArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("_source");
			JSONObject ob = new JSONObject();
			ob.put("id", jsonObject.get("article_public_id"));
			ob.put("title", jsonObject.get("title"));
			ob.put("source_url", jsonObject.get("source_url"));
			ob.put("emotionalIndex", jsonObject.get("emotionalIndex"));
			ob.put("publish_time", jsonObject.get("publish_time"));
			ob.put("source_name", jsonObject.get("source_name"));
			list.add(ob);
		}
		return list.toJSONString();
	}
	private String getpush(User user) {
		String keywords = getkeywords(user,0);
		
		JSONObject timeJson = new JSONObject();
        timeJson = DateUtil.dateRoll(new Date(), Calendar.HOUR, -24);
        String times = timeJson.getString("times");
        String timee = timeJson.getString("timee");
		String params = "matchingmode=0&searchType=1&timeType=1&stopword=&esindex=postal&timee="+timee+"&estype=infor&times="+times+"&emotionalIndex=1,2,3&size=10&page=1&keyword="+keywords;
		String url = es_search_url + MonitorConstant.es_api_search_list;
//		System.err.println("============push_PO all=================");
//		System.out.println("url:"+url);
//		System.out.println("params:"+params);
//		System.err.println("=============push_PO all=================");
		String esResponse = "";
		int i = 0;
		while(i<3){
			i++;
			try {
				esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JSONArray all = clean(esResponse);
		//positive
		String positiveparams = "";
		i=0;
		while(i<3){
			i++;
			try {
				positiveparams = "matchingmode=0&searchType=1&timeType=1&stopword=&esindex=postal&timee="+timee+"&estype=infor&times="+times+"&emotionalIndex=1&size=10&page=1&keyword="+keywords;
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		System.err.println("============push_PO positiveparams=================");
//		System.out.println("url:"+url);
//		System.out.println("params:"+params);
//		System.err.println("=============push_PO positiveparams=================");
		String positiveesResponse = MyHttpRequestUtil.sendPostEsSearch(url, positiveparams);
		JSONArray positive = clean(positiveesResponse);
		//negative
		String negativeparams =  "matchingmode=0&searchType=1&timeType=1&stopword=&esindex=postal&timee="+timee+"&estype=infor&times="+times+"&emotionalIndex=3&size=10&page=1&keyword="+keywords;
		String negativeesResponse = "";
		i=0;
		while(i<3){
			i++;
			try {
				negativeesResponse = MyHttpRequestUtil.sendPostEsSearch(url, negativeparams);
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		System.err.println("============push_PO negativeparams=================");
//		System.out.println("url:"+url);
//		System.out.println("params:"+params);
//		System.err.println("=============push_PO negativeparams=================");
		JSONArray negative = clean(negativeesResponse);
		JSONObject result = new JSONObject();
		result.put("all", all);
		result.put("positive", positive);
		result.put("negative", negative);
		return result.toJSONString();
	}
	private JSONArray clean(String esResponse) {
		JSONObject parseObject = JSONObject.parseObject(esResponse);
		JSONArray jsonArray = parseObject.getJSONArray("data");
		JSONArray list = new JSONArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("_source");
			JSONObject ob = new JSONObject();
			String string = jsonObject.getString("content");
			ob.put("id", jsonObject.get("article_public_id"));
			String content = string.length()>180?string.substring(0,180)+"...":string;
			if (content != null) {
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(content);
				content = m.replaceAll("");
				}
			ob.put("content",content);
			ob.put("title", jsonObject.get("title"));
			ob.put("source_url", jsonObject.get("source_url"));
			ob.put("emotionalIndex", jsonObject.get("emotionalIndex"));
			ob.put("publish_time", jsonObject.get("publish_time"));
			ob.put("source_name", jsonObject.get("source_name"));
			
			list.add(ob);
		}
		
		return list;
	}
	private String getkeywords(User user,int type) {
		List<String> keywords= projectDao.getKeywordsByUser2(user.getUser_id(),type);
		String keyword = "";
		List<String> keywordList = new ArrayList<>();
		for (String string : keywords) {
			if(null != string && !"".equals(string.trim())){
				String[] split = string.split(",");
				for (String string2 : split) {
					keywordList.add(string2.trim());
				}
			}
		}
		Set set = new HashSet();
		set.addAll(keywordList);     // 将list所有元素添加到set中    set集合特性会自动去重复
		keywordList.clear();
		keywordList.addAll(set);
		for (String kw : keywordList) {
			keyword += kw+",";
		}
		return keyword.length()>1?keyword.substring(0,keyword.length()-1):"";
	}
	private String getprojectstatus(User user,Integer type) {
		List<Map<String, Object>> projectlist = projectDao.getprojectByUser2(user.getUser_id(),type);
		JSONArray result = new JSONArray();
		for (Map<String, Object> map : projectlist) {
			String project_id = map.get("project_id").toString();
			String group_id = map.get("group_id").toString();
			JSONObject paramJson = new JSONObject();
			List<Integer> emotionalIndex = new ArrayList<Integer>();
			emotionalIndex.add(1);
			emotionalIndex.add(2);
			emotionalIndex.add(3);
//			String dateday = DateUtil.getDateday();
			paramJson.put("group_id", group_id);
			paramJson.put("project_id", project_id);
			paramJson.put("projectid", project_id);
			paramJson.put("projectId", project_id);
			paramJson.put("timeType",1);
			paramJson.put("precise",0);
			paramJson.put("matchingmode",1);
			paramJson.put("similar",0);
			paramJson.put("searchType",1);
			paramJson.put("page",1);
			paramJson.put("times","");
			paramJson.put("timee","");
			paramJson.put("emotionalIndex",emotionalIndex);
			int i = 0;
			JSONObject articleList = new JSONObject();
			while(i<3){
				try {
					articleList = monitorService.getArticleSynthesizeList(paramJson);
					Integer code = articleList.getInteger("code");
					if(code==200){
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			JSONObject prostatus = new JSONObject();
			prostatus.put("project_name", map.get("project_name"));
			prostatus.put("value",articleList.getJSONObject("data").get("totalCount"));
			result.add(prostatus);
		}
		
		result = sortProxyAndCdn(result);
		
		
		return result.toJSONString();
	}
	//集合排序
	private static JSONArray sortProxyAndCdn(JSONArray bindArrayResult) {
        bindArrayResult.sort(Comparator.comparing(obj -> ((JSONObject) obj).getBigDecimal("value")).reversed());
      return bindArrayResult;
    }
	
	
	private String getWarning(User user) throws NumberFormatException, java.text.ParseException {
		List<Map<String, Object>> warninglist = warningarticleDao.selectWAlsitByUser(user.getUser_id());
//		long USERID =  Long.parseLong("15720821513");
//		List<Map<String, Object>> warninglist = warningarticleDao.selectWAlsitByUser(USERID);
		JSONArray list = new JSONArray();
		for (Map<String, Object> res : warninglist) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String string = res.get("article_detail").toString();
			JSONObject parseObject = JSONObject.parseObject(string);
			res.remove("article_detail");
			res.put("source_name", parseObject.get("sourcewebsitename"));
			String string2 = res.get("publish_time").toString();
			String a = string2.substring(0, string2.length()-2);
			res.put("publish_time",a);
			list.add(new JSONObject(res));
		}
		return list.toJSONString();
	}
	private String conversionHotList(JSONObject hotList) {
		JSONArray jsonArray=new JSONArray();
		if(hotList!=null) {
			jsonArray = hotList.getJSONArray("data");
		}
		JSONArray list = new JSONArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("_source");
			JSONObject jsonObject2 = new JSONObject();
			jsonObject2.put("publish_time", jsonObject.get("publish_time"));
			jsonObject2.put("topic", jsonObject.get("topic"));
			jsonObject2.put("id", jsonObject.get("article_public_id"));
			jsonObject2.put("original_weight", jsonObject.get("original_weight"));
			jsonObject2.put("source_name", jsonObject.get("source_name"));
			jsonObject2.put("source_url", jsonObject.get("source_url"));
			jsonObject2.put("rank", Integer.parseInt(jsonObject.get("rank").toString()));
			list.add(jsonObject2);
		}
		
	    
		List<Map<String,Object>> dataArr = (List)JSONArray.parseArray(list.toJSONString(),Map.class);
		
		dataArr = dataArr.stream()
                .sorted((map2, map1) -> (int) map2.get("rank") - (int) map1.get("rank")).limit(5)
                .collect(Collectors.toList());
		
		return JSON.toJSONString(dataArr);
	}
	private String getReport(Integer report_type,User user) {
		List<Map<String, Object>> result =  reportCustomDao.searchReportByUserAndType2(user.getUser_id(),report_type);
		JSONArray list = new JSONArray();
		for (Map<String, Object> res : result) {
			list.add(new JSONObject(res));
		}
		return list.toString();
	}
	
	
	public static void main(String[] args) {
		getPolicyData();
	}
	
	
	/**
	 * 政策数据
	 * @return
	 */
	public static String getPolicyData() {
		
		String url = "http://www.gov.cn/zhengce/zuixin.htm";
		JSONArray array = new JSONArray();
		try {
			String gethtml = gethtml(url);
			Document parse = Jsoup.parse(gethtml);
			Elements select = parse.select(".news_box > .list > ul > li");
			for (int i = 0; i < select.size()&& i<5; i++) {
				JSONObject object = new JSONObject();
				Element element = select.get(i);
				String source_url = element.getElementsByTag("a").get(0).attr("href");
				object.put("source_url", source_url);
				int rank = i+1;
				object.put("rank", rank);
				object.put("original_weight", 100000);
				
				object.put("source_name", "国务院");
				String topic = element.getElementsByTag("a").get(0).text();
				object.put("topic", topic);
				
				String publish_time = element.getElementsByClass("date").get(0).text();
				object.put("publish_time", publish_time+" 00:00:00");
				array.add(object);
			}
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return array.toJSONString();
		
		
		
		
	}
	
	
	/**
	 * 经济数据
	 * @return
	 */
	public static String getFinaceData() {
		
		String url = "http://finance.eastmoney.com/a/cgnjj.html";
		JSONArray array = new JSONArray();
		try {
			String gethtml = gethtml(url);
			Document parse = Jsoup.parse(gethtml);
			Elements select = parse.select("#newsListContent li");
			for (int i = 0; i < select.size(); i++) {
				
				JSONObject object = new JSONObject();
				Element element = select.get(i);
				String topic = element.getElementsByTag("a").get(0).text();
				if(!topic.equals("")) {
					String source_url = element.getElementsByTag("a").get(0).attr("href");
					object.put("source_url", source_url);
					int rank = i+1;
					object.put("rank", rank);
					object.put("original_weight", 100000);
					
					object.put("source_name", "中方财富网");
					
					object.put("topic", topic);
					
	               String publish_time = "2021-"+element.getElementsByClass("time").get(0).text()+":00";
			       //String publish_time = element.getElementsByClass("time").get(0).text();
				   publish_time = publish_time.replaceAll("月", "-").replaceAll("日", "");
					try {
						object.put("publish_time", DateUtil.FormatDate(publish_time));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					array.add(object);
				}
				
			}
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return array.toJSONString();
		
		
		
		
	}
	
	
	
	
	public static String gethtml(String url) throws ParseException, IOException, InterruptedException {

		org.apache.http.client.CookieStore cookieStore = new BasicCookieStore();
		CloseableHttpClient httpclient = HttpClients.createDefault();

		Thread.sleep(1);
		String string = null;
		HttpGet httpget = new HttpGet(url);
		RequestConfig config = RequestConfig.custom().setConnectTimeout(10 * 1000).setSocketTimeout(20 * 1000).build();
		httpget.setConfig(config);
		httpget.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
		
		CloseableHttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		Integer statu = response.getStatusLine().getStatusCode();
		List<Cookie> cookies = null;
		if (entity != null) {
			string = EntityUtils.toString(entity, "utf-8");
			cookies = cookieStore.getCookies();
		}
		response.close();
		httpclient.close();
		return string;
	}
	
	
	
	
}
