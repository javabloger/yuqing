package com.stonedt.intelligence.entity;

import java.io.Serializable;

/**
 *
 * @date  2020年4月30日 上午11:39:19
 */
public class ProjectTask implements Serializable{

	private static final long serialVersionUID = 146255634538848352L;
	
	private Integer id;
	
	private String create_time;
	
	private Long project_id;
	
	private Integer project_type;
	
	private String subject_word;
	
	private String character_word;
	
	private String event_word;
	
	private String regional_word;
	
	private String stop_word;
	
	private Integer analysis_flag;
	
	private Integer volume_flag;

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

	public Long getProject_id() {
		return project_id;
	}

	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}

	public Integer getProject_type() {
		return project_type;
	}

	public void setProject_type(Integer project_type) {
		this.project_type = project_type;
	}

	public String getSubject_word() {
		return subject_word;
	}

	public void setSubject_word(String subject_word) {
		this.subject_word = subject_word;
	}

	public String getCharacter_word() {
		return character_word;
	}

	public void setCharacter_word(String character_word) {
		this.character_word = character_word;
	}

	public String getEvent_word() {
		return event_word;
	}

	public void setEvent_word(String event_word) {
		this.event_word = event_word;
	}

	public String getRegional_word() {
		return regional_word;
	}

	public void setRegional_word(String regional_word) {
		this.regional_word = regional_word;
	}

	public String getStop_word() {
		return stop_word;
	}

	public void setStop_word(String stop_word) {
		this.stop_word = stop_word;
	}

	public Integer getAnalysis_flag() {
		return analysis_flag;
	}

	public void setAnalysis_flag(Integer analysis_flag) {
		this.analysis_flag = analysis_flag;
	}

	public Integer getVolume_flag() {
		return volume_flag;
	}

	public void setVolume_flag(Integer volume_flag) {
		this.volume_flag = volume_flag;
	}
	

}
