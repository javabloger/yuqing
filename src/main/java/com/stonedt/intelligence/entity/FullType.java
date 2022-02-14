package com.stonedt.intelligence.entity;

import java.io.Serializable;

/**
 *	全文搜索分类菜单实体
 */
public class FullType implements Serializable{

	private static final long serialVersionUID = -5086646339123925205L;
	
	private Integer id;
	private Integer only_id;
	private String create_time;
	private Integer type;
	private String name;
	private String value;
	private Integer type_one_id;
	private Integer type_two_id;
	private String icon;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getOnly_id() {
		return only_id;
	}
	public void setOnly_id(Integer only_id) {
		this.only_id = only_id;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Integer getType_one_id() {
		return type_one_id;
	}
	public void setType_one_id(Integer type_one_id) {
		this.type_one_id = type_one_id;
	}
	public Integer getType_two_id() {
		return type_two_id;
	}
	public void setType_two_id(Integer type_two_id) {
		this.type_two_id = type_two_id;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
}
