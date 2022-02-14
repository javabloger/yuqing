package com.stonedt.intelligence.entity;

import java.io.Serializable;

/**
 *	全文搜索词实体
 */
public class FullWord implements Serializable{

	private static final long serialVersionUID = 7223774137282862600L;
	
	private Integer id;
	private String create_time;
	private Long user_id;
	private String search_word;
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
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getSearch_word() {
		return search_word;
	}
	public void setSearch_word(String search_word) {
		this.search_word = search_word;
	}
	
}
