
package com.stonedt.intelligence.entity;
/**
* <p>预警信息</p>
* <p>Title: WarningSetting</p>  
* <p>Description: </p>  
* @author Mapeng 
* @date Apr 16, 2020  
*/

public class WarningSetting {

	private Integer id; // 自增id;
	private String create_time; // '创建时间';
	private Long warning_setting_id; // '预警设置公共id';
	private Long project_id;  // '方案id';
	private Integer warning_status;  // '预警开关 ( 0:关，1:开 )';
	private String warning_name;  // '预警名称';
	private String warning_word;  // '预警词(英文逗号分隔)';
	private String warning_classify;  // '来源类型(1-11)(英文逗号分隔)';
	private Integer warning_content;  // '预警内容(0:全部 1:敏感)';
	private Integer warning_similar;  // '相似文章合并（0：取消合并 1：合并）';
	private Integer warning_match;  // '匹配方式（1：全文 2：标题 3：正文）';
	private Integer warning_deduplication;  // '预警去重（0：关闭 1：开启）';
	private String warning_source; // '预警来源json（[type]1:系统推送 2：邮箱推送 [email]:邮箱地址，可为空）';
	private String warning_receive_time;// '接收时间json [start]:开始时间 [end]:结束时间';
	private Integer weekend_warning;  // '周末预警（0：关闭 1：开启）';
	private String warning_interval; // '预警间隔json（[type]1:实时预警 2：定时预警 [time]:时间，可为空）';
	
	private String project_name; //方案名称
	private String group_name; //方案组名称
	private Long user_id; //用户id
	
	
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
	public Long getWarning_setting_id() {
		return warning_setting_id;
	}
	public void setWarning_setting_id(Long warning_setting_id) {
		this.warning_setting_id = warning_setting_id;
	}
	public Long getProject_id() {
		return project_id;
	}
	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}
	public Integer getWarning_status() {
		return warning_status;
	}
	public void setWarning_status(Integer warning_status) {
		this.warning_status = warning_status;
	}
	public String getWarning_name() {
		return warning_name;
	}
	public void setWarning_name(String warning_name) {
		this.warning_name = warning_name;
	}
	public String getWarning_word() {
		return warning_word;
	}
	public void setWarning_word(String warning_word) {
		this.warning_word = warning_word;
	}
	public String getWarning_classify() {
		return warning_classify;
	}
	public void setWarning_classify(String warning_classify) {
		this.warning_classify = warning_classify;
	}
	public Integer getWarning_content() {
		return warning_content;
	}
	public void setWarning_content(Integer warning_content) {
		this.warning_content = warning_content;
	}
	public Integer getWarning_similar() {
		return warning_similar;
	}
	public void setWarning_similar(Integer warning_similar) {
		this.warning_similar = warning_similar;
	}
	public Integer getWarning_match() {
		return warning_match;
	}
	public void setWarning_match(Integer warning_match) {
		this.warning_match = warning_match;
	}
	public Integer getWarning_deduplication() {
		return warning_deduplication;
	}
	public void setWarning_deduplication(Integer warning_deduplication) {
		this.warning_deduplication = warning_deduplication;
	}
	public String getWarning_source() {
		return warning_source;
	}
	public void setWarning_source(String warning_source) {
		this.warning_source = warning_source;
	}
	public String getWarning_receive_time() {
		return warning_receive_time;
	}
	public void setWarning_receive_time(String warning_receive_time) {
		this.warning_receive_time = warning_receive_time;
	}
	public Integer getWeekend_warning() {
		return weekend_warning;
	}
	public void setWeekend_warning(Integer weekend_warning) {
		this.weekend_warning = weekend_warning;
	}
	public String getWarning_interval() {
		return warning_interval;
	}
	public void setWarning_interval(String warning_interval) {
		this.warning_interval = warning_interval;
	}
	public String getProject_name() {
		return project_name;
	}
	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	@Override
	public String toString() {
		return "WarningSetting [id=" + id + ", create_time=" + create_time + ", warning_setting_id="
				+ warning_setting_id + ", project_id=" + project_id + ", warning_status=" + warning_status
				+ ", warning_name=" + warning_name + ", warning_word=" + warning_word + ", warning_classify="
				+ warning_classify + ", warning_content=" + warning_content + ", warning_similar=" + warning_similar
				+ ", warning_match=" + warning_match + ", warning_deduplication=" + warning_deduplication
				+ ", warning_source=" + warning_source + ", warning_receive_time=" + warning_receive_time
				+ ", weekend_warning=" + weekend_warning + ", warning_interval=" + warning_interval + ", project_name="
				+ project_name + ", group_name=" + group_name + ", user_id=" + user_id + "]";
	}
}
