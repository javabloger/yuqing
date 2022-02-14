package com.stonedt.intelligence.entity;

import java.util.Date;

public class Analysis {
	
	private Integer id;
	private String create_time;
	private Long analysis_id;
	private Long project_id;
	private Integer time_period;
	private String data_overview;
	private String relative_news;
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
	private String ner;
	private String category_rank;

	private String industrial_distribution;//行业分布统计
	private String event_statistics;//事件统计
	
	
	public String getCategory_rank() {
		return category_rank;
	}
	public void setCategory_rank(String category_rank) {
		this.category_rank = category_rank;
	}
	private Date CreateTime;
	private String updateTime;
	private Long AnalysisId;
	public Analysis() {
		super();
		// TODO Auto-generated constructor stub
	}
	private Long ProjectId;
	private String RelativeNews;
	private String KeywordIndex;
	private String EmotionalProportion;
	private String PlanWordHit;
	private String PopularInformation;
	private String PopularEvent;
	private String HotCompany;
	private String HotPeople;
	private String HotSpot;
	private Integer TimePeriod;
	private String keyword_emotion_statistical;
	
	public String getNer() {
		return ner;
	}
	public void setNer(String ner) {
		this.ner = ner;
	}
	public String getSelfmedia_volume_rank() {
		return selfmedia_volume_rank;
	}
	public void setSelfmedia_volume_rank(String selfmedia_volume_rank) {
		this.selfmedia_volume_rank = selfmedia_volume_rank;
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
	public String getRelative_news() {
		return relative_news;
	}
	public void setRelative_news(String relative_news) {
		this.relative_news = relative_news;
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
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(Date createTime) {
		CreateTime = createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public Long getAnalysisId() {
		return AnalysisId;
	}
	public void setAnalysisId(Long analysisId) {
		AnalysisId = analysisId;
	}
	public Long getProjectId() {
		return ProjectId;
	}
	public void setProjectId(Long projectId) {
		ProjectId = projectId;
	}
	public String getRelativeNews() {
		return RelativeNews;
	}
	public void setRelativeNews(String relativeNews) {
		RelativeNews = relativeNews;
	}
	public String getKeywordIndex() {
		return KeywordIndex;
	}
	public void setKeywordIndex(String keywordIndex) {
		KeywordIndex = keywordIndex;
	}
	public String getEmotionalProportion() {
		return EmotionalProportion;
	}
	public void setEmotionalProportion(String emotionalProportion) {
		EmotionalProportion = emotionalProportion;
	}
	public String getPlanWordHit() {
		return PlanWordHit;
	}
	public void setPlanWordHit(String planWordHit) {
		PlanWordHit = planWordHit;
	}
	public String getPopularInformation() {
		return PopularInformation;
	}
	public void setPopularInformation(String popularInformation) {
		PopularInformation = popularInformation;
	}
	public String getPopularEvent() {
		return PopularEvent;
	}
	public void setPopularEvent(String popularEvent) {
		PopularEvent = popularEvent;
	}
	public String getHotCompany() {
		return HotCompany;
	}
	public void setHotCompany(String hotCompany) {
		HotCompany = hotCompany;
	}
	public String getHotPeople() {
		return HotPeople;
	}
	public void setHotPeople(String hotPeople) {
		HotPeople = hotPeople;
	}
	public String getHotSpot() {
		return HotSpot;
	}
	public void setHotSpot(String hotSpot) {
		HotSpot = hotSpot;
	}
	public Integer getTimePeriod() {
		return TimePeriod;
	}
	public void setTimePeriod(Integer timePeriod) {
		TimePeriod = timePeriod;
	}
	public String getKeyword_emotion_trend() {
		return keyword_emotion_trend;
	}
	public void setKeyword_emotion_trend(String keyword_emotion_trend) {
		this.keyword_emotion_trend = keyword_emotion_trend;
	}
	public String getKeyword_emotion_statistical() {
		return keyword_emotion_statistical;
	}
	public void setKeyword_emotion_statistical(String keyword_emotion_statistical) {
		this.keyword_emotion_statistical = keyword_emotion_statistical;
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

	public Analysis(Integer id, String create_time, Long analysis_id, Long project_id, Integer time_period,
					String data_overview, String relative_news, String emotional_proportion, String plan_word_hit,
					String keyword_emotion_trend, String hot_event_ranking, String highword_cloud, String keyword_index,
					String media_activity_analysis, String hot_spot_ranking, String data_source_distribution,
					String data_source_analysis, String keyword_exposure_rank, String selfmedia_volume_rank, String ner, String category_rank) {
		super();
		this.id = id;
		this.create_time = create_time;
		this.analysis_id = analysis_id;
		this.project_id = project_id;
		this.time_period = time_period;
		this.data_overview = data_overview;
		this.relative_news = relative_news;
		this.emotional_proportion = emotional_proportion;
		this.plan_word_hit = plan_word_hit;
		this.keyword_emotion_trend = keyword_emotion_trend;
		this.hot_event_ranking = hot_event_ranking;
		this.highword_cloud = highword_cloud;
		this.keyword_index = keyword_index;
		this.media_activity_analysis = media_activity_analysis;
		this.hot_spot_ranking = hot_spot_ranking;
		this.data_source_distribution = data_source_distribution;
		this.data_source_analysis = data_source_analysis;
		this.keyword_exposure_rank = keyword_exposure_rank;
		this.selfmedia_volume_rank = selfmedia_volume_rank;
		this.ner = ner;
		this.category_rank = category_rank;
	}
	
	
	
}
