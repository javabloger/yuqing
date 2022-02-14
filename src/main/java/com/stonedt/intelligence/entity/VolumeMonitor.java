package com.stonedt.intelligence.entity;
/** 

* @author 作者 ZouFangHao: 

* @version 创建时间：2020年4月15日 下午5:25:32 

* 类说明   声量监测实体类

*/
public class VolumeMonitor {

	private Integer id;
	private String create_time;
	private Long volume_monitor_id;
	private Long project_id;
	private Integer time_period;
	private String keyword_emotion_statistical;
	private String keyword_source_distribution;
	private String keyword_news_rank;
	private String topic_cluster_analysis;
	private String keyword_emotion_trend;
	private String highword_cloud;
	private String keyword_exposure_rank;
	private String keyword_correlation_news;
	private String user_portrait_label;
	private String social_user_volume_rank;
	private String media_user_volume_rank;
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
	public Long getVolume_monitor_id() {
		return volume_monitor_id;
	}
	public void setVolume_monitor_id(Long volume_monitor_id) {
		this.volume_monitor_id = volume_monitor_id;
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
	public String getKeyword_emotion_statistical() {
		return keyword_emotion_statistical;
	}
	public void setKeyword_emotion_statistical(String keyword_emotion_statistical) {
		this.keyword_emotion_statistical = keyword_emotion_statistical;
	}
	public String getKeyword_source_distribution() {
		return keyword_source_distribution;
	}
	public void setKeyword_source_distribution(String keyword_source_distribution) {
		this.keyword_source_distribution = keyword_source_distribution;
	}
	public String getKeyword_news_rank() {
		return keyword_news_rank;
	}
	public void setKeyword_news_rank(String keyword_news_rank) {
		this.keyword_news_rank = keyword_news_rank;
	}
	public String getTopic_cluster_analysis() {
		return topic_cluster_analysis;
	}
	public void setTopic_cluster_analysis(String topic_cluster_analysis) {
		this.topic_cluster_analysis = topic_cluster_analysis;
	}
	public String getKeyword_emotion_trend() {
		return keyword_emotion_trend;
	}
	public void setKeyword_emotion_trend(String keyword_emotion_trend) {
		this.keyword_emotion_trend = keyword_emotion_trend;
	}
	public String getHighword_cloud() {
		return highword_cloud;
	}
	public void setHighword_cloud(String highword_cloud) {
		this.highword_cloud = highword_cloud;
	}
	public String getKeyword_exposure_rank() {
		return keyword_exposure_rank;
	}
	public void setKeyword_exposure_rank(String keyword_exposure_rank) {
		this.keyword_exposure_rank = keyword_exposure_rank;
	}
	public String getKeyword_correlation_news() {
		return keyword_correlation_news;
	}
	public void setKeyword_correlation_news(String keyword_correlation_news) {
		this.keyword_correlation_news = keyword_correlation_news;
	}
	public String getUser_portrait_label() {
		return user_portrait_label;
	}
	public void setUser_portrait_label(String user_portrait_label) {
		this.user_portrait_label = user_portrait_label;
	}
	public String getSocial_user_volume_rank() {
		return social_user_volume_rank;
	}
	public void setSocial_user_volume_rank(String social_user_volume_rank) {
		this.social_user_volume_rank = social_user_volume_rank;
	}
	public String getMedia_user_volume_rank() {
		return media_user_volume_rank;
	}
	public void setMedia_user_volume_rank(String media_user_volume_rank) {
		this.media_user_volume_rank = media_user_volume_rank;
	}
	@Override
	public String toString() {
		return "VolumeMonitor [id=" + id + ", create_time=" + create_time + ", volume_monitor_id=" + volume_monitor_id
				+ ", project_id=" + project_id + ", time_period=" + time_period + ", keyword_emotion_statistical="
				+ keyword_emotion_statistical + ", keyword_source_distribution=" + keyword_source_distribution
				+ ", keyword_news_rank=" + keyword_news_rank + ", topic_cluster_analysis=" + topic_cluster_analysis
				+ ", keyword_emotion_trend=" + keyword_emotion_trend + ", highword_cloud=" + highword_cloud
				+ ", keyword_exposure_rank=" + keyword_exposure_rank + ", keyword_correlation_news="
				+ keyword_correlation_news + ", user_portrait_label=" + user_portrait_label
				+ ", social_user_volume_rank=" + social_user_volume_rank + ", media_user_volume_rank="
				+ media_user_volume_rank + "]";
	}
}
