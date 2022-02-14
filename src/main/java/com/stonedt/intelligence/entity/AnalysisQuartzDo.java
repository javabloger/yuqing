package com.stonedt.intelligence.entity;

import java.io.Serializable;

public class AnalysisQuartzDo implements Serializable {

	private static final long serialVersionUID = 7689436409703760069L;

	private Integer id;

	private String create_time;

	private Long analysis_id;

	private Long project_id;

	private Integer time_period;

	private String data_overview;

	private String emotional_proportion;

	private String plan_word_hit;

	private String keyword_emotion_trend;

	private String hot_event_ranking;

	private String highword_cloud;

	private String keyword_index;

	private String media_activity_analysis;

	private String hot_spot_ranking;

	private String data_source_distribution;

	private String data_source_analysis;

	private String keyword_exposure_rank;

	private String selfmedia_volume_rank;

	private String ner;// 实体
	
	private String category_rank;//分类统计

	private String industrial_distribution;//行业分布

	private String event_statistics;//事件统计
	
	

	
	private String relative_news;
	private String popular_information;
	private String popular_event;
	private String hot_company;
	private String hot_people;
	private String hot_spot;
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
	public Long getAnalysis_id() {
		return analysis_id;
	}
	public void setAnalysis_id(Long analysis_id) {
		this.analysis_id = analysis_id;
	}
	public Long getProject_id() {
		return project_id;
	}
	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}
	public Integer getTime_period() {
		return time_period;
	}
	public void setTime_period(Integer time_period) {
		this.time_period = time_period;
	}
	public String getData_overview() {
		return data_overview;
	}
	public void setData_overview(String data_overview) {
		this.data_overview = data_overview;
	}
	public String getEmotional_proportion() {
		return emotional_proportion;
	}
	public void setEmotional_proportion(String emotional_proportion) {
		this.emotional_proportion = emotional_proportion;
	}
	public String getPlan_word_hit() {
		return plan_word_hit;
	}
	public void setPlan_word_hit(String plan_word_hit) {
		this.plan_word_hit = plan_word_hit;
	}
	public String getKeyword_emotion_trend() {
		return keyword_emotion_trend;
	}
	public void setKeyword_emotion_trend(String keyword_emotion_trend) {
		this.keyword_emotion_trend = keyword_emotion_trend;
	}
	public String getHot_event_ranking() {
		return hot_event_ranking;
	}
	public void setHot_event_ranking(String hot_event_ranking) {
		this.hot_event_ranking = hot_event_ranking;
	}
	public String getHighword_cloud() {
		return highword_cloud;
	}
	public void setHighword_cloud(String highword_cloud) {
		this.highword_cloud = highword_cloud;
	}
	public String getKeyword_index() {
		return keyword_index;
	}
	public void setKeyword_index(String keyword_index) {
		this.keyword_index = keyword_index;
	}
	public String getMedia_activity_analysis() {
		return media_activity_analysis;
	}
	public void setMedia_activity_analysis(String media_activity_analysis) {
		this.media_activity_analysis = media_activity_analysis;
	}
	public String getHot_spot_ranking() {
		return hot_spot_ranking;
	}
	public void setHot_spot_ranking(String hot_spot_ranking) {
		this.hot_spot_ranking = hot_spot_ranking;
	}
	public String getData_source_distribution() {
		return data_source_distribution;
	}
	public void setData_source_distribution(String data_source_distribution) {
		this.data_source_distribution = data_source_distribution;
	}
	public String getData_source_analysis() {
		return data_source_analysis;
	}
	public void setData_source_analysis(String data_source_analysis) {
		this.data_source_analysis = data_source_analysis;
	}
	public String getKeyword_exposure_rank() {
		return keyword_exposure_rank;
	}
	public void setKeyword_exposure_rank(String keyword_exposure_rank) {
		this.keyword_exposure_rank = keyword_exposure_rank;
	}
	public String getSelfmedia_volume_rank() {
		return selfmedia_volume_rank;
	}
	public void setSelfmedia_volume_rank(String selfmedia_volume_rank) {
		this.selfmedia_volume_rank = selfmedia_volume_rank;
	}
	public String getNer() {
		return ner;
	}
	public void setNer(String ner) {
		this.ner = ner;
	}
	public String getRelative_news() {
		return relative_news;
	}
	public void setRelative_news(String relative_news) {
		this.relative_news = relative_news;
	}
	public String getPopular_information() {
		return popular_information;
	}
	public void setPopular_information(String popular_information) {
		this.popular_information = popular_information;
	}
	public String getPopular_event() {
		return popular_event;
	}
	public void setPopular_event(String popular_event) {
		this.popular_event = popular_event;
	}
	public String getHot_company() {
		return hot_company;
	}
	public void setHot_company(String hot_company) {
		this.hot_company = hot_company;
	}
	public String getHot_people() {
		return hot_people;
	}
	public void setHot_people(String hot_people) {
		this.hot_people = hot_people;
	}
	public String getHot_spot() {
		return hot_spot;
	}
	public void setHot_spot(String hot_spot) {
		this.hot_spot = hot_spot;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getCategory_rank() {
		return category_rank;
	}
	public void setCategory_rank(String category_rank) {
		this.category_rank = category_rank;
	}

	public String getIndustrial_distribution() {
		return industrial_distribution;
	}

	public void setIndustrial_distribution(String industrial_distribution) {
		this.industrial_distribution = industrial_distribution;
	}

	public String getEvent_statistics() {
		return event_statistics;
	}

	public void setEvent_statistics(String event_statistics) {
		this.event_statistics = event_statistics;
	}
}
