package com.stonedt.intelligence.constant;
/**
 *	全文搜索 常量
 * @date  2020年4月17日 下午4:51:09
 */
public class SearchConstant {
	
	// 热点资讯 es api
	public static final String es_api_hot_rank = "/yqsearch/hotrank";
	/**
	 * 热门
	 */
	public static final String ES_API_HOT = "/hotnews/hotuniquelist";
	/**
	 * 投诉
	 */
	public static final String ES_API_COMPLAIN = "/complain/complainlist";
	/**
	 * 市长信箱
	 */
	public static final String ES_API_MAYOR_MAIL_BOX = "/mayormailbox/mayormailboxlist";
	/**
	 * 公告
	 */
	public static final String ES_API_ANNOUNCEMENT = "/announcement/announcementlist";
	/**
	 * 公告类型
	 */
	public static final String ES_API_ANNOUNCEMENT_RTYPE = "/announcement/announcementrtype";
	/**
	 * 研报公告详情
	 */
	public static final String ES_API_ANNOUNCEMENT_DETAIL = "/commonsearch/getcommondatadetail";
	/**
	 * 研报
	 */
	public static final String ES_API_RESEARCH_REPORT = "/researchreport/researchreportlist";
	/**
	 * 研报评级
	 */
	public static final String ES_API_RESEARCH_REPORT_INDUSTRY =  "/researchreport/researchreportratingtype";
	/**
	 * 通用接口
	 */
	public static final String ES_API_COMMON_SEARCH = "/commonsearch/superdatalist";
	/**
	 * 招标详情数据
	 */
	public static final String ES_API_BIDDING_DETAIL = "/invitate/getinvitatedetail";
	/**
	 * 招聘数据
	 */
	public static final String ES_API_JOBS_LIST= "/jobs/jobslist";
	/**
	 * 招聘详情
	 */
	public static final String ES_API_JOBS_DETAIL = "/jobs/getjobsdetail";
	/**
	 * 工商数据
	 */
	public static final String ES_API_COMPANY_LIST = "/company/companylist";
	/**
	 * 工商分类
	 */
	public static final String ES_API_COMPANY_INDUSTRY = "/company/companyindustry";
	/**
	 * 工商详情
	 */
	public static final String ES_API_COMPANY_DETAIL =  "/company/getcompanydetail";
	/**
	 * 法律文书
	 */
	public static final String ES_API_JUDGMENT_LIST = "/judgment/judgmentlist";
	/**
	 * 法律文书 分类
	 */
	public static final String ES_API_JUDGMENT_CASE_TYPE = "/judgment/judgmentcaseType";
	/**
	 * 通用详情接口
	 */
	public static final String ES_API_COMMON_DATA_DETAIL = "/commonsearch/getcommondatadetail";
	/**
	 * 专利数据
	 */
	public static final String ES_API_PATENT_LIST = "/patent/patentlist";
	/**
	 * 专利分类
	 */
	public static final String ES_API_PATENT_TYPE = "/patent/patentinformationtype";
	/**
	 * 专利详情
	 */
	public static final String ES_API_PATENT_DETAIL = "/patent/getpatentdetail";
	/**
	 * 投资融资 列表
	 */
	public static final String ES_API_INVESTMENT_LIST = "/investment/investmentlist";
	/**
	 * 投资融资 分类
	 */
	public static final String ES_API_INVESTMENT_TYPE = "/investment/investmentrounds";
	/**
	 * 投资融资 详情
	 */
	public static final String ES_API_INVESTMENT_DETAIL = "/investment/getinvestmentdetail";
	/**
	 * 百度知道
	 */
	public static final String ES_API_BAIDU_KNOWS_LIST = "/baiduknows/baiduknowslist";
	/**
	 * 学术论文
	 */
	public static final String ES_API_THESISN_LIST = "/thesisn/thesisnlist";
	/**
	 * 学术论文 详情
	 */
	public static final String ES_API_THESISN_DETAIL = "/thesisn/getthesisndetail";

}
