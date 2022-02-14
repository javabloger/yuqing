package com.stonedt.intelligence.quartz;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.constant.PublicoptionConstant;
import com.stonedt.intelligence.constant.SearchConstant;
import com.stonedt.intelligence.dao.PublicOptionDao;
import com.stonedt.intelligence.entity.PublicoptionEntity;
import com.stonedt.intelligence.service.FullSearchService;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.MyHttpRequestUtil;
import com.stonedt.intelligence.vo.FullSearchParam;;

 
/**
 * 舆情研判定时任务
 */
@Component
@EnableScheduling
public class publicoptionQuartz {

    // 定时任务开关
    @Value("${schedule.publicoption.open}")
    private Integer schedule_publicoption_open;
 // es搜索地址
    @Value("${es.search.url}")
    private String es_search_url;
    
    @Value("${es.hot.search.url}")
    private String es_hot_search_url;
//    private  String es_search_url="http://221.231.137.209:7120";
//    private  String es_search_url="http://127.0.0.1:8004";
    private  String similarityMaxTitle = null; 
    
    @Autowired
    private PublicOptionDao PublicOptionDao;
    @Autowired
    private FullSearchService fullSearchService;

    /**
     * 舆情研判
     */
//    @PostConstruct
    @Scheduled(cron = "0 0/2 * * * ?")
    public void popularInformation() {
        if (schedule_publicoption_open == 1) {
        	//publicoptionevent
        	System.out.println("=============================");
        	System.out.println("开始舆情研判");
        	System.out.println("=============================");
        	List<PublicoptionEntity> result = PublicOptionDao.getUnfinishedPublicoptionevent();
        	for (int i = 0; i < result.size(); i++) {
        		try {
        			PublicoptionEntity publicoptionEntity = result.get(i);
        			Map<String, Object> map = new HashMap<String, Object>();
        			map.put("publicoption_id", publicoptionEntity.getId());
        			//溯源分析
        			try {
        				String back_analysis = getback_analysis(publicoptionEntity);
        				map.put("back_analysis", back_analysis);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        			try {
        				//事件脉络   
        				String event_context = gatevent_context(publicoptionEntity);
        				map.put("event_context", event_context);
        			} catch (Exception e) {
        				// TODO: handle exception
        			}
        			
        			//事件跟踪
        			try {
        				String event_trace = getevent_trace(publicoptionEntity);
        				map.put("event_trace", event_trace);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        			//热点分析 hot_analysis 
        			try {
        				String hot_analysis = gethot_analysis(publicoptionEntity);
        				map.put("hot_analysis", hot_analysis);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
//        		重点网民分析 netizens_analysis 
        			try {
        				String netizens_analysis = getnetizens_analysis(publicoptionEntity);
        				map.put("netizens_analysis", netizens_analysis);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
//        		统计 statistics
        			try {
        				String statistics = getstatistics(publicoptionEntity);
        				map.put("statistics", statistics);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        			//propagation_analysis
        			try {
        				String propagation_analysis = getpropagation_analysis(publicoptionEntity);
        				map.put("propagation_analysis", propagation_analysis);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        			//thematic_analysis
        			try {
        				String thematic_analysis = getthematic_analysis(publicoptionEntity);
        				map.put("thematic_analysis", thematic_analysis);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        			try {
        				JSONObject unscramble_contentOb =new JSONObject();
        				JSONObject parseObject = JSONObject.parseObject(map.get("thematic_analysis").toString());
        				String wemediaTitle = parseObject.getJSONArray("netizen").getJSONObject(0).getString("title");
        				String main_mediaTitle = parseObject.getJSONArray("media").getJSONObject(0).getString("title");
        				unscramble_contentOb.put("wemedia", wemediaTitle);
        				unscramble_contentOb.put("main_media", main_mediaTitle);
        				map.put("unscramble_content", unscramble_contentOb.toJSONString());
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        			System.out.println("===============================");
        			System.err.println(map);
        			System.out.println("--------------------------------");
        			System.err.println(JSON.toJSONString(map));
        			System.out.println("===============================");
        			PublicOptionDao.savepublicoptionDetail(map);
        			PublicOptionDao.updateStatusbyid(publicoptionEntity.getId(),3);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
        }
    }
    
    private  String getthematic_analysis(PublicoptionEntity publicoptionEntity) {
    	JSONObject result = new JSONObject();
    	try {
    		JSONArray view = getNAobject(publicoptionEntity);
    		result.put("view", view);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	try {
    		JSONArray netizen = gethotbyclassify(publicoptionEntity,7);
    		result.put("netizen", netizen);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	try {
    		JSONArray media = gethotbyclassify(publicoptionEntity,8);
    		result.put("media", media);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result.toJSONString();
	}

	private  JSONArray gethotbyclassify(PublicoptionEntity publicoptionEntity,int type) {
		String url = es_search_url + PublicoptionConstant.es_api_qbsearchcontent;
		String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    	String eventendtime = checkString(publicoptionEntity.getEventendtime());
    	String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    	String eventstopwords = checkString(publicoptionEntity.getEventstopwords());
    	String searchwords = "";
//    	eventkeywords = "海南司法";
    	String params = "times=" + eventstarttime + "&timee=" + eventendtime + "&keyword=" + eventkeywords + "&stopword=" + eventstopwords
                + "&searchkeyword=" + searchwords + "&origintype=0&emotionalIndex=1,2,3&projecttype=2&classify="+type;
    	String sendPost = getEsRequset(url, params,null,null);
//    	String sendPost =	    	"{\"took\":9797,\"timed_out\":false,\"_shards\":{\"total\":5,\"successful\":5,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":502734,\"max_score\":0.0,\"hits\":[]},\"aggregations\":{\"top_score\":{\"doc_count_error_upper_bound\":202,\"sum_other_doc_count\":464917,\"buckets\":[{\"key\":\"2020北京车展现场直击，宝马THE5\",\"doc_count\":1057,\"top_score_hits\":{\"hits\":{\"total\":1057,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"5706236d7f8b650abbed3191d519499f\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"5706236d7f8b650abbed3191d519499f\",\"publish_time\":\"2020-09-28 19:34:03\",\"sourcewebsitename\":\"趣头条\",\"title\":\"2020北京车展现场直击，宝马THE5\",\"similarvolume\":\"0\"},\"sort\":[1601321643000]}]}}},{\"key\":\"卖猪肉的大哥刀法不错，切猪肉跟切菜一样，真想问问刀在哪买的！\",\"doc_count\":1056,\"top_score_hits\":{\"hits\":{\"total\":1056,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"e875525d20b9d0628e2269b3fd70ad78\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"e875525d20b9d0628e2269b3fd70ad78\",\"publish_time\":\"2020-09-28 17:01:18\",\"sourcewebsitename\":\"趣头条\",\"title\":\"卖猪肉的大哥刀法不错，切猪肉跟切菜一样，真想问问刀在哪买的！\",\"similarvolume\":\"0\"},\"sort\":[1601312478000]}]}}},{\"key\":\"美女球迷说中超，为主队花样打CALL\",\"doc_count\":1056,\"top_score_hits\":{\"hits\":{\"total\":1056,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"f64a486df3659d9f3e2ffcd88f9c1d35\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"f64a486df3659d9f3e2ffcd88f9c1d35\",\"publish_time\":\"2020-09-28 17:15:57\",\"sourcewebsitename\":\"趣头条\",\"title\":\"美女球迷说中超，为主队花样打CALL\",\"similarvolume\":\"0\"},\"sort\":[1601313357000]}]}}},{\"key\":\"前方记者亲历球迷入场看球的盛况\",\"doc_count\":1054,\"top_score_hits\":{\"hits\":{\"total\":1054,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"e4f5b8f131cdab575e9357ea09f28077\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"e4f5b8f131cdab575e9357ea09f28077\",\"publish_time\":\"2020-09-28 17:16:11\",\"sourcewebsitename\":\"趣头条\",\"title\":\"前方记者亲历球迷入场看球的盛况\",\"similarvolume\":\"0\"},\"sort\":[1601313371000]}]}}},{\"key\":\"2020北京车展现场直击，大众高尔夫8\",\"doc_count\":1047,\"top_score_hits\":{\"hits\":{\"total\":1047,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"4ac428b4b3412843c23df1d4d206a19a\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"4ac428b4b3412843c23df1d4d206a19a\",\"publish_time\":\"2020-09-28 17:42:08\",\"sourcewebsitename\":\"趣头条\",\"title\":\"2020北京车展现场直击，大众高尔夫8\",\"similarvolume\":\"0\"},\"sort\":[1601314928000]}]}}},{\"key\":\"高合HiPhix\",\"doc_count\":1047,\"top_score_hits\":{\"hits\":{\"total\":1047,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"f1c7a37124e2f0a24bd60d94bd468e35\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"f1c7a37124e2f0a24bd60d94bd468e35\",\"publish_time\":\"2020-09-28 15:36:53\",\"sourcewebsitename\":\"趣头条\",\"title\":\"高合HiPhix\",\"similarvolume\":\"0\"},\"sort\":[1601307413000]}]}}},{\"key\":\"黄健翔分析本赛季中超赛场上本土球员的成长\",\"doc_count\":1047,\"top_score_hits\":{\"hits\":{\"total\":1047,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"3cca85e43e54c0edeb9ed3ddceb86df9\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"3cca85e43e54c0edeb9ed3ddceb86df9\",\"publish_time\":\"2020-09-28 17:15:56\",\"sourcewebsitename\":\"趣头条\",\"title\":\"黄健翔分析本赛季中超赛场上本土球员的成长\",\"similarvolume\":\"0\"},\"sort\":[1601313356000]}]}}},{\"key\":\"2020北京车展现场直击，名爵MG5\",\"doc_count\":1044,\"top_score_hits\":{\"hits\":{\"total\":1044,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"fd612bc8aa2c4a336713b9b7eaa39a55\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"fd612bc8aa2c4a336713b9b7eaa39a55\",\"publish_time\":\"2020-09-28 19:58:32\",\"sourcewebsitename\":\"趣头条\",\"title\":\"2020北京车展现场直击，名爵MG5\",\"similarvolume\":\"0\"},\"sort\":[1601323112000]}]}}},{\"key\":\"2020北京车展现场直击，荣威IMAX8\",\"doc_count\":1043,\"top_score_hits\":{\"hits\":{\"total\":1043,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"778f55c9fa41ca38fd64b747f479abb7\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"778f55c9fa41ca38fd64b747f479abb7\",\"publish_time\":\"2020-09-28 19:02:10\",\"sourcewebsitename\":\"趣头条\",\"title\":\"2020北京车展现场直击，荣威IMAX8\",\"similarvolume\":\"0\"},\"sort\":[1601319730000]}]}}},{\"key\":\"2020北京车展现场直击，日产Ariya\",\"doc_count\":1042,\"top_score_hits\":{\"hits\":{\"total\":1042,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"babfca46cbf1b11d1be355e5e02090dc\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"babfca46cbf1b11d1be355e5e02090dc\",\"publish_time\":\"2020-09-28 18:36:32\",\"sourcewebsitename\":\"趣头条\",\"title\":\"2020北京车展现场直击，日产Ariya\",\"similarvolume\":\"0\"},\"sort\":[1601318192000]}]}}},{\"key\":\"一旦爆发核战争，老百姓只能等死？非也，这三个地方可保小命！\",\"doc_count\":1035,\"top_score_hits\":{\"hits\":{\"total\":1035,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"38938e488a93c7782e81bcdbf260e057\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"38938e488a93c7782e81bcdbf260e057\",\"publish_time\":\"2020-09-28 20:25:58\",\"sourcewebsitename\":\"趣头条\",\"title\":\"一旦爆发核战争，老百姓只能等死？非也，这三个地方可保小命！\",\"similarvolume\":\"0\"},\"sort\":[1601324758000]}]}}},{\"key\":\"北京车展现场直击：奔驰宝马大众，这些新车都凑齐了\",\"doc_count\":1011,\"top_score_hits\":{\"hits\":{\"total\":1011,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"b71d0529ed7049118187561a31c649e\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"b71d0529ed7049118187561a31c649e\",\"publish_time\":\"2020-09-29 12:40:41\",\"sourcewebsitename\":\"趣头条\",\"title\":\"北京车展现场直击：奔驰宝马大众，这些新车都凑齐了\",\"similarvolume\":\"0\"},\"sort\":[1601383241000]}]}}},{\"key\":\"鼻子不堵了！两味药，冲散鼻中痰湿，打开鼻窍\",\"doc_count\":1011,\"top_score_hits\":{\"hits\":{\"total\":1011,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"d464514505fad3848d5734c43e1b9af7\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"d464514505fad3848d5734c43e1b9af7\",\"publish_time\":\"2020-09-29 09:20:49\",\"sourcewebsitename\":\"趣头条\",\"title\":\"鼻子不堵了！两味药，冲散鼻中痰湿，打开鼻窍\",\"similarvolume\":\"0\"},\"sort\":[1601371249000]}]}}},{\"key\":\"乳腺癌、肺癌转移了，不一定是“判死刑”！医生告诉你还能活多久\",\"doc_count\":994,\"top_score_hits\":{\"hits\":{\"total\":994,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"7d568db54b7e705b21585df51c6c48f8\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"7d568db54b7e705b21585df51c6c48f8\",\"publish_time\":\"2020-09-29 14:53:10\",\"sourcewebsitename\":\"趣头条\",\"title\":\"乳腺癌、肺癌转移了，不一定是“判死刑”！医生告诉你还能活多久\",\"similarvolume\":\"0\"},\"sort\":[1601391190000]}]}}},{\"key\":\"如何自己判断是否是下肢动脉硬化闭塞症？\",\"doc_count\":994,\"top_score_hits\":{\"hits\":{\"total\":994,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"347663990f582b8c6a6b5a48dc656e10\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"347663990f582b8c6a6b5a48dc656e10\",\"publish_time\":\"2020-09-29 14:19:13\",\"sourcewebsitename\":\"趣头条\",\"title\":\"如何自己判断是否是下肢动脉硬化闭塞症？\",\"similarvolume\":\"0\"},\"sort\":[1601389153000]}]}}},{\"key\":\"脸上莫名其妙长斑，一检查发现，都是因为平时太懒了\",\"doc_count\":993,\"top_score_hits\":{\"hits\":{\"total\":993,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"5e55e5bf811f74c762d719c2d0f7e063\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"5e55e5bf811f74c762d719c2d0f7e063\",\"publish_time\":\"2020-09-29 10:13:08\",\"sourcewebsitename\":\"趣头条\",\"title\":\"脸上莫名其妙长斑，一检查发现，都是因为平时太懒了\",\"similarvolume\":\"0\"},\"sort\":[1601374388000]}]}}},{\"key\":\"造成月经不调的习惯有哪些？多与这4种不良习惯有关，赶紧戒掉\",\"doc_count\":984,\"top_score_hits\":{\"hits\":{\"total\":984,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"e5d522c033607bc6652dce9955a2efcc\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"e5d522c033607bc6652dce9955a2efcc\",\"publish_time\":\"2020-09-29 10:00:02\",\"sourcewebsitename\":\"趣头条\",\"title\":\"造成月经不调的习惯有哪些？多与这4种不良习惯有关，赶紧戒掉\",\"similarvolume\":\"0\"},\"sort\":[1601373602000]}]}}},{\"key\":\"颈椎病的康复治疗有哪些？\",\"doc_count\":942,\"top_score_hits\":{\"hits\":{\"total\":942,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"291b8b7c92b516a94bbe2e7a1a27efd1\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"291b8b7c92b516a94bbe2e7a1a27efd1\",\"publish_time\":\"2020-09-28 18:17:06\",\"sourcewebsitename\":\"趣头条\",\"title\":\"颈椎病的康复治疗有哪些？\",\"similarvolume\":\"0\"},\"sort\":[1601317026000]}]}}},{\"key\":\"星座小知识：十二星座不得不忍受的缺点，如何应对摩羯座的冷漠\",\"doc_count\":852,\"top_score_hits\":{\"hits\":{\"total\":852,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"3cd91bdee46a5f9bff34f780ea73b0aa\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"3cd91bdee46a5f9bff34f780ea73b0aa\",\"publish_time\":\"2020-09-29 13:45:04\",\"sourcewebsitename\":\"趣头条\",\"title\":\"星座小知识：十二星座不得不忍受的缺点，如何应对摩羯座的冷漠\",\"similarvolume\":\"0\"},\"sort\":[1601387104000]}]}}},{\"key\":\"刘建宏前瞻天津泰达、河南建业和青岛黄海的保级前景\",\"doc_count\":841,\"top_score_hits\":{\"hits\":{\"total\":841,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"9de7b815edd825a85d2586e46f6d67cd\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"9de7b815edd825a85d2586e46f6d67cd\",\"publish_time\":\"2020-09-28 17:15:47\",\"sourcewebsitename\":\"趣头条\",\"title\":\"刘建宏前瞻天津泰达、河南建业和青岛黄海的保级前景\",\"similarvolume\":\"0\"},\"sort\":[1601313347000]}]}}},{\"key\":\"归化球员选择范围扩大，国家队可以灵活选用\",\"doc_count\":836,\"top_score_hits\":{\"hits\":{\"total\":836,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"93753dce5acdab5563ebccdb26795752\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"93753dce5acdab5563ebccdb26795752\",\"publish_time\":\"2020-09-28 17:15:37\",\"sourcewebsitename\":\"趣头条\",\"title\":\"归化球员选择范围扩大，国家队可以灵活选用\",\"similarvolume\":\"0\"},\"sort\":[1601313337000]}]}}},{\"key\":\"为什么手腕突然疼痛用不上力气？有这4个可能，你是哪个\",\"doc_count\":835,\"top_score_hits\":{\"hits\":{\"total\":835,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"5177deaf00f5963015386e71619b9a1e\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"5177deaf00f5963015386e71619b9a1e\",\"publish_time\":\"2020-09-28 17:58:39\",\"sourcewebsitename\":\"趣头条\",\"title\":\"为什么手腕突然疼痛用不上力气？有这4个可能，你是哪个\",\"similarvolume\":\"0\"},\"sort\":[1601315919000]}]}}},{\"key\":\"受用一生的十句话，你一定要看\",\"doc_count\":757,\"top_score_hits\":{\"hits\":{\"total\":757,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"facf14c6fddf3edf19bfed2d93ee59f8\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"facf14c6fddf3edf19bfed2d93ee59f8\",\"publish_time\":\"2020-09-28 21:57:32\",\"sourcewebsitename\":\"趣头条\",\"title\":\"受用一生的十句话，你一定要看\",\"similarvolume\":\"0\"},\"sort\":[1601330252000]}]}}},{\"key\":\"你想知道的2021年初级会计实务学习方法\",\"doc_count\":747,\"top_score_hits\":{\"hits\":{\"total\":747,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"f221cf71c1ef79351c75895f7127660\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"f221cf71c1ef79351c75895f7127660\",\"publish_time\":\"2020-09-29 13:32:43\",\"sourcewebsitename\":\"趣头条\",\"title\":\"你想知道的2021年初级会计实务学习方法\",\"similarvolume\":\"0\"},\"sort\":[1601386363000]}]}}},{\"key\":\"扁平足没有及时矫正，膝盖、脊柱都受罪！医生教你预防的动作\",\"doc_count\":719,\"top_score_hits\":{\"hits\":{\"total\":719,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"a58d3065abe0431742aa8af03021066f\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"a58d3065abe0431742aa8af03021066f\",\"publish_time\":\"2020-09-28 15:27:47\",\"sourcewebsitename\":\"趣头条\",\"title\":\"扁平足没有及时矫正，膝盖、脊柱都受罪！医生教你预防的动作\",\"similarvolume\":\"0\"},\"sort\":[1601306867000]}]}}},{\"key\":\"放化疗期间，病人怎么补充营养？要忌口吗？医生详细解答\",\"doc_count\":677,\"top_score_hits\":{\"hits\":{\"total\":677,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"faa0f4395b15baf5d4513c407f43a35c\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"faa0f4395b15baf5d4513c407f43a35c\",\"publish_time\":\"2020-09-28 15:25:27\",\"sourcewebsitename\":\"趣头条\",\"title\":\"放化疗期间，病人怎么补充营养？要忌口吗？医生详细解答\",\"similarvolume\":\"0\"},\"sort\":[1601306727000]}]}}},{\"key\":\"八旬老人突然腰背痛，却查出肠癌！怎样区分脊柱转移瘤和腰椎病？\",\"doc_count\":669,\"top_score_hits\":{\"hits\":{\"total\":669,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"f3a1d7878635ae4f21c31a7c27279222\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"f3a1d7878635ae4f21c31a7c27279222\",\"publish_time\":\"2020-09-28 15:25:50\",\"sourcewebsitename\":\"趣头条\",\"title\":\"八旬老人突然腰背痛，却查出肠癌！怎样区分脊柱转移瘤和腰椎病？\",\"similarvolume\":\"0\"},\"sort\":[1601306750000]}]}}},{\"key\":\"孩子希望你能理解爸爸的一番苦心，加油学习\",\"doc_count\":663,\"top_score_hits\":{\"hits\":{\"total\":663,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"df94c23c3fff0935af375828b67b5ddd\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"df94c23c3fff0935af375828b67b5ddd\",\"publish_time\":\"2020-09-28 17:17:19\",\"sourcewebsitename\":\"趣头条\",\"title\":\"孩子希望你能理解爸爸的一番苦心，加油学习\",\"similarvolume\":\"0\"},\"sort\":[1601313439000]}]}}},{\"key\":\"惠威D1100蓝牙有源音响试听！\",\"doc_count\":663,\"top_score_hits\":{\"hits\":{\"total\":663,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"4084f78f2d75605c81091f0f88a43920\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"4084f78f2d75605c81091f0f88a43920\",\"publish_time\":\"2020-09-28 16:00:44\",\"sourcewebsitename\":\"趣头条\",\"title\":\"惠威D1100蓝牙有源音响试听！\",\"similarvolume\":\"0\"},\"sort\":[1601308844000]}]}}},{\"key\":\"孩子有扁平足，平时没有症状，能正常运动吗？医生的话让人放心\",\"doc_count\":661,\"top_score_hits\":{\"hits\":{\"total\":661,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"cb4ea54faff22c568ab21e06e6a8ffc6\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"cb4ea54faff22c568ab21e06e6a8ffc6\",\"publish_time\":\"2020-09-29 14:51:34\",\"sourcewebsitename\":\"趣头条\",\"title\":\"孩子有扁平足，平时没有症状，能正常运动吗？医生的话让人放心\",\"similarvolume\":\"0\"},\"sort\":[1601391094000]}]}}},{\"key\":\"儿童剧：杜子腾考100分第一名有奖状，考40分倒数第一名也有奖状\",\"doc_count\":634,\"top_score_hits\":{\"hits\":{\"total\":634,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"f4eb5f7113af8d90fbc796a7f7f7589d\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"f4eb5f7113af8d90fbc796a7f7f7589d\",\"publish_time\":\"2020-09-29 12:11:20\",\"sourcewebsitename\":\"趣头条\",\"title\":\"儿童剧：杜子腾考100分第一名有奖状，考40分倒数第一名也有奖状\",\"similarvolume\":\"0\"},\"sort\":[1601381480000]}]}}},{\"key\":\"海螺怎么吃？教你海边特色吃法，做法超简单，又一道中秋节硬菜！\",\"doc_count\":606,\"top_score_hits\":{\"hits\":{\"total\":606,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"42b74e747f024b67fbd7d0e52c7ddfb0\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"42b74e747f024b67fbd7d0e52c7ddfb0\",\"publish_time\":\"2020-09-29 10:40:40\",\"sourcewebsitename\":\"趣头条\",\"title\":\"海螺怎么吃？教你海边特色吃法，做法超简单，又一道中秋节硬菜！\",\"similarvolume\":\"0\"},\"sort\":[1601376040000]}]}}},{\"key\":\"汪峰到底多有钱？章子怡无意间说漏嘴，网友：贫穷限制想象\",\"doc_count\":578,\"top_score_hits\":{\"hits\":{\"total\":578,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"ebd9fddb5f33f7ea4ec0cc136c122813\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"ebd9fddb5f33f7ea4ec0cc136c122813\",\"publish_time\":\"2020-09-29 16:37:14\",\"sourcewebsitename\":\"趣头条\",\"title\":\"汪峰到底多有钱？章子怡无意间说漏嘴，网友：贫穷限制想象\",\"similarvolume\":\"0\"},\"sort\":[1601397434000]}]}}},{\"key\":\"小黄瓜不要炒着吃了，教你农村特色吃法，不腌咸菜不凉拌，特香！\",\"doc_count\":576,\"top_score_hits\":{\"hits\":{\"total\":576,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"8dd874e01108bde1fea3887fa7be4c00\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"8dd874e01108bde1fea3887fa7be4c00\",\"publish_time\":\"2020-09-28 10:23:28\",\"sourcewebsitename\":\"趣头条\",\"title\":\"小黄瓜不要炒着吃了，教你农村特色吃法，不腌咸菜不凉拌，特香！\",\"similarvolume\":\"0\"},\"sort\":[1601288608000]}]}}},{\"key\":\"来看看这次央视镜头下的女明星！杨紫唐嫣迪丽热巴颜值逆天\",\"doc_count\":575,\"top_score_hits\":{\"hits\":{\"total\":575,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"7360288c32ed73732e4c3171c6a987ce\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"7360288c32ed73732e4c3171c6a987ce\",\"publish_time\":\"2020-09-29 16:34:00\",\"sourcewebsitename\":\"趣头条\",\"title\":\"来看看这次央视镜头下的女明星！杨紫唐嫣迪丽热巴颜值逆天\",\"similarvolume\":\"0\"},\"sort\":[1601397240000]}]}}},{\"key\":\"大选已无悬念？最新民调出炉，65%美国人的态度说明一切\",\"doc_count\":567,\"top_score_hits\":{\"hits\":{\"total\":567,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"b7e268bd22bcb5f9bb9caa5407179a69\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"b7e268bd22bcb5f9bb9caa5407179a69\",\"publish_time\":\"2020-09-28 01:18:57\",\"sourcewebsitename\":\"趣头条\",\"title\":\"大选已无悬念？最新民调出炉，65%美国人的态度说明一切\",\"similarvolume\":\"0\"},\"sort\":[1601255937000]}]}}},{\"key\":\"边境气温骤降！印军大批山地部队冻伤，印媒：牛皮被彻底戳穿\",\"doc_count\":566,\"top_score_hits\":{\"hits\":{\"total\":566,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"d76805f8515d2a47c24abac5a6f14e85\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"d76805f8515d2a47c24abac5a6f14e85\",\"publish_time\":\"2020-09-28 01:08:58\",\"sourcewebsitename\":\"趣头条\",\"title\":\"边境气温骤降！印军大批山地部队冻伤，印媒：牛皮被彻底戳穿\",\"similarvolume\":\"0\"},\"sort\":[1601255338000]}]}}},{\"key\":\"高考报考规则：什么是提前批？到底要不要报考？\",\"doc_count\":560,\"top_score_hits\":{\"hits\":{\"total\":560,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"d38daccb93f641322dcb0af74eff460b\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"d38daccb93f641322dcb0af74eff460b\",\"publish_time\":\"2020-09-28 22:44:40\",\"sourcewebsitename\":\"趣头条\",\"title\":\"高考报考规则：什么是提前批？到底要不要报考？\",\"similarvolume\":\"0\"},\"sort\":[1601333080000]}]}}},{\"key\":\"如果您家的老人没事总爱去医院，可要小心认知障碍！\",\"doc_count\":541,\"top_score_hits\":{\"hits\":{\"total\":541,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"41498afd998e7ffa2861df86d82e384a\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"41498afd998e7ffa2861df86d82e384a\",\"publish_time\":\"2020-09-28 20:01:11\",\"sourcewebsitename\":\"58同镇\",\"title\":\"如果您家的老人没事总爱去医院，可要小心认知障碍！\",\"similarvolume\":\"2\"},\"sort\":[1601323271000]}]}}},{\"key\":\"你以为邓超陈赫鹿晗真的情比金坚？可不要小看了男明星之间的竞争\",\"doc_count\":529,\"top_score_hits\":{\"hits\":{\"total\":529,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"41b077c783016655dcb286a14d4414f4\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"41b077c783016655dcb286a14d4414f4\",\"publish_time\":\"2020-09-29 10:57:13\",\"sourcewebsitename\":\"趣头条\",\"title\":\"你以为邓超陈赫鹿晗真的情比金坚？可不要小看了男明星之间的竞争\",\"similarvolume\":\"0\"},\"sort\":[1601377033000]}]}}},{\"key\":\"欧阳妮妮新剧被吐槽，一头卷发土气凸嘴明显，导演后悔选她做女主\",\"doc_count\":525,\"top_score_hits\":{\"hits\":{\"total\":525,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"a7114d238505fa4167fd630fb102374a\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"a7114d238505fa4167fd630fb102374a\",\"publish_time\":\"2020-09-28 17:30:38\",\"sourcewebsitename\":\"趣头条\",\"title\":\"欧阳妮妮新剧被吐槽，一头卷发土气凸嘴明显，导演后悔选她做女主\",\"similarvolume\":\"0\"},\"sort\":[1601314238000]}]}}},{\"key\":\"打响反美第一枪？一枚炸弹在首都引爆，美方3名美中情局人员丧生\",\"doc_count\":483,\"top_score_hits\":{\"hits\":{\"total\":483,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"7e1d6481a91f3dcb5fe71f08c51ec76d\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"7e1d6481a91f3dcb5fe71f08c51ec76d\",\"publish_time\":\"2020-09-28 01:08:56\",\"sourcewebsitename\":\"趣头条\",\"title\":\"打响反美第一枪？一枚炸弹在首都引爆，美方3名美中情局人员丧生\",\"similarvolume\":\"0\"},\"sort\":[1601255336000]}]}}},{\"key\":\"巩俐国际电影节全程中文演讲，登台第一句话：我是中国演员巩俐\",\"doc_count\":481,\"top_score_hits\":{\"hits\":{\"total\":481,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"b4c24629df667f32d75eb7f0daf8f394\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"b4c24629df667f32d75eb7f0daf8f394\",\"publish_time\":\"2020-09-28 17:06:53\",\"sourcewebsitename\":\"趣头条\",\"title\":\"巩俐国际电影节全程中文演讲，登台第一句话：我是中国演员巩俐\",\"similarvolume\":\"0\"},\"sort\":[1601312813000]}]}}},{\"key\":\"邱淑贞曾携女儿出席酒会，21岁沈月穿仙女裙，获外国男子跪地示爱\",\"doc_count\":481,\"top_score_hits\":{\"hits\":{\"total\":481,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"26c9c2389dc34032ff7b3d685426ac6d\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"26c9c2389dc34032ff7b3d685426ac6d\",\"publish_time\":\"2020-09-28 16:57:19\",\"sourcewebsitename\":\"趣头条\",\"title\":\"邱淑贞曾携女儿出席酒会，21岁沈月穿仙女裙，获外国男子跪地示爱\",\"similarvolume\":\"0\"},\"sort\":[1601312239000]}]}}},{\"key\":\"平原一中远足活动第2集，前几个班级到达终点站，大家都很兴奋\",\"doc_count\":464,\"top_score_hits\":{\"hits\":{\"total\":464,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"75827080fa03625bc46c698eab5cb1f4\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"75827080fa03625bc46c698eab5cb1f4\",\"publish_time\":\"2020-09-29 16:07:51\",\"sourcewebsitename\":\"趣头条\",\"title\":\"平原一中远足活动第2集，前几个班级到达终点站，大家都很兴奋\",\"similarvolume\":\"0\"},\"sort\":[1601395671000]}]}}},{\"key\":\"印军用物资被倒卖？拉达克大量士兵冻伤，撤军迫在眉睫\",\"doc_count\":462,\"top_score_hits\":{\"hits\":{\"total\":462,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"5213970751720db0358958a679204aa4\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"5213970751720db0358958a679204aa4\",\"publish_time\":\"2020-09-29 15:46:33\",\"sourcewebsitename\":\"趣头条\",\"title\":\"印军用物资被倒卖？拉达克大量士兵冻伤，撤军迫在眉睫\",\"similarvolume\":\"0\"},\"sort\":[1601394393000]}]}}},{\"key\":\"2020年9月29日，平原一中高二学生远足纪实第1集，目的地张官店\",\"doc_count\":457,\"top_score_hits\":{\"hits\":{\"total\":457,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"7a8d80a5a58418a3837a20eaa76684f9\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"7a8d80a5a58418a3837a20eaa76684f9\",\"publish_time\":\"2020-09-29 16:05:51\",\"sourcewebsitename\":\"趣头条\",\"title\":\"2020年9月29日，平原一中高二学生远足纪实第1集，目的地张官店\",\"similarvolume\":\"0\"},\"sort\":[1601395551000]}]}}},{\"key\":\"王斯然跟GAI说以后要分房睡…\\n\\n王斯然：因为我要半夜喂奶\",\"doc_count\":452,\"top_score_hits\":{\"hits\":{\"total\":452,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"2ea477af6f02530de774ddefe4386aa1\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"2ea477af6f02530de774ddefe4386aa1\",\"publish_time\":\"2020-09-29 14:39:27\",\"sourcewebsitename\":\"趣头条\",\"title\":\"王斯然跟GAI说以后要分房睡…\\n\\n王斯然：因为我要半夜喂奶\",\"similarvolume\":\"0\"},\"sort\":[1601390367000]}]}}},{\"key\":\"你是学霸吗？4-6=1能成立？简约而不简单，能答对的人，智商很高\",\"doc_count\":442,\"top_score_hits\":{\"hits\":{\"total\":442,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"b21c66e6816342fe51890be0b7ebe10c\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"b21c66e6816342fe51890be0b7ebe10c\",\"publish_time\":\"2020-09-28 13:53:08\",\"sourcewebsitename\":\"趣头条\",\"title\":\"你是学霸吗？4-6=1能成立？简约而不简单，能答对的人，智商很高\",\"similarvolume\":\"0\"},\"sort\":[1601301188000]}]}}},{\"key\":\"走进车八岭\",\"doc_count\":439,\"top_score_hits\":{\"hits\":{\"total\":439,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"ddb13f1256d200f1a9d38e21e4571d05\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"ddb13f1256d200f1a9d38e21e4571d05\",\"publish_time\":\"2020-09-28 11:18:01\",\"sourcewebsitename\":\"趣头条\",\"title\":\"走进车八岭\",\"similarvolume\":\"0\"},\"sort\":[1601291881000]}]}}}]}}}";
//    	System.out.println(sendPost);
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	JSONArray jsonArray = parseObject.getJSONObject("aggregations").getJSONObject("top_score").getJSONArray("buckets");
    	int size = jsonArray.size();
    	if(size>10){
    		size =10;
    	}
    	JSONArray result = new JSONArray();
    	for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONObject jsonObject2 = jsonObject.getJSONObject("top_score_hits").getJSONObject("hits");
			JSONObject jsonObject3 = jsonObject2.getJSONArray("hits").getJSONObject(0).getJSONObject("_source");
			JSONObject ob = new JSONObject();
			ob.put("hot", jsonObject2.get("total"));
			ob.put("title", jsonObject3.get("title"));
			ob.put("author", jsonObject3.get("sourcewebsitename"));
			ob.put("publish_time", jsonObject3.get("publish_time"));
			result.add(ob);
		}
		return result;
	}

	private  String getpropagation_analysis(PublicoptionEntity publicoptionEntity) {
    	JSONObject result = new JSONObject();
    	//media
    	try {
    		JSONArray media = getPAmedia(publicoptionEntity);
    		result.put("media", media);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//source
    	try {
    		JSONObject source= getPAsource(publicoptionEntity);
    		result.put("source", source);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toJSONString();
	}

//	private  JSONObject getPAsource(PublicoptionEntity publicoptionEntity) {
//		JSONObject resault = new JSONObject();
//		// all 
//		JSONArray all= getdatasourcestatistics(publicoptionEntity);
//		return resault;
//	}

	private  JSONObject getPAsource(PublicoptionEntity publicoptionEntity) {
		// 
		JSONObject result = new JSONObject();
		String url = es_search_url + PublicoptionConstant.es_api_datasourcestatistics;
    	String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    	String eventendtime = checkString(publicoptionEntity.getEventendtime());
    	String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    	String eventstopwords = checkString(publicoptionEntity.getEventstopwords());
    	String searchwords = "";
    	
    	String params = "times=" + eventstarttime + "&timee=" + eventendtime + "&keyword=" + eventkeywords + "&stopword=" + eventstopwords
                + "&searchkeyword=" + searchwords + "&origintype=0&searchType=0&size=1&emotionalIndex=1,2,3&projecttype=2";
    	String sendPost = getEsRequset(url, params,null,null);
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	JSONArray jsonArray = parseObject.getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
    	JSONArray clinetlist = new JSONArray();
    	JSONArray websitelist = new JSONArray();
    	JSONArray BBSlist = new JSONArray();
    	JSONArray zflist = new JSONArray();
    	JSONArray weibolist = new JSONArray();
    	JSONArray wechatlist = new JSONArray();
    	for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Integer integer = jsonObject.getInteger("key");
			JSONArray jsonArray2 = jsonObject.getJSONObject("group_by_tags").getJSONArray("buckets");
			switch (integer) {
			case 7:
				//clinet;
				clinetlist.addAll(jsonArray2);
				break;
			case 8:
				//website;
				websitelist.addAll(jsonArray2);
				break;
			case 4:
				//BBS zf;
//				BBSlist.addAll(jsonArray2);
				zflist.addAll(jsonArray2);
				break;
			case 2:
				//weibo;
				weibolist.addAll(jsonArray2);
				break;
			case 1:
				//wechat;
				wechatlist.addAll(jsonArray2);
				break;
			case 3:
				//zf;
				zflist.addAll(jsonArray2);
				break;
			}
		}
    	JSONArray zfarray = cleanarray(zflist,"政府网站");
    	result.put("BBS", zfarray);
    	JSONArray clinetarray = cleanarray(clinetlist,"客户端");
    	result.put("clinet", clinetarray);
    	JSONArray websitearray = cleanarray(websitelist,"网站");
    	result.put("website", websitearray);
    	JSONArray weiboarrray = cleanarray(weibolist,"微博");
    	result.put("weibo", weiboarrray);
    	JSONArray wechatarrray = cleanarray(wechatlist,"微信");
    	result.put("wechat", wechatarrray);
    	JSONArray all = new JSONArray();
    	all.addAll(zfarray);
    	all.addAll(clinetarray);
    	all.addAll(websitearray);
    	all.addAll(weiboarrray);
    	all.addAll(wechatarrray);
    	all = sortProxyAndCdn(all);
    	int size = all.size();
    	if(size>10){
    		size = 10;
    	}
    	JSONArray alllist = new JSONArray();
    	for (int i = 0; i < size; i++) {
			alllist.add(all.getJSONObject(i));
		}
    	result.put("all", alllist);
		return result;
	}

	private  JSONArray cleanarray(JSONArray zflist, String media_type) {
		JSONArray result = new JSONArray();
		JSONArray result1 = new JSONArray();
		for (int j = 0; j < zflist.size(); j++) {
			JSONObject ob = new JSONObject();
			JSONObject jsonObject = zflist.getJSONObject(j);
			ob.put("name", jsonObject.get("key"));
			ob.put("media_type", media_type);
			ob.put("total", jsonObject.get("doc_count"));
			int negative =0;
			JSONArray jsonArray = jsonObject.getJSONObject("top-terms-emotion").getJSONArray("buckets");
			for (int k = 0; k <jsonArray.size(); k++) {
				JSONObject jsonObject2 = jsonArray.getJSONObject(k);
				if(3== jsonObject2.getInteger("key")){
					negative = jsonObject2.getIntValue("doc_count");
				}
			}
			ob.put("negative", negative);
			result.add(ob);
		}
		
		if(media_type.equals("政府网站")){
			JSONArray newlist = new JSONArray();
			for (int i = 0; i < result.size(); i++) {
				JSONObject jsonObject = result.getJSONObject(i);
				String name = jsonObject.getString("name");
				if(!(null != name && ("百度贴吧".equals(name)||"知乎".equals(name)))){
					newlist.add(jsonObject);
				}
			}
			result = sortProxyAndCdn(newlist);
		}
		int size = result.size();
		if(size >10){
			size = 10;
		}
		for (int i = 0; i < size; i++) {
			result1.add(result.get(i));
		}
		System.out.println(result1);
		return result1;
	}
	
	
	 private  JSONArray sortProxyAndCdn(JSONArray bindArrayResult) {
	      List<JSONObject> list = JSONArray.parseArray(bindArrayResult.toJSONString(), JSONObject.class);
	      Collections.sort(list, new Comparator<JSONObject>() {
	          @Override
	          public int compare(JSONObject o1, JSONObject o2) {
	              int a = o1.getString("total").length();
	              int b = o2.getString("total").length();
	              if (a > b) {
	                  return -1;
	              } else if(a == b) {
	                  return 0;
	              } else
	                  return 1;
	              }
	      });
	      JSONArray jsonArray = JSONArray.parseArray(list.toString());
	      return jsonArray;
	    }

	private  JSONArray getPAmedia(PublicoptionEntity publicoptionEntity) {
		JSONArray result = new JSONArray();
		String url = es_search_url + PublicoptionConstant.es_api_medialist;
		String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    	String eventendtime = checkString(publicoptionEntity.getEventendtime());
    	String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    	String searchwords = "";
    	String params = "times=" + eventstarttime + "&timee=" + eventendtime + "&keyword=" + eventkeywords + "&stopword=&searchkeyword=" + searchwords + "&origintype=0&emotionalIndex=1,2,3&projecttype=2&size=12";
    	String sendPost = getEsRequset(url, params,null,null);
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	JSONArray jsonArray = parseObject.getJSONObject("aggregations").getJSONObject("top-terms-aggregation").getJSONArray("buckets");
    	int count = 0;
    	for (int i = 0; i < jsonArray.size(); i++) {
    		JSONObject jsonObject = jsonArray.getJSONObject(i);
    		try {
    			String string = jsonObject.getString("key");
    			if(count < 10 && null != string && !"".equals(string.trim()) ){
    				count ++;
    				JSONArray jsonArray2 = jsonObject.getJSONObject("top-terms-emotion").getJSONArray("buckets");
    				String source = "";
    				for (int j = 0; j < jsonArray2.size(); j++) {
						JSONObject jsonObject2 = jsonArray2.getJSONObject(j);
						String string2 = jsonObject2.getString("key");
						if(null != string2 && !"".equals(string2)){
							source = string2;
							break;
						}
					}
    				
    		    	String resultStr = getEsRequset(es_hot_search_url + PublicoptionConstant.es_api_media_medialist, "name="+string,null,null);
    		    	JSONObject parseObject2 = JSONObject.parseObject(resultStr);
    		    	JSONArray jsonArray3 = parseObject2.getJSONArray("data");
    		    	JSONObject wemadie = new JSONObject();
    		    	for (int j = 0; j < jsonArray3.size(); j++) {
						JSONObject jsonObject2 = jsonArray3.getJSONObject(j).getJSONObject("_source");
						if(j== 0){
							wemadie = jsonObject2;
						}
						Integer integer = jsonObject2.getInteger("platform_id");
						String platform_name = jsonObject2.getString("platform_name");
						if(null != integer && null == platform_name){
							switch(integer){
				               case 1:jsonObject2.put("platform_name", "汽车之家"); break;
				               case 2:jsonObject2.put("platform_name", "人民号"); break;
				               case 3:jsonObject2.put("platform_name", "携程"); break;
				               case 4:jsonObject2.put("platform_name", "东方头条"); break;
				               case 5:jsonObject2.put("platform_name", "百家号"); break;
				               case 6:jsonObject2.put("platform_name", "大风号"); break;
				               case 7:jsonObject2.put("platform_name", "同花顺号"); break;
				               case 8:jsonObject2.put("platform_name", "云掌号"); break;
				               case 9:jsonObject2.put("platform_name", "中金在线"); break;
				               case 10:jsonObject2.put("platform_name", "快传号"); break;
				               case 11:jsonObject2.put("platform_name", "企鹅号"); break;
				               case 12:jsonObject2.put("platform_name", "雪球"); break;
				               case 13:jsonObject2.put("platform_name", "网易号"); break;
				               case 14:jsonObject2.put("platform_name", "今日头条"); break;
				               case 15:jsonObject2.put("platform_name", "搜狐号"); break;
				               case 16:jsonObject2.put("platform_name", "新浪看点"); break;
				               case 17:jsonObject2.put("platform_name", "天天快报"); break;
				               case 18:jsonObject2.put("platform_name", "财富号"); break;
				               case 19:jsonObject2.put("platform_name", "大众点评"); break;
				               case 20:jsonObject2.put("platform_name", "小红书"); break;
				               case 21:jsonObject2.put("platform_name", "58本地版"); break;
				               case 22:jsonObject2.put("platform_name", "大鱼号"); break;
				               case 23:jsonObject2.put("platform_name", "腾讯新闻"); break;
				               case 24:jsonObject2.put("platform_name", "西瓜视频"); break;
				               case 25:jsonObject2.put("platform_name", "趣头条"); break;
				               case 26:jsonObject2.put("platform_name", "看点快报"); break;
				               case 27:jsonObject2.put("platform_name", "扬子扬眼"); break;
				               case 28:jsonObject2.put("platform_name", "新华财经传媒"); break;
				               case 29:jsonObject2.put("platform_name", "掌上南通"); break;
				               case 30:jsonObject2.put("platform_name", "扬州发布"); break;
				               case 31:jsonObject2.put("platform_name", "宽频中吴"); break;
				               case 32:jsonObject2.put("platform_name", "交汇点新闻"); break;
				               case 33:jsonObject2.put("platform_name", "常州手机台"); break;
				               case 34:jsonObject2.put("platform_name", "Zaker"); break;
				               case 35:jsonObject2.put("platform_name", "东财股吧"); break;

						}
						}
						platform_name = jsonObject2.getString("platform_name");
						if(null !=platform_name && platform_name.equals(source)){
							wemadie = jsonObject2;
							break;
						}
					}
    		    	JSONObject ob = new JSONObject();
    		    	ob.put("name", jsonObject.get("key"));
    		    	ob.put("abstract", wemadie.get("slogan").toString().replaceAll("\n\r", ""));
    		    	ob.put("logo", wemadie.get("logo"));
    		    	ob.put("source_name", wemadie.get("platform_name"));
    		    	ob.put("fans", wemadie.get("focus_count"));
    		    	ob.put("publishs", jsonObject.get("doc_count"));
    		    	result.add(ob);
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
			}
    		// /media/wemedialist
		}
//		System.out.println(sendPost);
		return result;
	}

	private  String getstatistics(PublicoptionEntity publicoptionEntity) {
    	//website
    	JSONObject result = new JSONObject();
    	try {
    		JSONArray website= getSwebsite(publicoptionEntity);
    		result.put("website", website);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	//weibo
    	try {
    		JSONArray weibo = getAuthorstatistics(publicoptionEntity,2);
    		result.put("weibo", weibo);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//social_media
    	try {
    		JSONArray social_media = getAuthorstatistics(publicoptionEntity,4);
    		result.put("social_media", social_media);
		} catch (Exception e) {
			e.printStackTrace();
		}
//    	//wemedia
    	try {
    		JSONArray wemedia = getAuthorstatistics(publicoptionEntity,null);
    		result.put("wemedia", wemedia);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result.toJSONString();
	}

	private  JSONArray getAuthorstatistics(PublicoptionEntity publicoptionEntity,Integer classify) {
		String url = es_search_url + PublicoptionConstant.es_api_authorstatistics;
		String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    	String eventendtime = checkString(publicoptionEntity.getEventendtime());
    	String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    	String eventstopwords = checkString(publicoptionEntity.getEventstopwords());
    	String searchwords = "";
    	String params = "times=" + eventstarttime + "&timee=" + eventendtime + "&keyword=" + eventkeywords + "&stopword=" + eventstopwords
                + "&searchkeyword=" + searchwords + "&origintype=0&emotionalIndex=1,2,3&projecttype=2&classify="+classify;
    	String sendPost = getEsRequset(url, params,null,null);
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	JSONArray jsonArray = parseObject.getJSONObject("aggregations").getJSONObject("top-terms-author").getJSONArray("buckets");
    	int size = jsonArray.size();
    	JSONArray result = new JSONArray();
    	for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if(null !=jsonObject.get("key") && !"".equals(jsonObject.get("key").toString().trim())){
				JSONObject ob = new JSONObject();
				ob.put("name", jsonObject.get("key"));
				ob.put("value", jsonObject.get("doc_count"));
				result.add(ob);
				if(result.size()==10){
					return result;
				}
			}
		}
		return result;
	}

	private  JSONArray getSwebsite(PublicoptionEntity publicoptionEntity) {
		JSONArray result = new JSONArray();
    	String sendPost = datasourceanalysis(publicoptionEntity,"1,2,3");
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	JSONArray jsonArray = parseObject.getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
    	int size = jsonArray.size();
    	if(size>10){
    		size = 10;
    	}
    	for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONObject ob = new JSONObject();
			ob.put("name", jsonObject.get("key"));
			ob.put("value", jsonObject.get("doc_count"));
			result.add(ob);
		}
		return result;
	}

	private   String getnetizens_analysis(PublicoptionEntity publicoptionEntity) {
    	JSONObject result = new JSONObject();
    	//objecat 重点对象事件
    	try {
    		JSONArray object = getNAobject(publicoptionEntity);
    		result.put("object", object);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//figure 重点人（重要网民）
    	try {
    		JSONArray figure = getNAfigure(publicoptionEntity);
    		result.put("figure", figure);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//relation 重点网民关系
    	try {
    		JSONObject relation = getNArelation(publicoptionEntity);
    		result.put("relation", relation);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toJSONString();
    	
    	
	}

	private  JSONArray getNAfigure(PublicoptionEntity publicoptionEntity) {
		String url = es_search_url + PublicoptionConstant.es_api_HotPeoplestatistics;
		String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    	String eventendtime = checkString(publicoptionEntity.getEventendtime());
    	String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    	String eventstopwords = checkString(publicoptionEntity.getEventstopwords());
    	String searchwords = "";
    	String params = "times=" + eventstarttime + "&timee=" + eventendtime + "&keyword=" + eventkeywords + "&stopword=" + eventstopwords
                + "&searchkeyword=" + searchwords + "&origintype=0&emotionalIndex=1,2,3&projecttype=2&classify=2";
    	String sendPost = getEsRequset(url, params,null,null);
    	JSONArray jsonArray = JSONObject.parseObject(sendPost).getJSONObject("aggregations").getJSONObject("top-terms-author").getJSONArray("buckets");
    	int size = jsonArray.size();
    	JSONArray result = new JSONArray();
    	for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if(null !=jsonObject.get("key") && !"".equals(jsonObject.get("key").toString().trim())){
				JSONObject ob = new JSONObject();
				ob.put("name", jsonObject.get("key"));
				ob.put("value", jsonObject.get("doc_count"));
				JSONObject jsonObject2 = jsonObject.getJSONObject("top_score_hits").getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source");
				String author_url = jsonObject2.getString("author_url");
				ob.put("author_url",author_url);
				ob.put("content", jsonObject2.getString("content"));
				result.add(ob);
				if(result.size()==10){
					return result;
				}
			}
		}
		return result;
	}

	private  JSONObject getNArelation(PublicoptionEntity publicoptionEntity) {
		String url = es_search_url + PublicoptionConstant.es_yq_qbsearchcontent;
		String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    	String eventendtime = checkString(publicoptionEntity.getEventendtime());
//    	String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    	String eventstopwords = checkString(publicoptionEntity.getEventstopwords());
    	String searchwords = "";
//    	eventkeywords = "海南司法";
    	String params = "times=" + eventstarttime + "&timee=" + eventendtime + "&keyword=" + similarityMaxTitle + "&stopword=" + eventstopwords
                + "&searchkeyword=" + searchwords + "&origintype=0&emotionalIndex=1,2,3&projecttype=2";
    	String sendPost = getEsRequset(url, params,null,null);
    	System.out.println(sendPost);
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	JSONArray jsonArray = parseObject.getJSONArray("data");
    	JSONArray data = new JSONArray();
    	JSONArray links = new JSONArray();
    	JSONObject jsonObject = new JSONObject();
    	jsonObject.put("name", similarityMaxTitle);
    	jsonObject.put("des", similarityMaxTitle);
    	jsonObject.put("symbolSize", 110);
    	jsonObject.put("category", 0);
    	
    	data.add(jsonObject);
    	List<String> names = new ArrayList<String>();
    	for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject2 = jsonArray.getJSONObject(i).getJSONObject("_source");
			Object object = jsonObject2.get("author");
			String name = "";
			if(null != object && !"".equals(object.toString().trim())){
				name = object.toString();
				
			}else{
				name = jsonObject2.get("source_name").toString();
			}
			boolean addflag = true;
			for (String string : names) {
				if(string.equals(name)){
					addflag = false;
				}
			}
			if(addflag){
				names.add(name);
				JSONObject ob = new JSONObject();
				Object heatvolumeOb = jsonObject2.get("heatvolume");
				int symbolSize = 8;
				if(heatvolumeOb != null && !"".equals(heatvolumeOb.toString())){
					String string2 = heatvolumeOb.toString();
					String[] split = string2.split("\\.");
					symbolSize = Integer.parseInt(split[0]);
				}
				ob.put("name", name);
				ob.put("des", name);
				ob.put("symbolSize", symbolSize);
				ob.put("category", 1);
				data.add(ob);
				JSONObject linkOb = new JSONObject();
				linkOb.put("source", similarityMaxTitle);
				linkOb.put("target", name);
				linkOb.put("name", "");
				linkOb.put("des", "");
				links.add(linkOb);
			}
		}
    	JSONObject jsonObject2 = new JSONObject();
    	jsonObject2.put("data", data);
    	jsonObject2.put("links", links);
    	return jsonObject2;
	}

	private  JSONArray getNAobject(PublicoptionEntity publicoptionEntity) {
		
    	String url = es_search_url + PublicoptionConstant.es_api_qbsearchcontent;
		String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    	String eventendtime = checkString(publicoptionEntity.getEventendtime());
    	String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    	String eventstopwords = checkString(publicoptionEntity.getEventstopwords());
    	String searchwords = "";
//    	eventkeywords = "海南司法";
    	String params = "times=" + eventstarttime + "&timee=" + eventendtime + "&keyword=" + eventkeywords + "&stopword=" + eventstopwords
                + "&searchkeyword=" + searchwords + "&origintype=0&emotionalIndex=1,2,3&projecttype=2";
    	String sendPost = getEsRequset(url, params,null,null);
//    	String sendPost =	    	"{\"took\":9797,\"timed_out\":false,\"_shards\":{\"total\":5,\"successful\":5,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":502734,\"max_score\":0.0,\"hits\":[]},\"aggregations\":{\"top_score\":{\"doc_count_error_upper_bound\":202,\"sum_other_doc_count\":464917,\"buckets\":[{\"key\":\"2020北京车展现场直击，宝马THE5\",\"doc_count\":1057,\"top_score_hits\":{\"hits\":{\"total\":1057,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"5706236d7f8b650abbed3191d519499f\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"5706236d7f8b650abbed3191d519499f\",\"publish_time\":\"2020-09-28 19:34:03\",\"sourcewebsitename\":\"趣头条\",\"title\":\"2020北京车展现场直击，宝马THE5\",\"similarvolume\":\"0\"},\"sort\":[1601321643000]}]}}},{\"key\":\"卖猪肉的大哥刀法不错，切猪肉跟切菜一样，真想问问刀在哪买的！\",\"doc_count\":1056,\"top_score_hits\":{\"hits\":{\"total\":1056,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"e875525d20b9d0628e2269b3fd70ad78\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"e875525d20b9d0628e2269b3fd70ad78\",\"publish_time\":\"2020-09-28 17:01:18\",\"sourcewebsitename\":\"趣头条\",\"title\":\"卖猪肉的大哥刀法不错，切猪肉跟切菜一样，真想问问刀在哪买的！\",\"similarvolume\":\"0\"},\"sort\":[1601312478000]}]}}},{\"key\":\"美女球迷说中超，为主队花样打CALL\",\"doc_count\":1056,\"top_score_hits\":{\"hits\":{\"total\":1056,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"f64a486df3659d9f3e2ffcd88f9c1d35\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"f64a486df3659d9f3e2ffcd88f9c1d35\",\"publish_time\":\"2020-09-28 17:15:57\",\"sourcewebsitename\":\"趣头条\",\"title\":\"美女球迷说中超，为主队花样打CALL\",\"similarvolume\":\"0\"},\"sort\":[1601313357000]}]}}},{\"key\":\"前方记者亲历球迷入场看球的盛况\",\"doc_count\":1054,\"top_score_hits\":{\"hits\":{\"total\":1054,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"e4f5b8f131cdab575e9357ea09f28077\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"e4f5b8f131cdab575e9357ea09f28077\",\"publish_time\":\"2020-09-28 17:16:11\",\"sourcewebsitename\":\"趣头条\",\"title\":\"前方记者亲历球迷入场看球的盛况\",\"similarvolume\":\"0\"},\"sort\":[1601313371000]}]}}},{\"key\":\"2020北京车展现场直击，大众高尔夫8\",\"doc_count\":1047,\"top_score_hits\":{\"hits\":{\"total\":1047,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"4ac428b4b3412843c23df1d4d206a19a\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"4ac428b4b3412843c23df1d4d206a19a\",\"publish_time\":\"2020-09-28 17:42:08\",\"sourcewebsitename\":\"趣头条\",\"title\":\"2020北京车展现场直击，大众高尔夫8\",\"similarvolume\":\"0\"},\"sort\":[1601314928000]}]}}},{\"key\":\"高合HiPhix\",\"doc_count\":1047,\"top_score_hits\":{\"hits\":{\"total\":1047,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"f1c7a37124e2f0a24bd60d94bd468e35\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"f1c7a37124e2f0a24bd60d94bd468e35\",\"publish_time\":\"2020-09-28 15:36:53\",\"sourcewebsitename\":\"趣头条\",\"title\":\"高合HiPhix\",\"similarvolume\":\"0\"},\"sort\":[1601307413000]}]}}},{\"key\":\"黄健翔分析本赛季中超赛场上本土球员的成长\",\"doc_count\":1047,\"top_score_hits\":{\"hits\":{\"total\":1047,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"3cca85e43e54c0edeb9ed3ddceb86df9\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"3cca85e43e54c0edeb9ed3ddceb86df9\",\"publish_time\":\"2020-09-28 17:15:56\",\"sourcewebsitename\":\"趣头条\",\"title\":\"黄健翔分析本赛季中超赛场上本土球员的成长\",\"similarvolume\":\"0\"},\"sort\":[1601313356000]}]}}},{\"key\":\"2020北京车展现场直击，名爵MG5\",\"doc_count\":1044,\"top_score_hits\":{\"hits\":{\"total\":1044,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"fd612bc8aa2c4a336713b9b7eaa39a55\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"fd612bc8aa2c4a336713b9b7eaa39a55\",\"publish_time\":\"2020-09-28 19:58:32\",\"sourcewebsitename\":\"趣头条\",\"title\":\"2020北京车展现场直击，名爵MG5\",\"similarvolume\":\"0\"},\"sort\":[1601323112000]}]}}},{\"key\":\"2020北京车展现场直击，荣威IMAX8\",\"doc_count\":1043,\"top_score_hits\":{\"hits\":{\"total\":1043,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"778f55c9fa41ca38fd64b747f479abb7\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"778f55c9fa41ca38fd64b747f479abb7\",\"publish_time\":\"2020-09-28 19:02:10\",\"sourcewebsitename\":\"趣头条\",\"title\":\"2020北京车展现场直击，荣威IMAX8\",\"similarvolume\":\"0\"},\"sort\":[1601319730000]}]}}},{\"key\":\"2020北京车展现场直击，日产Ariya\",\"doc_count\":1042,\"top_score_hits\":{\"hits\":{\"total\":1042,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"babfca46cbf1b11d1be355e5e02090dc\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"babfca46cbf1b11d1be355e5e02090dc\",\"publish_time\":\"2020-09-28 18:36:32\",\"sourcewebsitename\":\"趣头条\",\"title\":\"2020北京车展现场直击，日产Ariya\",\"similarvolume\":\"0\"},\"sort\":[1601318192000]}]}}},{\"key\":\"一旦爆发核战争，老百姓只能等死？非也，这三个地方可保小命！\",\"doc_count\":1035,\"top_score_hits\":{\"hits\":{\"total\":1035,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"38938e488a93c7782e81bcdbf260e057\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"38938e488a93c7782e81bcdbf260e057\",\"publish_time\":\"2020-09-28 20:25:58\",\"sourcewebsitename\":\"趣头条\",\"title\":\"一旦爆发核战争，老百姓只能等死？非也，这三个地方可保小命！\",\"similarvolume\":\"0\"},\"sort\":[1601324758000]}]}}},{\"key\":\"北京车展现场直击：奔驰宝马大众，这些新车都凑齐了\",\"doc_count\":1011,\"top_score_hits\":{\"hits\":{\"total\":1011,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"b71d0529ed7049118187561a31c649e\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"b71d0529ed7049118187561a31c649e\",\"publish_time\":\"2020-09-29 12:40:41\",\"sourcewebsitename\":\"趣头条\",\"title\":\"北京车展现场直击：奔驰宝马大众，这些新车都凑齐了\",\"similarvolume\":\"0\"},\"sort\":[1601383241000]}]}}},{\"key\":\"鼻子不堵了！两味药，冲散鼻中痰湿，打开鼻窍\",\"doc_count\":1011,\"top_score_hits\":{\"hits\":{\"total\":1011,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"d464514505fad3848d5734c43e1b9af7\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"d464514505fad3848d5734c43e1b9af7\",\"publish_time\":\"2020-09-29 09:20:49\",\"sourcewebsitename\":\"趣头条\",\"title\":\"鼻子不堵了！两味药，冲散鼻中痰湿，打开鼻窍\",\"similarvolume\":\"0\"},\"sort\":[1601371249000]}]}}},{\"key\":\"乳腺癌、肺癌转移了，不一定是“判死刑”！医生告诉你还能活多久\",\"doc_count\":994,\"top_score_hits\":{\"hits\":{\"total\":994,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"7d568db54b7e705b21585df51c6c48f8\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"7d568db54b7e705b21585df51c6c48f8\",\"publish_time\":\"2020-09-29 14:53:10\",\"sourcewebsitename\":\"趣头条\",\"title\":\"乳腺癌、肺癌转移了，不一定是“判死刑”！医生告诉你还能活多久\",\"similarvolume\":\"0\"},\"sort\":[1601391190000]}]}}},{\"key\":\"如何自己判断是否是下肢动脉硬化闭塞症？\",\"doc_count\":994,\"top_score_hits\":{\"hits\":{\"total\":994,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"347663990f582b8c6a6b5a48dc656e10\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"347663990f582b8c6a6b5a48dc656e10\",\"publish_time\":\"2020-09-29 14:19:13\",\"sourcewebsitename\":\"趣头条\",\"title\":\"如何自己判断是否是下肢动脉硬化闭塞症？\",\"similarvolume\":\"0\"},\"sort\":[1601389153000]}]}}},{\"key\":\"脸上莫名其妙长斑，一检查发现，都是因为平时太懒了\",\"doc_count\":993,\"top_score_hits\":{\"hits\":{\"total\":993,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"5e55e5bf811f74c762d719c2d0f7e063\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"5e55e5bf811f74c762d719c2d0f7e063\",\"publish_time\":\"2020-09-29 10:13:08\",\"sourcewebsitename\":\"趣头条\",\"title\":\"脸上莫名其妙长斑，一检查发现，都是因为平时太懒了\",\"similarvolume\":\"0\"},\"sort\":[1601374388000]}]}}},{\"key\":\"造成月经不调的习惯有哪些？多与这4种不良习惯有关，赶紧戒掉\",\"doc_count\":984,\"top_score_hits\":{\"hits\":{\"total\":984,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"e5d522c033607bc6652dce9955a2efcc\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"e5d522c033607bc6652dce9955a2efcc\",\"publish_time\":\"2020-09-29 10:00:02\",\"sourcewebsitename\":\"趣头条\",\"title\":\"造成月经不调的习惯有哪些？多与这4种不良习惯有关，赶紧戒掉\",\"similarvolume\":\"0\"},\"sort\":[1601373602000]}]}}},{\"key\":\"颈椎病的康复治疗有哪些？\",\"doc_count\":942,\"top_score_hits\":{\"hits\":{\"total\":942,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"291b8b7c92b516a94bbe2e7a1a27efd1\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"291b8b7c92b516a94bbe2e7a1a27efd1\",\"publish_time\":\"2020-09-28 18:17:06\",\"sourcewebsitename\":\"趣头条\",\"title\":\"颈椎病的康复治疗有哪些？\",\"similarvolume\":\"0\"},\"sort\":[1601317026000]}]}}},{\"key\":\"星座小知识：十二星座不得不忍受的缺点，如何应对摩羯座的冷漠\",\"doc_count\":852,\"top_score_hits\":{\"hits\":{\"total\":852,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"3cd91bdee46a5f9bff34f780ea73b0aa\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"3cd91bdee46a5f9bff34f780ea73b0aa\",\"publish_time\":\"2020-09-29 13:45:04\",\"sourcewebsitename\":\"趣头条\",\"title\":\"星座小知识：十二星座不得不忍受的缺点，如何应对摩羯座的冷漠\",\"similarvolume\":\"0\"},\"sort\":[1601387104000]}]}}},{\"key\":\"刘建宏前瞻天津泰达、河南建业和青岛黄海的保级前景\",\"doc_count\":841,\"top_score_hits\":{\"hits\":{\"total\":841,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"9de7b815edd825a85d2586e46f6d67cd\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"9de7b815edd825a85d2586e46f6d67cd\",\"publish_time\":\"2020-09-28 17:15:47\",\"sourcewebsitename\":\"趣头条\",\"title\":\"刘建宏前瞻天津泰达、河南建业和青岛黄海的保级前景\",\"similarvolume\":\"0\"},\"sort\":[1601313347000]}]}}},{\"key\":\"归化球员选择范围扩大，国家队可以灵活选用\",\"doc_count\":836,\"top_score_hits\":{\"hits\":{\"total\":836,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"93753dce5acdab5563ebccdb26795752\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"93753dce5acdab5563ebccdb26795752\",\"publish_time\":\"2020-09-28 17:15:37\",\"sourcewebsitename\":\"趣头条\",\"title\":\"归化球员选择范围扩大，国家队可以灵活选用\",\"similarvolume\":\"0\"},\"sort\":[1601313337000]}]}}},{\"key\":\"为什么手腕突然疼痛用不上力气？有这4个可能，你是哪个\",\"doc_count\":835,\"top_score_hits\":{\"hits\":{\"total\":835,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"5177deaf00f5963015386e71619b9a1e\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"5177deaf00f5963015386e71619b9a1e\",\"publish_time\":\"2020-09-28 17:58:39\",\"sourcewebsitename\":\"趣头条\",\"title\":\"为什么手腕突然疼痛用不上力气？有这4个可能，你是哪个\",\"similarvolume\":\"0\"},\"sort\":[1601315919000]}]}}},{\"key\":\"受用一生的十句话，你一定要看\",\"doc_count\":757,\"top_score_hits\":{\"hits\":{\"total\":757,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"facf14c6fddf3edf19bfed2d93ee59f8\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"facf14c6fddf3edf19bfed2d93ee59f8\",\"publish_time\":\"2020-09-28 21:57:32\",\"sourcewebsitename\":\"趣头条\",\"title\":\"受用一生的十句话，你一定要看\",\"similarvolume\":\"0\"},\"sort\":[1601330252000]}]}}},{\"key\":\"你想知道的2021年初级会计实务学习方法\",\"doc_count\":747,\"top_score_hits\":{\"hits\":{\"total\":747,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"f221cf71c1ef79351c75895f7127660\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"f221cf71c1ef79351c75895f7127660\",\"publish_time\":\"2020-09-29 13:32:43\",\"sourcewebsitename\":\"趣头条\",\"title\":\"你想知道的2021年初级会计实务学习方法\",\"similarvolume\":\"0\"},\"sort\":[1601386363000]}]}}},{\"key\":\"扁平足没有及时矫正，膝盖、脊柱都受罪！医生教你预防的动作\",\"doc_count\":719,\"top_score_hits\":{\"hits\":{\"total\":719,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"a58d3065abe0431742aa8af03021066f\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"a58d3065abe0431742aa8af03021066f\",\"publish_time\":\"2020-09-28 15:27:47\",\"sourcewebsitename\":\"趣头条\",\"title\":\"扁平足没有及时矫正，膝盖、脊柱都受罪！医生教你预防的动作\",\"similarvolume\":\"0\"},\"sort\":[1601306867000]}]}}},{\"key\":\"放化疗期间，病人怎么补充营养？要忌口吗？医生详细解答\",\"doc_count\":677,\"top_score_hits\":{\"hits\":{\"total\":677,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"faa0f4395b15baf5d4513c407f43a35c\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"faa0f4395b15baf5d4513c407f43a35c\",\"publish_time\":\"2020-09-28 15:25:27\",\"sourcewebsitename\":\"趣头条\",\"title\":\"放化疗期间，病人怎么补充营养？要忌口吗？医生详细解答\",\"similarvolume\":\"0\"},\"sort\":[1601306727000]}]}}},{\"key\":\"八旬老人突然腰背痛，却查出肠癌！怎样区分脊柱转移瘤和腰椎病？\",\"doc_count\":669,\"top_score_hits\":{\"hits\":{\"total\":669,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"f3a1d7878635ae4f21c31a7c27279222\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"f3a1d7878635ae4f21c31a7c27279222\",\"publish_time\":\"2020-09-28 15:25:50\",\"sourcewebsitename\":\"趣头条\",\"title\":\"八旬老人突然腰背痛，却查出肠癌！怎样区分脊柱转移瘤和腰椎病？\",\"similarvolume\":\"0\"},\"sort\":[1601306750000]}]}}},{\"key\":\"孩子希望你能理解爸爸的一番苦心，加油学习\",\"doc_count\":663,\"top_score_hits\":{\"hits\":{\"total\":663,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"df94c23c3fff0935af375828b67b5ddd\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"df94c23c3fff0935af375828b67b5ddd\",\"publish_time\":\"2020-09-28 17:17:19\",\"sourcewebsitename\":\"趣头条\",\"title\":\"孩子希望你能理解爸爸的一番苦心，加油学习\",\"similarvolume\":\"0\"},\"sort\":[1601313439000]}]}}},{\"key\":\"惠威D1100蓝牙有源音响试听！\",\"doc_count\":663,\"top_score_hits\":{\"hits\":{\"total\":663,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"4084f78f2d75605c81091f0f88a43920\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"4084f78f2d75605c81091f0f88a43920\",\"publish_time\":\"2020-09-28 16:00:44\",\"sourcewebsitename\":\"趣头条\",\"title\":\"惠威D1100蓝牙有源音响试听！\",\"similarvolume\":\"0\"},\"sort\":[1601308844000]}]}}},{\"key\":\"孩子有扁平足，平时没有症状，能正常运动吗？医生的话让人放心\",\"doc_count\":661,\"top_score_hits\":{\"hits\":{\"total\":661,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"cb4ea54faff22c568ab21e06e6a8ffc6\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"cb4ea54faff22c568ab21e06e6a8ffc6\",\"publish_time\":\"2020-09-29 14:51:34\",\"sourcewebsitename\":\"趣头条\",\"title\":\"孩子有扁平足，平时没有症状，能正常运动吗？医生的话让人放心\",\"similarvolume\":\"0\"},\"sort\":[1601391094000]}]}}},{\"key\":\"儿童剧：杜子腾考100分第一名有奖状，考40分倒数第一名也有奖状\",\"doc_count\":634,\"top_score_hits\":{\"hits\":{\"total\":634,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"f4eb5f7113af8d90fbc796a7f7f7589d\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"f4eb5f7113af8d90fbc796a7f7f7589d\",\"publish_time\":\"2020-09-29 12:11:20\",\"sourcewebsitename\":\"趣头条\",\"title\":\"儿童剧：杜子腾考100分第一名有奖状，考40分倒数第一名也有奖状\",\"similarvolume\":\"0\"},\"sort\":[1601381480000]}]}}},{\"key\":\"海螺怎么吃？教你海边特色吃法，做法超简单，又一道中秋节硬菜！\",\"doc_count\":606,\"top_score_hits\":{\"hits\":{\"total\":606,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"42b74e747f024b67fbd7d0e52c7ddfb0\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"42b74e747f024b67fbd7d0e52c7ddfb0\",\"publish_time\":\"2020-09-29 10:40:40\",\"sourcewebsitename\":\"趣头条\",\"title\":\"海螺怎么吃？教你海边特色吃法，做法超简单，又一道中秋节硬菜！\",\"similarvolume\":\"0\"},\"sort\":[1601376040000]}]}}},{\"key\":\"汪峰到底多有钱？章子怡无意间说漏嘴，网友：贫穷限制想象\",\"doc_count\":578,\"top_score_hits\":{\"hits\":{\"total\":578,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"ebd9fddb5f33f7ea4ec0cc136c122813\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"ebd9fddb5f33f7ea4ec0cc136c122813\",\"publish_time\":\"2020-09-29 16:37:14\",\"sourcewebsitename\":\"趣头条\",\"title\":\"汪峰到底多有钱？章子怡无意间说漏嘴，网友：贫穷限制想象\",\"similarvolume\":\"0\"},\"sort\":[1601397434000]}]}}},{\"key\":\"小黄瓜不要炒着吃了，教你农村特色吃法，不腌咸菜不凉拌，特香！\",\"doc_count\":576,\"top_score_hits\":{\"hits\":{\"total\":576,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"8dd874e01108bde1fea3887fa7be4c00\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"8dd874e01108bde1fea3887fa7be4c00\",\"publish_time\":\"2020-09-28 10:23:28\",\"sourcewebsitename\":\"趣头条\",\"title\":\"小黄瓜不要炒着吃了，教你农村特色吃法，不腌咸菜不凉拌，特香！\",\"similarvolume\":\"0\"},\"sort\":[1601288608000]}]}}},{\"key\":\"来看看这次央视镜头下的女明星！杨紫唐嫣迪丽热巴颜值逆天\",\"doc_count\":575,\"top_score_hits\":{\"hits\":{\"total\":575,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"7360288c32ed73732e4c3171c6a987ce\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"7360288c32ed73732e4c3171c6a987ce\",\"publish_time\":\"2020-09-29 16:34:00\",\"sourcewebsitename\":\"趣头条\",\"title\":\"来看看这次央视镜头下的女明星！杨紫唐嫣迪丽热巴颜值逆天\",\"similarvolume\":\"0\"},\"sort\":[1601397240000]}]}}},{\"key\":\"大选已无悬念？最新民调出炉，65%美国人的态度说明一切\",\"doc_count\":567,\"top_score_hits\":{\"hits\":{\"total\":567,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"b7e268bd22bcb5f9bb9caa5407179a69\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"b7e268bd22bcb5f9bb9caa5407179a69\",\"publish_time\":\"2020-09-28 01:18:57\",\"sourcewebsitename\":\"趣头条\",\"title\":\"大选已无悬念？最新民调出炉，65%美国人的态度说明一切\",\"similarvolume\":\"0\"},\"sort\":[1601255937000]}]}}},{\"key\":\"边境气温骤降！印军大批山地部队冻伤，印媒：牛皮被彻底戳穿\",\"doc_count\":566,\"top_score_hits\":{\"hits\":{\"total\":566,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"d76805f8515d2a47c24abac5a6f14e85\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"d76805f8515d2a47c24abac5a6f14e85\",\"publish_time\":\"2020-09-28 01:08:58\",\"sourcewebsitename\":\"趣头条\",\"title\":\"边境气温骤降！印军大批山地部队冻伤，印媒：牛皮被彻底戳穿\",\"similarvolume\":\"0\"},\"sort\":[1601255338000]}]}}},{\"key\":\"高考报考规则：什么是提前批？到底要不要报考？\",\"doc_count\":560,\"top_score_hits\":{\"hits\":{\"total\":560,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"d38daccb93f641322dcb0af74eff460b\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"d38daccb93f641322dcb0af74eff460b\",\"publish_time\":\"2020-09-28 22:44:40\",\"sourcewebsitename\":\"趣头条\",\"title\":\"高考报考规则：什么是提前批？到底要不要报考？\",\"similarvolume\":\"0\"},\"sort\":[1601333080000]}]}}},{\"key\":\"如果您家的老人没事总爱去医院，可要小心认知障碍！\",\"doc_count\":541,\"top_score_hits\":{\"hits\":{\"total\":541,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"41498afd998e7ffa2861df86d82e384a\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"41498afd998e7ffa2861df86d82e384a\",\"publish_time\":\"2020-09-28 20:01:11\",\"sourcewebsitename\":\"58同镇\",\"title\":\"如果您家的老人没事总爱去医院，可要小心认知障碍！\",\"similarvolume\":\"2\"},\"sort\":[1601323271000]}]}}},{\"key\":\"你以为邓超陈赫鹿晗真的情比金坚？可不要小看了男明星之间的竞争\",\"doc_count\":529,\"top_score_hits\":{\"hits\":{\"total\":529,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"41b077c783016655dcb286a14d4414f4\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"41b077c783016655dcb286a14d4414f4\",\"publish_time\":\"2020-09-29 10:57:13\",\"sourcewebsitename\":\"趣头条\",\"title\":\"你以为邓超陈赫鹿晗真的情比金坚？可不要小看了男明星之间的竞争\",\"similarvolume\":\"0\"},\"sort\":[1601377033000]}]}}},{\"key\":\"欧阳妮妮新剧被吐槽，一头卷发土气凸嘴明显，导演后悔选她做女主\",\"doc_count\":525,\"top_score_hits\":{\"hits\":{\"total\":525,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"a7114d238505fa4167fd630fb102374a\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"a7114d238505fa4167fd630fb102374a\",\"publish_time\":\"2020-09-28 17:30:38\",\"sourcewebsitename\":\"趣头条\",\"title\":\"欧阳妮妮新剧被吐槽，一头卷发土气凸嘴明显，导演后悔选她做女主\",\"similarvolume\":\"0\"},\"sort\":[1601314238000]}]}}},{\"key\":\"打响反美第一枪？一枚炸弹在首都引爆，美方3名美中情局人员丧生\",\"doc_count\":483,\"top_score_hits\":{\"hits\":{\"total\":483,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"7e1d6481a91f3dcb5fe71f08c51ec76d\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"7e1d6481a91f3dcb5fe71f08c51ec76d\",\"publish_time\":\"2020-09-28 01:08:56\",\"sourcewebsitename\":\"趣头条\",\"title\":\"打响反美第一枪？一枚炸弹在首都引爆，美方3名美中情局人员丧生\",\"similarvolume\":\"0\"},\"sort\":[1601255336000]}]}}},{\"key\":\"巩俐国际电影节全程中文演讲，登台第一句话：我是中国演员巩俐\",\"doc_count\":481,\"top_score_hits\":{\"hits\":{\"total\":481,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"b4c24629df667f32d75eb7f0daf8f394\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"b4c24629df667f32d75eb7f0daf8f394\",\"publish_time\":\"2020-09-28 17:06:53\",\"sourcewebsitename\":\"趣头条\",\"title\":\"巩俐国际电影节全程中文演讲，登台第一句话：我是中国演员巩俐\",\"similarvolume\":\"0\"},\"sort\":[1601312813000]}]}}},{\"key\":\"邱淑贞曾携女儿出席酒会，21岁沈月穿仙女裙，获外国男子跪地示爱\",\"doc_count\":481,\"top_score_hits\":{\"hits\":{\"total\":481,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"26c9c2389dc34032ff7b3d685426ac6d\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"26c9c2389dc34032ff7b3d685426ac6d\",\"publish_time\":\"2020-09-28 16:57:19\",\"sourcewebsitename\":\"趣头条\",\"title\":\"邱淑贞曾携女儿出席酒会，21岁沈月穿仙女裙，获外国男子跪地示爱\",\"similarvolume\":\"0\"},\"sort\":[1601312239000]}]}}},{\"key\":\"平原一中远足活动第2集，前几个班级到达终点站，大家都很兴奋\",\"doc_count\":464,\"top_score_hits\":{\"hits\":{\"total\":464,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"75827080fa03625bc46c698eab5cb1f4\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"75827080fa03625bc46c698eab5cb1f4\",\"publish_time\":\"2020-09-29 16:07:51\",\"sourcewebsitename\":\"趣头条\",\"title\":\"平原一中远足活动第2集，前几个班级到达终点站，大家都很兴奋\",\"similarvolume\":\"0\"},\"sort\":[1601395671000]}]}}},{\"key\":\"印军用物资被倒卖？拉达克大量士兵冻伤，撤军迫在眉睫\",\"doc_count\":462,\"top_score_hits\":{\"hits\":{\"total\":462,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"5213970751720db0358958a679204aa4\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"5213970751720db0358958a679204aa4\",\"publish_time\":\"2020-09-29 15:46:33\",\"sourcewebsitename\":\"趣头条\",\"title\":\"印军用物资被倒卖？拉达克大量士兵冻伤，撤军迫在眉睫\",\"similarvolume\":\"0\"},\"sort\":[1601394393000]}]}}},{\"key\":\"2020年9月29日，平原一中高二学生远足纪实第1集，目的地张官店\",\"doc_count\":457,\"top_score_hits\":{\"hits\":{\"total\":457,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"7a8d80a5a58418a3837a20eaa76684f9\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"7a8d80a5a58418a3837a20eaa76684f9\",\"publish_time\":\"2020-09-29 16:05:51\",\"sourcewebsitename\":\"趣头条\",\"title\":\"2020年9月29日，平原一中高二学生远足纪实第1集，目的地张官店\",\"similarvolume\":\"0\"},\"sort\":[1601395551000]}]}}},{\"key\":\"王斯然跟GAI说以后要分房睡…\\n\\n王斯然：因为我要半夜喂奶\",\"doc_count\":452,\"top_score_hits\":{\"hits\":{\"total\":452,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"2ea477af6f02530de774ddefe4386aa1\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"2ea477af6f02530de774ddefe4386aa1\",\"publish_time\":\"2020-09-29 14:39:27\",\"sourcewebsitename\":\"趣头条\",\"title\":\"王斯然跟GAI说以后要分房睡…\\n\\n王斯然：因为我要半夜喂奶\",\"similarvolume\":\"0\"},\"sort\":[1601390367000]}]}}},{\"key\":\"你是学霸吗？4-6=1能成立？简约而不简单，能答对的人，智商很高\",\"doc_count\":442,\"top_score_hits\":{\"hits\":{\"total\":442,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"b21c66e6816342fe51890be0b7ebe10c\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"b21c66e6816342fe51890be0b7ebe10c\",\"publish_time\":\"2020-09-28 13:53:08\",\"sourcewebsitename\":\"趣头条\",\"title\":\"你是学霸吗？4-6=1能成立？简约而不简单，能答对的人，智商很高\",\"similarvolume\":\"0\"},\"sort\":[1601301188000]}]}}},{\"key\":\"走进车八岭\",\"doc_count\":439,\"top_score_hits\":{\"hits\":{\"total\":439,\"max_score\":null,\"hits\":[{\"_index\":\"postal\",\"_type\":\"infor\",\"_id\":\"ddb13f1256d200f1a9d38e21e4571d05\",\"_score\":null,\"_source\":{\"classify\":7,\"article_public_id\":\"ddb13f1256d200f1a9d38e21e4571d05\",\"publish_time\":\"2020-09-28 11:18:01\",\"sourcewebsitename\":\"趣头条\",\"title\":\"走进车八岭\",\"similarvolume\":\"0\"},\"sort\":[1601291881000]}]}}}]}}}";
//    	System.out.println(sendPost);
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	JSONArray jsonArray = parseObject.getJSONObject("aggregations").getJSONObject("top_score").getJSONArray("buckets");
    	int size = jsonArray.size();
    	if(size>10){
    		size =10;
    	}
    	JSONArray result = new JSONArray();
    	for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONObject jsonObject2 = jsonObject.getJSONObject("top_score_hits").getJSONObject("hits");
			JSONObject jsonObject3 = jsonObject2.getJSONArray("hits").getJSONObject(0).getJSONObject("_source");
			JSONObject ob = new JSONObject();
			String title = jsonObject3.get("title").toString();
			if(i==0){
				similarityMaxTitle = title;
			}
			ob.put("hot", jsonObject2.get("total"));
			ob.put("title",title );
			ob.put("author", jsonObject3.get("sourcewebsitename"));
			ob.put("publish_time", jsonObject3.get("publish_time"));
			result.add(ob);
		}
		return result;
	}

	private  String gethot_analysis(PublicoptionEntity publicoptionEntity) {
    	JSONArray result = new JSONArray(); 
    	try {
    		String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    		String eventendtime = checkString(publicoptionEntity.getEventendtime());
    		String eventkeywords = checkString(publicoptionEntity.getEventkeywords());
    		String[] split = eventkeywords.split("\\+");
    		String mainkeywords = split[0];
    		String affiliatedkeywords = split[1];
    		String keywords =mainkeywords+","; 
    		if(null !=  affiliatedkeywords && !"".equals(affiliatedkeywords.trim())){
    			String[] split2 = mainkeywords.split(",");
    			affiliatedkeywords = affiliatedkeywords.replaceAll("\\(", "").replaceAll("\\)", "");
    			String[] split3 = affiliatedkeywords.split("\\|");
    			for (String mainkeyword : split2) {
    				for (String affiliatedkeyword : split3) {
    					keywords += mainkeyword +"_"+affiliatedkeyword+",";
    				}
    			}
    		}
    		keywords = keywords.substring(0,keywords.length()-1);
    		FullSearchParam searchParam = new FullSearchParam();
    		searchParam.setPageNum(1);
    		searchParam.setPageSize(10);
    		searchParam.setSearchWord("");
    		searchParam.setClassify("");
    		searchParam.setSource_name("");
    		searchParam.setTimeType(2);
    		String timetype = searchParam.getTimetype(); 
    		timetype = "spider_time";
    		String searchparam = "spider_time";
    		if(StringUtils.equals(searchParam.getSource_name(), "全部")) {
    			searchParam.setSource_name("");
    		}
//		eventendtime = "2020-12-22 23:59:59";
    		String params = "page="+searchParam.getPageNum()+ "&topic="+keywords + 
    				"&searchparam=" + searchparam + "&searchType=0" +
    				"&source_name="+searchParam.getSource_name() +
    				"&classify=" + searchParam.getClassify() + "&size=" + searchParam.getPageSize() +
    				"&times="+eventstarttime+
    				"&timee="+eventendtime+
    				"&timetype="+timetype;
    		String url = es_search_url + SearchConstant.ES_API_HOT;
    		String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
    		JSONObject parseObject = JSONObject.parseObject(esResponse);
    		JSONArray jsonArray = parseObject.getJSONArray("data");
    		for (int i = 0; i < jsonArray.size(); i++) {
    			JSONObject jsonObject = jsonArray.getJSONObject(i);
    			JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
    			JSONObject ob = new JSONObject();
    			ob.put("publish_time", jsonObject2.get("spider_time"));
    			ob.put("topic", jsonObject2.get("topic"));
    			ob.put("id", jsonObject2.get("article_public_id"));
    			ob.put("original_weight", jsonObject2.get("original_weight"));
    			ob.put("source_name", jsonObject2.get("source_name"));
    			ob.put("emotionalIndex", jsonObject2.get("emotionalIndex"));
    			ob.put("source_url", jsonObject2.get("source_url"));
    			result.add(ob);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toJSONString();
	}

	/**
     * 事件跟踪
     * @param publicoptionEntity
     * @return
     */
    private  String getevent_trace(PublicoptionEntity publicoptionEntity) {
    	JSONObject ETOb= new JSONObject();
    	//feelings_of_source 数据来源情感占比分析
    	try {
    		JSONArray feelings_of_source= gatfeelings_of_source(publicoptionEntity);
    		ETOb.put("feelings_of_source", feelings_of_source);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//feelings_of_timeline 时间线范围对应情感分析
    	try {
    		JSONArray feelings_of_timeline= gatfeelings_of_timeline(publicoptionEntity);
    		ETOb.put("feelings_of_timeline", feelings_of_timeline);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//positive 正面对应的社交帐号以及网站列表排名
    	try {
    		JSONArray positive = getpositiveOrnegative(publicoptionEntity,"1");
    		ETOb.put("positive", positive);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//negative 负面对应的社交帐号以及网站列表排名
    	try {
    		JSONArray negative = getpositiveOrnegative(publicoptionEntity,"3");
    		ETOb.put("negative", negative);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ETOb.toJSONString(); 
	}

    private  JSONArray getpositiveOrnegative(PublicoptionEntity publicoptionEntity,String type) {
    	String sendPost = datasourceanalysis(publicoptionEntity,type);
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	JSONArray jsonArray = parseObject.getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
    	int size = jsonArray.size();
    	if(size>10){
    		size = 10;
    	}
    	JSONArray result = new JSONArray();
    	for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONObject ob =new JSONObject();
			ob.put("name", jsonObject.get("key"));
			ob.put("value", jsonObject.get("doc_count"));
			result.add(ob);
		}
		return result;
	}

	/**
     * 事件跟踪 - 时间线范围对应情感分析
     * @param publicoptionEntity
     * @return
     */
    private  JSONArray gatfeelings_of_timeline(PublicoptionEntity publicoptionEntity) {
    	
    	String url = es_search_url + PublicoptionConstant.es_api_sentimentFlagChart;
		String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    	String eventendtime = checkString(publicoptionEntity.getEventendtime());
    	String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    	String eventstopwords = checkString(publicoptionEntity.getEventstopwords());
    	String searchwords = "";
    	
    	String params = "times=" + eventstarttime + "&timee=" + eventendtime + "&keyword=" + eventkeywords + "&stopword=" + eventstopwords
                + "&searchkeyword=" + searchwords + "&origintype=0&emotionalIndex=1,2,3&projecttype=2";
    	String sendPost = getEsRequset(url, params,null,null);
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	JSONArray jsonArray = parseObject.getJSONObject("aggregations").getJSONObject("group_by_grabTime").getJSONArray("buckets");
    	JSONArray reault = new JSONArray(); 
    	for (int i = 0; i < jsonArray.size(); i++) {
    		JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONArray ob = new JSONArray();
    		JSONArray jsonArray2 = jsonObject.getJSONObject("top-terms-classify").getJSONArray("buckets");
    		String string = jsonObject.getString("key_as_string");
			ob.add(string);
    		ob = analysisFOS(jsonArray2,ob);
    		reault.add(ob);
		}
		return reault;
	}

	/**
     * 事件跟踪 - 数据来源情感占比分析
     * @param publicoptionEntity
     */
	private  JSONArray gatfeelings_of_source(PublicoptionEntity publicoptionEntity) {
    	String sendPost = datasourceanalysis(publicoptionEntity,"1,2,3");
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	JSONArray jsonArray = parseObject.getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
    	int size = jsonArray.size();
    	if(size>8){
    		size = 8;
    	}
    	JSONArray reault = new JSONArray(); 
    	for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONArray ob = new JSONArray();
			JSONArray jsonArray2 = jsonObject.getJSONObject("top-terms-emotion").getJSONArray("buckets");
			String string = jsonObject.getString("key");
			ob.add(string);
			ob = analysisFOS(jsonArray2,ob);
			reault.add(ob);
		}
    	System.err.println(reault);
    	return reault;
	}
	
	private  JSONArray analysisFOS(JSONArray jsonArray2,JSONArray ob ) {
		int positive = 0;
		int neutral = 0;
		int negative = 0;
		for (int j = 0; j < jsonArray2.size(); j++) {
			JSONObject jsonObject2 = jsonArray2.getJSONObject(j);
			if(jsonObject2.getInteger("key")== 1){
				Object object = jsonObject2.get("doc_count"); 
				if(object!= null ){
					positive = (int)object;
				}
			} 
		}
		for (int j = 0; j < jsonArray2.size(); j++) {
			JSONObject jsonObject2 = jsonArray2.getJSONObject(j);
			if(jsonObject2.getInteger("key")== 2){
				Object object = jsonObject2.get("doc_count"); 
				if(object!= null ){
					neutral = (int)object;
				}
			}
		}
		for (int j = 0; j < jsonArray2.size(); j++) {
			JSONObject jsonObject2 = jsonArray2.getJSONObject(j);
			if(jsonObject2.getInteger("key")== 3){
				Object object = jsonObject2.get("doc_count"); 
				if(object!= null ){
					negative = (int)object;
				}
			}
		}
		ob.add(positive);
		ob.add(neutral);
		ob.add(negative);

		return ob;
	}

	/**
	 * 
	 * @param publicoptionEntity
	 * @param emotionalIndex
	 * @return 
	 */
	private  String datasourceanalysis(PublicoptionEntity publicoptionEntity,String emotionalIndex) {
		String url = es_search_url + PublicoptionConstant.es_api_datasourceanalysis;
		String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    	String eventendtime = checkString(publicoptionEntity.getEventendtime());
    	String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    	String eventstopwords = checkString(publicoptionEntity.getEventstopwords());
    	String searchwords = "";
    	
    	String params = "times=" + eventstarttime + "&timee=" + eventendtime + "&keyword=" + eventkeywords + "&stopword=" + eventstopwords
                + "&searchkeyword=" + searchwords + "&origintype=0&emotionalIndex="+emotionalIndex+"&projecttype=2";
    	String sendPost = getEsRequset(url, params,null,null);
    	System.err.println(sendPost);
    	return sendPost;
	}

	/**
     * 事件脉络
     * @param publicoptionEntity
     * @return
     */
    private  String gatevent_context(PublicoptionEntity publicoptionEntity) {
    	JSONArray result = new JSONArray();
    	try {
    		String url = es_search_url + PublicoptionConstant.es_api_qbsearchcontent;
    		String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    		String eventendtime = checkString(publicoptionEntity.getEventendtime());
    		String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    		String eventstopwords = checkString(publicoptionEntity.getEventstopwords());
    		String searchwords = "";
//    	eventkeywords = "海南司法";
    		String params = "keyword=" + eventkeywords + "&stopword=" + eventstopwords
    				+ "&searchkeyword=" + searchwords + "&origintype=0&emotionalIndex=1,2,3&projecttype=2";
    		JSONArray timelist = timepartition(eventstarttime,eventendtime);
    		for (int i = 0; i < timelist.size(); i++) {
    			JSONObject jsonObject4 = timelist.getJSONObject(i);
    			String sendPost = getEsRequset(url, params+"&times=" + jsonObject4.get("eventstarttime") + "&timee=" + jsonObject4.get("eventendtime"),null,null);
    			JSONObject parseObject = JSONObject.parseObject(sendPost);
    			JSONArray jsonArray = parseObject.getJSONObject("aggregations").getJSONObject("top_score").getJSONArray("buckets");
    			int size = jsonArray.size();
    			if(size >0) {
    				JSONObject jsonObject = jsonArray.getJSONObject(0);
    				JSONObject jsonObject2 = jsonObject.getJSONObject("top_score_hits").getJSONObject("hits");
    				JSONObject jsonObject3 = jsonObject2.getJSONArray("hits").getJSONObject(0).getJSONObject("_source");
    				JSONObject ob = new JSONObject();
    				ob.put("heat", jsonObject2.get("total"));
    				ob.put("title", jsonObject3.get("title"));
    				ob.put("contenta", "");
    				ob.put("author", jsonObject3.get("sourcewebsitename"));
    				ob.put("publish_time", jsonObject3.get("publish_time"));
    				result.add(ob);
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toJSONString();
	}


    private  JSONArray timepartition(String eventstarttime, String eventendtime) {
    	long startlong = DateUtil.toTimestamp(eventstarttime);
    	long endlong = DateUtil.toTimestamp(eventendtime);
    	long interval= (endlong-startlong)/10;
    	JSONArray result = new JSONArray();
    	for (int i = 0; i < 10; i++) {
			JSONObject Ob =new JSONObject();
			Ob.put("eventstarttime", DateUtil.timeStamp2Date(startlong));
			startlong = startlong+interval;
			Ob.put("eventendtime", DateUtil.timeStamp2Date(startlong));
			result.add(Ob);
		}
//    	System.out.println(interval);
		return result;
	}

	/**
     * 溯源分析
     * @param publicoptionEntity
     * @return
     */
	private  String getback_analysis(PublicoptionEntity publicoptionEntity) {
    	JSONObject BAOb = new JSONObject();
    	//total highest highest_total trend
    	try {
    		JSONObject bAhighest = getBAhighest(publicoptionEntity);
    		BAOb.putAll(bAhighest);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//first_article
    	try {
    		JSONObject first_article = getBAfirstarticle(publicoptionEntity);
    		BAOb.put("first_article", first_article);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//source_ranking
    	try {
    		String source_ranking = getBAsource_ranking(publicoptionEntity);
    		BAOb.put("source_ranking", source_ranking);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		return BAOb.toJSONString();
	}
    
    

    

    /**
     * 溯源分析 -数据来源前5的网站
     * @param publicoptionEntity
     */
	private  String getBAsource_ranking(PublicoptionEntity publicoptionEntity) {
		String url = es_search_url + PublicoptionConstant.es_api_websitestatistics;
    	String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    	String eventendtime = checkString(publicoptionEntity.getEventendtime());
    	String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    	String eventstopwords = checkString(publicoptionEntity.getEventstopwords());
    	String searchwords = "";
    	
    	String params = "times=" + eventstarttime + "&timee=" + eventendtime + "&keyword=" + eventkeywords + "&stopword=" + eventstopwords
                + "&searchkeyword=" + searchwords + "&origintype=0&emotionalIndex=1,2,3&projecttype=2";
    	String sendPost = getEsRequset(url, params,null,null);
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	JSONArray jsonArray = parseObject.getJSONObject("aggregations").getJSONObject("top-terms-sourcewebsitename").getJSONArray("buckets");
    	int size = jsonArray.size();
    	if(size>8){
    		size =8;
    	}
    	String list = "";
    	for (int i = 0; i < size; i++) {
    		JSONObject jsonObject = jsonArray.getJSONObject(i);
    		list += jsonObject.getString("key")+",";
		}
    	
    	return list.substring(0,list.length()-1);
		
	}


	/**
	 * 溯源分析 - 第一条相关资讯（时间，平台，标题）
	 * @param publicoptionEntity
	 * @return
	 */
	private  JSONObject getBAfirstarticle(PublicoptionEntity publicoptionEntity) {
		String url = es_search_url + PublicoptionConstant.es_api_search_list;
    	String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    	String eventendtime = checkString(publicoptionEntity.getEventendtime());
    	String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    	String eventstopwords = checkString(publicoptionEntity.getEventstopwords());
    	String searchwords = "";
    	
    	String params = "times=" + eventstarttime + "&timee=" + eventendtime + "&keyword=" + eventkeywords + "&stopword=" + eventstopwords
                + "&searchkeyword=" + searchwords + "&origintype=0&searchType=0&size=1&emotionalIndex=1,2,3&projecttype=2";
    	String sendPost = getEsRequset(url, params,null,null);
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	JSONArray jsonArray = parseObject.getJSONArray("data");
    	JSONObject result = new JSONObject();
    	if(jsonArray.size()>0){
    		JSONObject jsonObject = jsonArray.getJSONObject(0).getJSONObject("_source");
    		result.put("title", jsonObject.get("title"));
    		result.put("source_name", jsonObject.get("source_name"));
    		result.put("publish_time", jsonObject.get("publish_time"));
    	}
    	return result;
	}


	/**
	 * 溯源分析 - 总量 最高获取量时间  最高获取量 增长趋势
	 * @param publicoptionEntity
	 * @return
	 */
	private  JSONObject getBAhighest(PublicoptionEntity publicoptionEntity) {
		String url = es_search_url + PublicoptionConstant.es_api_keyword_temporaldatanum;
    	String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    	String eventendtime = checkString(publicoptionEntity.getEventendtime());
    	String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    	String eventstopwords = checkString(publicoptionEntity.getEventstopwords());
    	String searchwords = "";
    	
    	String params = "times=" + eventstarttime + "&timee=" + eventendtime + "&keyword=" + eventkeywords + "&stopword=" + eventstopwords
                + "&searchkeyword=" + searchwords + "&origintype=0&emotionalIndex=1,2,3&projecttype=2&interval=day";
    	String sendPost = getEsRequset(url, params,null,null);
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	JSONArray jsonArray = parseObject.getJSONObject("aggregations").getJSONObject("group_by_grabTime").getJSONArray("buckets");
    	JSONObject result = new JSONObject();
    	result.put("highest_total", 0);
    	result.put("highest", eventendtime.substring(0,10));
    	result.put("total",parseObject.getJSONObject("hits").getInteger("total") );
    	int size = jsonArray.size();
    	int partsize=size/2;
    	int a=size%2;
    	int firstPart=0;
    	int lastPart=0;
    	for (int i = 0; i < size; i++) {
    		JSONObject jsonObject = jsonArray.getJSONObject(i);
    		Integer integer = jsonObject.getInteger("doc_count");
    		if(integer > result.getInteger("highest_total")){
    			result.put("highest_total", integer);
    			result.put("highest", jsonObject.getString("key_as_string").substring(0,10));
    		}
    		if(i<partsize){
    			firstPart += integer;
    		}else if(i>=partsize+a){
    			lastPart += integer;
    		}
		}
    	if(firstPart<lastPart){
    		result.put("trend","热烈");
    	}else{
    		result.put("trend","平缓");
    	}
    	return result.get("highest")== null? null:result;
	}




	private  String getEsRequset(String url, String params,String checkMsg,String checkMsgKey) {
		int i =0;
		while (i<3) {
			i++;
			String sendPost = "";
			try {
				sendPost = sendPost(url,params);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(null != checkMsg && !"".equals(checkMsg.trim())){
				if(JSONObject.parseObject(sendPost).get(checkMsgKey).equals(checkMsg)){
					continue;
				}
			}
			return sendPost;
		}
		return null;
	}

	public  String checkString(String string){
		if(null == string || "".equals(string.trim())){
			return "";
		}
		return string;
	}
	public  void main(String[] args) {
		PublicoptionEntity pe= new PublicoptionEntity();
		pe.setId(9);
		pe.setEventname("司法专题教育矫正2020-5月舆情报告");
		pe.setEventkeywords("海南+(司法|教育矫正|社区矫正|依法接收入矫|收监执行|依法实施社区矫正|依法实施教育矫正|依法实施监督管理)");
		pe.setEventstopwords("null");
		pe.setEventstarttime("2020-11-07 15:00:00");
		pe.setEventendtime("2020-12-15 12:27:56");
		pe.setCreatetime("2020-05-14 17:27:58");
		pe.setStatus(2);
		pe.setUpdatetime("2020-10-10 17:28:05");
		pe.setUser_id(13337717360L);
//		String getback_analysis = gethot_analysis(pe);
//		System.out.println(getback_analysis);
//		JSONArray timepartition = timepartition("2020-10-07 15:00:00","2020-10-15 12:27:56");
//		System.out.println(timepartition);
		getnetizens_analysis(pe);
	}

	/**
     * 发送post请求
     *
     * @date 2020年4月13日 下午4:02:23
     */
    public  String sendPost(String url, String params) {
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
}
