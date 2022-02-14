package com.stonedt.intelligence.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.entity.FullPolymerization;
import com.stonedt.intelligence.entity.FullType;
import com.stonedt.intelligence.entity.FullWord;
import com.stonedt.intelligence.vo.FullSearchParam;
import org.apache.ibatis.annotations.Param;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 *	全文搜索
 * @date  2020年4月17日 下午4:46:02
 */
public interface FullSearchService {
	/**
	 * 律师列表
	 * @param param
	 * @return
	 */
	JSONObject lawyerList(FullSearchParam param);
	
	/**
	 * 被执行人
	 * @param param
	 * @return
	 */
	JSONObject executionPersonList(FullSearchParam param);
	
	/**
	 * 	资讯数据列表
	 */
	JSONObject informationList(FullSearchParam Param);


	JSONObject informationListSearch(FullSearchParam Param);
	
	/**
	 * 	热点数据列表
	 */
	JSONObject hotList(FullSearchParam Param)  throws UnsupportedEncodingException;
	
	/**
	 * 一级分类
	 * @return
	 */
	List<FullType> listFullTypeByFirst();
	
	/**
	 * 二级分类
	 * @param type_one_id
	 * @return
	 */
	List<FullType> listFullTypeBysecond(Integer type_one_id);
	
	/**
	 * 三级分类
	 * @param type_two_id
	 * @return
	 */
	List<FullType> listFullTypeBythird(Integer type_two_id);
	
	/**
	 * 获取聚合分类
	 * @return
	 */
	List<FullPolymerization> listFullPolymerization();
	
	/**
	 * 获取全文搜索一级分类列表 通过id list
	 * @param idList
	 * @return
	 */
	List<FullType> listFullTypeOneByIdList(@Param("idList")List<Integer> idList);

	/**
	 * 投诉列表
	 * @param searchParam
	 * @return
	 */
	JSONObject complaintList(FullSearchParam searchParam);

	/**
	 * 公告列表
	 * @param searchParam
	 * @return
	 */
	JSONObject announcementList(FullSearchParam searchParam);

	/**
	 * 公告Rtype
	 * @param searchParam
	 * @return
	 */
	JSONArray announcementRtype(FullSearchParam searchParam);
	
	/**
	 * 研报列表
	 * @param searchParam
	 * @return
	 */
	JSONObject reportList(FullSearchParam searchParam);
	
	/**
	 * 研报分类
	 * @param searchParam
	 * @return
	 */
	JSONArray reportIndustry(FullSearchParam searchParam);

	/**
	 * 公告、研报详情
	 * @param type
	 * @param article_public_id
	 * @return
	 */
	String getReportDetail(String type, String article_public_id);

	/**
	 * 招标列表
	 * @param searchParam
	 * @return
	 */
	JSONObject biddingList(FullSearchParam searchParam);

	/**
	 * 招标详情
	 * @param article_public_id
	 * @return
	 */
	JSONObject biddingDetail(String article_public_id);

	/**
	 * 招聘列表
	 * @param searchParam
	 * @return
	 */
	JSONObject inviteList(FullSearchParam searchParam);

	/**
	 * 招聘详情
	 * @param record_id
	 * @return
	 */
	JSONObject getInviteDetail(String record_id);

	/**
	 * 工商列表
	 * @param searchParam
	 * @return
	 */
	JSONObject companyList(FullSearchParam searchParam);

	/**
	 * 工商行业分类
	 * @param searchParam
	 * @return
	 */
	JSONArray companyIndustry(FullSearchParam searchParam);

	/**
	 * 工商详情
	 * @param article_public_id
	 * @return
	 */
	JSONObject companyDetails(String article_public_id);

	/**
	 * 法律文书
	 * @param searchParam
	 * @return
	 */
	JSONObject judgmentList(FullSearchParam searchParam);

	/**
	 * 法律文书案件类型 CaseType
	 * @param searchParam
	 * @return
	 */
	JSONArray judgmentCaseType(FullSearchParam searchParam);

	/**
	 * 法律文书详情
	 * @param articleid
	 * @return
	 */
	JSONObject judgmentDetail(String articleid);

	/**
	 * 知识产权
	 * @param searchParam
	 * @return
	 */
	JSONObject knowLedgeList(FullSearchParam searchParam);

	/**
	 * 知识产权 专利类型
	 * @param searchParam
	 * @return
	 */
	JSONArray knowLedgeCaseType(FullSearchParam searchParam);

	/**
	 * 知识产权 详情
	 * @param articleid
	 * @return
	 */
	JSONObject knowLedgeDetail(String articleid);

	/**
	 * 投资融资
	 * @param searchParam
	 * @return
	 */
	JSONObject investmentList(FullSearchParam searchParam);

	/**
	 * 投资融资 类型
	 * @param searchParam
	 * @return
	 */
	JSONArray investmentType(FullSearchParam searchParam);

	/**
	 * 投资融资 详情
	 * @param articleid
	 * @return
	 */
	JSONObject investmentDetail(String articleid);

	/**
	 * 百度知道
	 * @param searchParam
	 * @return
	 */
	JSONObject baiduKnowsList(FullSearchParam searchParam);

	/**
	 * 百度学术
	 * @param searchParam
	 * @return
	 */
	JSONObject thesisnList(FullSearchParam searchParam);

	/**
	 * 百度学术 -详情
	 * @param articleid
	 * @return
	 */
	JSONObject thesisnDetail(String articleid);

	/**
	 * 获取导航栏
	 * @param menuStyle
	 * @param onlyid
	 * @param polyid
	 * @return
	 */
	JSONObject getBreadCrumbs(Integer menuStyle, Integer fulltype, Integer onlyid, Integer polyid);

	
	/**
	 *  12小时热点
	 */
//	List<Map<String, Object>> twelveHotArticle();

	Boolean saveFullWord(FullWord fullWord);


	JSONObject getSearchWordById(JSONObject paramJson);

	JSONObject lawyerDetailData(String article_public_id);

	JSONObject executionPersonDetailData(String article_public_id);

	JSONObject professorList(FullSearchParam professorListParam);

	JSONObject professorDetailData(String article_public_id);

	JSONObject doctorList(FullSearchParam doctorListParam);

	JSONObject doctorDetailData(String article_public_id);

	String hotBaiduList();

}
