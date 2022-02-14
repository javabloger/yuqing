package com.stonedt.intelligence.entity;

import java.io.Serializable;

/**
 *	数据报告详情数据实体类
 */
public class ReportDetail implements Serializable{

	private static final long serialVersionUID = -6050219486204498740L;

	private Integer id;
	
	private String create_time;
	
	private Long report_id;
	
	private String data_overview;
	
	private String emotion_analysis;
	
	private String hot_event_ranking;
	
	private String media_activity_analysis;
	
	private String self_media_ranking;
	
	private String high_word_index;
	
	private String hot_spot_ranking;
	
	private String netizen_word_cloud;
	
	private String media_cord_cloud;
	
	private String hot_people;
	
	private String hot_spots;
	
	private String topic_clustering;
	
	private String social_v_ranking;
	
	
	
	private String report_name;
	private Integer report_type;
	private String report_starttime;
	private String report_endtime;
	private String report_time;
	
	private String highword_cloud;
	private String keyword_index;
	
	
	private String highword_cloud_index;
	private String ner;
	
	
	
	
	
	
	
	
	public String getNer() {
		return ner;
	}
	public void setNer(String ner) {
		this.ner = ner;
	}
	public String getHighword_cloud_index() {
		return highword_cloud_index;
	}
	public void setHighword_cloud_index(String highword_cloud_index) {
		this.highword_cloud_index = highword_cloud_index;
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
	public Long getReport_id() {
		return report_id;
	}
	public void setReport_id(Long report_id) {
		this.report_id = report_id;
	}
	public String getData_overview() {
		return data_overview;
	}
	public void setData_overview(String data_overview) {
		this.data_overview = data_overview;
	}
	public String getEmotion_analysis() {
		return emotion_analysis;
	}
	public void setEmotion_analysis(String emotion_analysis) {
		this.emotion_analysis = emotion_analysis;
	}
	public String getHot_event_ranking() {
		return hot_event_ranking;
	}
	public void setHot_event_ranking(String hot_event_ranking) {
		this.hot_event_ranking = hot_event_ranking;
	}
	public String getMedia_activity_analysis() {
		return media_activity_analysis;
	}
	public void setMedia_activity_analysis(String media_activity_analysis) {
		this.media_activity_analysis = media_activity_analysis;
	}
	public String getSelf_media_ranking() {
		return self_media_ranking;
	}
	public void setSelf_media_ranking(String self_media_ranking) {
		this.self_media_ranking = self_media_ranking;
	}
	public String getHigh_word_index() {
		return high_word_index;
	}
	public void setHigh_word_index(String high_word_index) {
		this.high_word_index = high_word_index;
	}
	public String getHot_spot_ranking() {
		return hot_spot_ranking;
	}
	public void setHot_spot_ranking(String hot_spot_ranking) {
		this.hot_spot_ranking = hot_spot_ranking;
	}
	public String getNetizen_word_cloud() {
		return netizen_word_cloud;
	}
	public void setNetizen_word_cloud(String netizen_word_cloud) {
		this.netizen_word_cloud = netizen_word_cloud;
	}
	public String getMedia_cord_cloud() {
		return media_cord_cloud;
	}
	public void setMedia_cord_cloud(String media_cord_cloud) {
		this.media_cord_cloud = media_cord_cloud;
	}
	public String getHot_people() {
		return hot_people;
	}
	public void setHot_people(String hot_people) {
		this.hot_people = hot_people;
	}
	public String getHot_spots() {
		return hot_spots;
	}
	public void setHot_spots(String hot_spots) {
		this.hot_spots = hot_spots;
	}
	public String getTopic_clustering() {
		return topic_clustering;
	}
	public void setTopic_clustering(String topic_clustering) {
		this.topic_clustering = topic_clustering;
	}
	public String getSocial_v_ranking() {
		return social_v_ranking;
	}
	public void setSocial_v_ranking(String social_v_ranking) {
		this.social_v_ranking = social_v_ranking;
	}
	public String getReport_name() {
		return report_name;
	}
	public void setReport_name(String report_name) {
		this.report_name = report_name;
	}
	public Integer getReport_type() {
		return report_type;
	}
	public void setReport_type(Integer report_type) {
		this.report_type = report_type;
	}
	public String getReport_starttime() {
		return report_starttime;
	}
	public void setReport_starttime(String report_starttime) {
		this.report_starttime = report_starttime;
	}
	public String getReport_endtime() {
		return report_endtime;
	}
	public void setReport_endtime(String report_endtime) {
		this.report_endtime = report_endtime;
	}
	public String getReport_time() {
		return report_time;
	}
	public void setReport_time(String report_time) {
		this.report_time = report_time;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	

	
	
}
