package com.stonedt.intelligence.entity;

import lombok.Data;

@Data
public class SystemLogEntity {
 
	private int id;
	private String user_browser;//用户浏览器
	private Integer user_id;//用户id
	private String user_browser_version;//用户浏览器版本
	private String operatingSystem;//操作系统
	private String username;//用户名
	private String loginip;//登陆ip
	private String module;//模块
	private String submodule;//子模块
	private String type;//类型
	private String createtime;//创建时间
	
	
	
}
