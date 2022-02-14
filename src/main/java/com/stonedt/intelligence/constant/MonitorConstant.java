package com.stonedt.intelligence.constant;

/**
 * description: 数据监测的数据接口 <br>
 * date: 2020/4/16 19:38 <br>
 * author: huajiancheng <br>
 * version: 1.0 <br>
 */
public interface MonitorConstant {
	// 相关文章
    public static String es_api_relatearticle_list = "/yqsearch/relatearticlelist";
    // 监测列表 取消合并
    public static String es_api_search_list = "/yqsearch/searchlist";
    // 监测列表  合并文章
    public static String es_api_similar_contentlist = "/yqsimilar/qbsearchbycontentlist";
    // 监测列表  合并文章-行业
    public static String es_api_similar_industry_list = "/yqsimilar/similarindustry";
    
    // 监测列表  合并文章-事件
    public static String es_api_similar_event_list = "/yqsimilar/similarevent";
    
    // 监测列表  合并文章-省份
    public static String es_api_similar_province_list = "/yqsimilar/similarprovince";
    
 // 监测列表  合并文章-市域
    public static String es_api_similar_city_list = "/yqsimilar/similarcity";
    
    
    
    // 相似文章合并返回分组id
    public static String es_api_similarsearch_content = "/yqsimilar/qbsearchcontent";
    
    
    // 相似文章合并返回分组id
    public static String es_api_similar_titlekeyword_search_content = "/yqsimilar/qbsimilarcontent";
    
    
 // 相似文章合并返回分组id
    public static String es_api_similar_titlekeyword_search_content_by_num_five = "/yqsimilar/qbsimilarcontentbytitlekeyword";
    
    
    // 跳转文章相似度，数据来源列表页面
    public static String es_api_similarsourcename_content = "/yqsimilar/qbsearchcontent";
    // 1、文章详情
    public static String es_api_article_newdetail = "/yqsearch/getArticlenewdetail";
    // 导出数据  全部导出
    public static String es_api_search__searchcontent = "/yqt/qbsearchcontent";
    //导出文章接口 部分导出
    public static final  String es_api_exportqbsearchconten = "/yqtids/qbsearchcontent";
    //获取数据源列表
    public static final String es_api_data_source = "/publicoption/websitestatistics";
    //情感切换
    public static final  String es_api_updateemtion = "/yqtemotion/updateemotion";
    //删除文章
    public static final  String es_api_deletedata = "/yqsearch/deletearticle";
    //事件标签
    public static final  String es_api_search_event_list = "/yqsearch/eventlable";
    //行业标签
    public static final  String es_api_search_industry_list = "/yqsearch/industrylable";
    //省份
    public static final  String es_api_search_province_list = "/yqsearch/statisticsprovince";
    //市
    public static final  String es_api_search_city_list = "/yqsearch/statisticscity";
    
    
    
}
