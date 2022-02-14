package com.stonedt.intelligence.entity;

import java.io.Serializable;

/**
 *	数据报告列表实体类
 */
public class ReportCustom implements Serializable{

	private static final long serialVersionUID = 8128613465315245208L;
	
	private Integer id;
	
	private String create_time;
	
	private Long report_id;
	
	private String report_name;
	
	private Integer report_type;
	
	private String report_starttime;
	
	private String report_endtime;
	
	private Integer report_status;
	
	private Integer report_topping;
	
	private String report_time;
	
	private Integer del_status;
	
	private Integer number_period;
	
	private Integer processes;
	
	private Integer module_sum;
	
	private Long template_id;
	
	private String template_info;
	
	private Long project_id;
	
	private String keyword;
	
	private String stopword;
	
	private Long user_id;

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

	public Integer getReport_status() {
		return report_status;
	}

	public void setReport_status(Integer report_status) {
		this.report_status = report_status;
	}

	public Integer getReport_topping() {
		return report_topping;
	}

	public void setReport_topping(Integer report_topping) {
		this.report_topping = report_topping;
	}

	public String getReport_time() {
		return report_time;
	}

	public void setReport_time(String report_time) {
		this.report_time = report_time;
	}

	public Integer getDel_status() {
		return del_status;
	}

	public void setDel_status(Integer del_status) {
		this.del_status = del_status;
	}

	public Integer getNumber_period() {
		return number_period;
	}

	public void setNumber_period(Integer number_period) {
		this.number_period = number_period;
	}

	public Integer getProcesses() {
		return processes;
	}

	public void setProcesses(Integer processes) {
		this.processes = processes;
	}

	public Integer getModule_sum() {
		return module_sum;
	}

	public void setModule_sum(Integer module_sum) {
		this.module_sum = module_sum;
	}

	public Long getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(Long template_id) {
		this.template_id = template_id;
	}

	public String getTemplate_info() {
		return template_info;
	}

	public void setTemplate_info(String template_info) {
		this.template_info = template_info;
	}

	public Long getProject_id() {
		return project_id;
	}

	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getStopword() {
		return stopword;
	}

	public void setStopword(String stopword) {
		this.stopword = stopword;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	
}
