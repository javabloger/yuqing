package com.stonedt.intelligence.vo;

/**
* <p>搜索接口参数</p>
* <p>Title: FullSearchParam</p>  
* <p>Description: </p>  
* @author Mapeng 
* @date Jun 23, 2020
 */
public class FullSearchParam {
	
	private Integer timeType;
	private Integer mergeType;
	private String matchMethod;
	
	private Integer pageNum = 1;
	private Integer pageSize = 25;
	private String totalCount;
	private String totalPage;
	private String article_public_idstr;
	private String searchWord;
	private String classify;
	private String startTime;
	private String endTime;
	private Integer sortType; //信息排序
	private String emotions; //情感
	private Integer matchType; //匹配方式
	
	private String searchParam;
	private String topic;
	private String source_name;
	private String timetype;
	private String rtype;
	private String website_id;
	private Integer establish = 0; //企业成立时间
	private Integer status = 0;
	private Integer publish = 0;
	
	private String matchingmode;
	private String kinds;
	private String times;
	private String timee;




	//筛选项
	private String city;
	private String eventIndex;
	private String industryIndex;
	private String province;
	private Integer similar;
	private Integer searchType;
	private String emotionalIndex;
	private Integer page;

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public String getEmotionalIndex() {
		return emotionalIndex;
	}

	public void setEmotionalIndex(String emotionalIndex) {
		this.emotionalIndex = emotionalIndex;
	}

	public Integer getSearchType() {
		return searchType;
	}

	public void setSearchType(Integer searchType) {
		this.searchType = searchType;
	}

	public Integer getSimilar() {
		return similar;
	}

	public void setSimilar(Integer similar) {
		this.similar = similar;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getEventIndex() {
		return eventIndex;
	}

	public void setEventIndex(String eventIndex) {
		this.eventIndex = eventIndex;
	}

	public String getIndustryIndex() {
		return industryIndex;
	}

	public void setIndustryIndex(String industryIndex) {
		this.industryIndex = industryIndex;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getMatchingmode() {
		return matchingmode;
	}
	public void setMatchingmode(String matchingmode) {
		this.matchingmode = matchingmode;
	}
	public String getKinds() {
		return kinds;
	}
	public void setKinds(String kinds) {
		this.kinds = kinds;
	}
	public String getTimes() {
		return times;
	}
	public void setTimes(String times) {
		this.times = times;
	}
	public String getTimee() {
		return timee;
	}
	public void setTimee(String timee) {
		this.timee = timee;
	}
	public Integer getTimeType() {
		return timeType;
	}
	public void setTimeType(Integer timeType) {
		this.timeType = timeType;
	}
	public Integer getMergeType() {
		return mergeType;
	}
	public void setMergeType(Integer mergeType) {
		this.mergeType = mergeType;
	}
	public Integer getPageNum() {
		return pageNum;
	}
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}
	public String getSearchWord() {
		return searchWord;
	}
	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}
	public String getClassify() {
		return classify;
	}
	public void setClassify(String classify) {
		this.classify = classify;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getEmotions() {
		return emotions;
	}
	public void setEmotions(String emotions) {
		this.emotions = emotions;
	}
	public Integer getMatchType() {
		return matchType;
	}
	public void setMatchType(Integer matchType) {
		this.matchType = matchType;
	}
	public Integer getSortType() {
		return sortType;
	}
	public void setSortType(Integer sortType) {
		this.sortType = sortType;
	}
	public String getSearchParam() {
		return searchParam;
	}
	public void setSearchParam(String searchParam) {
		this.searchParam = searchParam;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getSource_name() {
		return source_name;
	}
	public void setSource_name(String source_name) {
		this.source_name = source_name;
	}
	public String getTimetype() {
		return timetype;
	}
	public void setTimetype(String timetype) {
		this.timetype = timetype;
	}
	public String getRtype() {
		return rtype;
	}
	public void setRtype(String rtype) {
		this.rtype = rtype;
	}
	public String getWebsite_id() {
		return website_id;
	}
	public void setWebsite_id(String website_id) {
		this.website_id = website_id;
	}
	public Integer getEstablish() {
		return establish;
	}
	public void setEstablish(Integer establish) {
		this.establish = establish;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getPublish() {
		return publish;
	}
	public void setPublish(Integer publish) {
		this.publish = publish;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	public String getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(String totalPage) {
		this.totalPage = totalPage;
	}
	public String getArticle_public_idstr() {
		return article_public_idstr;
	}
	public void setArticle_public_idstr(String article_public_idstr) {
		this.article_public_idstr = article_public_idstr;
	}
	public String getMatchMethod() {
		return matchMethod;
	}
	public void setMatchMethod(String matchMethod) {
		this.matchMethod = matchMethod;
	}


	@Override
	public String toString() {
		return "FullSearchParam{" +
				"timeType=" + timeType +
				", mergeType=" + mergeType +
				", matchMethod='" + matchMethod + '\'' +
				", pageNum=" + pageNum +
				", pageSize=" + pageSize +
				", totalCount='" + totalCount + '\'' +
				", totalPage='" + totalPage + '\'' +
				", article_public_idstr='" + article_public_idstr + '\'' +
				", searchWord='" + searchWord + '\'' +
				", classify='" + classify + '\'' +
				", startTime='" + startTime + '\'' +
				", endTime='" + endTime + '\'' +
				", sortType=" + sortType +
				", emotions='" + emotions + '\'' +
				", matchType=" + matchType +
				", searchParam='" + searchParam + '\'' +
				", topic='" + topic + '\'' +
				", source_name='" + source_name + '\'' +
				", timetype='" + timetype + '\'' +
				", rtype='" + rtype + '\'' +
				", website_id='" + website_id + '\'' +
				", establish=" + establish +
				", status=" + status +
				", publish=" + publish +
				", matchingmode='" + matchingmode + '\'' +
				", kinds='" + kinds + '\'' +
				", times='" + times + '\'' +
				", timee='" + timee + '\'' +
				", city='" + city + '\'' +
				", eventIndex='" + eventIndex + '\'' +
				", industryIndex='" + industryIndex + '\'' +
				", province='" + province + '\'' +
				", similar=" + similar +
				", searchType=" + searchType +
				", emotionalIndex='" + emotionalIndex + '\'' +
				", page=" + page +
				'}';
	}
}
