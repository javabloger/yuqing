package com.stonedt.intelligence.constant;

/**
 *	数据报告常量
 */
public class ReportConstant {

	// 资讯列表 es api
	public final static String es_api_search_list = "/yqsearch/searchlist";
	
	// 来源数据量分布 es api
	public final static String es_api_media_exposure = "/yqtenterprise/mediaexposure";
	
	// 情感分析数据统计分布 es api
	public final static String es_api_emotion_rate = "/yqtindutry/totalDatasearch";
	
	// 相似文章列表 es api
	public final static String es_api_similar_list = "/yqsimilar/qbsearchbycontentlist";
	
	// 相似文章合并返回分组id es api
	public final static String es_api_similar_ids = "/yqsimilar/qbsearchcontent";
	
	// 高频词接口 返回结巴需要的title es api
	public final static String es_api_highwords_titles = "/yqtenterprise/highwords";
	
	// 热点地区排名 es api
	public final static String es_api_hot_spot_ranking = "/yqtreport/geographicalDistribution";
	
	// 媒体活跃度分析 es api
	public final static String es_api_media_active = "/yqsearch/activemedia";
	
	// 自媒体热度排名 es api
	public final static String es_api_media_list = "/yqsearch/medialist";
	
	// 自媒体信息 es api
	public final static String es_api_wemedia_info = "/media/wemedialist";
	
	// 实体识别 es api
	public final static String es_api_ner = "/yqsearch/ner";
	
	// 政策 es api
	public final static String es_api_policy = "/yqsearch/policy";
	
	
	// 高频词 无需结巴接口 es api
	public final static String es_api_keyword_list = "/yqsearch/keywordlist";
	
	// 分类标签
	public final static String es_api_category_list = "/yqsearch/categorylist";
	
	// 去除文章类型titlekeyword
	public final static String searchlistnottitlekeyword = "/yqsearch/searchlistnottitlekeyword";

	//行业分布分析
	public static final  String es_api_search_industry_list = "/yqsearch/industrylable";

	//事件统计
	public static final  String es_api_search_event_list = "/yqsearch/eventlable";
	
	public final static String[] spotArray = new String[]{
			"北京", "天津", "河北", "山西", "内蒙古", "辽宁", "吉林", "黑龙江", "上海", "江苏", 
			"浙江", "安徽", "福建", "江西", "山东", "河南", "湖北", "湖南", "重庆", "四川",
			"贵州", "云南", "西藏", "陕西", "甘肃", "青海", "宁夏", "新疆", "广东", "广西",
			"海南", "台湾", "香港"
	};
	
}
