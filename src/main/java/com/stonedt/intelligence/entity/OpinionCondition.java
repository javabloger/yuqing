package com.stonedt.intelligence.entity;

import java.io.Serializable;

/**
 *	偏好设置实体类
 */
public class OpinionCondition implements Serializable{

	private static final long serialVersionUID = 8839974637709449961L;
	
	private Integer id;
	
	private String create_time;
	
	private Long opinion_condition_id;
	
	private Long project_id;
	
	private Integer time;
	
	private Integer precise;
	
	private String emotion;
	
	private Integer similar;
	
	private Integer sort;
	
	private Integer matchs;

	private String times;

	private String timee;

	private String classify;//数据来源

	private String websitename;/*网站名称*/

	private String author;/*作者名称*/

	private String organization;/*涉及机构*/

	private String categorylable;/*文章分类*/

	private String enterprisetype;/*涉及企业*/

	private String hightechtype;/*高科技型企业*/

	private String policylableflag;/*涉及政策*/

	private String datasource_type;/*数据品类*/

	private String eventIndex;/*涉及事件*/

	private String industryIndex;/*涉及行业*/

	private String province;/*涉及省份*/

	private String city;/*涉及城市*/

	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public Long getOpinion_condition_id() {
		return opinion_condition_id;
	}

	public void setOpinion_condition_id(Long opinion_condition_id) {
		this.opinion_condition_id = opinion_condition_id;
	}

	public Long getProject_id() {
		return project_id;
	}

	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public Integer getPrecise() {
		return precise;
	}

	public void setPrecise(Integer precise) {
		this.precise = precise;
	}

	public String getEmotion() {
		return emotion;
	}

	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}

	public Integer getSimilar() {
		return similar;
	}

	public void setSimilar(Integer similar) {
		this.similar = similar;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getMatchs() {
		return matchs;
	}

	public void setMatchs(Integer matchs) {
		this.matchs = matchs;
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify;
	}

	public String getWebsitename() {
		return websitename;
	}

	public void setWebsitename(String websitename) {
		this.websitename = websitename;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getCategorylable() {
		return categorylable;
	}

	public void setCategorylable(String categorylable) {
		this.categorylable = categorylable;
	}

	public String getEnterprisetype() {
		return enterprisetype;
	}

	public void setEnterprisetype(String enterprisetype) {
		this.enterprisetype = enterprisetype;
	}

	public String getHightechtype() {
		return hightechtype;
	}

	public void setHightechtype(String hightechtype) {
		this.hightechtype = hightechtype;
	}

	public String getPolicylableflag() {
		return policylableflag;
	}

	public void setPolicylableflag(String policylableflag) {
		this.policylableflag = policylableflag;
	}

	public String getDatasource_type() {
		return datasource_type;
	}

	public void setDatasource_type(String datasource_type) {
		this.datasource_type = datasource_type;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
