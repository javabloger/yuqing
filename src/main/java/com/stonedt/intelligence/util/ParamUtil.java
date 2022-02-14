package com.stonedt.intelligence.util;

public class ParamUtil {
	private String field;
	private String keyword;
	public ParamUtil() {
		super();
	}
	public ParamUtil(String field, String keyword) {
		super();
		this.field = field;
		this.keyword = keyword;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	@Override
	public String toString() {
		return "ParamUtil [field=" + field + ", keyword=" + keyword + "]";
	}
}
