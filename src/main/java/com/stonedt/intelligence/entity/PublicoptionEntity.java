package com.stonedt.intelligence.entity;

/**
 * 
 * @author wangyi
 *
 */
public class PublicoptionEntity {
	private int id;
	private String eventname;//任务名称
	private String eventkeywords;//任务关键词
	private String eventstopwords;//任务屏蔽词
	private String eventstarttime;//任务开始时间
	private String eventendtime;//任务结束时间
	private String createtime;//创建时间
	private int status;//1.创建失败2.正在创建3.创建成功
	private String updatetime;//更新时间
	private Long user_id;
	private String netizens_analysis;
	private Integer page;
	private String emotionalIndex;
	public String getEmotionalIndex() {
		return emotionalIndex;
	}
	public void setEmotionalIndex(String emotionalIndex) {
		this.emotionalIndex = emotionalIndex;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	@Override
	public String toString() {
		return "PublicoptionEntity [id=" + id + ", eventname=" + eventname + ", eventkeywords=" + eventkeywords
				+ ", eventstopwords=" + eventstopwords + ", eventstarttime=" + eventstarttime + ", eventendtime="
				+ eventendtime + ", createtime=" + createtime + ", status=" + status + ", updatetime=" + updatetime
				+ ", user_id=" + user_id + ", netizens_analysis=" + netizens_analysis + "]";
	}
	public String getNetizens_analysis() {
		return netizens_analysis;
	}
	public void setNetizens_analysis(String netizens_analysis) {
		this.netizens_analysis = netizens_analysis;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEventname() {
		return eventname;
	}
	public void setEventname(String eventname) {
		this.eventname = eventname;
	}
	public String getEventkeywords() {
		return eventkeywords;
	}
	public void setEventkeywords(String eventkeywords) {
		this.eventkeywords = eventkeywords;
	}
	public String getEventstopwords() {
		return eventstopwords;
	}
	public void setEventstopwords(String eventstopwords) {
		this.eventstopwords = eventstopwords;
	}
	public String getEventstarttime() {
		return eventstarttime;
	}
	public void setEventstarttime(String eventstarttime) {
		this.eventstarttime = eventstarttime;
	}
	public String getEventendtime() {
		return eventendtime;
	}
	public void setEventendtime(String eventendtime) {
		this.eventendtime = eventendtime;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	
}
