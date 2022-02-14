package com.stonedt.intelligence.entity;

import java.util.Date;

public class Project {
	private Integer id;
	private Date createTime;
	private Long projectId;
	private String projectName;
	private Date updateTime;
	private Integer projectType;
	private String projectDescription;
	private String subjectWord;
	private String characterWord;
	private String eventWord;
	private String regionalWord;
	private String stopWord;
	private Integer delStatus;
	private Long groupId;
	private Long userId;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getProjectType() {
		return projectType;
	}
	public void setProjectType(Integer projectType) {
		this.projectType = projectType;
	}
	public String getProjectDescription() {
		return projectDescription;
	}
	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}
	public String getSubjectWord() {
		return subjectWord;
	}
	public void setSubjectWord(String subjectWord) {
		this.subjectWord = subjectWord;
	}
	public String getCharacterWord() {
		return characterWord;
	}
	public void setCharacterWord(String characterWord) {
		this.characterWord = characterWord;
	}
	public String getEventWord() {
		return eventWord;
	}
	public void setEventWord(String eventWord) {
		this.eventWord = eventWord;
	}
	public String getRegionalWord() {
		return regionalWord;
	}
	public void setRegionalWord(String regionalWord) {
		this.regionalWord = regionalWord;
	}
	public String getStopWord() {
		return stopWord;
	}
	public void setStopWord(String stopWord) {
		this.stopWord = stopWord;
	}
	public Integer getDelStatus() {
		return delStatus;
	}
	public void setDelStatus(Integer delStatus) {
		this.delStatus = delStatus;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	
	
}
