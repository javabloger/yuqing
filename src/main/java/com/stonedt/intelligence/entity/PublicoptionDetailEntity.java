package com.stonedt.intelligence.entity;

import java.util.Date;

import lombok.Data;

public class PublicoptionDetailEntity {
	private Integer id;
	private Integer publicoption_id;
	private String back_analysis;
	private String event_context;
	private String event_trace;
	private String hot_analysis;
	private String netizens_analysis;
	private String statistics;
	private String propagation_analysis;
	private String thematic_analysis;
	private String unscramble_content;
	private Date create_time;
	private Integer detail_status;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPublicoption_id() {
		return publicoption_id;
	}
	public void setPublicoption_id(Integer publicoption_id) {
		this.publicoption_id = publicoption_id;
	}
	public String getBack_analysis() {
		return back_analysis;
	}
	public void setBack_analysis(String back_analysis) {
		this.back_analysis = back_analysis;
	}
	public String getEvent_context() {
		return event_context;
	}
	public void setEvent_context(String event_context) {
		this.event_context = event_context;
	}
	public String getEvent_trace() {
		return event_trace;
	}
	public void setEvent_trace(String event_trace) {
		this.event_trace = event_trace;
	}
	public String getHot_analysis() {
		return hot_analysis;
	}
	public void setHot_analysis(String hot_analysis) {
		this.hot_analysis = hot_analysis;
	}
	public String getNetizens_analysis() {
		return netizens_analysis;
	}
	public void setNetizens_analysis(String netizens_analysis) {
		this.netizens_analysis = netizens_analysis;
	}
	public String getStatistics() {
		return statistics;
	}
	public void setStatistics(String statistics) {
		this.statistics = statistics;
	}
	public String getPropagation_analysis() {
		return propagation_analysis;
	}
	public void setPropagation_analysis(String propagation_analysis) {
		this.propagation_analysis = propagation_analysis;
	}
	public String getThematic_analysis() {
		return thematic_analysis;
	}
	public void setThematic_analysis(String thematic_analysis) {
		this.thematic_analysis = thematic_analysis;
	}
	public String getUnscramble_content() {
		return unscramble_content;
	}
	public void setUnscramble_content(String unscramble_content) {
		this.unscramble_content = unscramble_content;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public Integer getDetail_status() {
		return detail_status;
	}
	public void setDetail_status(Integer detail_status) {
		this.detail_status = detail_status;
	}
	@Override
	public String toString() {
		return "PublicoptionDetailEntity [id=" + id + ", publicoption_id=" + publicoption_id + ", back_analysis="
				+ back_analysis + ", event_context=" + event_context + ", event_trace=" + event_trace
				+ ", hot_analysis=" + hot_analysis + ", netizens_analysis=" + netizens_analysis + ", statistics="
				+ statistics + ", propagation_analysis=" + propagation_analysis + ", thematic_analysis="
				+ thematic_analysis + ", unscramble_content=" + unscramble_content + ", create_time=" + create_time
				+ ", detail_status=" + detail_status + "]";
	}
}
